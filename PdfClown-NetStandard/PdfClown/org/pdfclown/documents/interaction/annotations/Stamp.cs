/*
  Copyright 2008-2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library" (the
  Program): see the accompanying README files for more info.

  This Program is free software; you can redistribute it and/or modify it under the terms
  of the GNU Lesser General Public License as published by the Free Software Foundation;
  either version 3 of the License, or (at your option) any later version.

  This Program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  either expressed or implied; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this
  Program (see README files); if not, go to the GNU website (http://www.gnu.org/licenses/).

  Redistribution and use, with or without modification, are permitted provided that such
  redistributions retain the above copyright notice, license and disclaimer, along with
  this list of conditions.
*/

using org.pdfclown.bytes;
using org.pdfclown.documents;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.objects;
using org.pdfclown.util;
using org.pdfclown.util.math.geom;

using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;

namespace org.pdfclown.documents.interaction.annotations
{
  /**
    <summary>Rubber stamp annotation [PDF:1.6:8.4.5].</summary>
    <remarks>It displays text or graphics intended to look as if they were stamped
    on the page with a rubber stamp.</remarks>
  */
  [PDF(VersionEnum.PDF13)]
  public sealed class Stamp
    : Markup
  {
    #region types
    /**
      <summary>Predefined stamp type [PDF:1.6:8.4.5].</summary>
    */
    public enum StandardTypeEnum
    {
      Accepted,
      Approved,
      AsIs,
      BusinessApproved,
      BusinessCompleted,
      BusinessConfidential,
      BusinessDraft,
      BusinessFinal,
      BusinessForComment,
      BusinessForPublicRelease,
      BusinessInformationOnly,
      BusinessNotApproved,
      BusinessNotForPublicRelease,
      BusinessVoid,
      BusinessPreliminaryResults,
      Confidential,
      Departmental,
      Draft,
      Experimental,
      Expired,
      Final,
      ForComment,
      ForPublicRelease,
      InitialHere,
      NotApproved,
      NotForPublicRelease,
      Rejected,
      SignHere,
      Sold,
      TopSecret,
      Witness
    }
    #endregion

    #region static
    #region fields
    private static readonly string CustomTypeName = "Custom";
    private static readonly StandardTypeEnum DefaultType = StandardTypeEnum.Draft;
    #endregion
    #endregion

    #region dynamic
    #region constructors
    /**
      <summary>Creates a new predefined stamp on the specified page.</summary>
      <param name="page">Page where this stamp has to be placed.</param>
      <param name="location">Position where this stamp has to be centered.</param>
      <param name="size">Dimension of the stamp:
        <list type="bullet">
          <item><c>null</c> to apply the natural size</item>
          <item><c>SizeF(0, height)</c> to scale the width proportionally to the height</item>
          <item><c>SizeF(width, 0)</c> to scale the height proportionally to the width</item>
        </list>
      </param>
      <param name="text">Annotation text.</param>
      <param name="type">Predefined stamp type.</param>
    */
    public Stamp(
      Page page,
      PointF location,
      SizeF? size,
      string text,
      StandardTypeEnum type
    ) : base(
      page,
      PdfName.Stamp,
      GeomUtils.Align(
        size.HasValue
          ? new RectangleF(0, 0,
            size.Value.Width > 0 ? size.Value.Width : size.Value.Height * type.GetAspect(),
            size.Value.Height > 0 ? size.Value.Height : size.Value.Width / type.GetAspect()
            )
          : new RectangleF(0, 0, 40 * type.GetAspect(), 40),
        location,
        new Point(0, 0)
        ),
      text
      )
    {TypeName = type.GetName().StringValue;}

    /**
      <summary>Creates a new custom stamp on the specified page.</summary>
      <param name="page">Page where this stamp has to be placed.</param>
      <param name="location">Position where this stamp has to be centered.</param>
      <param name="text">Annotation text.</param>
      <param name="appearance">Custom appearance.</param>
    */
    public Stamp(
      Page page,
      PointF location,
      string text,
      FormXObject appearance
    ) : base(
      page,
      PdfName.Stamp,
      GeomUtils.Align(
        appearance.Box.ToPath().GetBounds(appearance.Matrix),
        location,
        new Point(0, 0)
        ),
      text
      )
    {
      Appearance.Normal[null] = appearance;
      TypeName = CustomTypeName;
    }

    internal Stamp(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets/Sets the type name of this stamp.</summary>
      <remarks>To ensure predictable rendering of the <see cref="StandardTypeEnum">standard stamp
      types</see> across the systems, <see cref="Document.Configuration.StampPath"/> must be defined
      so as to embed the corresponding templates.</remarks>
    */
    public string TypeName
    {
      get
      {
        PdfName typeNameObject = (PdfName)BaseDataObject[PdfName.Name];
        return typeNameObject != null ? typeNameObject.StringValue : DefaultType.GetName().StringValue;
      }
      set
      {
        PdfName typeNameObject = PdfName.Get(value);
        BaseDataObject[PdfName.Name] = (typeNameObject != null && !typeNameObject.Equals(DefaultType.GetName()) ? typeNameObject : null);

        StandardTypeEnum? standardType = StampStandardTypeEnumExtension.Get(typeNameObject);
        if(standardType.HasValue)
        {
          /*
            NOTE: Standard stamp types leverage predefined appearances.
          */
          Appearance.Normal[null] = Document.Configuration.GetStamp(standardType.Value);
        }
      }
    }

    /**
      <summary>Gets/Sets the rotation applied to the stamp.</summary>
    */
    public int Rotation
    {
      get
      {
        IPdfNumber rotationObject = (IPdfNumber)BaseDataObject[PdfName.Rotate];
        return rotationObject != null ? rotationObject.IntValue : 0;
      }
      set
      {
        BaseDataObject[PdfName.Rotate] = (value != 0 ? new PdfInteger(value) : null);

        FormXObject appearance = Appearance.Normal[null];
        // Custom appearance?
        if(appearance != null)
        {
          /*
            NOTE: Custom appearances are responsible of their proper rotation.
            NOTE: Rotation must preserve the original scale factor.
          */
          RectangleF oldBox = Box;
          RectangleF unscaledOldBox = appearance.Box.ToPath().GetBounds(appearance.Matrix);
          SizeF scale = new SizeF(oldBox.Width / unscaledOldBox.Width, oldBox.Height / unscaledOldBox.Height);

          Matrix matrix = new Matrix();
          matrix.Rotate(value);
          appearance.Matrix = matrix;

          RectangleF appearanceBox = appearance.Box;
          appearanceBox  = new RectangleF(0, 0, appearanceBox.Width * scale.Width, appearanceBox.Height * scale.Height);
          Box = GeomUtils.Align(
            appearanceBox.ToPath().GetBounds(appearance.Matrix),
            oldBox.Center(),
            new Point(0, 0)
            );
        }
      }
    }
    #endregion
    #endregion
    #endregion
  }

  internal static class StampStandardTypeEnumExtension
  {
    private class TypeItem
    {
      public readonly float Aspect;
      public readonly PdfName Code;

      internal TypeItem(
        PdfName code,
        float aspect
        )
      {
        Code = code;
        Aspect = aspect;
      }
    }

    private static readonly Dictionary<Stamp.StandardTypeEnum,TypeItem> codes;

    static StampStandardTypeEnumExtension()
    {
      codes = new Dictionary<Stamp.StandardTypeEnum,TypeItem>();
      codes[Stamp.StandardTypeEnum.Accepted] = new TypeItem(PdfName.SHAccepted, 1.14f);
      codes[Stamp.StandardTypeEnum.Approved] = new TypeItem(PdfName.Approved, 3.8f);
      codes[Stamp.StandardTypeEnum.AsIs] = new TypeItem(PdfName.AsIs, 3.8f);
      codes[Stamp.StandardTypeEnum.BusinessApproved] = new TypeItem(PdfName.SBApproved, 3.3f);
      codes[Stamp.StandardTypeEnum.BusinessCompleted] = new TypeItem(PdfName.SBCompleted, 3.55f);
      codes[Stamp.StandardTypeEnum.BusinessConfidential] = new TypeItem(PdfName.SBConfidential, 4.23f);
      codes[Stamp.StandardTypeEnum.BusinessDraft] = new TypeItem(PdfName.SBDraft, 2.27f);
      codes[Stamp.StandardTypeEnum.BusinessFinal] = new TypeItem(PdfName.SBFinal, 1.97f);
      codes[Stamp.StandardTypeEnum.BusinessForComment] = new TypeItem(PdfName.SBForComment, 4.28f);
      codes[Stamp.StandardTypeEnum.BusinessForPublicRelease] = new TypeItem(PdfName.SBForPublicRelease, 5.85f);
      codes[Stamp.StandardTypeEnum.BusinessInformationOnly] = new TypeItem(PdfName.SBInformationOnly, 5.55f);
      codes[Stamp.StandardTypeEnum.BusinessNotApproved] = new TypeItem(PdfName.SBNotApproved, 4.42f);
      codes[Stamp.StandardTypeEnum.BusinessNotForPublicRelease] = new TypeItem(PdfName.SBNotForPublicRelease, 6.98f);
      codes[Stamp.StandardTypeEnum.BusinessVoid] = new TypeItem(PdfName.SBVoid, 1.83f);
      codes[Stamp.StandardTypeEnum.BusinessPreliminaryResults] = new TypeItem(PdfName.SBPreliminaryResults, 6.14f);
      codes[Stamp.StandardTypeEnum.Confidential] = new TypeItem(PdfName.Confidential, 3.8f);
      codes[Stamp.StandardTypeEnum.Departmental] = new TypeItem(PdfName.Departmental, 3.8f);
      codes[Stamp.StandardTypeEnum.Draft] = new TypeItem(PdfName.Draft, 3.8f);
      codes[Stamp.StandardTypeEnum.Experimental] = new TypeItem(PdfName.Experimental, 3.8f);
      codes[Stamp.StandardTypeEnum.Expired] = new TypeItem(PdfName.Expired, 3.8f);
      codes[Stamp.StandardTypeEnum.Final] = new TypeItem(PdfName.Final, 3.8f);
      codes[Stamp.StandardTypeEnum.ForComment] = new TypeItem(PdfName.ForComment, 3.8f);
      codes[Stamp.StandardTypeEnum.ForPublicRelease] = new TypeItem(PdfName.ForPublicRelease, 3.8f);
      codes[Stamp.StandardTypeEnum.InitialHere] = new TypeItem(PdfName.SHInitialHere, 3.29f);
      codes[Stamp.StandardTypeEnum.NotApproved] = new TypeItem(PdfName.NotApproved, 3.8f);
      codes[Stamp.StandardTypeEnum.NotForPublicRelease] = new TypeItem(PdfName.NotForPublicRelease, 3.8f);
      codes[Stamp.StandardTypeEnum.Rejected] = new TypeItem(PdfName.SBRejected, 1.0f);
      codes[Stamp.StandardTypeEnum.SignHere] = new TypeItem(PdfName.SHSignHere, 3.29f);
      codes[Stamp.StandardTypeEnum.Sold] = new TypeItem(PdfName.Sold, 3.8f);
      codes[Stamp.StandardTypeEnum.TopSecret] = new TypeItem(PdfName.TopSecret, 3.8f);
      codes[Stamp.StandardTypeEnum.Witness] = new TypeItem(PdfName.SHWitness, 3.29f);
    }

    public static Stamp.StandardTypeEnum? Get(
      PdfName name
      )
    {
      foreach(KeyValuePair<Stamp.StandardTypeEnum,TypeItem> entry in codes)
      {
        if(entry.Value.Code.Equals(name))
          return entry.Key;
      }
      return null;
    }

    /**
      <summary>Gets the aspect ratio of the original Acrobat standard stamp.</summary>
    */
    public static float GetAspect(
      this Stamp.StandardTypeEnum type
      )
    {return codes[type].Aspect;}

    public static PdfName GetName(
      this Stamp.StandardTypeEnum type
      )
    {return codes[type].Code;}
  }
}
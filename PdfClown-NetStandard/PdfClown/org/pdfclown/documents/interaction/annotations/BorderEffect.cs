/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

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
using org.pdfclown.documents.contents;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;

namespace org.pdfclown.documents.interaction.annotations
{
  /**
    <summary>Border effect [PDF:1.6:8.4.3].</summary>
  */
  [PDF(VersionEnum.PDF15)]
  public sealed class BorderEffect
    : PdfObjectWrapper<PdfDictionary>
  {
    #region types
    /**
      <summary>Border effect type [PDF:1.6:8.4.3].</summary>
    */
    public enum TypeEnum
    {
      /**
        No effect.
      */
      None,
      /**
        Cloudy.
      */
      Cloudy
    }
    #endregion

    #region static
    #region fields
    private static readonly double DefaultIntensity = 0;
    private static readonly TypeEnum DefaultType = TypeEnum.None;

    private static readonly Dictionary<TypeEnum,PdfName> TypeEnumCodes;
    #endregion

    #region constructors
    static BorderEffect()
    {
      TypeEnumCodes = new Dictionary<TypeEnum,PdfName>();
      TypeEnumCodes[TypeEnum.None] = PdfName.S;
      TypeEnumCodes[TypeEnum.Cloudy] = PdfName.C;
    }
    #endregion

    #region interface
    #region private
    /**
      <summary>Gets the code corresponding to the given value.</summary>
    */
    private static PdfName ToCode(
      TypeEnum value
      )
    {return TypeEnumCodes[value];}

    /**
      <summary>Gets the style corresponding to the given value.</summary>
    */
    private static TypeEnum ToTypeEnum(
      PdfName value
      )
    {
      foreach(KeyValuePair<TypeEnum,PdfName> type in TypeEnumCodes)
      {
        if(type.Value.Equals(value))
          return type.Key;
      }
      return DefaultType;
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    /**
      <summary>Creates a non-reusable instance.</summary>
    */
    public BorderEffect(
      TypeEnum type
      ) : this(null, type)
    {}

    /**
      <summary>Creates a non-reusable instance.</summary>
    */
    public BorderEffect(
      TypeEnum type,
      double intensity
      ) : this(null, type, intensity)
    {}

    /**
      <summary>Creates a reusable instance.</summary>
    */
    public BorderEffect(
      Document context,
      TypeEnum type
      ) : this(context, type, DefaultIntensity)
    {}

    /**
      <summary>Creates a reusable instance.</summary>
    */
    public BorderEffect(
      Document context,
      TypeEnum type,
      double intensity
      ) : base(context, new PdfDictionary())
    {
      Type = type;
      Intensity = intensity;
    }

    internal BorderEffect(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets/Sets the effect intensity.</summary>
      <returns>Value in the range 0-2.</returns>
    */
    public double Intensity
    {
      get
      {
        IPdfNumber intensityObject = (IPdfNumber)BaseDataObject[PdfName.I];
        return intensityObject != null ? intensityObject.DoubleValue : DefaultIntensity;
      }
      set
      {BaseDataObject[PdfName.I] = value != DefaultIntensity ? PdfReal.Get(value) : null;}
    }

    /**
      <summary>Gets/Sets the effect type.</summary>
    */
    public TypeEnum Type
    {
      get
      {return ToTypeEnum((PdfName)BaseDataObject[PdfName.S]);}
      set
      {BaseDataObject[PdfName.S] = value != DefaultType ? ToCode(value) : null;}
    }
    #endregion
    #endregion
    #endregion
  }
}
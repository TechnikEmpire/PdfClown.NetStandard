/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library"
  (the Program): see the accompanying README files for more info.

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

using org.pdfclown;
using org.pdfclown.objects;
using org.pdfclown.util;

using System;

namespace org.pdfclown.documents.contents.layers
{
  /**
    <summary>Visibility expression, used to compute visibility of content based on a set of layers
    [PDF:1.7:4.10.1].</summary>
  */
  [@PDF(VersionEnum.PDF16)]
  public class VisibilityExpression
    : PdfObjectWrapper<PdfArray>
  {
    #region types
    public enum OperatorEnum
    {
      And,
      Or,
      Not
    }

    private class OperandsImpl
      : Array<IPdfObjectWrapper>
    {
      private class ItemWrapper
        : IWrapper<IPdfObjectWrapper>
      {
        public IPdfObjectWrapper Wrap(
          PdfDirectObject baseObject
          )
        {
          if(baseObject.Resolve() is PdfArray)
            return VisibilityExpression.Wrap(baseObject);
          else
            return Layer.Wrap(baseObject);
        }
      }

      private static readonly ItemWrapper Wrapper = new ItemWrapper();

      public OperandsImpl(
        PdfDirectObject baseObject
        ) : base(Wrapper, baseObject)
      {}

      public override int Count
      {
        get
        {return base.Count - 1;}
      }

      public override int IndexOf(
        IPdfObjectWrapper item
        )
      {
        int index = base.IndexOf(item);
        return index > 0 ? index - 1 : -1;
      }

      public override void Insert(
        int index,
        IPdfObjectWrapper item
        )
      {
        if(PdfName.Not.Equals(base[0]) && base.Count >=2)
          throw new ArgumentException("'Not' operator requires only one operand.");

        ValidateItem(item);
        base.Insert(index + 1, item);
      }

      public override void RemoveAt(
        int index
        )
      {base.RemoveAt(index + 1);}

      public override IPdfObjectWrapper this[
        int index
        ]
      {
        get
        {return base[index + 1];}
        set
        {
          ValidateItem(value);
          base[index + 1] = value;
        }
      }

      private void ValidateItem(
        IPdfObjectWrapper item
        )
      {
        if(!(item is VisibilityExpression
          || item is Layer))
          throw new ArgumentException("Operand MUST be either VisibilityExpression or Layer");
      }
    }
    #endregion

    #region static
    #region interface
    #region public
    public static VisibilityExpression Wrap(
      PdfDirectObject baseObject
      )
    {return baseObject != null ? new VisibilityExpression(baseObject) : null;}
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public VisibilityExpression(
      Document context,
      OperatorEnum @operator,
      params IPdfObjectWrapper[] operands
      ) : base(context, new PdfArray((PdfDirectObject)null))
    {
      Operator = @operator;
      var operands_ = Operands;
      foreach(var operand in operands)
      {operands_.Add(operand);}
    }

    protected VisibilityExpression(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    public Array<IPdfObjectWrapper> Operands
    {
      get
      {return new OperandsImpl(BaseObject);}
    }

    public OperatorEnum Operator
    {
      get
      {return OperatorEnumExtension.Get((PdfName)BaseDataObject[0]);}
      set
      {
        if(value == OperatorEnum.Not && BaseDataObject.Count > 2)
          throw new ArgumentException("'Not' operator requires only one operand.");

        BaseDataObject[0] = value.GetName();
      }
    }
    #endregion
    #endregion
    #endregion
  }

  internal static class OperatorEnumExtension
  {
    private static readonly BiDictionary<VisibilityExpression.OperatorEnum,PdfName> codes;

    static OperatorEnumExtension()
    {
      codes = new BiDictionary<VisibilityExpression.OperatorEnum,PdfName>();
      codes[VisibilityExpression.OperatorEnum.And] = PdfName.And;
      codes[VisibilityExpression.OperatorEnum.Not] = PdfName.Not;
      codes[VisibilityExpression.OperatorEnum.Or] = PdfName.Or;
    }

    public static VisibilityExpression.OperatorEnum Get(
      PdfName name
      )
    {
      if(name == null)
        throw new ArgumentNullException();

      VisibilityExpression.OperatorEnum? @operator = codes.GetKey(name);
      if(!@operator.HasValue)
        throw new NotSupportedException("Operator unknown: " + name);

      return @operator.Value;
    }

    public static PdfName GetName(
      this VisibilityExpression.OperatorEnum @operator
      )
    {return codes[@operator];}
  }
}


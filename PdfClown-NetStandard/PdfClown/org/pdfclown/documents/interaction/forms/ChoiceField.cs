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
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.files;
using org.pdfclown.objects;
using org.pdfclown.util;

using System;
using System.Collections;
using System.Collections.Generic;

namespace org.pdfclown.documents.interaction.forms
{
  /**
    <summary>Choice field [PDF:1.6:8.6.3].</summary>
  */
  [PDF(VersionEnum.PDF12)]
  public abstract class ChoiceField
    : Field
  {
    #region dynamic
    #region constructors
    /**
      <summary>Creates a new choice field within the given document context.</summary>
    */
    protected ChoiceField(
      string name,
      Widget widget
      ) : base(PdfName.Ch, name, widget)
    {}

    protected ChoiceField(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    public ChoiceItems Items
    {
      get
      {return new ChoiceItems(BaseDataObject.Get<PdfArray>(PdfName.Opt));}
      set
      {BaseDataObject[PdfName.Opt] = value.BaseObject;}
    }

    /**
      <summary>Gets/Sets whether more than one of the field's items may be selected simultaneously.
      </summary>
    */
    public bool MultiSelect
    {
      get
      {return (Flags & FlagsEnum.MultiSelect) == FlagsEnum.MultiSelect;}
      set
      {Flags = EnumUtils.Mask(Flags, FlagsEnum.MultiSelect, value);}
    }

    /**
      <summary>Gets/Sets whether validation action is triggered as soon as a selection is made,
      without requiring the user to exit the field.</summary>
    */
    public bool ValidatedOnChange
    {
      get
      {return (Flags & FlagsEnum.CommitOnSelChange) == FlagsEnum.CommitOnSelChange;}
      set
      {Flags = EnumUtils.Mask(Flags, FlagsEnum.CommitOnSelChange, value);}
    }

    /**
      <returns>Either a string (single-selection) or a list of strings (multi-selection).</returns>
      <seealso cref="MultiSelect"/>
    */
    public override object Value
    {
      get
      {
        PdfDataObject valueObject = PdfObject.Resolve(GetInheritableAttribute(PdfName.V));
        if(MultiSelect)
        {
          IList<string> values = new List<string>();
          if(valueObject != null)
          {
            if(valueObject is PdfArray)
            {
              foreach(PdfDirectObject valueItemObject in (PdfArray)valueObject)
              {values.Add(((PdfString)valueItemObject).StringValue);}
            }
            else
            {values.Add(((PdfString)valueObject).StringValue);}
          }
          return values;
        }
        else
          return valueObject != null ? ((PdfString)valueObject).Value : null;
      }
      set
      {
        if(value is string)
        {BaseDataObject[PdfName.V] = new PdfTextString((string)value);}
        else if(value is IList<string>)
        {
          if(!MultiSelect)
            throw new ArgumentException("IList<string> value is only allowed when MultiSelect flag is active.");

          PdfDataObject oldValueObject = BaseDataObject.Resolve(PdfName.V);
          PdfArray valuesObject;
          if(oldValueObject is PdfArray)
          {
            valuesObject = (PdfArray)oldValueObject;
            valuesObject.Clear();
          }
          else
          {valuesObject = new PdfArray();}

          foreach(string valueItem in (IList<string>)value)
          {valuesObject.Add(new PdfTextString(valueItem));}

          if(valuesObject != oldValueObject)
          {BaseDataObject[PdfName.V] = valuesObject;}
        }
        else if(value == null)
        {BaseDataObject[PdfName.V] = null;}
        else
          throw new ArgumentException("Value MUST be either a string or an IList<string>");
      }
    }
    #endregion
    #endregion
    #endregion
  }
}
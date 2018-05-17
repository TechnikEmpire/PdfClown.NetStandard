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
using org.pdfclown.objects;

using System;

namespace org.pdfclown.documents.interaction.forms
{
  /**
    <summary>Signature field [PDF:1.6:8.6.3].</summary>
  */
  [PDF(VersionEnum.PDF13)]
  public sealed class SignatureField
    : Field
  {
    //TODO
    #region dynamic
    #region constructors
    /**
      <summary>Creates a new signature field within the given document context.</summary>
    */
//TODO:dictionary mandatory items (if any)!!!
    public SignatureField(
      string name,
      Widget widget
      ) : base(
        PdfName.Sig,
        name,
        widget
        )
    {}

    internal SignatureField(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    /**
      <returns>A <see cref="PdfDictionary"/>.</returns>
    */
    public override object Value
    {
      get
      {return BaseDataObject.Resolve(PdfName.V);}
      set
      {
        if(!(value == null
            || value is PdfDictionary))
          throw new ArgumentException("Value MUST be a PdfDictionary");

        BaseDataObject[PdfName.V] = (PdfDictionary)value;
      }
    }
    #endregion
    #endregion
    #endregion
  }
}
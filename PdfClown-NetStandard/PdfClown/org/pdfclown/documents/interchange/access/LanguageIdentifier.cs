/*
  Copyright 2012-2015 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown.documents;
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.interaction;
using actions = org.pdfclown.documents.interaction.actions;
using org.pdfclown.files;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;

namespace org.pdfclown.documents.interchange.access
{
  /**
    <summary>Language identifier [PDF:1.7:10.8.1][RFC 3066].</summary>
    <remarks>
      <para>Language identifiers can be based on codes defined by the International Organization for
      Standardization in ISO 639 (language code) and ISO 3166 (country code) or registered with the
      Internet Assigned Numbers Authority (<a href="http://iana.org">IANA</a>), or they can include
      codes created for private use.</para>
      <para>A language identifier consists of a primary code optionally followed by one or more
      subcodes (each preceded by a hyphen).</para>
    </remarks>
  */
  [PDF(VersionEnum.PDF14)]
  public sealed class LanguageIdentifier
    : PdfObjectWrapper<PdfTextString>
  {
    #region static
    #region interface
    #region public
    /**
      <summary>Wraps a language identifier base object into a language identifier object.</summary>
    */
    public static LanguageIdentifier Wrap(
      PdfDirectObject baseObject
      )
    {
      if(baseObject == null)
        return null;

      if(baseObject.Resolve() is PdfTextString)
        return new LanguageIdentifier(baseObject);
      else
        throw new ArgumentException("It doesn't represent a valid language identifier object.", "baseObject");
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public LanguageIdentifier(
      string code
      ) : base(new PdfTextString(code))
    {}

    internal LanguageIdentifier(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    public override string ToString(
      )
    {return BaseDataObject.StringValue;}
    #endregion
    #endregion
    #endregion
  }
}
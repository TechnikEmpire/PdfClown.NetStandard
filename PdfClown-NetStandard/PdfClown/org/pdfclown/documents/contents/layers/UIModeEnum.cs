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

using org.pdfclown.objects;
using org.pdfclown.util;

using System;

namespace org.pdfclown.documents.contents.layers
{
  /**
    <summary>List mode specifying which layers should be displayed to the user [PDF:1.7:4.10.3].
    </summary>
  */
  public enum UIModeEnum
  {
    /**
      <summary>All the layers are displayed.</summary>
    */
    AllPages,
    /**
      <summary>Only the layers referenced by one or more visible pages are displayed.</summary>
    */
    VisiblePages
  }

  internal static class UIModeEnumExtension
  {
    private static readonly BiDictionary<UIModeEnum,PdfName> codes;

    static UIModeEnumExtension()
    {
      codes = new BiDictionary<UIModeEnum,PdfName>();
      codes[UIModeEnum.AllPages] = PdfName.AllPages;
      codes[UIModeEnum.VisiblePages] = PdfName.VisiblePages;
    }

    public static UIModeEnum Get(
      PdfName name
      )
    {
      if(name == null)
        return UIModeEnum.AllPages;

      UIModeEnum? uiMode = codes.GetKey(name);
      if(!uiMode.HasValue)
        throw new NotSupportedException("UI mode unknown: " + name);

      return uiMode.Value;
    }

    public static PdfName GetName(
      this UIModeEnum uiMode
      )
    {return codes[uiMode];}
  }
}
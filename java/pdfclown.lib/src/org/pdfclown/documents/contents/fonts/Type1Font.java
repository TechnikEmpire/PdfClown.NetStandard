/*
  Copyright 2007-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.fonts;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDirectObject;

/**
  Type 1 font [PDF:1.6:5.5.1;AFM:4.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2.1, 04/16/15
*/
/*
  NOTE: Type 1 fonts encompass several formats:
  * AFM+PFB;
  * CFF;
  * OpenFont/CFF (in case "CFF" table's Top DICT has no CIDFont operators).
*/
@PDF(VersionEnum.PDF10)
public class Type1Font
  extends SimpleFont
{
  // <dynamic>
  // <fields>
  protected AfmParser.FontMetrics metrics;
  // </fields>

  // <constructors>
  Type1Font(
    Document context
    )
  {super(context);}

  Type1Font(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>
  // </dynamic>
}
/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.layers;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfDirectObject;

/**
  A collection of mutually-exclusive layers [PDF:1.7:4.10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 04/20/15
*/
@PDF(VersionEnum.PDF15)
public final class OptionGroup
  extends Array<Layer>
{
  // <static>
  // <interface>
  // <public>
  public static OptionGroup wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new OptionGroup(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public OptionGroup(
    Document context
    )
  {super(context, Layer.class);}

  private OptionGroup(
    PdfDirectObject baseObject
    )
  {super(Layer.class, baseObject);}
  // </constructors>
  // </dynamic>
}

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

package org.pdfclown.documents.interaction.annotations;

import org.pdfclown.objects.PdfName;

/**
  Line ending style [PDF:1.6:8.4.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 03/21/15
*/
public enum LineEndStyleEnum
{
  /**
    Square.
  */
  Square(PdfName.Square),
  /**
    Circle.
  */
  Circle(PdfName.Circle),
  /**
    Diamond.
  */
  Diamond(PdfName.Diamond),
  /**
    Open arrow.
  */
  OpenArrow(PdfName.OpenArrow),
  /**
    Closed arrow.
  */
  ClosedArrow(PdfName.ClosedArrow),
  /**
    None.
  */
  None(PdfName.None),
  /**
    Butt.
  */
  Butt(PdfName.Butt),
  /**
    Reverse open arrow.
  */
  ReverseOpenArrow(PdfName.ROpenArrow),
  /**
    Reverse closed arrow.
  */
  ReverseClosedArrow(PdfName.RClosedArrow),
  /**
    Slash.
  */
  Slash(PdfName.Slash);

  /**
    Gets the line ending style corresponding to the given value.
  */
  public static LineEndStyleEnum get(
    PdfName value
    )
  {
    for(LineEndStyleEnum style : LineEndStyleEnum.values())
    {
      if(style.getCode().equals(value))
        return style;
    }
    return null;
  }

  private final PdfName code;

  private LineEndStyleEnum(
    PdfName code
    )
  {this.code = code;}

  public PdfName getCode(
    )
  {return code;}
}
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

package org.pdfclown.documents.contents.layers;

import org.pdfclown.objects.PdfName;

/**
  Intended use of layers [PDF:1.7:4.10.1].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/26/15
*/
public enum IntentEnum
{
  /**
    Intended for interactive use by document consumers.
  */
  View(PdfName.View),
  /**
    Intended to represent a document designer's structural organization of artwork.
  */
  Design(PdfName.Design),
  /**
    Set of all intents (valid for {@link ILayerConfiguration#getIntents()} only).
  */
  All(PdfName.All);

  public static IntentEnum valueOf(
    PdfName name
    )
  {
    if(name == null)
      return View;

    for(IntentEnum value : values())
    {
      if(value.getName().equals(name))
        return value;
    }
    throw new UnsupportedOperationException("Intent unknown: " + name);
  }

  PdfName name;

  private IntentEnum(
    PdfName name
    )
  {this.name = name;}

  public PdfName getName(
    )
  {return name;}
}
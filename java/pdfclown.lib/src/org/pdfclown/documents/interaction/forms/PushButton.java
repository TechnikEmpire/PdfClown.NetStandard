/*
  Copyright 2008-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.forms;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.annotations.Widget;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.util.EnumUtils;

/**
  Pushbutton field [PDF:1.6:8.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.2.0, 03/10/15
*/
@PDF(VersionEnum.PDF12)
public final class PushButton
  extends ButtonField
{
  // <class>
  // <dynamic>
  // <constructors>
  /**
    Creates a new pushbutton within the given document context.
  */
  public PushButton(
    String name,
    Widget widget,
    String caption
    )
  {
    super(name, widget);
    setFlags(EnumUtils.mask(getFlags(), FlagsEnum.Pushbutton, true));
    setValue(caption);
  }

  PushButton(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PushButton clone(
    Document context
    )
  {return (PushButton)super.clone(context);}
  
  @Override
  public void setValue(
    Object value
    )
  {/* NOOP: This type of button retains no permanent value. */}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
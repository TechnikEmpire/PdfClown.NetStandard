/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Page actions [PDF:1.6:8.5.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class PageActions
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <dynamic>
  // <constructors>
  public PageActions(
    Document context
    )
  {super(context, new PdfDictionary());}

  PageActions(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PageActions clone(
    Document context
    )
  {return (PageActions)super.clone(context);}

  /**
    Gets the action to be performed when the page is closed.
  */
  public Action getOnClose(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.C));}

  /**
    Gets the action to be performed when the page is opened.
  */
  public Action getOnOpen(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.O));}

  /**
    @see #getOnClose()
  */
  public void setOnClose(
    Action value
    )
  {getBaseDataObject().put(PdfName.C, value.getBaseObject());}

  /**
    @see #getOnOpen()
  */
  public void setOnOpen(
    Action value
    )
  {getBaseDataObject().put(PdfName.O, value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
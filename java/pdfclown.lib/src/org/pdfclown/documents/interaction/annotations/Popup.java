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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Rectangle2D;
import java.util.Date;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.colorSpaces.DeviceColor;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Pop-up annotation [PDF:1.6:8.4.5].
  <p>It displays text in a pop-up window for entry and editing.
  It typically does not appear alone but is associated with a markup annotation,
  its parent annotation, and is used for editing the parent's text.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF13)
public final class Popup
  extends Annotation<Popup>
{
  // <class>
  // <dynamic>
  // <fields>
  private Markup<?> markup;
  // </fields>
  
  // <constructors>
  public Popup(
    Page page,
    Rectangle2D box,
    String text
    )
  {super(page, PdfName.Popup, box, text);}

  Popup(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Popup clone(
    Document context
    )
  {return (Popup)super.clone(context);}

  @Override
  public DeviceColor getColor(
    )
  {return getMarkup() != null ? markup.getColor() : super.getColor();}

  /**
    Gets the markup associated with this annotation.
  */
  public Markup<?> getMarkup(
    )
  {return markup != null ? markup : (markup = (Markup<?>)Annotation.wrap(getBaseDataObject().get(PdfName.Parent)));}
  
  @Override
  public Date getModificationDate(
    )
  {return getMarkup() != null ? markup.getModificationDate() : super.getModificationDate();}

  @Override
  public String getText(
    )
  {return getMarkup() != null ? markup.getText() : super.getText();}
  
  /**
    Gets whether the annotation should initially be displayed open.
  */
  public boolean isOpen(
    )
  {
    PdfBoolean openObject = (PdfBoolean)getBaseDataObject().get(PdfName.Open);
    return openObject != null
      ? openObject.getValue()
      : false;
  }
  
  @Override
  public void setColor(
    DeviceColor value
    )
  {
    if(getMarkup() != null)
    {markup.setColor(value);}
    else
    {super.setColor(value);}
  }
  
  @Override
  public void setModificationDate(
    Date value
    )
  {
    if(getMarkup() != null)
    {markup.setModificationDate(value);}
    else
    {super.setModificationDate(value);}
  }
  
  /**
    @see #isOpen()
  */
  public void setOpen(
    boolean value
    )
  {getBaseDataObject().put(PdfName.Open, PdfBoolean.get(value));}
  
  @Override
  public void setText(
    String value
    )
  {
    if(getMarkup() != null)
    {markup.setText(value);}
    else
    {super.setText(value);}
  }

  /**
    @see #setOpen(boolean)
  */
  public Popup withOpen(
    boolean value
    )
  {
    setOpen(value);
    return self();
  }
  // </public>

  // <internal>
  /**
    @see #getMarkup()
  */
  void setMarkup(
    Markup<?> value
    )
  {
    PdfDictionary baseDataObject = getBaseDataObject();
    baseDataObject.put(PdfName.Parent, value.getBaseObject());
    /*
      NOTE: The markup annotation's properties override those of this pop-up annotation.
    */
    baseDataObject.remove(PdfName.Contents);
    baseDataObject.remove(PdfName.M);
    baseDataObject.remove(PdfName.C);
  }
  // </internal>
 // </interface>
  // </dynamic>
  // </class>
}
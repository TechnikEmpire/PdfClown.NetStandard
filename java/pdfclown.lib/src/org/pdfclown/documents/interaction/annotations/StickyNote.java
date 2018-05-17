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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Text annotation [PDF:1.6:8.4.5].
  <p>It represents a sticky note attached to a point in the PDF document.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF10)
public final class StickyNote
  extends Markup<StickyNote>
{
  // <class>
  // <classes>
  /**
    Icon to be used in displaying the annotation [PDF:1.6:8.4.5].
  */
  public enum IconTypeEnum
  {
    /**
      Comment.
    */
    Comment(PdfName.Comment),
    /**
      Help.
    */
    Help(PdfName.Help),
    /**
      Insert.
    */
    Insert(PdfName.Insert),
    /**
      Key.
    */
    Key(PdfName.Key),
    /**
      New paragraph.
    */
    NewParagraph(PdfName.NewParagraph),
    /**
      Note.
    */
    Note(PdfName.Note),
    /**
      Paragraph.
    */
    Paragraph(PdfName.Paragraph);

    /**
      Gets the highlighting mode corresponding to the given value.
    */
    public static IconTypeEnum get(
      PdfName value
      )
    {
      for(IconTypeEnum iconType : IconTypeEnum.values())
      {
        if(iconType.getCode().equals(value))
          return iconType;
      }
      return null;
    }

    private final PdfName code;

    private IconTypeEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final IconTypeEnum DefaultIconType = IconTypeEnum.Note;
  private static final boolean DefaultOpen = false;
  // </fields>
  // </static>
  
  // <dynamic>
  // <constructors>
  public StickyNote(
    Page page,
    Point2D location,
    String text
    )
  {
    super(
      page,
      PdfName.Text,
      new Rectangle2D.Double(location.getX(), location.getY(), 0, 0),
      text
      );
  }

  StickyNote(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public StickyNote clone(
    Document context
    )
  {return (StickyNote)super.clone(context);}

  /**
    Gets the icon to be used in displaying the annotation.
  */
  public IconTypeEnum getIconType(
    )
  {
    PdfName nameObject = (PdfName)getBaseDataObject().get(PdfName.Name);
    return nameObject != null ? IconTypeEnum.get(nameObject) : DefaultIconType;
  }

  /**
    Gets whether the annotation should initially be displayed open.
  */
  public boolean isOpen(
    )
  {
    PdfBoolean openObject = (PdfBoolean)getBaseDataObject().get(PdfName.Open);
    return openObject != null ? openObject.getValue() : DefaultOpen;
  }

//TODO:State and StateModel!!!

  /**
    @see #getIconType()
  */
  public void setIconType(
    IconTypeEnum value
    )
  {getBaseDataObject().put(PdfName.Name, value != null && value != DefaultIconType ? value.getCode() : null);}

  /**
    @see #isOpen()
  */
  public void setOpen(
    boolean value
    )
  {getBaseDataObject().put(PdfName.Open, value != DefaultOpen ? PdfBoolean.get(value) : null);}

  /**
    @see #setIconType(IconTypeEnum)
  */
  public StickyNote withIconType(
    IconTypeEnum value
    )
  {
    setIconType(value);
    return self();
  }

  /**
    @see #setOpen(boolean)
  */
  public StickyNote withOpen(
    boolean value
    )
  {
    setOpen(value);
    return self();
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Sound annotation [PDF:1.6:8.4.5].
  <p>When the annotation is activated, the sound is played.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF12)
public final class Sound
  extends Markup<Sound>
{
  // <class>
  // <classes>
  /**
    Icon to be used in displaying the annotation [PDF:1.6:8.4.5].
  */
  public enum IconTypeEnum
  {
    /**
      Speaker.
    */
    Speaker(PdfName.Speaker),
    /**
      Microphone.
    */
    Microphone(PdfName.Mic);

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
  private static final IconTypeEnum DefaultIconType = IconTypeEnum.Speaker;
  // </fields>
  // </static>
  
  // <dynamic>
  // <constructors>
  public Sound(
    Page page,
    Rectangle2D box,
    String text,
    org.pdfclown.documents.multimedia.Sound content
    )
  {
    super(page, PdfName.Sound, box, text);
    setContent(content);
  }

  Sound(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Sound clone(
    Document context
    )
  {return (Sound)super.clone(context);}

  /**
    Gets the sound to be played.
  */
  public org.pdfclown.documents.multimedia.Sound getContent(
    )
  {
    return new org.pdfclown.documents.multimedia.Sound(
      getBaseDataObject().get(PdfName.Sound)
      );
  }

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
    @see #getContent()
  */
  public void setContent(
    org.pdfclown.documents.multimedia.Sound value
    )
  {
    if(value == null)
      throw new IllegalArgumentException("Content MUST be defined.");

    getBaseDataObject().put(PdfName.Sound, value.getBaseObject());
  }

  /**
    @see #getIconType()
  */
  public void setIconType(
    IconTypeEnum value
    )
  {getBaseDataObject().put(PdfName.Name, value != null && value != DefaultIconType ? value.getCode() : null);}
  
  /**
    Popups not supported.
  */
  @Override
  public void setPopup(
    Popup value
    ) throws UnsupportedOperationException
  {throw new UnsupportedOperationException();}

  /**
    @see #setContent(org.pdfclown.documents.multimedia.Sound)
  */
  public Sound withContent(
    org.pdfclown.documents.multimedia.Sound value
    )
  {
    setContent(value);
    return self();
  }

  /**
    @see #setIconType(IconTypeEnum)
  */
  public Sound withIconType(
    IconTypeEnum value
    )
  {
    setIconType(value);
    return self();
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
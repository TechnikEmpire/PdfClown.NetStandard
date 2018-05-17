/*
  Copyright 2013-2015 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfDate;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfTextString;

/**
  Markup annotation [PDF:1.6:8.4.5].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.2.0, 03/21/15
*/
@PDF(VersionEnum.PDF11)
public abstract class Markup<T extends Markup<T>>
  extends Annotation<T>
{
  // <class>
  // <classes>
  /**
    Annotation relationship [PDF:1.6:8.4.5].
    
    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.3
    @version 0.1.3, 01/22/13
  */
  @PDF(VersionEnum.PDF16)
  public enum ReplyTypeEnum
  {
    Thread(PdfName.R),
    Group(PdfName.Group);
    
    public static ReplyTypeEnum get(
      PdfName value
      )
    {
      if(value == null)
        return Thread;
      
      for(ReplyTypeEnum replyType : ReplyTypeEnum.values())
      {
        if(replyType.getCode().equals(value))
          return replyType;
      }
      return null;
    }
  
    private final PdfName code;
  
    private ReplyTypeEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>
  
  // <dynamic>
  // <constructors>
  protected Markup(
    Page page,
    PdfName subtype,
    Rectangle2D box,
    String text
    )
  {
    super(page, subtype, box, text);
    setCreationDate(new Date());
  }
  
  protected Markup(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the constant opacity value to be used in painting the annotation. This value applies to all
    visible elements of the annotation (including its background and border) but not to the popup 
    window that appears when the annotation is opened.
  */
  @PDF(VersionEnum.PDF14)
  public double getAlpha(
    )
  {return (Double)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.CA), 1d);}

  /**
    Gets the annotation editor. It is displayed as a text label in the title bar of the annotation's
    pop-up window when open and active. By convention, it identifies the user who added the 
    annotation.
  */
  @PDF(VersionEnum.PDF11)
  public String getAuthor(
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.T));}

  /**
    Gets the date and time when the annotation was created.
  */
  @PDF(VersionEnum.PDF15)
  public Date getCreationDate(
    )
  {
    PdfDirectObject creationDateObject = getBaseDataObject().get(PdfName.CreationDate);
    return creationDateObject instanceof PdfDate ? ((PdfDate)creationDateObject).getValue() : null;
  }
  
  /**
    Gets the annotation that this one is in reply to. Both annotations must be on the same page of 
    the document. The relationship between the two annotations is specified by the 
    {@link #getReplyType() replyType} property.
  */
  @PDF(VersionEnum.PDF15)
  public Annotation<?> getInReplyTo(
    )
  {return Annotation.wrap(getBaseDataObject().get(PdfName.IRT));}
  
  /**
    Gets the pop-up annotation associated with this one.
  */
  @PDF(VersionEnum.PDF13)
  public Popup getPopup(
    )
  {return (Popup)Annotation.wrap(getBaseDataObject().get(PdfName.Popup));}
  
  /**
    Gets the relationship between this annotation and one specified by 
    {@link #getInReplyTo() inReplyTo} property. 
  */
  @PDF(VersionEnum.PDF16)
  public ReplyTypeEnum getReplyType(
    )
  {return ReplyTypeEnum.get((PdfName)getBaseDataObject().get(PdfName.RT));}
  
  /**
    Gets the text representing a short description of the subject being addressed by the annotation.
  */
  @PDF(VersionEnum.PDF15)
  public String getSubject(
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.Subj));}
  
  /**
    @see #getAlpha()
  */
  public void setAlpha(
    double value
    )
  {getBaseDataObject().put(PdfName.CA, PdfReal.get(value));}

  /**
    @see #getAuthor(String)
  */
  public void setAuthor(
    String value
    )
  {
    getBaseDataObject().put(PdfName.T, PdfTextString.get(value));
    setModificationDate(new Date());
  }

  /**
    @see #getCreationDate()
  */
  public void setCreationDate(
    Date value
    )
  {getBaseDataObject().put(PdfName.CreationDate, PdfDate.get(value));}

  /**
    @see #getInReplyTo()
  */
  public void setInReplyTo(
    Annotation<?> value
    )
  {getBaseDataObject().put(PdfName.IRT, PdfObjectWrapper.getBaseObject(value));}

  /**
    @throws UnsupportedOperationException
      If pop-up annotations can't be associated with this markup.
    @see #getPopup()
  */
  public void setPopup(
    Popup value
    ) throws UnsupportedOperationException
  {
    value.setMarkup(this);
    getBaseDataObject().put(PdfName.Popup, value.getBaseObject());
  }

  /**
    @see #getReplyType()
  */
  public void setReplyType(
    ReplyTypeEnum value
    )
  {getBaseDataObject().put(PdfName.RT, value != null ? value.getCode() : null);}
  
  /**
    @see #getSubject()
  */
  public void setSubject(
    String value
    )
  {
    getBaseDataObject().put(PdfName.Subj, PdfTextString.get(value));
    setModificationDate(new Date());
  }
  
  /**
    @see #setAlpha(double)
  */
  public T withAlpha(
    double value
    )
  {
    setAlpha(value);
    return self();
  }
  
  /**
    @see #setAuthor(String)
  */
  public T withAuthor(
    String value
    )
  {
    setAuthor(value);
    return self();
  }

  /**
    @see #setCreationDate(Date)
  */
  public T withCreationDate(
    Date value
    )
  {
    setCreationDate(value);
    return self();
  }
  
  /**
    @see #setInReplyTo(Annotation)
  */
  public T withInReplyTo(
    Annotation<?> value
    )
  {
    setInReplyTo(value);
    return self();
  }

  /**
    @see #setPopup(Popup)
  */
  public T withPopup(
    Popup value
    ) throws UnsupportedOperationException
  {
    setPopup(value);
    return self();
  }

  /**
    @see #setReplyType(ReplyTypeEnum)
  */
  public T withReplyType(
    ReplyTypeEnum value
    )
  {
    setReplyType(value);
    return self();
  }

  /**
    @see #setSubject(String)
  */
  public T withSubject(
    String value
    )
  {
    setSubject(value);
    return self();
  }
  // </public>

  // <protected>
  protected PdfName getTypeBase(
    )
  {return (PdfName)getBaseDataObject().get(PdfName.IT);}
  
  protected void setTypeBase(
    PdfName value
    )
  {getBaseDataObject().put(PdfName.IT, value);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}

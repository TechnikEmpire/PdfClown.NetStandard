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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Border effect [PDF:1.6:8.4.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF15)
public final class BorderEffect
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Border effect type [PDF:1.6:8.4.3].
  */
  public enum TypeEnum
  {
    /**
      No effect.
    */
    None(PdfName.S),
    /**
      Cloudy.
    */
    Cloudy(PdfName.C);

    public static TypeEnum get(
      PdfName value
      )
    {
      for(TypeEnum type : TypeEnum.values())
      {
        if(type.getCode().equals(value))
          return type;
      }
      return null;
    }

    private final PdfName code;

    private TypeEnum(
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
  private static final double DefaultIntensity = 0;
  private static final TypeEnum DefaultType = TypeEnum.None;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a non-reusable instance.
  */
  public BorderEffect(
    TypeEnum type
    )
  {this(null, type);}
  
  /**
    Creates a non-reusable instance.
  */
  public BorderEffect(
    TypeEnum type,
    double intensity
    )
  {this(null, type, intensity);}
  
  /**
    Creates a reusable instance.
  */
  public BorderEffect(
    Document context,
    TypeEnum type
    )
  {this(context, type, DefaultIntensity);}
  
  /**
    Creates a reusable instance.
  */
  public BorderEffect(
    Document context,
    TypeEnum type,
    double intensity
    )
  {
    super(context, new PdfDictionary());
    setType(type);
    setIntensity(intensity);
  }

  BorderEffect(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public BorderEffect clone(
    Document context
    )
  {return (BorderEffect)super.clone(context);}

  /**
    Gets the effect intensity.
    
    @return Value in the range 0-2.
  */
  public double getIntensity(
    )
  {
    PdfNumber<?> intensityObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.I);
    return intensityObject != null ? intensityObject.getDoubleValue() : DefaultIntensity;
  }

  /**
    Gets the effect type.
  */
  public TypeEnum getType(
    )
  {
    PdfName typeObject = (PdfName)getBaseDataObject().get(PdfName.S);
    return typeObject != null ? TypeEnum.get(typeObject) : DefaultType;
  }

  /**
    @see #getIntensity()
  */
  public void setIntensity(
    double value
    )
  {getBaseDataObject().put(PdfName.I, value != DefaultIntensity ? PdfReal.get(value) : null);}

  /**
    @see #getType()
  */
  public void setType(
    TypeEnum value
    )
  {getBaseDataObject().put(PdfName.S, value != null && value != DefaultType ? value.getCode() : null);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
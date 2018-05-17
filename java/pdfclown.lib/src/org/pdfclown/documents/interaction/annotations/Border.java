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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Border characteristics [PDF:1.6:8.4.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF11)
public final class Border
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Border style [PDF:1.6:8.4.3].
  */
  public enum StyleEnum
  {
    /**
      Solid.
    */
    Solid(PdfName.S),
    /**
      Dashed.

      @see #getPattern()
    */
    Dashed(PdfName.D),
    /**
      Beveled.
    */
    Beveled(PdfName.B),
    /**
      Inset.
    */
    Inset(PdfName.I),
    /**
      Underline.
    */
    Underline(PdfName.U);

    /**
      Gets the style corresponding to the given value.
    */
    public static StyleEnum get(
      PdfName value
      )
    {
      for(StyleEnum style : StyleEnum.values())
      {
        if(style.getCode().equals(value))
          return style;
      }
      return null;
    }

    private final PdfName code;

    private StyleEnum(
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
  private static final LineDash DefaultLineDash = new LineDash(new double[]{3});
  private static final StyleEnum DefaultStyle = StyleEnum.Solid;
  private static final double DefaultWidth = 1;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a non-reusable instance.
  */
  public Border(
    double width
    )
  {this(null, width);}
  
  /**
    Creates a non-reusable instance.
  */
  public Border(
    double width,
    StyleEnum style
    )
  {this(null, width, style);}
  
  /**
    Creates a non-reusable instance.
  */
  public Border(
    double width,
    LineDash pattern
    )
  {this(null, width, pattern);}
  
  /**
    Creates a reusable instance.
  */
  public Border(
    Document context,
    double width
    )
  {this(context, width, null, null);}
  
  /**
    Creates a reusable instance.
  */
  public Border(
    Document context,
    double width,
    StyleEnum style
    )
  {this(context, width, style, null);}
  
  /**
    Creates a reusable instance.
  */
  public Border(
    Document context,
    double width,
    LineDash pattern
    )
  {this(context, width, StyleEnum.Dashed, pattern);}
  
  private Border(
    Document context,
    double width,
    StyleEnum style,
    LineDash pattern
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.Border}
        )
      );
    setWidth(width);
    setStyle(style);
    setPattern(pattern);
  }

  Border(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Border clone(
    Document context
    )
  {return (Border)super.clone(context);}

  /**
    Gets the dash pattern used in case of dashed border.
  */
  public LineDash getPattern(
    )
  {
    PdfArray dashObject = (PdfArray)getBaseDataObject().get(PdfName.D);
    return dashObject != null ? LineDash.get(dashObject, null) : DefaultLineDash;
  }

  /**
    Gets the border style.
  */
  public StyleEnum getStyle(
    )
  {
    PdfName styleObject = (PdfName)getBaseDataObject().get(PdfName.S);
    return styleObject != null ? StyleEnum.get(styleObject) : DefaultStyle;
  }

  /**
    Gets the border width in points.
  */
  public double getWidth(
    )
  {
    PdfNumber<?> widthObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.W);
    return widthObject != null ? widthObject.getDoubleValue() : DefaultWidth;
  }

  /**
    @see #getPattern()
  */
  public void setPattern(
    LineDash value
    )
  {
    PdfArray dashObject = null;
    if(value != null)
    {
      dashObject = new PdfArray();
      for(double dashItem : value.getDashArray())
      {dashObject.add(PdfReal.get(dashItem));}
    }
    getBaseDataObject().put(PdfName.D, dashObject);
  }

  /**
    @see #getStyle()
  */
  public void setStyle(
    StyleEnum value
    )
  {getBaseDataObject().put(PdfName.S, value != null && value != DefaultStyle ? value.getCode() : null);}

  /**
    @see #getWidth()
  */
  public void setWidth(
    double value
    )
  {getBaseDataObject().put(PdfName.W, PdfReal.get(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
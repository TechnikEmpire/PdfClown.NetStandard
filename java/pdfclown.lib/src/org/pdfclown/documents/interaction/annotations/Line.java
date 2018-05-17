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
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  Line annotation [PDF:1.6:8.4.5].
  <p>It displays displays a single straight line on the page.
  When opened, it displays a pop-up window containing the text of the associated note.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.2.0, 03/21/15
*/
@PDF(VersionEnum.PDF13)
public final class Line
  extends Markup<Line>
{
  // <class>
  // <static>
  // <fields>
  private static final double DefaultLeaderLineExtensionLength = 0;
  private static final double DefaultLeaderLineLength = 0;
  private static final LineEndStyleEnum DefaultLineEndStyle = LineEndStyleEnum.None;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public Line(
    Page page,
    Point2D startPoint,
    Point2D endPoint,
    String text,
    DeviceRGBColor color
    )
  {
    super(
      page,
      PdfName.Line,
      new Rectangle2D.Double(
        startPoint.getX(),
        startPoint.getY(),
        endPoint.getX()-startPoint.getX(),
        endPoint.getY()-startPoint.getY()
        ),
      text
      );
    getBaseDataObject().put(
      PdfName.L,
      new PdfArray(new PdfDirectObject[]{PdfReal.get(0), PdfReal.get(0), PdfReal.get(0), PdfReal.get(0)})
      );
    setStartPoint(startPoint);
    setEndPoint(endPoint);
    setColor(color);
  }

  Line(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Line clone(
    Document context
    )
  {return (Line)super.clone(context);}

  /**
    Gets the ending coordinates.
  */
  public Point2D getEndPoint(
    )
  {
    PdfArray coordinatesObject = (PdfArray)getBaseDataObject().get(PdfName.L);
    return new Point2D.Double(
      ((PdfNumber<?>)coordinatesObject.get(2)).getDoubleValue(),
      ((PdfNumber<?>)coordinatesObject.get(3)).getDoubleValue()
      );
  }

  /**
    Gets the style of the ending line ending.
  */
  public LineEndStyleEnum getEndStyle(
    )
  {
    PdfArray endstylesObject = (PdfArray)getBaseDataObject().get(PdfName.LE);
    return endstylesObject != null
      ? LineEndStyleEnum.get((PdfName)endstylesObject.get(1))
      : DefaultLineEndStyle;
  }

  /**
    Gets the color with which to fill the interior of the annotation's line endings.
  */
  public DeviceRGBColor getFillColor(
    )
  {
    PdfArray fillColorObject = (PdfArray)getBaseDataObject().get(PdfName.IC);
    if(fillColorObject == null)
      return null;
//TODO:use baseObject constructor!!!
    return new DeviceRGBColor(
      ((PdfNumber<?>)fillColorObject.get(0)).getDoubleValue(),
      ((PdfNumber<?>)fillColorObject.get(1)).getDoubleValue(),
      ((PdfNumber<?>)fillColorObject.get(2)).getDoubleValue()
      );
  }

  /**
    Gets the length of leader line extensions that extend
    in the opposite direction from the leader lines.
  */
  public double getLeaderLineExtensionLength(
    )
  {
    PdfNumber<?> leaderLineExtensionLengthObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.LLE);
    return leaderLineExtensionLengthObject != null
      ? leaderLineExtensionLengthObject.getDoubleValue()
      : DefaultLeaderLineExtensionLength;
  }

  /**
    Gets the length of leader lines that extend from each endpoint
    of the line perpendicular to the line itself.
    <p>A positive value means that the leader lines appear in the direction
    that is clockwise when traversing the line from its starting point
    to its ending point; a negative value indicates the opposite direction.</p>
  */
  public double getLeaderLineLength(
    )
  {
    PdfNumber<?> leaderLineLengthObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.LL);
    return leaderLineLengthObject != null
      ? -leaderLineLengthObject.getDoubleValue()
      : DefaultLeaderLineLength;
  }

  /**
    Gets the starting coordinates.
  */
  public Point2D getStartPoint(
    )
  {
    PdfArray coordinatesObject = (PdfArray)getBaseDataObject().get(PdfName.L);
    return new Point2D.Double(
      ((PdfNumber<?>)coordinatesObject.get(0)).getDoubleValue(),
      ((PdfNumber<?>)coordinatesObject.get(1)).getDoubleValue()
      );
  }

  /**
    Gets the style of the starting line ending.
  */
  public LineEndStyleEnum getStartStyle(
    )
  {
    PdfArray endstylesObject = (PdfArray)getBaseDataObject().get(PdfName.LE);
    return endstylesObject != null
      ? LineEndStyleEnum.get((PdfName)endstylesObject.get(0))
      : DefaultLineEndStyle;
  }

  /**
    Gets whether the contents should be shown as a caption.
  */
  public boolean isCaptionVisible(
    )
  {
    PdfBoolean captionVisibleObject = (PdfBoolean)getBaseDataObject().get(PdfName.Cap);
    return captionVisibleObject != null
      ? captionVisibleObject.getValue()
      : false;
  }

  /**
    @see #isCaptionVisible()
  */
  public void setCaptionVisible(
    boolean value
    )
  {getBaseDataObject().put(PdfName.Cap, PdfBoolean.get(value));}

  /**
    @see #getEndPoint()
  */
  public void setEndPoint(
    Point2D value
    )
  {
    PdfArray coordinatesObject = (PdfArray)getBaseDataObject().get(PdfName.L);
    coordinatesObject.set(2, PdfReal.get(value.getX()));
    coordinatesObject.set(3, PdfReal.get(getPage().getBox().getHeight() - value.getY()));
  }

  /**
    @see #getEndStyle()
  */
  public void setEndStyle(
    LineEndStyleEnum value
    )
  {ensureLineEndStylesObject().set(1, (value != null ? value : DefaultLineEndStyle).getCode());}

  /**
    @see #getFillColor()
  */
  public void setFillColor(
     DeviceRGBColor value
    )
  {getBaseDataObject().put(PdfName.IC, value.getBaseDataObject());}

  /**
    @see #getLeaderLineExtensionLength()
  */
  public void setLeaderLineExtensionLength(
    double value
    )
  {
    getBaseDataObject().put(PdfName.LLE, PdfReal.get(value));
    /*
      NOTE: If leader line extension entry is present, leader line MUST be too.
    */
    if(!getBaseDataObject().containsKey(PdfName.LL))
    {setLeaderLineLength(DefaultLeaderLineLength);}
  }

  /**
    @see #getLeaderLineLength()
  */
  public void setLeaderLineLength(
    double value
    )
  {getBaseDataObject().put(PdfName.LL,PdfReal.get(-value));}

  /**
    @see #getStartPoint()
  */
  public void setStartPoint(
    Point2D value
    )
  {
    PdfArray coordinatesObject = (PdfArray)getBaseDataObject().get(PdfName.L);
    coordinatesObject.set(0, PdfReal.get(value.getX()));
    coordinatesObject.set(1, PdfReal.get(getPage().getBox().getHeight() - value.getY()));
  }

  /**
    @see #getStartStyle()
  */
  public void setStartStyle(
    LineEndStyleEnum value
    )
  {ensureLineEndStylesObject().set(0, (value != null ? value : DefaultLineEndStyle).getCode());}
  
  /**
    @see #setCaptionVisible(boolean)
  */
  public Line withCaptionVisible(
    boolean value
    )
  {
    setCaptionVisible(value);
    return self();
  }

  /**
    @see #setEndPoint(Point2D)
  */
  public Line withEndPoint(
    Point2D value
    )
  {
    setEndPoint(value);
    return self();
  }

  /**
    @see #setEndStyle(LineEndStyleEnum)
  */
  public Line withEndStyle(
    LineEndStyleEnum value
    )
  {
    setEndStyle(value);
    return self();
  }

  /**
    @see #setFillColor(DeviceRGBColor)
  */
  public Line withFillColor(
     DeviceRGBColor value
    )
  {
    setFillColor(value);
    return self();
  }

  /**
    @see #setLeaderLineExtensionLength(double)
  */
  public Line withLeaderLineExtensionLength(
    double value
    )
  {
    setLeaderLineExtensionLength(value);
    return self();
  }

  /**
    @see #setLeaderLineLength(double)
  */
  public Line withLeaderLineLength(
    double value
    )
  {
    setLeaderLineLength(value);
    return self();
  }

  /**
    @see #setStartPoint(Point2D)
  */
  public Line withStartPoint(
    Point2D value
    )
  {
    setStartPoint(value);
    return self();
  }

  /**
    @see #setStartStyle(LineEndStyleEnum)
  */
  public Line withStartStyle(
    LineEndStyleEnum value
    )
  {
    setStartStyle(value);
    return self();
  }
  // </public>

  // <private>
  private PdfArray ensureLineEndStylesObject(
    )
  {
    PdfArray endStylesObject = (PdfArray)getBaseDataObject().get(PdfName.LE);
    if(endStylesObject == null)
    {
      getBaseDataObject().put(
        PdfName.LE,
        endStylesObject = new PdfArray(
          new PdfDirectObject[]
          {
            DefaultLineEndStyle.getCode(),
            DefaultLineEndStyle.getCode()
          }
          )
        );
    }
    return endStylesObject;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
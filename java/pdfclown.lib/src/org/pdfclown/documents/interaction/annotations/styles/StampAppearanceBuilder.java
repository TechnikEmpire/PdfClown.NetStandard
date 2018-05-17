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

package org.pdfclown.documents.interaction.annotations.styles;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.Length;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.composition.Length.UnitModeEnum;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.interaction.annotations.Stamp;

/**
  Appearance builder for rubber stamp annotations.
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @see Stamp
  @since 0.1.2.1
  @version 0.1.2.1, 03/21/15
*/
public class StampAppearanceBuilder
{
  // <classes>
  public enum TypeEnum
  {
    Round,
    Squared,
    Striped
  }
  // </classes>
  
  // <static>
  // <fields>
  private static final Length DefaultBorderRadius = new Length(.05, UnitModeEnum.Relative);
  private static final Length DefaultBorderWidth = new Length(.025, UnitModeEnum.Relative);
  private static final Color<?> DefaultColor = DeviceRGBColor.get(java.awt.Color.RED);
  // </fields>
  // </static>
  
  // <dynamic>
  // <fields>
  private boolean borderDoubled = true;
  private Length borderRadius = DefaultBorderRadius;
  private Length borderWidth = DefaultBorderWidth;
  private Color<?> color = DefaultColor;
  private Font font;
  private String text;
  private TypeEnum type;
  private double width;
  
  private Document document;
  // </fields>
  
  // <constructors>
  public StampAppearanceBuilder(
    Document document,
    TypeEnum type,
    String text,
    double width,
    Font font
    )
  {
    this.document = document;
    this.type = type;
    this.width = width;
    setText(text);
    setFont(font);
  }
  // </constructors>
  
  // <interface>
  // <public>
  public FormXObject build(
    )
  {
    boolean isRound = type == TypeEnum.Round;
    boolean isStriped = type == TypeEnum.Striped;
    double textScale = .5;
    double borderWidth = this.borderWidth.getValue(width);
    double doubleBorderGap = borderDoubled ? borderWidth : 0;
    double fontSize = 10;
    fontSize *= ((width - (isStriped ? 2 : doubleBorderGap * 2 + (borderWidth * (borderDoubled ? 1.5 : 1) * 2) + width * (isRound ?  .15 : .05))) / textScale) / font.getWidth(text, fontSize);
    double height = isRound ? width : (font.getAscent(fontSize) * 1.2 + doubleBorderGap * 2 + (borderWidth * (borderDoubled ? 1.5 : 1) * 2));
    Dimension2D size = new org.pdfclown.util.math.geom.Dimension(width, height);
    
    FormXObject appearance = new FormXObject(document, size);
    {
      PrimitiveComposer composer = new PrimitiveComposer(appearance);
      if(color != null)
      {
        composer.setStrokeColor(color);
        composer.setFillColor(color);
      }
      composer.setTextScale(textScale);
      composer.setFont(font, fontSize);
      composer.showText(text, new Point2D.Double(size.getWidth() / 2, size.getHeight() / 2 - font.getDescent(fontSize) * .4), XAlignmentEnum.Center, YAlignmentEnum.Middle, 0);
      
      double borderRadius = isRound ? 0 : this.borderRadius.getValue((size.getWidth() + size.getHeight()) / 2);
      Rectangle2D prevBorderBox = appearance.getBox();
      for(int borderStep = 0, borderStepLimit = (borderDoubled ? 2 : 1); borderStep < borderStepLimit; borderStep++)
      {
        if(borderStep == 0)
        {composer.setLineWidth(borderWidth);}
        else
        {composer.setLineWidth(composer.getState().getLineWidth() / 2);}
        
        double lineWidth =  (borderStep > 0 ? composer.getState().getLineWidth() / 2 : borderWidth);
        double marginY = lineWidth / 2 + (borderStep > 0 ? composer.getState().getLineWidth() + doubleBorderGap : 0);
        double marginX = isStriped ? 0 : marginY;
        Rectangle2D borderBox = new Rectangle2D.Double(prevBorderBox.getX() + marginX, prevBorderBox.getY() + marginY, prevBorderBox.getWidth() - marginX * 2, prevBorderBox.getHeight() - marginY * 2);

        if(isRound)
        {composer.drawEllipse(borderBox);}
        else
        {
          if(isStriped)
          {
            composer.drawLine(new Point2D.Double(borderBox.getMinX(), borderBox.getMinY()), new Point2D.Double(borderBox.getMaxX(), borderBox.getMinY()));
            composer.drawLine(new Point2D.Double(borderBox.getMinX(), borderBox.getMaxY()), new Point2D.Double(borderBox.getMaxX(), borderBox.getMaxY()));
          }
          else
          {composer.drawRectangle(borderBox,  borderRadius * (1 - .5 * borderStep));}
        }
        composer.stroke();
        prevBorderBox = borderBox;
      }
      composer.flush();
    }
    return appearance;
  }
  
  public void setBorderDoubled(
    boolean value
    )
  {borderDoubled = value;}
  
  public void setBorderRadius(
    Length value
    )
  {borderRadius = value;}
  
  public void setBorderWidth(
    Length value
    )
  {borderWidth = value;}
  
  public void setColor(
    Color<?> value
    )
  {color = value;}
  
  public void setFont(
    Font value
    )
  {font = value;}
  
  public void setText(
    String value
    )
  {text = value.toUpperCase();}
  
  public StampAppearanceBuilder withBorderDoubled(
    boolean value
    )
  {
    setBorderDoubled(value);
    return this;
  }
  
  public StampAppearanceBuilder withBorderRadius(
    Length value
    )
  {
    setBorderRadius(value);
    return this;
  }
  
  public StampAppearanceBuilder withBorderWidth(
    Length value
    )
  {
    setBorderWidth(value);
    return this;
  }
  
  public StampAppearanceBuilder withColor(
    Color<?> value
    )
  {
    setColor(value);
    return this;
  }
  
  public StampAppearanceBuilder withFont(
    Font value
    )
  {
    setFont(value);
    return this;
  }
  
  public StampAppearanceBuilder withText(
    String value
    )
  {
    setText(value);
    return this;
  }
  // </public>
  // </interface>
  // </dynamic>
}
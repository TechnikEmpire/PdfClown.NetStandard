/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library" (the
  Program): see the accompanying README files for more info.

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

using colors = org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using fonts = org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.objects;

using System.Drawing;

namespace org.pdfclown.documents.interaction.annotations.styles
{
  /**
    <summary>Appearance builder for rubber stamp annotations.</summary>
    <seealso cref="org.pdfclown.documents.interaction.annotations.Stamp"/>
  */
  public class StampAppearanceBuilder
  {
    #region types
    public enum TypeEnum
    {
      Round,
      Squared,
      Striped
    }
    #endregion

    #region static
    #region fields
    private static readonly Length DefaultBorderRadius = new Length(.05, Length.UnitModeEnum.Relative);
    private static readonly Length DefaultBorderWidth = new Length(.025, Length.UnitModeEnum.Relative);
    private static readonly colors::Color DefaultColor = colors::DeviceRGBColor.Get(System.Drawing.Color.Red);
    #endregion
    #endregion

    #region dynamic
    #region fields
    private bool borderDoubled = true;
    private Length borderRadius = DefaultBorderRadius;
    private Length borderWidth = DefaultBorderWidth;
    private colors::Color color = DefaultColor;
    private fonts::Font font;
    private string text;
    private TypeEnum type;
    private float width;

    private Document document;
    #endregion

    #region constructors
    public StampAppearanceBuilder(
      Document document,
      TypeEnum type,
      string text,
      float width,
      fonts::Font font
      )
    {
      this.document = document;
      this.type = type;
      this.width = width;
      Text = text;
      Font = font;
    }
    #endregion

    #region interface
    #region public
    public bool BorderDoubled
    {
      set
      {borderDoubled = value;}
    }

    public Length BorderRadius
    {
      set
      {borderRadius = value;}
    }

    public Length BorderWidth
    {
      set
      {borderWidth = value;}
    }

    public FormXObject Build(
      )
    {
      bool isRound = type == TypeEnum.Round;
      bool isStriped = type == TypeEnum.Striped;
      double textScale = .5;
      double borderWidth = this.borderWidth.GetValue(width);
      double doubleBorderGap = borderDoubled ? borderWidth : 0;
      double fontSize = 10;
      fontSize *= ((width - (isStriped ? 2 : doubleBorderGap * 2 + (borderWidth * (borderDoubled ? 1.5 : 1) * 2) + width * (isRound ?  .15 : .05))) / textScale) / font.GetWidth(text, fontSize);
      float height = (float)(isRound ? width : (font.GetAscent(fontSize) * 1.2 + doubleBorderGap * 2 + (borderWidth * (borderDoubled ? 1.5 : 1) * 2)));
      SizeF size = new SizeF(width, height);

      FormXObject appearance = new FormXObject(document, size);
      {
        PrimitiveComposer composer = new PrimitiveComposer(appearance);
        if(color != null)
        {
          composer.SetStrokeColor(color);
          composer.SetFillColor(color);
        }
        composer.SetTextScale(textScale);
        composer.SetFont(font, fontSize);
        composer.ShowText(text, new PointF(size.Width / 2, (float)(size.Height / 2 - font.GetDescent(fontSize) * .4)), XAlignmentEnum.Center, YAlignmentEnum.Middle, 0);

        double borderRadius = isRound ? 0 : this.borderRadius.GetValue((size.Width + size.Height) / 2);
        RectangleF prevBorderBox = appearance.Box;
        for(int borderStep = 0, borderStepLimit = (borderDoubled ? 2 : 1); borderStep < borderStepLimit; borderStep++)
        {
          if(borderStep == 0)
          {composer.SetLineWidth(borderWidth);}
          else
          {composer.SetLineWidth(composer.State.LineWidth / 2);}

          float lineWidth =  (float)(borderStep > 0 ? composer.State.LineWidth / 2 : borderWidth);
          float marginY = (float)(lineWidth / 2 + (borderStep > 0 ? composer.State.LineWidth + doubleBorderGap : 0));
          float marginX = isStriped ? 0 : marginY;
          RectangleF borderBox = new RectangleF(prevBorderBox.X + marginX, prevBorderBox.Y + marginY, prevBorderBox.Width - marginX * 2, prevBorderBox.Height - marginY * 2);

          if(isRound)
          {composer.DrawEllipse(borderBox);}
          else
          {
            if(isStriped)
            {
              composer.DrawLine(new PointF(borderBox.Left, borderBox.Top), new PointF(borderBox.Right, borderBox.Top));
              composer.DrawLine(new PointF(borderBox.Left, borderBox.Bottom), new PointF(borderBox.Right, borderBox.Bottom));
            }
            else
            {composer.DrawRectangle(borderBox,  borderRadius * (1 - .5 * borderStep));}
          }
          composer.Stroke();
          prevBorderBox = borderBox;
        }
        composer.Flush();
      }
      return appearance;
    }

    public colors::Color Color
    {
      set
      {color = value;}
    }

    public fonts::Font Font
    {
      set
      {font = value;}
    }

    public string Text
    {
      set
      {text = value.ToUpper();}
    }
    #endregion
    #endregion
    #endregion
  }
}

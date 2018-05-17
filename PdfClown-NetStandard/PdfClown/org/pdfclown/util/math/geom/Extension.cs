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

using System;
using System.Drawing;
using System.Drawing.Drawing2D;

namespace org.pdfclown.util.math.geom
{
  public static class Extension
  {
    public static void Add(
      this RectangleF rectangle,
      PointF point
      )
    {
      if(point.X < rectangle.Left)
      {
        rectangle.Width += (rectangle.X - point.X);
        rectangle.X = point.X;
      }
      else if(point.X > rectangle.Right)
      {rectangle.Width = point.X - rectangle.X;}
      if(point.Y < rectangle.Top)
      {
        rectangle.Height += (rectangle.Y - point.Y);
        rectangle.Y = point.Y;
      }
      else if(point.Y > rectangle.Bottom)
      {rectangle.Height = point.Y - rectangle.Y;}
    }

    public static PointF Center(
      this RectangleF rectangle
      )
    {return new PointF(rectangle.CenterX(), rectangle.CenterY());}

    public static float CenterX(
      this RectangleF rectangle
      )
    {return rectangle.Left + rectangle.Width / 2;}

    public static float CenterY(
      this RectangleF rectangle
      )
    {return rectangle.Top + rectangle.Height / 2;}

    public static GraphicsPath ToPath(
      this RectangleF rectangle
      )
    {
      var path = new GraphicsPath();
      path.AddRectangle(rectangle);
      return path;
    }

    public static PointF Transform(
      this Matrix matrix,
      PointF point
      )
    {
      var points = new PointF[]{point};
      matrix.TransformPoints(points);
      return points[0];
    }
  }
}


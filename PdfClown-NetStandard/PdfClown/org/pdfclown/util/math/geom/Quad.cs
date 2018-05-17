/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

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
  /**
    <summary>Quadrilateral shape.</summary>
  */
  public class Quad
  {
    #region static
    #region interface
    #region public
    public static Quad Get(
      RectangleF rectangle
      )
    {return new Quad(GetPoints(rectangle));}

    public static PointF[] GetPoints(
      RectangleF rectangle
      )
    {
      PointF[] points = new PointF[4];
      {
        points[0] = new PointF(rectangle.Left, rectangle.Top);
        points[1] = new PointF(rectangle.Right, rectangle.Top);
        points[2] = new PointF(rectangle.Right, rectangle.Bottom);
        points[3] = new PointF(rectangle.Left, rectangle.Bottom);
      }
      return points;
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region fields
    private PointF[] points;

    private GraphicsPath path;
    #endregion

    #region constructors
    public Quad(
      params PointF[] points
      )
    {Points = points;}
    #endregion

    #region interface
    #region public
    public bool Contains(
      PointF point
      )
    {return Path.IsVisible(point);}

    public bool Contains(
      float x,
      float y
      )
    {return Path.IsVisible(x, y);}

    public RectangleF GetBounds(
      )
    {return Path.GetBounds();}

    public GraphicsPathIterator GetPathIterator(
      )
    {return new GraphicsPathIterator(Path);}

    /**
      <summary>Expands the size of this quad stretching around its center.</summary>
      <param name="value">Expansion extent.</param>
      <returns>This quad.</returns>
    */
    public Quad Inflate(
      float value
      )
    {return Inflate(value, value);}

    /**
      <summary>Expands the size of this quad stretching around its center.</summary>
      <param name="valueX">Expansion's horizontal extent.</param>
      <param name="valueY">Expansion's vertical extent.</param>
      <returns>This quad.</returns>
    */
    public Quad Inflate(
      float valueX,
      float valueY
      )
    {
      Matrix matrix = new Matrix();
      RectangleF oldBounds = Path.GetBounds();
      matrix.Translate(-oldBounds.X, -oldBounds.Y);
      path.Transform(matrix);
      matrix = new Matrix();
      matrix.Scale(1 + valueX * 2 / oldBounds.Width, 1 + valueY * 2 / oldBounds.Height);
      path.Transform(matrix);
      RectangleF newBounds = path.GetBounds();
      matrix = new Matrix();
      matrix.Translate(oldBounds.X - (newBounds.Width - oldBounds.Width) / 2, oldBounds.Y - (newBounds.Height - oldBounds.Height) / 2);
      path.Transform(matrix);

      points = path.PathPoints;
      return this;
    }

    public PointF[] Points
    {
      get
      {return points;}
      set
      {
        if(value.Length != 4)
          throw new ArgumentException("Cardinality MUST be 4.","points");

        points = value;
        path = null;
      }
    }
    #endregion

    #region private
    private GraphicsPath Path
    {
      get
      {
        if(path == null)
        {
          path = new GraphicsPath(FillMode.Alternate);
          path.AddPolygon(points);
        }
        return path;
      }
    }
    #endregion
    #endregion
    #endregion
  }
}
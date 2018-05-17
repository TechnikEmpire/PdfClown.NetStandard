/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util.math.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
  Quadrilateral shape.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 03/21/15
*/
public class Quad
  implements Shape
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static Quad get(
    Rectangle2D rectangle
    )
  {return new Quad(getPoints(rectangle));}

  public static Point2D[] getPoints(
    Rectangle2D rectangle
    )
  {
    Point2D[] points = new Point2D[4];
    {
      points[0] = new Point2D.Double(rectangle.getMinX(), rectangle.getMinY());
      points[1] = new Point2D.Double(rectangle.getMaxX(), rectangle.getMinY());
      points[2] = new Point2D.Double(rectangle.getMaxX(), rectangle.getMaxY());
      points[3] = new Point2D.Double(rectangle.getMinX(), rectangle.getMaxY());
    }
    return points;
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private Point2D[] points;

  private Path2D path;
  // </fields>

  // <constructors>
  public Quad(
    Point2D... points
    )
  {setPoints(points);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public boolean contains(
    Point2D p
    )
  {return getPath().contains(p);}

  @Override
  public boolean contains(
    Rectangle2D r
    )
  {return getPath().contains(r);}

  @Override
  public boolean contains(
    double x,
    double y
    )
  {return getPath().contains(x, y);}

  @Override
  public boolean contains(
    double x,
    double y,
    double w,
    double h
    )
  {return getPath().contains(x, y, w, h);}

  @Override
  public Rectangle getBounds(
    )
  {return getPath().getBounds();}

  @Override
  public Rectangle2D getBounds2D(
    )
  {return getPath().getBounds2D();}

  @Override
  public PathIterator getPathIterator(
    AffineTransform at
    )
  {return getPath().getPathIterator(at);}

  @Override
  public PathIterator getPathIterator(
    AffineTransform at,
    double flatness
    )
  {return getPath().getPathIterator(at, flatness);}

  public Point2D[] getPoints(
    )
  {return points;}

  /**
    Expands the size of this quad stretching around its center.
    
    @param value
      Expansion extent.
    @return
      This quad.
  */
  public Quad inflate(
    double value
    )
  {return inflate(value, value);}
  
  /**
    Expands the size of this quad stretching around its center.
    
    @param valueX
      Expansion's horizontal extent.
    @param valueY
      Expansion's vertical extent.
    @return
      This quad.
  */
  public Quad inflate(
    double valueX,
    double valueY
    )
  {
    AffineTransform matrix = new AffineTransform();
    Rectangle2D oldBounds = getPath().getBounds2D();
    matrix.translate(-oldBounds.getX(), -oldBounds.getY());
    path.transform(matrix);
    matrix = new AffineTransform();
    matrix.scale(1 + valueX * 2 / oldBounds.getWidth(), 1 + valueY * 2 / oldBounds.getHeight());
    path.transform(matrix);
    Rectangle2D newBounds = path.getBounds2D();
    matrix = new AffineTransform();
    matrix.translate(oldBounds.getX() - (newBounds.getWidth() - oldBounds.getWidth()) / 2, oldBounds.getY() - (newBounds.getHeight() - oldBounds.getHeight()) / 2);
    path.transform(matrix);
    
    points = GeomUtils.getPoints(path.getPathIterator(null));
    return this;
  }

  @Override
  public boolean intersects(
    Rectangle2D r
    )
  {return getPath().intersects(r);}

  @Override
  public boolean intersects(
    double x,
    double y,
    double w,
    double h
    )
  {return getPath().intersects(x, y, w, h);}

  public void setPoints(
    Point2D[] value
    )
  {
    if(value.length != 4)
      throw new IllegalArgumentException("'points' MUST have a cardinality of 4.");

    points = value;
    path = null;
  }
  // </public>

  // <private>
  private Path2D getPath(
    )
  {
    if(path == null)
    {
      for(Point2D point : points)
      {
        if(path == null)
        {
          path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
          path.moveTo(point.getX(), point.getY());
        }
        else
        {path.lineTo(point.getX(), point.getY());}
      }
      path.closePath();
    }
    return path;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}

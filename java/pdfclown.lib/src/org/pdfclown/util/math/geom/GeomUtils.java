/*
  Copyright 2012-2015 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
  Geometric utilities.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2.1, 03/21/15
*/
public class GeomUtils
{
  public static Rectangle2D align(
    Rectangle2D rectangle,
    Point2D anchor,
    Point alignment
    )
  {
    return new Rectangle2D.Double(
      anchor.getX() - rectangle.getWidth() * (1 - Double.compare(alignment.getX(), 0)) / 2,
      anchor.getY() - rectangle.getHeight() * (1 - Double.compare(alignment.getY(), 0)) / 2,
      rectangle.getWidth(),
      rectangle.getHeight()
      );
  }

  public static Rectangle2D getBounds(
    Rectangle2D rectangle,
    double rotation
    )
  {
    Area area = new Area(rectangle);
    AffineTransform transform = new AffineTransform();
    transform.rotate(Math.toRadians(rotation));
    area.transform(transform);
    return area.getBounds2D();
  }

  public static Point2D[] getPoints(
    PathIterator pathIterator
    )
  {
    List<Point2D> points = new ArrayList<Point2D>();
    float[] coords = new float[6];
    while(!pathIterator.isDone())
    {
      pathIterator.currentSegment(coords);
      points.add(new Point2D.Double(coords[0], coords[1]));
      pathIterator.next();
    }
    return points.toArray(new Point2D[0]);
  }

  /**
    Gets the size scaled to the specified limit.
    In particular, the limit matches the largest dimension and proportionally scales the other one;
    for example, a limit 300 applied to size Dimension2D(100, 200) returns Dimension2D(150, 300).

    @param size Size to scale.
    @param limit Scale limit.
    @return Scaled size.
  */
  public static Dimension2D scale(
    Dimension2D size,
    double limit
    )
  {
    if(limit == 0)
      return (Dimension2D)size.clone();
    else
    {
      double sizeRatio = size.getWidth() / size.getHeight();
      return sizeRatio > 1
        ? new Dimension(limit, limit / sizeRatio)
        : new Dimension(limit * sizeRatio, limit);
    }
  }

  /**
    Gets the size scaled to the specified limit.
    In particular, implicit (zero-valued) limit dimensions correspond to proportional dimensions;
    for example, a limit Dimension2D(0, 300) means 300 high and proportionally wide.

    @param size Size to scale.
    @param limit Scale limit.
    @return Scaled size.
  */
  public static Dimension2D scale(
    Dimension2D size,
    Dimension2D limit
    )
  {
    if(limit.getWidth() == 0)
    {
      if(limit.getHeight() == 0)
        return (Dimension2D)size.clone();
      else
        return new Dimension(limit.getHeight() * size.getWidth() / size.getHeight(), limit.getHeight());
    }
    else if(limit.getHeight() == 0)
      return new Dimension(limit.getWidth(), limit.getWidth() * size.getHeight() / size.getWidth());
    else
      return (Dimension2D)limit.clone();
  }
}

/*
  Copyright 2007-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfNumber;

/**
  Line Dash Pattern [PDF:1.6:4.3.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF10)
public final class LineDash
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Gets the pattern corresponding to the specified components.
  */
  public static LineDash get(
    PdfArray dashArray,
    PdfNumber<?> dashPhase
    )
  {
    if(dashArray == null)
      return null;

    // Dash array.
    double[] dashArrayValue = new double[dashArray.size()];
    for(int index = 0, length = dashArrayValue.length; index < length; index++)
    {dashArrayValue[index] = ((PdfNumber<?>)dashArray.get(index)).getDoubleValue();}
    // Dash phase.
    double dashPhaseValue = dashPhase != null ? ((PdfNumber<?>)dashPhase).getDoubleValue() : 0;

    return new LineDash(dashArrayValue, dashPhaseValue);
  }
  // </public>
  // </interface>
  // </static>
  
  // <dynamic>
  // <fields>
  private final double[] dashArray;
  private final double dashPhase;
  // </fields>

  // <constructors>
  public LineDash(
    )
  {this(null);}

  public LineDash(
    double[] dashArray
    )
  {this(dashArray,0);}

  public LineDash(
    double[] dashArray,
    double dashPhase
    )
  {
    this.dashArray = dashArray != null ? dashArray : new double[0]; // [FIX:9] NullPointerException if dashArray not initialized.
    this.dashPhase = dashPhase;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the lengths of alternating dashes and gaps.
  */
  public double[] getDashArray(
    )
  {return dashArray;}

  /**
    Gets the distance into the dash pattern at which to start the dash.
  */
  public double getDashPhase(
    )
  {return dashPhase;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
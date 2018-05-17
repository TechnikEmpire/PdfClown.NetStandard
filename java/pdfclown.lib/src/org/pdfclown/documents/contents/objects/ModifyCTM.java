/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.util.math.SquareMatrix;

/**
  'Modify the current transformation matrix (CTM) by concatenating the specified matrix'
  operation [PDF:1.6:4.3.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF10)
public final class ModifyCTM
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "cm";
  // </fields>

  // <interface>
  // <public>
  public static ModifyCTM getResetCTM(
    GraphicsState state
    )
  {
    return new ModifyCTM(
      SquareMatrix.get(state.getCtm()).solve(
        SquareMatrix.get(state.getInitialCtm())
        ).toTransform()
      );
  }
  // </static>

  // <dynamic>
  // <constructors>
  public ModifyCTM(
    AffineTransform value
    )
  {
    this(
      value.getScaleX(),
      value.getShearY(),
      value.getShearX(),
      value.getScaleY(),
      value.getTranslateX(),
      value.getTranslateY()
      );
  }

  public ModifyCTM(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f
    )
  {
    super(
      Operator,
      PdfReal.get(a),
      PdfReal.get(b),
      PdfReal.get(c),
      PdfReal.get(d),
      PdfReal.get(e),
      PdfReal.get(f)
      );
  }

  public ModifyCTM(
    List<PdfDirectObject> operands
    )
  {super(Operator, operands);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public void scan(
    GraphicsState state
    )
  {
    state.getCtm().concatenate(getValue());

    Graphics2D context = state.getScanner().getRenderContext();
    if(context != null)
    {context.setTransform(state.getCtm());}
  }

  public AffineTransform getValue(
    )
  {
    return new AffineTransform(
      ((PdfNumber<?>)operands.get(0)).getDoubleValue(),
      ((PdfNumber<?>)operands.get(1)).getDoubleValue(),
      ((PdfNumber<?>)operands.get(2)).getDoubleValue(),
      ((PdfNumber<?>)operands.get(3)).getDoubleValue(),
      ((PdfNumber<?>)operands.get(4)).getDoubleValue(),
      ((PdfNumber<?>)operands.get(5)).getDoubleValue()
      );
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
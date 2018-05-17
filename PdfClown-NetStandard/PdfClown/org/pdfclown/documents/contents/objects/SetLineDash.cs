/*
  Copyright 2007-2015 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown.bytes;
using org.pdfclown.objects;

using System.Collections.Generic;

namespace org.pdfclown.documents.contents.objects
{
  /**
    <summary>'Set the line dash pattern' operation [PDF:1.6:4.3.3].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public sealed class SetLineDash
    : Operation
  {
    #region static
    #region fields
    public static readonly string OperatorKeyword = "d";
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public SetLineDash(
      LineDash lineDash
      ) : base(OperatorKeyword, (PdfDirectObject)new PdfArray())
    {Value = lineDash;}

    public SetLineDash(
      IList<PdfDirectObject> operands
      ) : base(OperatorKeyword, operands)
    {}
    #endregion

    #region interface
    #region public
    public override void Scan(
      ContentScanner.GraphicsState state
      )
    {state.LineDash = Value;}

    public LineDash Value
    {
      get
      {return LineDash.Get((PdfArray)operands[0], (IPdfNumber)operands[1]);}
      set
      {
        operands.Clear();
        // 1. Dash array.
        double[] dashArray = value.DashArray;
        PdfArray baseDashArray = new PdfArray(dashArray.Length);
        foreach(double dashItem in dashArray)
        {baseDashArray.Add(PdfReal.Get(dashItem));}
        operands.Add(baseDashArray);
        // 2. Dash phase.
        operands.Add(PdfReal.Get(value.DashPhase));
      }
    }
    #endregion
    #endregion
    #endregion
  }
}
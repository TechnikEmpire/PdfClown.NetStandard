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

using org.pdfclown.objects;

namespace org.pdfclown.documents.contents
{
  /**
    <summary>Line Dash Pattern [PDF:1.6:4.3.2].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public sealed class LineDash
  {
    #region static
    #region interface
    #region public
    /**
      <summary>Gets the pattern corresponding to the specified components.</summary>
    */
    public static LineDash Get(
      PdfArray dashArray,
      IPdfNumber dashPhase
      )
    {
      if(dashArray == null)
        return null;

      // Dash array.
      double[] dashArrayValue = new double[dashArray.Count];
      for(int index = 0, length = dashArrayValue.Length; index < length; index++)
      {dashArrayValue[index] = ((IPdfNumber)dashArray[index]).DoubleValue;}
      // Dash phase.
      double dashPhaseValue = dashPhase != null ? ((IPdfNumber)dashPhase).DoubleValue : 0;

      return new LineDash(dashArrayValue, dashPhaseValue);
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region fields
    private readonly double[] dashArray;
    private readonly double dashPhase;
    #endregion

    #region constructors
    public LineDash(
      ) : this(null)
    {}

    public LineDash(
      double[] dashArray
      ) : this(dashArray,0)
    {}

    public LineDash(
      double[] dashArray,
      double dashPhase
      )
    {
      this.dashArray = dashArray != null ? dashArray : new double[0]; // [FIX:9] NullPointerException if dashArray not initialized.
      this.dashPhase = dashPhase;
    }
    #endregion

    #region interface
    #region public
    public double[] DashArray
    {
      get
      {return dashArray;}
    }

    public double DashPhase
    {
      get
      {return dashPhase;}
    }
    #endregion
    #endregion
    #endregion
  }
}
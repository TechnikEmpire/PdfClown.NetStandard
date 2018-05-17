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
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.objects;

using System.Collections.Generic;
using System.Linq;

namespace org.pdfclown.documents.contents.objects
{
  /**
    <summary>'Set the color to use for nonstroking operations' operation [PDF:1.6:4.5.7].</summary>
  */
  [PDF(VersionEnum.PDF12)]
  public class SetFillColor
    : Operation
  {
    #region static
    #region fields
    /**
      <summary>'Set the color to use for nonstroking operations in any color space' operator.</summary>
    */
    [PDF(VersionEnum.PDF12)]
    public static readonly string ExtendedOperatorKeyword = "scn";
    /**
      <summary>'Set the color to use for nonstroking operations in a device, CIE-based (other than ICCBased),
      or Indexed color space' operator.</summary>
    */
    [PDF(VersionEnum.PDF11)]
    public static readonly string OperatorKeyword = "sc";
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public SetFillColor(
      Color value
      ) : this(ExtendedOperatorKeyword, value)
    {}

    public SetFillColor(
      IList<PdfDirectObject> operands
      ) : this(ExtendedOperatorKeyword, operands)
    {}

    public SetFillColor(
      string @operator,
      IList<PdfDirectObject> operands
      ) : base(@operator, operands)
    {}

    protected SetFillColor(
      string @operator,
      Color value
      ) : base(@operator, new List<PdfDirectObject>(value.Components))
    {}

    /**
      <param name="operator">Graphics operator.</param>
      <param name="name">Name of the color resource entry (see <see cref="Pattern"/>).</param>
     */
    protected SetFillColor(
      string @operator,
      PdfName name
      ) : this(@operator, name, null)
    {}

    /**
      <param name="operator">Graphics operator.</param>
      <param name="name">Name of the color resource entry (see <see cref="Pattern"/>).</param>
      <param name="underlyingColor">Color used to colorize the pattern.</param>
     */
    protected SetFillColor(
      string @operator,
      PdfName name,
      Color underlyingColor
      ) : base(@operator, new List<PdfDirectObject>())
    {
      if(underlyingColor != null)
      {
        foreach(PdfDirectObject component in underlyingColor.Components)
        {operands.Add(component);}
      }
      operands.Add(name);
    }
    #endregion

    #region interface
    #region public
    public IList<PdfDirectObject> Components
    {
      get
      {return operands;}
    }

    public override void Scan(
      ContentScanner.GraphicsState state
      )
    {
      state.FillColor = state.FillColorSpace.GetColor(
        operands,
        state.Scanner.ContentContext
        );
    }
    #endregion
    #endregion
    #endregion
  }
}
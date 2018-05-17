/*
  Copyright 2008-2010 Stefano Chizzolini. http://www.pdfclown.org

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

using System.Drawing.Drawing2D;

namespace org.pdfclown.documents.contents.objects
{
  /**
    <summary>'Close the current subpath by appending a straight line segment
    from the current point to the starting point of the subpath' operation
    [PDF:1.6:4.4.1].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public sealed class CloseSubpath
    : Operation
  {
    #region static
    #region fields
    public static readonly string OperatorKeyword = "h";

    public static readonly CloseSubpath Value = new CloseSubpath();
    #endregion
    #endregion

    #region dynamic
    #region constructors
    private CloseSubpath(
      ) : base(OperatorKeyword)
    {}
    #endregion

    #region interface
    #region public
    public override void Scan(
      ContentScanner.GraphicsState state
      )
    {
      GraphicsPath pathObject = state.Scanner.RenderObject;
      if(pathObject != null)
      {pathObject.CloseFigure();}
    }
    #endregion
    #endregion
    #endregion
  }
}
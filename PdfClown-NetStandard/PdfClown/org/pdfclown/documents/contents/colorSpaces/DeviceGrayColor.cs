/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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

using System;
using System.Collections.Generic;

namespace org.pdfclown.documents.contents.colorSpaces
{
  /**
    <summary>Device Gray color value [PDF:1.6:4.5.3].</summary>
  */
  [PDF(VersionEnum.PDF11)]
  public sealed class DeviceGrayColor
    : DeviceColor
  {
    #region static
    #region fields
    public static readonly DeviceGrayColor Black = new DeviceGrayColor(0);
    public static readonly DeviceGrayColor White = new DeviceGrayColor(1);

    public static readonly DeviceGrayColor Default = Black;
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets the color corresponding to the specified components.</summary>
      <param name="components">Color components to convert.</param>
     */
    public static new DeviceGrayColor Get(
      PdfArray components
      )
    {
      return (components != null
        ? new DeviceGrayColor(components)
        : Default
        );
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public DeviceGrayColor(
      double g
      ) : this(
        new List<PdfDirectObject>(
          new PdfDirectObject[]
          {
            PdfReal.Get(NormalizeComponent(g))
          }
        )
      )
    {}

    internal DeviceGrayColor(
      IList<PdfDirectObject> components
      ) : base(
        DeviceGrayColorSpace.Default,
        new PdfArray(components)
        )
    {}
    #endregion

    #region interface
    #region public
    public override object Clone(
      Document context
      )
    {throw new NotImplementedException();}

    /**
      <summary>Gets/Sets the gray component.</summary>
    */
    public double G
    {
      get
      {return GetComponentValue(0);}
      set
      {SetComponentValue(0, value);}
    }
    #endregion
    #endregion
    #endregion
  }
}
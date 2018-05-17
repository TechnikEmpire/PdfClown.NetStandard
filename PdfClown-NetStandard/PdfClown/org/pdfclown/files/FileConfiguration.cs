/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

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

using System;

namespace org.pdfclown.files
{
  /**
    <summary>File configuration.</summary>
  */
  public sealed class FileConfiguration
  {
    #region dynamic
    #region fields
    private string realFormat;
    private bool streamFilterEnabled;
    private XRefModeEnum xrefMode = XRefModeEnum.Plain;

    private readonly File file;
    #endregion

    #region constructors
    internal FileConfiguration(
      File file
      )
    {
      this.file = file;

      RealPrecision = 0;
      StreamFilterEnabled = true;
    }
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets the file associated with this configuration.</summary>
    */
    public File File
    {
      get
      {return file;}
    }

    /**
      <summary>Gets/Sets the number of decimal places applied to real numbers' serialization.</summary>
    */
    public int RealPrecision
    {
      get
      {return realFormat.Length - realFormat.IndexOf('.') - 1;}
      set
      {realFormat = "0." + new string('#', value <= 0 ? 5 : value);}
    }

    /**
      <summary>Gets/Sets whether PDF stream objects have to be filtered for compression.</summary>
    */
    public bool StreamFilterEnabled
    {
      get
      {return streamFilterEnabled;}
      set
      {streamFilterEnabled = value;}
    }

    /**
      <summary>Gets the document's cross-reference mode.</summary>
    */
    public XRefModeEnum XRefMode
    {
      get
      {return xrefMode;}
      set
      {file.Document.CheckCompatibility(xrefMode = value);}
    }
    #endregion

    #region internal
    internal string RealFormat
    {
      get
      {return realFormat;}
    }
    #endregion
    #endregion
    #endregion
  }
}

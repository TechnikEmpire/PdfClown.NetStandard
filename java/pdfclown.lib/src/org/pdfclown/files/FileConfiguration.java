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

package org.pdfclown.files;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.pdfclown.util.StringUtils;

/**
  File configuration.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 03/30/15
*/
public final class FileConfiguration
{
  // <dynamic>
  // <fields>
  private DecimalFormat realFormat;
  private boolean streamFilterEnabled;
  private XRefModeEnum xrefMode = XRefModeEnum.Plain;

  private final File file;
  // </fields>

  // <constructors>
  FileConfiguration(
    File file
    )
  {
    this.file = file;

    setRealPrecision(0);
    setStreamFilterEnabled(true);
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the file associated with this configuration.
  */
  public File getFile(
    )
  {return file;}

  public DecimalFormat getRealFormat(
    )
  {return realFormat;}

  /**
    Gets the number of decimal places applied to real numbers' serialization.
  */
  public int getRealPrecision(
    )
  {return realFormat.getMaximumFractionDigits();}

  /**
    Gets the document's cross-reference mode.
  */
  public XRefModeEnum getXRefMode(
    )
  {return xrefMode;}

  /**
    Gets whether PDF stream objects have to be filtered for compression.
  */
  public boolean isStreamFilterEnabled(
    )
  {return streamFilterEnabled;}

  /**
    @see #getRealPrecision()
  */
  public void setRealPrecision(
    int value
    )
  {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    realFormat = new DecimalFormat("0." + StringUtils.repeat("#", value <= 0 ? 5 : value), symbols);
  }

  /**
    @see #isStreamFilterEnabled()
  */
  public void setStreamFilterEnabled(
    boolean value
    )
  {streamFilterEnabled = value;}

  /**
    @see #getXrefMode()
  */
  public void setXRefMode(
    XRefModeEnum value
    )
  {file.getDocument().checkCompatibility(xrefMode = value);}

  /**
    @see #setRealPrecision(int)
  */
  public FileConfiguration withRealPrecision(
    int value
    )
  {
    setRealPrecision(value);
    return this;
  }

  /**
    @see #setStreamFilterEnabled(boolean)
  */
  public FileConfiguration withStreamFilterEnabled(
    boolean value
    )
  {
    setStreamFilterEnabled(value);
    return this;
  }

  /**
    @see #setXRefMode(XRefModeEnum)
  */
  public FileConfiguration withXRefMode(
    XRefModeEnum value
    )
  {
    setXRefMode(value);
    return this;
  }
  // </public>
  // </interface>
  // </dynamic>
}
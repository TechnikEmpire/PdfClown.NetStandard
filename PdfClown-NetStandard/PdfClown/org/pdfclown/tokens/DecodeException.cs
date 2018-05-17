/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

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

using System;

namespace org.pdfclown.tokens
{
  /**
    <summary>Exception thrown in case of missing code-to-character mapping.</summary>
  */
  public class DecodeException
    : Exception
  {
    #region dynamic
    #region fields
    private byte[] bytes;
    private int index;
    #endregion

    #region constructors
    public DecodeException(
      byte[] bytes,
      int index
      ) : base(String.Format("Missing character mapping for byte sequence starting with {0:X2} at position {1}", bytes[index], index))
    {
      this.bytes = bytes;
      this.index = index;
    }
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets the byte array to decode.</summary>
    */
    public byte[] Bytes
    {
      get
      {return bytes;}
    }

    /**
      <summary>Gets the position of the missing sequence in the byte array to decode.</summary>
    */
    public int Index
    {
      get
      {return index;}
    }
    #endregion
    #endregion
    #endregion
  }
}
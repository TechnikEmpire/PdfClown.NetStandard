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
  public class EncodeException
    : Exception
  {
    #region dynamic
    #region fields
    private int index;
    private string text;
    #endregion

    #region constructors
    public EncodeException(
      char textChar
    ) : this(new String(textChar, 1), 0)
    {}

    public EncodeException(
      string text,
      int index
      ) : base(String.Format("Missing code mapping for character {0} ('{1}') at position {2} in \"{3}\"", (int)text[index], text[index], index, text))
    {
      this.text = text;
      this.index = index;
    }
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets the position of the missing character in the string to encode.</summary>
    */
    public int Index
    {
      get
      {return index;}
    }

    /**
      <summary>Gets the string to encode.</summary>
    */
    public string Text
    {
      get
      {return text;}
    }

    /**
      <summary>Gets the missing character.</summary>
    */
    public char UndefinedChar
    {
      get
      {return text[index];}
    }
    #endregion
    #endregion
    #endregion
  }
}
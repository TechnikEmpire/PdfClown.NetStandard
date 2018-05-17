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

package org.pdfclown.tokens;

/**
  Exception thrown in case of missing character-to-code mapping.
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/08/15
*/
public class EncodeException
  extends RuntimeException
{
  // <static>
  // <fields>
  private static final long serialVersionUID = 1L;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private int index;
  private String text;
  // </fields>

  // <constructors>
  public EncodeException(
    char textChar
    )
  {this(Character.toString(textChar), 0);}
  
  public EncodeException(
    String text,
    int index
    )
  {
    super(String.format("Missing code mapping for character \\u%04X ('%s') at position %s in \"%s\"", (int)text.charAt(index), text.charAt(index), index, text));
    this.text = text;
    this.index = index;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the position of the missing character in the string to encode.
  */
  public int getIndex(
    )
  {return index;}

  /**
    Gets the string to encode.
  */
  public String getText(
    )
  {return text;}
  
  /**
    Gets the missing character.
  */
  public char getUndefinedChar(
    )
  {return text.charAt(index);}
  // </public>
  // </interface>
  // </dynamic>
}

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

package org.pdfclown.util.parsers;

/**
  Exception thrown in case of unexpected condition while parsing PostScript-based data.
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.2.0, 03/10/15
*/
public class PostScriptParseException
  extends ParseException
{
  // <class>
  // <static>
  // <fields>
  private static final long serialVersionUID = 1L;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private final Object token;
  private final PostScriptParser.TokenTypeEnum tokenType;
  // </fields>

  // <constructors>
  public PostScriptParseException(
    String message
    )
  {this(message, -1);}

  public PostScriptParseException(
    String message,
    PostScriptParser parser
    )
  {this(message, null, parser);}

  public PostScriptParseException(
    String message,
    long position
    )
  {this(message, position, null, null);}
  
  public PostScriptParseException(
    String message,
    long position,
    Object token,
    PostScriptParser.TokenTypeEnum tokenType
    )
  {this(message, null, position, token, tokenType);}
  
  public PostScriptParseException(
    Throwable cause
    )
  {this(null, cause);}

  public PostScriptParseException(
    String message,
    Throwable cause
    )
  {this(message, cause, -1);}

  public PostScriptParseException(
    String message,
    Throwable cause,
    PostScriptParser parser
    )
  {this(message, cause, parser.getPosition(), parser.getToken(), parser.getTokenType());}

  public PostScriptParseException(
    String message,
    Throwable cause,
    long position
    )
  {this(message, cause, position, null, null);}
  
  public PostScriptParseException(
    String message,
    Throwable cause,
    long position,
    Object token,
    PostScriptParser.TokenTypeEnum tokenType
    )
  {
    super(message, cause, position);

    this.token = token;
    this.tokenType = tokenType;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the token on which the exception happened.
  */
  public Object getToken(
    )
  {return token;}
  
  /**
    Gets the type of the token on which the exception happened.
  */
  public PostScriptParser.TokenTypeEnum getTokenType(
    )
  {return tokenType;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}

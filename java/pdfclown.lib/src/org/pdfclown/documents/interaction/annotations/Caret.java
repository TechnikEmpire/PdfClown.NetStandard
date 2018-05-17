/*
  Copyright 2008-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Caret annotation [PDF:1.6:8.4.5].
  <p>It displays a visual symbol that indicates the presence of text edits.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.2.0, 03/21/15
*/
@PDF(VersionEnum.PDF15)
public final class Caret
  extends Markup<Caret>
{
  // <class>
  // <classes>
  /**
    Symbol type [PDF:1.6:8.4.5].
  */
  public enum SymbolTypeEnum
  {
    /**
      None.
    */
    None(PdfName.None),
    /**
      New paragraph.
    */
    NewParagraph(PdfName.P);

    /**
      Gets the symbol type corresponding to the given value.
    */
    public static SymbolTypeEnum get(
      PdfName value
      )
    {
      for(SymbolTypeEnum symbolType : SymbolTypeEnum.values())
      {
        if(symbolType.getCode().equals(value))
          return symbolType;
      }
      return null;
    }

    private final PdfName code;

    private SymbolTypeEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final SymbolTypeEnum DefaultSymbolType = SymbolTypeEnum.None;
  // </fields>
  // </static>
  
  // <dynamic>
  // <constructors>
  public Caret(
    Page page,
    Rectangle2D box,
    String text
    )
  {super(page, PdfName.Caret, box, text);}

  Caret(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Caret clone(
    Document context
    )
  {return (Caret)super.clone(context);}

  /**
    Gets the symbol to be used in displaying the annotation.
  */
  public SymbolTypeEnum getSymbolType(
    )
  {
    PdfName symbolObject = (PdfName)getBaseDataObject().get(PdfName.Sy);
    return symbolObject != null ? SymbolTypeEnum.get(symbolObject) : DefaultSymbolType;
  }

  /**
    @see #getSymbolType()
  */
  public void setSymbolType(
    SymbolTypeEnum value
    )
  {getBaseDataObject().put(PdfName.Sy, value != null && value != DefaultSymbolType ? value.getCode() : null);}

  /**
    @see #setSymbolType(SymbolTypeEnum)
  */
  public Caret withSymbolType(
    SymbolTypeEnum value
    )
  {
    setSymbolType(value);
    return self();
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
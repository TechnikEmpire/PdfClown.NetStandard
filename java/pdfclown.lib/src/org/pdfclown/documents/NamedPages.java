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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.NameTree;
import org.pdfclown.objects.PdfDirectObject;

/**
  Named pages [PDF:1.6:3.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF13)
public final class NamedPages
  extends NameTree<Page>
{
  // <class>
  // <dynamic>
  // <constructors>
  public NamedPages(
    Document context
    )
  {super(context);}

  NamedPages(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public NamedPages clone(
    Document context
    )
  {return (NamedPages)super.clone(context);}
  // </public>

  // <protected>
  @Override
  protected Page wrapValue(
    PdfDirectObject baseObject
    )
  {return Page.wrap(baseObject);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}

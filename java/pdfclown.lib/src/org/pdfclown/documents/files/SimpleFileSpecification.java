/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.files;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfString;

/**
  Simple reference to the contents of another file [PDF:1.6:3.10.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF11)
public final class SimpleFileSpecification
  extends FileSpecification<PdfString>
{
  // <class>
  // <dynamic>
  // <constructors>
  SimpleFileSpecification(
    Document context,
    String path
    )
  {super(context, new PdfString(path));}

  SimpleFileSpecification(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public SimpleFileSpecification clone(
    Document context
    )
  {return (SimpleFileSpecification)super.clone(context);}

  @Override
  public String getPath(
    )
  {return (String)getBaseDataObject().getValue();}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}

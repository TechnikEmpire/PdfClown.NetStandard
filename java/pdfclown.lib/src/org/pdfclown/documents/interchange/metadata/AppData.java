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

package org.pdfclown.documents.interchange.metadata;

import java.util.Date;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDate;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfSimpleObject;

/**
  Private application data dictionary [PDF:1.7:10.4].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/24/15
*/
@PDF(VersionEnum.PDF13)
public class AppData
  extends PdfObjectWrapper<PdfDictionary>
{
  // <static>
  // <interface>
  static AppData wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new AppData(baseObject) : null;}
  // </interface>
  // </static>
  
  // <dynamic>
  // <constructors>
  AppData(
    Document context
    )
  {super(context, new PdfDictionary());}

  private AppData(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>
  
  // <interface>
  // <public>
  /**
    Gets the private data associated to the application.
    <p>It can be any type, although dictionary is its typical form.</p>
  */
  public PdfDataObject getData(
    )
  {return getBaseDataObject().get(PdfName.Private);}
  
  /**
    Gets the date when the contents of the holder ({@link Document document}, {@link Page page}, or 
    {@link FormXObject form}) were most recently modified by this application.
  */
  public Date getModificationDate(
    )
  {return (Date)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.LastModified));}
  
  /**
    @see #getData()
  */
  public void setData(
    PdfDataObject value
    )
  {getBaseDataObject().put(PdfName.Private, PdfObject.unresolve(value));}
  // </public>
  
  // <internal>
  /**
    <span style="color:red">For internal use only.</span>
    <p>Use the {@link IAppDataHolder#touch(PdfName)} method of the containing object ({@link 
    Document document}, {@link Page page}, or {@link FormXObject form}) instead.</p>
    
    @see #getModificationDate()
  */
  public void setModificationDate(
    Date value
    )
  {getBaseDataObject().put(PdfName.LastModified, new PdfDate(value));}
  // </internal>
  // </interface>
  // </dynamic>
}

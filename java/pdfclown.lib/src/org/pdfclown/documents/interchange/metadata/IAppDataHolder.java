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

import org.pdfclown.objects.PdfName;

/**
  Private application data holder [PDF:1.7:10.4].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/24/15
*/
public interface IAppDataHolder
{
  /**
    Gets the page-piece dictionary holding private application data.
  */
  AppDataCollection getAppData(
    );

  /**
    Gets the private data of the specified application, creating it in case no entry in the {@link 
    #getAppData() private application data collection} matches the {@code appName}.
  */
  AppData getAppData(
    PdfName appName
    );

  /**
    Gets the date and time when the holder's contents were most recently modified.
  */
  Date getModificationDate(
    );
  
  /**
    Updates the {@link #getModificationDate() modification date} with the current system date,
    synchronizing it with the corresponding private application data.
    <p>If no entry in the {@link #getAppData() private application data collection} matches the 
    {@code appName}, a new one is automatically created.</p>
    
    @param appName
      Application name corresponding to an entry in the {@link #getAppData() private application 
      data collection}.
  */
  void touch(
    PdfName appName
    );
  
  /**
    Updates the {@link #getModificationDate() modification date} synchronizing it with the 
    corresponding private application data.
    <p>If no entry in the {@link #getAppData() private application data collection} matches the 
    {@code appName}, a new one is automatically created.</p>
    
    @param appName
      Application name corresponding to an entry in the {@link #getAppData() private application 
      data collection}.
    @param modificationDate
      When the specified application last altered the content of this holder. 
  */
  void touch(
    PdfName appName,
    Date modificationDate
    );
}

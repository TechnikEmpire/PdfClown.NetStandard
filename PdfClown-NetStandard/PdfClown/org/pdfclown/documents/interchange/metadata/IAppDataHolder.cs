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

using org.pdfclown.objects;

using System;

namespace org.pdfclown.documents.interchange.metadata
{
  /**
    <summary>Private application data holder [PDF:1.7:10.4].</summary>
  */
  public interface IAppDataHolder
  {
    /**
      <summary>Gets the page-piece dictionary holding private application data.</summary>
    */
    AppDataCollection AppData
    {
      get;
    }

    /**
      <summary>Gets the private data of the specified application, creating it in case no entry in
      the <see cref="AppData">private application data collection</see> matches the
      <code>appName</code>.</summary>
    */
    AppData GetAppData(
      PdfName appName
      );

    /**
      <summary>Gets the date and time when the holder's contents were most recently modified.
      </summary>
    */
    DateTime? ModificationDate
    {
      get;
    }

    /**
      <summary>Updates the <see cref="ModificationDate">modification date</see> with the current
      system date, synchronizing it with the corresponding private application data.</summary>
      <remarks>If no entry in the <see cref="AppData">private application data collection</see>
      matches the <code>appName</code>, a new one is automatically created.</remarks>
      <param name="appName">Application name corresponding to an entry in the <see cref="AppData">
      private application data collection</see>.</remarks>
    */
    void Touch(
      PdfName appName
      );

    /**
      <summary>Updates the <see cref="ModificationDate">modification date</see> synchronizing it
      with the corresponding private application data.</summary>
      <remarks>If no entry in the <see cref="AppData">private application data collection</see>
      matches the <code>appName</code>, a new one is automatically created.</remarks>
      <param name="appName">Application name corresponding to an entry in the <see cref="AppData">
      private application data collection</see>.</remarks>
      <param name="modificationDate">When the specified application last altered the content of this
      holder.</param>
    */
    void Touch(
      PdfName appName,
      DateTime modificationDate
      );
  }
}


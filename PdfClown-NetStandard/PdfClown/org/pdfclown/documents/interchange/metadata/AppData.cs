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

using org.pdfclown.documents;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.objects;

using System;

namespace org.pdfclown.documents.interchange.metadata
{
  /**
    <summary>Private application data dictionary [PDF:1.7:10.4].</summary>
  */
  [PDF(VersionEnum.PDF13)]
  public class AppData
    : PdfObjectWrapper<PdfDictionary>
  {
    #region static
    #region interface
    internal static AppData Wrap(
      PdfDirectObject baseObject
      )
    {return baseObject != null ? new AppData(baseObject) : null;}
    #endregion
    #endregion

    #region dynamic
    #region constructors
    internal AppData(
      Document context
      ) : base(context, new PdfDictionary())
    {}

    private AppData(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    /**
      <summary>Gets/Sets the private data associated to the application.</summary>
      <remarks>It can be any type, although dictionary is its typical form.</remarks>
    */
    public PdfDataObject Data
    {
      get
      {return BaseDataObject[PdfName.Private];}
      set
      {BaseDataObject[PdfName.Private] = PdfObject.Unresolve(value);}
    }

    /**
      <summary>Gets the date when the contents of the holder (<see cref="Document">document</see>,
      <see cref="Page">page</see>, or <see cref="FormXObject">form</see>) were most recently
      modified by this application.</summary>
      <remarks>To update it, use the <see cref="IAppDataHolder.Touch(PdfName)"/> method of the
      holder.</remarks>
    */
    public DateTime ModificationDate
    {
      get
      {return (DateTime)PdfSimpleObject<object>.GetValue(BaseDataObject[PdfName.LastModified]);}
      internal set
      {BaseDataObject[PdfName.LastModified] = new PdfDate(value);}
    }
    #endregion
    #endregion
  }
}

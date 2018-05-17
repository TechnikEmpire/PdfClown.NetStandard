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
using System.Collections;
using System.Collections.Generic;

namespace org.pdfclown.documents.interchange.metadata
{
  /**
    <summary>A page-piece dictionary used to hold private application data [PDF:1.7:10.4].</summary>
  */
  [PDF(VersionEnum.PDF13)]
  public sealed class AppDataCollection
    : PdfObjectWrapper<PdfDictionary>,
      IDictionary<PdfName,AppData>
  {
    #region static
    #region interface
    public static AppDataCollection Wrap(
      PdfDirectObject baseObject,
      IAppDataHolder holder
      )
    {return baseObject != null ? new AppDataCollection(baseObject, holder) : null;}
    #endregion
    #endregion

    #region dynamic
    private IAppDataHolder holder;

    #region constructors
    private AppDataCollection(
      PdfDirectObject baseObject,
      IAppDataHolder holder
      ) : base(baseObject)
    {this.holder = holder;}
    #endregion

    #region interface
    public AppData Ensure(
      PdfName key
      )
    {
      AppData appData = this[key];
      if(appData == null)
      {
        BaseDataObject[key] = (appData = new AppData(Document)).BaseObject;
        holder.Touch(key);
      }
      return appData;
    }

    #region IDictionary
    public void Add(
      PdfName key,
      AppData value
      )
    {throw new NotSupportedException();}

    public void Clear(
      )
    {BaseDataObject.Clear();}

    public bool ContainsKey(
      PdfName key
      )
    {return BaseDataObject.ContainsKey(key);}

    public ICollection<PdfName> Keys
    {
      get
      {return BaseDataObject.Keys;}
    }

    public bool Remove(
      PdfName key
      )
    {return BaseDataObject.Remove(key);}

    public AppData this[
      PdfName key
      ]
    {
      get
      {return AppData.Wrap(BaseDataObject[key]);}
      set
      {throw new NotSupportedException();}
    }

    public bool TryGetValue(
      PdfName key,
      out AppData value
      )
    {throw new NotImplementedException();}

    public ICollection<AppData> Values
    {
      get
      {
        ICollection<AppData> values = new List<AppData>();
        foreach(PdfDirectObject valueObject in BaseDataObject.Values)
        {values.Add(AppData.Wrap(valueObject));}
        return values;
      }
    }

    #region ICollection
    public void Add(
      KeyValuePair<PdfName,AppData> item
      )
    {throw new NotSupportedException();}

    public bool Contains(
      KeyValuePair<PdfName,AppData> item
      )
    {return item.Value.BaseObject.Equals(BaseDataObject[item.Key]);}

    public void CopyTo(
      KeyValuePair<PdfName,AppData>[] array,
      int arrayIndex
      )
    {throw new NotImplementedException();}

    public int Count
    {
      get
      {return BaseDataObject.Count;}
    }

    public bool IsReadOnly
    {
      get
      {return false;}
    }

    public bool Remove(
      KeyValuePair<PdfName,AppData> item
      )
    {
      if(Contains(item))
        return Remove(item.Key);
      else
        return false;
    }

    #region IEnumerable<KeyValuePair<PdfName,AppData>>
    public IEnumerator<KeyValuePair<PdfName,AppData>> GetEnumerator(
      )
    {
      foreach(PdfName key in Keys)
      {yield return new KeyValuePair<PdfName,AppData>(key,this[key]);}
    }

    #region IEnumerable
    IEnumerator IEnumerable.GetEnumerator(
      )
    {return ((IEnumerable<KeyValuePair<PdfName,AppData>>)this).GetEnumerator();}
    #endregion
    #endregion
    #endregion
    #endregion
    #endregion
    #endregion
  }
}


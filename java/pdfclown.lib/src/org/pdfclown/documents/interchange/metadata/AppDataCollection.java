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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.MapEntry;

/**
  A page-piece dictionary used to hold private application data [PDF:1.7:10.4].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/24/15
*/
@PDF(VersionEnum.PDF13)
public final class AppDataCollection
  extends PdfObjectWrapper<PdfDictionary>
  implements Map<PdfName,AppData>
{
  // <static>
  // <interface>
  public static AppDataCollection wrap(
    PdfDirectObject baseObject,
    IAppDataHolder holder
    )
  {return baseObject != null ? new AppDataCollection(baseObject, holder) : null;}
  // </interface>
  // </static>
  
  // <dynamic>
  private IAppDataHolder holder;
  
  // <constructors>
  private AppDataCollection(
    PdfDirectObject baseObject,
    IAppDataHolder holder
    )
  {
    super(baseObject);
    this.holder = holder;
  }
  // </constructors>

  // <interface>
  public AppData ensure(
    PdfName key
    )
  {
    AppData appData = get(key);
    if(appData == null)
    {
      getBaseDataObject().put(key, (appData = new AppData(getDocument())).getBaseObject());
      holder.touch(key);
    }
    return appData;
  }
  
  // <Map>
  @Override
  public void clear(
    )
  {getBaseDataObject().clear();}

  @Override
  public boolean containsKey(
    Object key
    )
  {return getBaseDataObject().containsKey(key);}

  @Override
  public boolean containsValue(
    Object value
    )
  {return getBaseDataObject().containsValue(value);}

  @Override
  public Set<Map.Entry<PdfName,AppData>> entrySet(
    )
  {
    Set<Map.Entry<PdfName,AppData>> entrySet = new HashSet<Map.Entry<PdfName,AppData>>();
    for(PdfName key : getBaseDataObject().keySet())
    {entrySet.add(new MapEntry<PdfName,AppData>(key, get(key)));}
    return entrySet;
  }

  @Override
  public AppData get(
    Object key
    )
  {return AppData.wrap(getBaseDataObject().get(key));}

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty();}

  @Override
  public Set<PdfName> keySet(
    )
  {return getBaseDataObject().keySet();}
  
  @Override
  public AppData put(
    PdfName key,
    AppData value
    )
  {throw new UnsupportedOperationException();}

  @Override
  public void putAll(
    Map<? extends PdfName,? extends AppData> collection
    )
  {throw new UnsupportedOperationException();}

  @Override
  public AppData remove(
    Object key
    )
  {return AppData.wrap(getBaseDataObject().remove(key));}

  @Override
  public int size(
    )
  {return getBaseDataObject().size();}

  @Override
  public Collection<AppData> values(
    )
  {
    Collection<AppData> values = new ArrayList<AppData>();
    for(PdfDirectObject valueObject : getBaseDataObject().values())
    {values.add(AppData.wrap(valueObject));}
    return values;
  }
  // </Map>
  // </interface>
  // </dynamic>
}

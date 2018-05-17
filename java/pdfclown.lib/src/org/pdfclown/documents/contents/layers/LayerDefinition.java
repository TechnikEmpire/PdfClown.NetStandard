/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.layers;

import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Optional content properties [PDF:1.7:4.10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 04/26/15
*/
@PDF(VersionEnum.PDF15)
public final class LayerDefinition
  extends PdfObjectWrapper<PdfDictionary>
  implements ILayerConfiguration
{
  // <static>
  // <interface>
  // <public>
  public static LayerDefinition wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new LayerDefinition(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public LayerDefinition(
    Document context
    )
  {super(context, new PdfDictionary());}

  private LayerDefinition(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public LayerDefinition clone(
    Document context
    )
  {return (LayerDefinition)super.clone(context);}

  /**
    Gets the layer configurations used under particular circumstances.
  */
  public Array<LayerConfiguration> getAlternateConfigurations(
    )
  {return Array.wrap(LayerConfiguration.class, getBaseDataObject().get(PdfName.Configs, PdfArray.class));}

  /**
    Gets the default layer configuration, that is the initial state of the optional content groups
    when a document is first opened.
  */
  public LayerConfiguration getDefaultConfiguration(
    )
  {return LayerConfiguration.wrap(getBaseDataObject().get(PdfName.D, PdfDictionary.class));}

  /**
    Gets the collection of all the layers existing in the document.
  */
  public Layers getLayers(
    )
  {return Layers.wrap(getBaseDataObject().get(PdfName.OCGs, PdfArray.class));}

  /**
    @see #getAlternateConfigurations()
  */
  public void setAlternateConfigurations(
    Array<LayerConfiguration> value
    )
  {getBaseDataObject().put(PdfName.Configs, value.getBaseObject());}

  /**
    @see #getDefaultConfiguration()
  */
  public void setDefaultConfiguration(
    LayerConfiguration value
    )
  {getBaseDataObject().put(PdfName.D, value.getBaseObject());}

  // <ILayerConfiguration>
  @Override
  public String getCreator(
    )
  {return getDefaultConfiguration().getCreator();}

  @Override
  public Set<PdfName> getIntents(
    )
  {return getDefaultConfiguration().getIntents();}
  
  @Override
  public Array<OptionGroup> getOptionGroups(
    )
  {return getDefaultConfiguration().getOptionGroups();}

  @Override
  public String getTitle(
    )
  {return getDefaultConfiguration().getTitle();}

  public UILayers getUILayers(
    )
  {return getDefaultConfiguration().getUILayers();}

  @Override
  public UIModeEnum getUIMode(
    )
  {return getDefaultConfiguration().getUIMode();}

  @Override
  public Boolean isVisible(
    )
  {return getDefaultConfiguration().isVisible();}

  @Override
  public void setCreator(
    String value
    )
  {getDefaultConfiguration().setCreator(value);}

  @Override
  public void setIntents(
    Set<PdfName> value
    )
  {getDefaultConfiguration().setIntents(value);}
  
  @Override
  public void setTitle(
    String value
    )
  {getDefaultConfiguration().setTitle(value);}

  @Override
  public void setUIMode(
    UIModeEnum value
    )
  {getDefaultConfiguration().setUIMode(value);}

  @Override
  public void setVisible(
    Boolean value
    )
  {getDefaultConfiguration().setVisible(value);}
  // </ILayerConfiguration>
  // </public>
  // </interface>
  // </dynamic>
}

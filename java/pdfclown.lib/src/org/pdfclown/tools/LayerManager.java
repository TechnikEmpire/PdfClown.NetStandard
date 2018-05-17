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

package org.pdfclown.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.PageAnnotations;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.PropertyList;
import org.pdfclown.documents.contents.PropertyListResources;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.XObjectResources;
import org.pdfclown.documents.contents.layers.Layer;
import org.pdfclown.documents.contents.layers.LayerConfiguration;
import org.pdfclown.documents.contents.layers.LayerDefinition;
import org.pdfclown.documents.contents.layers.LayerEntity;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentMarker;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.ContentPlaceholder;
import org.pdfclown.documents.contents.objects.MarkedContent;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.documents.interaction.annotations.Annotation;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReference;

/**
  Tool to manage layers (aka OCGs).
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/20/15
*/
public class LayerManager
{
  // <dynamic>
  // <interface>
  // <public>
  /**
    Removes the specified layers from the document.
    
    @param layers
      Layers to remove (they MUST belong to the same document).
  */
  public void remove(
    Layer... layers
    )
  {remove(false, layers);}

  /**
    Removes the specified layers from the document.
    
    @param preserveContent
      Whether the layer contents have to be flattened only.
    @param layers
      Layers to remove (they MUST belong to the same document).
  */
  public void remove(
    boolean preserveContent,
    Layer... layers
    )
  {
    Document document = layers[0].getDocument();

    // 1. Page contents.
    Set<Layer> removedLayers = new HashSet<Layer>(Arrays.asList(layers));
    Set<LayerEntity> layerEntities = new HashSet<LayerEntity>(removedLayers);
    Set<XObject> layerXObjects = new HashSet<XObject>();
    for(Page page : document.getPages())
    {removeLayerContents(page, removedLayers, layerEntities, layerXObjects, preserveContent);}

    // 2. Layer definitions.
    Set<PdfReference> removedLayerReferences = new HashSet<PdfReference>();
    for(Layer removedLayer : removedLayers)
    {removedLayerReferences.add((PdfReference)removedLayer.getBaseObject());}
    LayerDefinition layerDefinition = document.getLayer();
    // 2.1. Clean default layer configuration!
    removeLayerReferences(layerDefinition.getDefaultConfiguration(), removedLayerReferences);
    // 2.2. Clean alternate layer configurations!
    for(LayerConfiguration layerConfiguration : layerDefinition.getAlternateConfigurations())
    {removeLayerReferences(layerConfiguration, removedLayerReferences);}
    // 2.3. Clean global layer collection!
    removeLayerReferences(layerDefinition.getLayers().getBaseDataObject(), removedLayerReferences);

    // 3. Entities.
    // 3.1. Clean the xobjects!
    for(XObject xObject : layerXObjects)
    {
      if(preserveContent)
      {xObject.setLayer(null);}
      else
      {xObject.delete();}
    }
    // 3.2. Clean the layer entities!
    for(LayerEntity layerEntity : layerEntities)
    {layerEntity.delete();}

    // 4. Reference cleanup.
    Optimizer.removeOrphanedObjects(document.getFile());
  }
  // </public>

  // <private>
  private void removeLayerContents(
    Page page,
    Set<Layer> removedLayers,
    Set<LayerEntity> layerEntities,
    Set<XObject> layerXObjects,
    boolean preserveContent
    )
  {
    Resources pageResources = page.getResources();

    // Collect the page's layer entities containing the layers!
    Set<PdfName> layerEntityNames = new HashSet<PdfName>();
    PropertyListResources pagePropertyLists = pageResources.getPropertyLists();
    for(Map.Entry<PdfName,PropertyList> propertyListEntry : pagePropertyLists.entrySet())
    {
      if(!(propertyListEntry.getValue() instanceof LayerEntity))
        continue;

      LayerEntity layerEntity = (LayerEntity)propertyListEntry.getValue();
      if(layerEntities.contains(layerEntity))
      {layerEntityNames.add(propertyListEntry.getKey());}
      else
      {
        List<Layer> members = layerEntity.getVisibilityMembers();
        for(Layer removedLayer : removedLayers)
        {
          if(members.contains(removedLayer))
          {
            layerEntityNames.add(propertyListEntry.getKey());
            layerEntities.add(layerEntity);
            break;
          }
        }
      }
    }

    // Collect the page's xobjects associated to the layers!
    Set<PdfName> layerXObjectNames = new HashSet<PdfName>();
    XObjectResources pageXObjects = pageResources.getXObjects();
    for(Map.Entry<PdfName,XObject> xObjectEntry : pageXObjects.entrySet())
    {
      if(layerXObjects.contains(xObjectEntry.getValue()))
      {layerXObjectNames.add(xObjectEntry.getKey());}
      else
      {
        if(layerEntities.contains(xObjectEntry.getValue().getLayer()))
        {
          layerXObjectNames.add(xObjectEntry.getKey());
          layerXObjects.add(xObjectEntry.getValue());
          break;
        }
      }
    }

    // 1.1. Remove the layered contents from the page!
    if(!layerEntityNames.isEmpty() || (!preserveContent && !layerXObjectNames.isEmpty()))
    {
      ContentScanner scanner = new ContentScanner(page);
      removeLayerContents(scanner, layerEntityNames, layerXObjectNames, preserveContent);
      scanner.getContents().flush();
    }

    // 1.2. Clean the page's layer entities from the purged references!
    for(PdfName layerEntityName : layerEntityNames)
    {pagePropertyLists.remove(layerEntityName);}

    // 1.3. Clean the page's xobjects from the purged references!
    if(!preserveContent)
    {
      for(PdfName layerXObjectName : layerXObjectNames)
      {pageXObjects.remove(layerXObjectName);}
    }

    // 1.4. Clean the page's annotations!
    {
      PageAnnotations pageAnnotations = page.getAnnotations();
      for(int index = pageAnnotations.size() - 1; index >= 0; index--)
      {
        Annotation<?> annotation = pageAnnotations.get(index);
        if(layerEntities.contains(annotation.getLayer()))
        {
          if(preserveContent)
          {annotation.setLayer(null);}
          else
          {annotation.delete();}
        }
      }
    }
  }

  private void removeLayerContents(
    ContentScanner level,
    Set<PdfName> layerEntityNames,
    Set<PdfName> layerXObjectNames,
    boolean preserveContent
    )
  {
    if(level == null)
      return;

    while(level.moveNext())
    {
      ContentObject content = level.getCurrent();
      if(content instanceof MarkedContent)
      {
        MarkedContent markedContent = (MarkedContent)content;
        ContentMarker marker = (ContentMarker)markedContent.getHeader();
        if(PdfName.OC.equals(marker.getTag()) // NOTE: /OC tag identifies layer (aka optional content) markers.
          && layerEntityNames.contains(marker.getName()))
        {
          if(preserveContent)
          {
            level.setCurrent(new ContentPlaceholder(markedContent.getObjects())); // Replaces the layer marked content block with an anonymous container, preserving its contents.
          }
          else
          {
            level.remove(); // Removes the layer marked content block along with its contents.
            continue;
          }
        }
      }
      else if(!preserveContent && content instanceof org.pdfclown.documents.contents.objects.XObject)
      {
        org.pdfclown.documents.contents.objects.XObject xObject = (org.pdfclown.documents.contents.objects.XObject)content;
        if(layerXObjectNames.contains(xObject.getName()))
        {
          level.remove();
          continue;
        }
      }
      if(content instanceof ContainerObject)
      {
        // Scan the inner level!
        removeLayerContents(
          level.getChildLevel(),
          layerEntityNames,
          layerXObjectNames,
          preserveContent
          );
      }
    }
  }

  private void removeLayerReferences(
    LayerConfiguration layerConfiguration,
    Set<PdfReference> layerReferences
    )
  {
    if(layerConfiguration == null)
      return;

    PdfDictionary layerConfigurationDictionary = layerConfiguration.getBaseDataObject();
    PdfArray usageArrayObject = (PdfArray)layerConfigurationDictionary.resolve(PdfName.AS);
    if(usageArrayObject != null)
    {
      for(PdfDirectObject usageItemObject : usageArrayObject)
      {removeLayerReferences((PdfDictionary)usageItemObject.resolve(), PdfName.OCGs, layerReferences);}
    }
    removeLayerReferences(layerConfigurationDictionary, PdfName.Locked, layerReferences);
    removeLayerReferences(layerConfigurationDictionary, PdfName.OFF, layerReferences);
    removeLayerReferences(layerConfigurationDictionary, PdfName.ON, layerReferences);
    removeLayerReferences(layerConfigurationDictionary, PdfName.Order, layerReferences);
    removeLayerReferences(layerConfigurationDictionary, PdfName.RBGroups, layerReferences);
  }

  private void removeLayerReferences(
    PdfDictionary dictionaryObject,
    PdfName key,
    Set<PdfReference> layerReferences
    )
  {
    if(dictionaryObject == null)
      return;

    removeLayerReferences((PdfArray)dictionaryObject.resolve(key), layerReferences);
  }

  private void removeLayerReferences(
    PdfArray arrayObject,
    Set<PdfReference> layerReferences
    )
  {
    if(arrayObject == null)
      return;

    for(int index = arrayObject.size() - 1; index >= 0; index--)
    {
      PdfDataObject itemObject = arrayObject.get(index);
      if(itemObject instanceof PdfReference)
      {
        if(layerReferences.contains((PdfReference)itemObject))
        {
          arrayObject.remove(index);

          if(index < arrayObject.size())
          {
            PdfDataObject nextObject = arrayObject.resolve(index);
            if(nextObject instanceof PdfArray) // Children array.
            {arrayObject.remove(index);}
          }
          continue;
        }
        else
        {itemObject = itemObject.resolve();}
      }
      if(itemObject instanceof PdfArray)
      {removeLayerReferences((PdfArray)itemObject, layerReferences);}
    }
  }
  // </private>
  // </interface>
  // </dynamic>
}

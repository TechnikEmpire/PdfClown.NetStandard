/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library" (the
  Program): see the accompanying README files for more info.

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
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.layers;
using org.pdfclown.documents.contents.objects;
using xobjects = org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;

namespace org.pdfclown.tools
{
  /**
    <summary>Tool to manage layers (aka OCGs).</summary>
  */
  public class LayerManager
  {
    #region dynamic
    #region interface
    #region public
    /**
      <summary>Removes the specified layers from the document.</summary>
      <param name="layers">Layers to remove (they MUST belong to the same document).</param>
    */
    public void Remove(
      params Layer[] layers
      )
    {Remove(false, layers);}

    /**
      <summary>Removes the specified layers from the document.</summary>
      <param name="preserveContent">Whether the layer contents have to be flattened only.</param>
      <param name="layers">Layers to remove (they MUST belong to the same document).</param>
    */
    public void Remove(
      bool preserveContent,
      params Layer[] layers
      )
    {
      var document = layers[0].Document;

      // 1. Page contents.
      var removedLayers = new HashSet<Layer>(layers);
      var layerEntities = new HashSet<LayerEntity>(removedLayers);
      var layerXObjects = new HashSet<xobjects::XObject>();
      foreach(Page page in document.Pages)
      {RemoveLayerContents(page, removedLayers, layerEntities, layerXObjects, preserveContent);}

      // 2. Layer definitions.
      HashSet<PdfReference> removedLayerReferences = new HashSet<PdfReference>();
      foreach(var removedLayer in removedLayers)
      {removedLayerReferences.Add((PdfReference)removedLayer.BaseObject);}
      var layerDefinition = document.Layer;
      // 2.1. Clean default layer configuration!
      RemoveLayerReferences(layerDefinition.DefaultConfiguration, removedLayerReferences);
      // 2.2. Clean alternate layer configurations!
      foreach(var layerConfiguration in layerDefinition.AlternateConfigurations)
      {RemoveLayerReferences(layerConfiguration, removedLayerReferences);}
      // 2.3. Clean global layer collection!
      RemoveLayerReferences(layerDefinition.Layers.BaseDataObject, removedLayerReferences);

      // 3. Entities.
      // 3.1. Clean the xobjects!
      foreach(var xObject in layerXObjects)
      {
        if(preserveContent)
        {xObject.Layer = null;}
        else
        {xObject.Delete();}
      }
      // 3.2. Clean the layer entities!
      foreach(var layerEntity in layerEntities)
      {layerEntity.Delete();}

      // 4. Reference cleanup.
      Optimizer.RemoveOrphanedObjects(document.File);
    }
    #endregion

    #region private
    private void RemoveLayerContents(
      Page page,
      ICollection<Layer> removedLayers,
      ICollection<LayerEntity> layerEntities,
      ICollection<xobjects::XObject> layerXObjects,
      bool preserveContent
      )
    {
      var pageResources = page.Resources;

      // Collect the page's layer entities containing the layers!
      HashSet<PdfName> layerEntityNames = new HashSet<PdfName>();
      var pagePropertyLists = pageResources.PropertyLists;
      foreach(var propertyListEntry in pagePropertyLists)
      {
        if(!(propertyListEntry.Value is LayerEntity))
          continue;

        var layerEntity = (LayerEntity)propertyListEntry.Value;
        if(layerEntities.Contains(layerEntity))
        {layerEntityNames.Add(propertyListEntry.Key);}
        else
        {
          var members = layerEntity.VisibilityMembers;
          foreach(var removedLayer in removedLayers)
          {
            if(members.Contains(removedLayer))
            {
              layerEntityNames.Add(propertyListEntry.Key);
              layerEntities.Add(layerEntity);
              break;
            }
          }
        }
      }

      // Collect the page's xobjects associated to the layers!
      HashSet<PdfName> layerXObjectNames = new HashSet<PdfName>();
      var pageXObjects = pageResources.XObjects;
      foreach(var xObjectEntry in pageXObjects)
      {
        if(layerXObjects.Contains(xObjectEntry.Value))
        {layerXObjectNames.Add(xObjectEntry.Key);}
        else
        {
          if(layerEntities.Contains(xObjectEntry.Value.Layer))
          {
            layerXObjectNames.Add(xObjectEntry.Key);
            layerXObjects.Add(xObjectEntry.Value);
            break;
          }
        }
      }

      // 1.1. Remove the layered contents from the page!
      if(layerEntityNames.Count > 0 || (!preserveContent && layerXObjectNames.Count > 0))
      {
        var scanner = new ContentScanner(page);
        RemoveLayerContents(scanner, layerEntityNames, layerXObjectNames, preserveContent);
        scanner.Contents.Flush();
      }

      // 1.2. Clean the page's layer entities from the purged references!
      foreach(var layerEntityName in layerEntityNames)
      {pagePropertyLists.Remove(layerEntityName);}

      // 1.3. Clean the page's xobjects from the purged references!
      if(!preserveContent)
      {
        foreach(var layerXObjectName in layerXObjectNames)
        {pageXObjects.Remove(layerXObjectName);}
      }

      // 1.4. Clean the page's annotations!
      {
        var pageAnnotations = page.Annotations;
        for(int index = pageAnnotations.Count - 1; index >= 0; index--)
        {
          var annotation = pageAnnotations[index];
          if(layerEntities.Contains(annotation.Layer))
          {
            if(preserveContent)
            {annotation.Layer = null;}
            else
            {annotation.Delete();}
          }
        }
      }
    }

    private void RemoveLayerContents(
      ContentScanner level,
      ICollection<PdfName> layerEntityNames,
      ICollection<PdfName> layerXObjectNames,
      bool preserveContent
      )
    {
      if(level == null)
        return;

      while(level.MoveNext())
      {
        ContentObject content = level.Current;
        if(content is MarkedContent)
        {
          var markedContent = (MarkedContent)content;
          var marker = (ContentMarker)markedContent.Header;
          if(PdfName.OC.Equals(marker.Tag) // NOTE: /OC tag identifies layer (aka optional content) markers.
            && layerEntityNames.Contains(marker.Name))
          {
            if(preserveContent)
            {
              level.Current = new ContentPlaceholder(markedContent.Objects); // Replaces the layer marked content block with an anonymous container, preserving its contents.
            }
            else
            {
              level.Remove(); // Removes the layer marked content block along with its contents.
              continue;
            }
          }
        }
        else if(!preserveContent && content is XObject)
        {
          var xObject = (XObject)content;
          if(layerXObjectNames.Contains(xObject.Name))
          {
            level.Remove();
            continue;
          }
        }
        if(content is ContainerObject)
        {
          // Scan the inner level!
          RemoveLayerContents(
            level.ChildLevel,
            layerEntityNames,
            layerXObjectNames,
            preserveContent
            );
        }
      }
    }

    private void RemoveLayerReferences(
      LayerConfiguration layerConfiguration,
      ICollection<PdfReference> layerReferences
      )
    {
      if(layerConfiguration == null)
        return;

      var layerConfigurationDictionary = layerConfiguration.BaseDataObject;
      var usageArrayObject = (PdfArray)layerConfigurationDictionary.Resolve(PdfName.AS);
      if(usageArrayObject != null)
      {
        foreach(var usageItemObject in usageArrayObject)
        {RemoveLayerReferences((PdfDictionary)usageItemObject.Resolve(), PdfName.OCGs, layerReferences);}
      }
      RemoveLayerReferences(layerConfigurationDictionary, PdfName.Locked, layerReferences);
      RemoveLayerReferences(layerConfigurationDictionary, PdfName.OFF, layerReferences);
      RemoveLayerReferences(layerConfigurationDictionary, PdfName.ON, layerReferences);
      RemoveLayerReferences(layerConfigurationDictionary, PdfName.Order, layerReferences);
      RemoveLayerReferences(layerConfigurationDictionary, PdfName.RBGroups, layerReferences);
    }

    private void RemoveLayerReferences(
      PdfDictionary dictionaryObject,
      PdfName key,
      ICollection<PdfReference> layerReferences
      )
    {
      if(dictionaryObject == null)
        return;

      RemoveLayerReferences((PdfArray)dictionaryObject.Resolve(key), layerReferences);
    }

    private void RemoveLayerReferences(
      PdfArray arrayObject,
      ICollection<PdfReference> layerReferences
      )
    {
      if(arrayObject == null)
        return;

      for(int index = arrayObject.Count - 1; index >= 0; index--)
      {
        PdfDataObject itemObject = arrayObject[index];
        if(itemObject is PdfReference)
        {
          if(layerReferences.Contains((PdfReference)itemObject))
          {
            arrayObject.RemoveAt(index);

            if(index < arrayObject.Count)
            {
              var nextObject = arrayObject.Resolve(index);
              if(nextObject is PdfArray) // Children array.
              {arrayObject.RemoveAt(index);}
            }
            continue;
          }
          else
          {itemObject = itemObject.Resolve();}
        }
        if(itemObject is PdfArray)
        {RemoveLayerReferences((PdfArray)itemObject, layerReferences);}
      }
    }
    #endregion
    #endregion
    #endregion
  }
}


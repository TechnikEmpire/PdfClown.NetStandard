package org.pdfclown.samples.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.layers.IUILayerNode;
import org.pdfclown.documents.contents.layers.Layer;
import org.pdfclown.documents.contents.layers.LayerDefinition;
import org.pdfclown.documents.contents.layers.UILayers;
import org.pdfclown.files.File;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates how to edit existing layers.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/20/15
*/
public class LayerEditingSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    File file = null;
    try
    {
      // 1. Opening the PDF file...
      {
        String filePath = promptFileChoice("Please select a PDF file");
        try
        {file = new File(filePath);}
        catch(Exception e)
        {throw new RuntimeException(filePath + " file access error.",e);}
      }
      Document document = file.getDocument();

      // 2. Get the layer definition!
      LayerDefinition layerDefinition = document.getLayer();
      if(!layerDefinition.exists())
      {System.out.println("\nNo layer definition available.");}
      else
      {
        while(true)
        {
          List<Layer> layers = new ArrayList<Layer>();

          // 3.1. Show structured layers!
          System.out.println("\nLayer structure:\n");
          showUILayers(layerDefinition.getUILayers(), 0, layers);

          // 3.2. Show unstructured layers!
          boolean hiddenShown = false;
          for(Layer layer : layerDefinition.getLayers()) // NOTE: LayerDefinition.Layers comprises all the layers (both structured and unstructured).
          {
            if(!layers.contains(layer))
            {
              if(!hiddenShown)
              {
                System.out.println("Hidden layers (not displayed in the viewer panel)");
                hiddenShown = true;
              }

              showLayer(layer, layers.size(), " ");
              layers.add(layer);
            }
          }

          System.out.println("[Q] Exit");

          String choice;
          while(true)
          {
            choice = promptChoice("Choose a layer to remove:").toUpperCase();
            if("Q".equals(choice))
              break;
            else
            {
              int layerIndex;
              try
              {layerIndex = Integer.parseInt(choice);}
              catch(Exception e)
              {continue;}
              
              if(layerIndex < 0 || layerIndex >= layers.size())
                continue;

              System.out.println("\nWhat to do with the contents associated to the removed layer?");
              Map<String,String> contentRemovalOptions = new HashMap<String,String>();
              contentRemovalOptions.put("0", "Remove layered content");
              contentRemovalOptions.put("1", "Flatten layered content");
              int contentRemovalChoice;
              try
              {contentRemovalChoice = Integer.parseInt(promptChoice(contentRemovalOptions));}
              catch(Exception e)
              {contentRemovalChoice = 0;}

              // 4. Remove the chosen layer!
              layers.get(layerIndex).delete(contentRemovalChoice == 1);
              break;
            }
          }
          if("Q".equals(choice))
            break;
        }
        if(file.isUpdated())
        {serialize(file, "Layer editing", "removing layers", "layers, optional content");}
      }
    }
    finally
    {
      // 4. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }

  private void showLayer(
    IUILayerNode layerNode,
    int index,
    String indentation
    )
  {System.out.println(indentation + (layerNode instanceof Layer ? "[" + index + "] " : "") + "\"" + layerNode.getTitle() + "\" (" + layerNode.getClass().getSimpleName() + ")");}

  private void showUILayers(
    UILayers uiLayers,
    int level,
    List<Layer> layers
    )
  {
    String indentation = getIndentation(level);
    for(IUILayerNode layerNode : uiLayers)
    {
      showLayer(layerNode, layers.size(), indentation);

      if(layerNode instanceof Layer)
      {layers.add((Layer)layerNode);}

      showUILayers(layerNode.getChildren(), level + 1, layers);
    }
  }
}
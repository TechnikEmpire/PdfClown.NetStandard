package org.pdfclown.samples.cli;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.annotations.Widget;
import org.pdfclown.documents.interaction.forms.Field;
import org.pdfclown.documents.interaction.forms.Form;
import org.pdfclown.files.File;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to inspect the AcroForm fields</b> of a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.2.0, 04/08/15
*/
public class AcroFormParsingSample
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

      // 2. Get the acroform!
      Form form = document.getForm();
      if(!form.exists())
      {System.out.println("\nNo acroform available (AcroForm dictionary not found).");}
      else
      {
        System.out.println("\nIterating through the fields collection...\n");

        // 3. Showing the acroform fields...
        HashMap<String,Integer> objCounters = new HashMap<String,Integer>();
        for(Field field : form.getFields().values())
        {
          System.out.println("* Field '" + field.getFullName() + "' (" + field.getBaseObject() + ")");

          String typeName = field.getClass().getSimpleName();
          System.out.println("    Type: " + typeName);
          System.out.println("    Value: " + field.getValue());
          System.out.println("    Data: " + field.getBaseDataObject().toString());

          int widgetIndex = 0;
          for(Widget widget : field.getWidgets())
          {
            System.out.println("    Widget " + (++widgetIndex) + ":");
            Page widgetPage = widget.getPage();
            System.out.println("      Page: " + (widgetPage == null ? "undefined" : widgetPage.getNumber() + " (" + widgetPage.getBaseObject() + ")"));

            Rectangle2D widgetBox = widget.getBox();
            System.out.println("      Coordinates: {x:" + Math.round(widgetBox.getX()) + "; y:" + Math.round(widgetBox.getY()) + "; width:" + Math.round(widgetBox.getWidth()) + "; height:" + Math.round(widgetBox.getHeight()) + "}");
          }

          objCounters.put(typeName, (objCounters.containsKey(typeName) ? objCounters.get(typeName) : 0) + 1);
        }

        int fieldCount = form.getFields().size();
        if(fieldCount == 0)
        {System.out.println("No field available.");}
        else
        {
          System.out.println("\nFields partial counts (grouped by type):");
          for(Map.Entry<String,Integer> entry : objCounters.entrySet())
          {System.out.println(" " + entry.getKey() + ": " + entry.getValue());}
          System.out.println("Fields total count: " + fieldCount);
        }
      }
    }
    finally
    {
      // 4. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }
}
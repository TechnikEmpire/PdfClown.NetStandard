package org.pdfclown.samples.cli;

import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.NamedDestinations;
import org.pdfclown.documents.Names;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfString;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to inspect the object names</b> within a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.2.0, 04/08/15
*/
public class NamesParsingSample extends Sample
{
  @Override
  public void run()
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

      // 2. Named objects extraction.
      Names names = document.getNames();
      if(!names.exists())
      {System.out.println("\nNo names dictionary.");}
      else
      {
        System.out.println("\nNames dictionary found (" + names.getDataContainer().getReference() + ")");

        NamedDestinations namedDestinations = names.getDestinations();
        if(!namedDestinations.exists())
        {System.out.println("\nNo named destinations.");}
        else
        {
          System.out.println("\nNamed destinations found (" + namedDestinations.getDataContainer().getReference() + ")");

          // Parsing the named destinations...
          for(Map.Entry<PdfString,Destination> namedDestination : namedDestinations.entrySet())
          {
            PdfString key = namedDestination.getKey();
            Destination value = namedDestination.getValue();

            System.out.println("  Destination '" + key + "' (" + value.getDataContainer().getReference() + ")");

            System.out.print("    Target Page: number = ");
            Object pageRef = value.getPage();
            if(pageRef instanceof Integer) // NOTE: numeric page refs are typical of remote destinations.
            {System.out.println(((Integer)pageRef) + 1);}
            else // NOTE: explicit page refs are typical of local destinations.
            {
              Page page = (Page)pageRef;
              System.out.println(page.getNumber() + "; ID = " + ((PdfReference)page.getBaseObject()).getId());
            }
          }

          System.out.println("Named destinations count = " + namedDestinations.size());
        }
      }
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }
}

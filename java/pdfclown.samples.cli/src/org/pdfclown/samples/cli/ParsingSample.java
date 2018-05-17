package org.pdfclown.samples.cli;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.objects.CompositeObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.Operation;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.documents.interchange.metadata.Metadata;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.util.io.IOUtils;
import org.pdfclown.util.parsers.ParseException;

/**
  This sample demonstrates <b>how to inspect the structure of a PDF document</b>.
  <h3>Remarks</h3>
  <p>This sample is just a limited exercise: see the API documentation
  to exploit all the available access functionalities.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 04/08/15
*/
public class ParsingSample
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
        catch(ParseException e)
        {throw new RuntimeException(filePath + " file parsing failed.",e);}
        catch(Exception e)
        {throw new RuntimeException(filePath + " file access error.",e);}
      }
      Document document = file.getDocument();

      // 2. Parsing the document...
      // 2.1. Metadata.
      // 2.1.1. Basic metadata.
      System.out.println("\nDocument information:");
      Information info = document.getInformation();
      if(info.exists())
      {
        for(Map.Entry<PdfName,Object> infoEntry : info.entrySet())
        {System.out.println(infoEntry.getKey() + ": " + infoEntry.getValue());}
      }
      else
      {System.out.println("No information available (Info dictionary doesn't exist).");}

      // 2.1.2. Advanced metadata.
      System.out.println("\nDocument metadata (XMP):");
      Metadata metadata = document.getMetadata();
      if(metadata.exists())
      {
        try
        {
          org.w3c.dom.Document metadataContent = metadata.getContent();
          System.out.println(toString(metadataContent));
        }
        catch (Exception e)
        {System.out.println("Metadata extraction failed: " + e.getMessage());}
      }
      else
      {System.out.println("No metadata available (Metadata stream doesn't exist).");}

      System.out.println("\nIterating through the indirect-object collection (please wait)...");

      // 2.2. Counting the indirect objects, grouping them by type...
      Map<String,Integer> objCounters = new TreeMap<String,Integer>();
      objCounters.put("xref free entry",0);
      for(PdfIndirectObject object : file.getIndirectObjects())
      {
        if(object.isInUse()) // In-use entry.
        {
          PdfDataObject dataObject = object.getDataObject();
          String typeName = (dataObject != null ? dataObject.getClass().getSimpleName() : "empty entry");
          if(objCounters.containsKey(typeName))
          {objCounters.put(typeName, objCounters.get(typeName) + 1);}
          else
          {objCounters.put(typeName, 1);}
        }
        else // Free entry.
        {objCounters.put("xref free entry", objCounters.get("xref free entry") + 1);}
      }
      System.out.println("\nIndirect objects partial counts (grouped by PDF object type):");
      for(Map.Entry<String,Integer> entry : objCounters.entrySet())
      {System.out.println(" " + entry.getKey() + ": " + entry.getValue());}
      System.out.println("Indirect objects total count: " + file.getIndirectObjects().size());

      // 2.3. Showing some page information...
      Pages pages = document.getPages();
      int pageCount = pages.size();
      System.out.println("\nPage count: " + pageCount);

      int pageIndex = (int)Math.floor(pageCount / 2d);
      System.out.println("Mid page:");
      printPageInfo(pages.get(pageIndex),pageIndex);

      pageIndex++;
      if(pageIndex < pageCount)
      {
        System.out.println("Next page:");
        printPageInfo(pages.get(pageIndex),pageIndex);
      }
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }

  private void printPageInfo(
    Page page,
    int index
    )
  {
    // 1. Showing basic page information...
    System.out.println(" Index (calculated): " + page.getIndex() + " (should be " + index + ")");
    System.out.println(" ID: " + ((PdfReference)page.getBaseObject()).getId());
    PdfDictionary pageDictionary = page.getBaseDataObject();
    System.out.println(" Dictionary entries:");
    for(PdfName key : pageDictionary.keySet())
    {System.out.println("  " + key.getValue());}
    for(Map.Entry<PdfName,PdfDirectObject> entry : pageDictionary.entrySet())
    {System.out.println("  " + entry.getKey().getValue() + " = " + entry.getValue());}
    
    // 2. Showing page contents information...
    Contents contents = page.getContents();
    System.out.println(" Content objects count: " + contents.size());
    System.out.println(" Content head:");
    printContentObjects(contents,0,0);

    // 3. Showing page resources information...
    {
      Resources resources = page.getResources();
      System.out.println(" Resources:");
      Map<PdfName, ? extends PdfObjectWrapper<?>> subResources;

      subResources = resources.getFonts();
      if(subResources != null)
      {System.out.println("  Font count: " + subResources.size());}

      subResources = resources.getXObjects();
      if(subResources != null)
      {System.out.println("  XObjects count: " + subResources.size());}

      subResources = resources.getColorSpaces();
      if(subResources != null)
      {System.out.println("  ColorSpaces count: " + subResources.size());}
    }
  }

  private int printContentObjects(
    List<ContentObject> objects,
    int index,
    int level
    )
  {
    String indentation = getIndentation(level);
    for(ContentObject object : objects)
    {
      /*
        NOTE: Contents are expressed through both simple operations and composite objects.
      */
      if(object instanceof Operation)
      {System.out.println("   " + indentation + (++index) + ": " + object);}
      else if(object instanceof CompositeObject)
      {
        System.out.println(
          "   " + indentation + object.getClass().getSimpleName()
            + "\n   " + indentation + "{"
          );
        index = printContentObjects(((CompositeObject)object).getObjects(),index,level+1);
        System.out.println("   " + indentation + "}");
      }
      if(index > 9)
        break;
    }
    return index;
  }

  private String toString(
    org.w3c.dom.Document document
    )
  {
    try
    {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(document), new StreamResult(writer));
      return writer.getBuffer().toString();
    }
    catch(Exception e)
    {
      System.out.println("Metadata content extraction failed: " + e.getMessage());
      return "";
    }
  }
}
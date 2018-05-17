package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.InlineImage;
import org.pdfclown.documents.contents.xObjects.ImageXObject;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.files.File;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to retrieve the precise position (page and coordinates)
  of each image</b> within a PDF document, using the page content scanning functionality.
  <h3>Remarks</h3>
  <p>This sample leverages the ContentScanner class, a powerful device for accessing
  each single content object within a page.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 04/08/15
*/
public class ContentScanningSample
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

      // 2. Parsing the document...
      System.out.println("\nLooking for images...");
      for(Page page : document.getPages())
      {
        scan(
          new ContentScanner(page), // Wraps the page contents into the scanner.
          page
          );
      }
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }

  /**
    Scans a content level looking for images.
  */
  /*
    NOTE: Page contents are represented by a sequence of content objects,
    possibly nested into multiple levels.
  */
  private void scan(
    ContentScanner level,
    Page page
    )
  {
    if(level == null)
      return;

    while(level.moveNext())
    {
      ContentObject object = level.getCurrent();
      if(object instanceof ContainerObject)
      {
        // Scan the inner level!
        scan(
          level.getChildLevel(),
          page
          );
      }
      else
      {
        ContentScanner.GraphicsObjectWrapper<?> objectWrapper = level.getCurrentWrapper();
        if(objectWrapper == null)
          continue;

        /*
          NOTE: Images can be represented on a page either as
          external objects (XObject) or inline objects.
        */
        Dimension2D imageSize = null; // Image native size.
        if(objectWrapper instanceof ContentScanner.XObjectWrapper)
        {
          ContentScanner.XObjectWrapper xObjectWrapper = (ContentScanner.XObjectWrapper)objectWrapper;
          XObject xObject = xObjectWrapper.getXObject();
          // Is the external object an image?
          if(xObject instanceof ImageXObject)
          {
            System.out.print(
              "External Image '" + xObjectWrapper.getName() + "' (" + xObject.getBaseObject() + ")" // Image key and indirect reference.
              );
            imageSize = xObject.getSize(); // Image native size.
          }
        }
        else if(objectWrapper instanceof ContentScanner.InlineImageWrapper)
        {
          System.out.print("Inline Image");
          InlineImage inlineImage = ((ContentScanner.InlineImageWrapper)objectWrapper).getInlineImage();
          imageSize = inlineImage.getSize(); // Image native size.
        }

        if(imageSize != null)
        {
          Rectangle2D box = objectWrapper.getBox(); // Image position (location and size) on the page.
          System.out.println(
            " on page " + page.getNumber() + " (" + page.getBaseObject() + ")" // Page index and indirect reference.
            );
          System.out.println("  Coordinates:");
          System.out.println("     x: " + Math.round(box.getX()));
          System.out.println("     y: " + Math.round(box.getY()));
          System.out.println("     width: " + Math.round(box.getWidth()) + " (native: " + Math.round(imageSize.getWidth()) + ")");
          System.out.println("     height: " + Math.round(box.getHeight()) + " (native: " + Math.round(imageSize.getHeight()) + ")");
        }
      }
    }
  }
}
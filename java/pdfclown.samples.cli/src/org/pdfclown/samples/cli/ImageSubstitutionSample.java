package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.XObjectResources;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.xObjects.ImageXObject;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfName;

/**
  This sample demonstrates <b>how to replace images</b> appearing in a PDF document's pages
  through their resource names.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 11/30/12
*/
public class ImageSubstitutionSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. Opening the PDF file...
    File file;
    {
      String filePath = promptFileChoice("Please select a PDF file");
      try
      {file = new File(filePath);}
      catch(Exception e)
      {throw new RuntimeException(filePath + " file access error.",e);}
    }
    Document document = file.getDocument();

    // 2. Replace the images!
    replaceImages(document);

    // 3. Serialize the PDF file!
    serialize(file, "Image substitution", "substituting a document's images", "image replacement");
  }

  private void replaceImages(
    Document document
    )
  {
    // Get the image used to replace existing ones!
    Image image = Image.get(getResourcePath("images" + java.io.File.separator + "gnu.jpg")); // Image is an abstract entity, as it still has to be included into the pdf document.
    // Add the image to the document!
    XObject imageXObject = image.toXObject(document); // XObject (i.e. external object) is, in PDF spec jargon, a reusable object.
    // Looking for images to replace...
    for(Page page : document.getPages())
    {
      Resources resources = page.getResources();
      XObjectResources xObjects = resources.getXObjects();
      if(xObjects == null)
        continue;

      for(PdfName xObjectKey : xObjects.keySet())
      {
        XObject xObject = xObjects.get(xObjectKey);
        // Is the page's resource an image?
        if(xObject instanceof ImageXObject)
        {
          System.out.println("Substituting " + xObjectKey + " image xobject.");
          xObjects.put(xObjectKey,imageXObject);
        }
      }
//TODO
//      for(Map.Entry<PdfName,XObject> xObjectEntry : xObjects.entrySet())
//      {
//        if(xObjectEntry.getValue() instanceof ImageXObject)
//        {
//          System.out.println("Substituting " + xObjectEntry.getKey() + " image xobject.");
//          xObjectEntry.setValue(imageXObject);
//        }
//      }
    }
  }
}
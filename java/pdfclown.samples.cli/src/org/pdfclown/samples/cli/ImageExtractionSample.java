package org.pdfclown.samples.cli;

import org.pdfclown.bytes.IBuffer;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to extract XObject images</b> from a PDF document.
  <h3>Remarks</h3>
  <p>Inline images are ignored.</p>
  <p>XObject images other than JPEG aren't currently supported for handling.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2.1, 04/08/15
*/
public class ImageExtractionSample
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

      // 2. Iterating through the indirect object collection...
      int index = 0;
      for(PdfIndirectObject indirectObject : file.getIndirectObjects())
      {
        // Get the data object associated to the indirect object!
        PdfDataObject dataObject = indirectObject.getDataObject();
        // Is this data object a stream?
        if(dataObject instanceof PdfStream)
        {
          PdfDictionary header = ((PdfStream)dataObject).getHeader();
          // Is this stream an image?
          if(header.containsKey(PdfName.Type)
            && header.get(PdfName.Type).equals(PdfName.XObject)
            && header.get(PdfName.Subtype).equals(PdfName.Image))
          {
            // Which kind of image?
            if(header.get(PdfName.Filter).equals(PdfName.DCTDecode)) // JPEG image.
            {
              // Get the image data (keeping it encoded)!
              IBuffer body = ((PdfStream)dataObject).getBody(false);
              // Export the image!
              exportImage(
                body,
                "ImageExtractionSample_" + (index++) + ".jpg"
                );
            }
            else // Unsupported image.
            {System.out.println("Image XObject " + indirectObject.getReference() + " couldn't be extracted (filter: " + header.get(PdfName.Filter) + ")");}
          }
        }
      }
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }

  private void exportImage(
    IBuffer data,
    String filename
    )
  {
    java.io.File outputFile = new java.io.File(getOutputPath(filename));
    java.io.BufferedOutputStream outputStream;
    try
    {
      outputFile.createNewFile();
      outputStream = new java.io.BufferedOutputStream(
        new java.io.FileOutputStream(outputFile)
        );
    }
    catch(Exception e)
    {throw new RuntimeException(outputFile.getPath() + " file couldn't be created.",e);}

    try
    {outputStream.write(data.toByteArray());}
    catch(Exception e)
    {throw new RuntimeException(outputFile.getPath() + " file writing has failed.",e);}
    finally
    {IOUtils.close(outputStream);}

    System.out.println("Output: " + outputFile.getPath());
  }
}
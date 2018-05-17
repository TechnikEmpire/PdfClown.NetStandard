package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfStream;

/**
  This sample demonstrates how to move stream data outside PDF files and keep external references to
  them; it demonstrates also the inverse process (reimporting stream data from external files).
  <p>Note that, due to security concerns, external streams are a discouraged feature which is often
  unsupported on third-party viewers and disabled by default on recent Adobe Acrobat versions; in
  the latter case, in order to bypass restrictions and allow access to external streams, users have
  to enable Enhanced Security from the Preferences dialog, specifying privileged locations.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 09/24/12
*/
public class StreamExternalizationSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. Externalizing the streams...
    String externalizedFilePath;
    {
      // 1.1. Opening the PDF file...
      File file;
      {
        String filePath = promptFileChoice("Please select a PDF file");
        try
        {file = new File(filePath);}
        catch(Exception e)
        {throw new RuntimeException(filePath + " file access error.",e);}
      }
      Document document = file.getDocument();
      /*
        NOTE: As we are going to export streams using paths relative to the output path, it's
        necessary to ensure they are properly resolved (otherwise they will be written relative to
        the current user directory).
      */
      file.setPath(getOutputPath());

      // 1.2. Iterating through the indirect objects to externalize streams...
      int filenameIndex = 0;
      for(PdfIndirectObject indirectObject : file.getIndirectObjects())
      {
        PdfDataObject dataObject = indirectObject.getDataObject();
        if(dataObject instanceof PdfStream)
        {
          PdfStream stream = (PdfStream)dataObject;
          if(stream.getDataFile() == null) // Internal stream to externalize.
          {
            stream.setDataFile(
              FileSpecification.get(
                document,
                getClass().getSimpleName() + "-external" + filenameIndex++
                ),
              true // Forces the stream data to be transferred to the external location.
              );
          }
        }
      }

      // 1.3. Serialize the PDF file!
      externalizedFilePath = serialize(file, SerializationModeEnum.Standard);
    }

    // 2. Reimporting the externalized streams...
    {
      // 2.1. Opening the PDF file...
      File file;
      try
      {file = new File(externalizedFilePath);}
      catch(Exception e)
      {throw new RuntimeException(externalizedFilePath + " file access error.",e);}

      // 2.2. Iterating through the indirect objects to internalize streams...
      for(PdfIndirectObject indirectObject : file.getIndirectObjects())
      {
        PdfDataObject dataObject = indirectObject.getDataObject();
        if(dataObject instanceof PdfStream)
        {
          PdfStream stream = (PdfStream)dataObject;
          if(stream.getDataFile() != null) // External stream to internalize.
          {
            stream.setDataFile(
              null,
              true // Forces the stream data to be transferred to the internal location.
              );
          }
        }
      }

      // 2.3. Serialize the PDF file!
      String externalizedFileName = new java.io.File(externalizedFilePath).getName();
      String internalizedFilePath = externalizedFileName.substring(0, externalizedFileName.indexOf(".pdf")) + "-reimported.pdf";
      serialize(file, internalizedFilePath, SerializationModeEnum.Standard);
    }
  }
}
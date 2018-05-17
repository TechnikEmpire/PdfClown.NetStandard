package org.pdfclown.samples.cli;

import java.util.Map;

import org.pdfclown.bytes.IBuffer;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.files.EmbeddedFile;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.files.FullFileSpecification;
import org.pdfclown.documents.interaction.annotations.Annotation;
import org.pdfclown.documents.interaction.annotations.FileAttachment;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfString;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates how to extract attachments from a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2.1, 04/08/15
*/
public class AttachmentExtractionSample
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

      // 2. Extracting attachments...
      // 2.1. Embedded files (document level).
      for(Map.Entry<PdfString,FileSpecification<?>> entry : document.getNames().getEmbeddedFiles().entrySet())
      {evaluateDataFile(entry.getValue());}

      // 2.2. File attachments (page level).
      for(Page page : document.getPages())
      {
        for(Annotation<?> annotation : page.getAnnotations())
        {
          if(annotation instanceof FileAttachment)
          {evaluateDataFile(((FileAttachment)annotation).getDataFile());}
        }
      }
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }

  private void evaluateDataFile(
    FileSpecification<?> dataFile
    )
  {
    if(dataFile instanceof FullFileSpecification)
    {
      EmbeddedFile embeddedFile = ((FullFileSpecification)dataFile).getEmbeddedFile();
      if(embeddedFile != null)
      {exportAttachment(embeddedFile.getData(), dataFile.getPath());}
    }
  }

  private void exportAttachment(
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
    {
      outputStream.write(data.toByteArray());
      outputStream.close();
    }
    catch(Exception e)
    {throw new RuntimeException(outputFile.getPath() + " file writing has failed.",e);}

    System.out.println("Output: " + outputFile.getPath());
  }
}
package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.files.File;
import org.pdfclown.tools.FormFlattener;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to flatten the AcroForm fields</b> of a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.2.0
  @version 0.2.0, 04/08/15
*/
public class AcroFormFlatteningSample
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

      // 2. Flatten the form!
      FormFlattener formFlattener = new FormFlattener();
      formFlattener.flatten(document);
      
      // 3. Serialize the PDF file!
      serialize(file);
    }
    finally
    {
      // 4. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }
}
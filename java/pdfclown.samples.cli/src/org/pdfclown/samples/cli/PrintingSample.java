package org.pdfclown.samples.cli;

import java.awt.print.PrinterException;

import org.pdfclown.files.File;
import org.pdfclown.tools.Renderer;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to print a PDF document</b>.
  <p>Note: printing is currently in pre-alpha stage; therefore this sample is
  nothing but an initial stub (no assumption to work!).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2.1, 04/08/15
*/
public class PrintingSample
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

      // 2. Printing the document...
      Renderer renderer = new Renderer();
      boolean silent = false;
      try
      {
        if(renderer.print(file.getDocument(), silent))
        {System.out.println("Print fulfilled.");}
        else
        {System.out.println("Print discarded.");}
      }
      catch(PrinterException e)
      {System.out.println("Print failed: " + e.getMessage());}
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }
}

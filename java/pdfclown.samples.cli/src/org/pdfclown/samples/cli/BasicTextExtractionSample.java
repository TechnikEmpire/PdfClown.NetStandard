package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.ShowText;
import org.pdfclown.documents.contents.objects.Text;
import org.pdfclown.files.File;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates the <b>low-level way to extract text</b> from a PDF document.
  <h3>Remarks</h3>
  <p>In order to obtain richer information about the extracted text content,
  see the other available samples ({@link TextInfoExtractionSample}, {@link AdvancedTextExtractionSample}).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2.1, 04/08/15
*/
public class BasicTextExtractionSample
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

      // 2. Text extraction from the document pages.
      for(Page page : document.getPages())
      {
        if(!promptNextPage(page, false))
        {
          quit();
          break;
        }

        extract(
          new ContentScanner(page) // Wraps the page contents into a scanner.
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
    Scans a content level looking for text.
  */
  /*
    NOTE: Page contents are represented by a sequence of content objects,
    possibly nested into multiple levels.
  */
  private void extract(
    ContentScanner level
    )
  {
    if(level == null)
      return;

    while(level.moveNext())
    {
      ContentObject content = level.getCurrent();
      if(content instanceof ShowText)
      {
        Font font = level.getState().getFont();
        // Extract the current text chunk, decoding it!
        System.out.println(font.decode(((ShowText)content).getText()));
      }
      else if(content instanceof Text
        || content instanceof ContainerObject)
      {
        // Scan the inner level!
        extract(level.getChildLevel());
      }
    }
  }
}

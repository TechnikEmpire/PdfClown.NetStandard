package org.pdfclown.samples.cli;

import java.net.URI;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.actions.GoToLocal;
import org.pdfclown.documents.interaction.actions.GoToURI;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.files.File;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to apply actions</b> to a PDF document.
  <p>In this case on document-opening a go-to-page-2 action is triggered;
  then on page-2-opening a go-to-URI action is triggered.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 04/08/15
*/
public class ActionSample
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
      Page page = document.getPages().get(1); // Page 2 (zero-based index).

      // 2. Applying actions...
      // 2.1. Local go-to.
      /*
        NOTE: This statement instructs the PDF viewer to go to page 2 on document opening.
      */
      document.getActions().setOnOpen(
        new GoToLocal(
          document,
          new LocalDestination(page)
          )
        );

      // 2.2. Remote go-to.
      try
      {
        /*
          NOTE: This statement instructs the PDF viewer to navigate to the given URI on page 2
          opening.
        */
        page.getActions().setOnOpen(
          new GoToURI(
            document,
            new URI("http://www.sourceforge.net/projects/clown")
            )
          );
      }
      catch(Exception exception)
      {throw new RuntimeException("Remote goto failed.",exception);}

      // 3. Serialize the PDF file!
      serialize(file, "Actions", "applying actions", "actions, creation, local goto, remote goto");
    }
    finally
    {
      // 4. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }
}
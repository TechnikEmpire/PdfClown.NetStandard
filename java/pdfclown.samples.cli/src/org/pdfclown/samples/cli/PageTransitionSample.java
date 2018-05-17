package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.navigation.page.Transition;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to apply visual transitions to the pages</b> of a PDF document.
  <h3>Remarks</h3>
  <p>To watch the transition effects applied to the document, you typically have to select
  the presentation (full screen) view mode on your PDF viewer (for example Adobe Reader).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 11/30/12
*/
public class PageTransitionSample
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

    // 2. Applying the visual transitions...
    Transition.StyleEnum[] transitionStyles = Transition.StyleEnum.values();
    int transitionStylesLength = transitionStyles.length;
    for(Page page : document.getPages())
    {
      // Apply a transition to the page!
      page.setTransition(
        new Transition(
          document,
          transitionStyles[(int)(Math.random()*(transitionStylesLength))], // NOTE: Random selection of the transition is done here just for demonstrative purposes; in real world, you would obviously choose only the appropriate enumeration constant among those available.
          .5 // Transition duration (half a second).
          )
        );
      // Set the display time of the page on presentation!
      page.setDuration(2); // Page display duration (2 seconds).
    }

    // 3. Serialize the PDF file!
    serialize(file, "Transition", "applying visual transitions to pages", "page transition");
  }
}
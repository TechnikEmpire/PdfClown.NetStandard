using org.pdfclown.documents;
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.interaction.navigation.page;
using org.pdfclown.files;
using org.pdfclown.tools;

using System;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to apply visual transitions to the pages of a PDF document.</summary>
    <remarks>To watch the transition effects applied to the document, you typically have to select
    the presentation (full screen) view mode on your PDF viewer (for example Adobe Reader).</remarks>
  */
  public class PageTransitionSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Opening the PDF file...
      string filePath = PromptFileChoice("Please select a PDF file");
      using(var file = new File(filePath))
      {
        Document document = file.Document;
  
        // 2. Applying the visual transitions...
        Transition.StyleEnum[] transitionStyles = (Transition.StyleEnum[])Enum.GetValues(typeof(Transition.StyleEnum));
        int transitionStylesLength = transitionStyles.Length;
        Random random = new Random();
        foreach(Page page in document.Pages)
        {
          // Apply a transition to the page!
          page.Transition = new Transition(
            document,
            transitionStyles[random.Next(transitionStylesLength)], // NOTE: Random selection of the transition is done here just for demonstrative purposes; in real world, you would obviously choose only the appropriate enumeration constant among those available.
            .5 // Transition duration (half a second).
            );
          // Set the display time of the page on presentation!
          page.Duration = 2; // Page display duration (2 seconds).
        }
  
        // 3. Serialize the PDF file!
        Serialize(file, "Transition", "applying visual transitions to pages", "page transition");
      }
    }
  }
}
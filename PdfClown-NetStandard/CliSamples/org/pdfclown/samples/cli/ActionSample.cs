using org.pdfclown.documents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.entities;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.interaction.actions;
using org.pdfclown.documents.interaction.navigation.document;
using org.pdfclown.files;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to apply actions to a document.</summary>
    <remarks>In this case, on document-opening a go-to-page-2 action is triggered;
    then on page-2-opening a go-to-URI action is triggered.</remarks>
  */
  public class ActionSample
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
        Page page = document.Pages[1]; // Page 2 (zero-based index).

        // 2. Applying actions...
        // 2.1. Local go-to.
        /*
          NOTE: This statement instructs the PDF viewer to go to page 2 on document opening.
        */
        document.Actions.OnOpen = new GoToLocal(
          document,
          new LocalDestination(page) // Page 2 (zero-based index).
          );

        // 2.2. Remote go-to.
        /*
          NOTE: This statement instructs the PDF viewer to navigate to the given URI on page 2
          opening.
        */
        page.Actions.OnOpen = new GoToURI(
          document,
          new Uri("http://www.sourceforge.net/projects/clown")
          );

        // 3. Serialize the PDF file!
        Serialize(file, "Actions", "applying actions", "actions, creation, local goto, remote goto");
      }
    }
  }
}
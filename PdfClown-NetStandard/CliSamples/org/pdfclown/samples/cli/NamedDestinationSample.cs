using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.objects;
using org.pdfclown.documents.interaction.actions;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.documents.interaction.navigation.document;
using org.pdfclown.files;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to manipulate the named destinations within a PDF document.</summary>
  */
  public class NamedDestinationSample
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
        Pages pages = document.Pages;
  
        // 2. Inserting page destinations...
        NamedDestinations destinations = document.Names.Destinations;
        /*
          NOTE: Here we are registering page 1 multiple times to test tree structure sorting and splitting.
        */
        Destination page1Destination = new LocalDestination(pages[0]);
        destinations[new PdfString("d31e1142")] = page1Destination;
        destinations[new PdfString("Z1")] = page1Destination;
        destinations[new PdfString("d38e1142")] = page1Destination;
        destinations[new PdfString("B84afaba8")] = page1Destination;
        destinations[new PdfString("z38e1142")] = page1Destination;
        destinations[new PdfString("d3A8e1142")] = page1Destination;
        destinations[new PdfString("f38e1142")] = page1Destination;
        destinations[new PdfString("B84afaba6")] = page1Destination;
        destinations[new PdfString("d3a8e1142")] = page1Destination;
        destinations[new PdfString("Z38e1142")] = page1Destination;
        if(pages.Count > 1)
        {
          LocalDestination page2Destination = new LocalDestination(pages[1], Destination.ModeEnum.FitHorizontal, 0, null);
          destinations[new PdfString("N84afaba6")] = page2Destination;

          // Let the viewer go to the second page on document opening!
          /*
            NOTE: Any time a named destination is applied, its name is retrieved and used as reference.
          */
          document.Actions.OnOpen = new GoToLocal(
            document,
            page2Destination // Its name ("N84afaba6") is retrieved behind the scenes.
            );
          // Define a link to the second page on the first one!
          new Link(
            pages[0],
            new RectangleF(0,0,100,50),
            "Link annotation",
            page2Destination // Its name ("N84afaba6") is retrieved behind the scenes.
            );

          if(pages.Count > 2)
          {destinations[new PdfString("1845505298")] = new LocalDestination(pages[2], Destination.ModeEnum.XYZ, new PointF(50, Single.NaN), null);}
        }
  
        // 3. Serialize the PDF file!
        Serialize(file, "Named destinations", "manipulating named destinations", "named destinations, creation");
      }
    }
  }
}
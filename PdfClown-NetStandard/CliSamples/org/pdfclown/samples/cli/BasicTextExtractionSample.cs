using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.objects;
using org.pdfclown.files;
using org.pdfclown.tools;

using System;
using System.Collections.Generic;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates the low-level way to extract text from a PDF document.</summary>
    <remarks>In order to obtain richer information about the extracted text content,
    see the other available samples (<see cref="TextInfoExtractionSample"/>,
    <see cref="AdvancedTextExtractionSample"/>).</remarks>
  */
  public class BasicTextExtractionSample
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

        // 2. Text extraction from the document pages.
        foreach(Page page in document.Pages)
        {
          if(!PromptNextPage(page, false))
          {
            Quit();
            break;
          }

          Extract(
            new ContentScanner(page) // Wraps the page contents into a scanner.
            );
        }
      }
    }

    /**
      <summary>Scans a content level looking for text.</summary>
    */
    /*
      NOTE: Page contents are represented by a sequence of content objects,
      possibly nested into multiple levels.
    */
    private void Extract(
      ContentScanner level
      )
    {
      if(level == null)
        return;

      while(level.MoveNext())
      {
        ContentObject content = level.Current;
        if(content is ShowText)
        {
          Font font = level.State.Font;
          // Extract the current text chunk, decoding it!
          Console.WriteLine(font.Decode(((ShowText)content).Text));
        }
        else if(content is Text
          || content is ContainerObject)
        {
          // Scan the inner level!
          Extract(level.ChildLevel);
        }
      }
    }
  }
}
using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.objects;
using org.pdfclown.documents.interaction.navigation.page;
using org.pdfclown.files;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to define, read and modify page labels.</summary>
  */
  public class PageLabelSample
    : Sample
  {
    public override void Run(
      )
    {
      string outputFilePath;
      {
        // 1. Opening the PDF file...
        string filePath = PromptFileChoice("Please select a PDF file");
        using(var file = new File(filePath))
        {
          Document document = file.Document;

          // 2. Defining the page labels...
          PageLabels pageLabels = document.PageLabels;
          pageLabels.Clear();
          /*
            NOTE: This sample applies labels to arbitrary page ranges: no sensible connection with their
            actual content has therefore to be expected.
          */
          int pageCount = document.Pages.Count;
          pageLabels[new PdfInteger(0)] = new PageLabel(document, "Introduction ", PageLabel.NumberStyleEnum.UCaseRomanNumber, 5);
          if(pageCount > 3)
          {pageLabels[new PdfInteger(3)] = new PageLabel(document, PageLabel.NumberStyleEnum.UCaseLetter);}
          if(pageCount > 6)
          {pageLabels[new PdfInteger(6)] = new PageLabel(document, "Contents ", PageLabel.NumberStyleEnum.ArabicNumber, 0);}

          // 3. Serialize the PDF file!
          outputFilePath = Serialize(file, "Page labelling", "labelling a document's pages", "page labels");
        }
      }

      {
        using(var file = new File(outputFilePath))
        {
          foreach(KeyValuePair<PdfInteger,PageLabel> entry in file.Document.PageLabels)
          {
            Console.WriteLine("Page label " + entry.Value.BaseObject);
            Console.WriteLine("    Initial page: " + (entry.Key.IntValue + 1));
            Console.WriteLine("    Prefix: " + (entry.Value.Prefix));
            Console.WriteLine("    Number style: " + (entry.Value.NumberStyle));
            Console.WriteLine("    Number base: " + (entry.Value.NumberBase));
          }
        }
      }
    }
  }
}
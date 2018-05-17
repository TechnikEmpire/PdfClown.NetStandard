using org.pdfclown.documents;
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.files;
using org.pdfclown.tools;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to stamp the page number on alternated corners
    of an existing PDF document's pages.</summary>
    <remarks>Stamping is just one of the several ways PDF contents can be manipulated using PDF Clown:
    contents can be inserted as (raw) data chunks, mid-level content objects, external forms, etc.</remarks>
  */
  public class PageNumberingSample
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
  
        // 2. Stamp the document!
        Stamp(document);
  
        // 3. Serialize the PDF file!
        Serialize(file, "Page numbering", "numbering a document's pages", "page numbering");
      }
    }

    private void Stamp(
      Document document
      )
    {
      // 1. Instantiate the stamper!
      /* NOTE: The PageStamper is optimized for dealing with pages. */
      PageStamper stamper = new PageStamper();

      // 2. Numbering each page...
      StandardType1Font font = new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Courier,
        true,
        false
        );
      DeviceRGBColor redColor = DeviceRGBColor.Get(System.Drawing.Color.Red);
      int margin = 32;
      foreach(Page page in document.Pages)
      {
        // 2.1. Associate the page to the stamper!
        stamper.Page = page;

        // 2.2. Stamping the page number on the foreground...
        {
          PrimitiveComposer foreground = stamper.Foreground;

          foreground.SetFont(font,16);
          foreground.SetFillColor(redColor);

          SizeF pageSize = page.Size;
          int pageNumber = page.Number;
          bool pageIsEven = (pageNumber % 2 == 0);
          foreground.ShowText(
            pageNumber.ToString(),
            new PointF(
              (pageIsEven
                ? margin
                : pageSize.Width - margin),
              pageSize.Height - margin
              ),
            (pageIsEven
              ? XAlignmentEnum.Left
              : XAlignmentEnum.Right),
            YAlignmentEnum.Bottom,
            0
            );
        }

        // 2.3. End the stamping!
        stamper.Flush();
      }
    }
  }
}
using org.pdfclown.documents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.files;
using org.pdfclown.util.math.geom;

using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to reuse a PDF page as a form (precisely: form XObject
    [PDF:1.6:4.9]).</summary>
    <remarks>Form XObjects are a convenient way to represent contents multiple times on multiple pages
    as templates.</remarks>
  */
  public class PageToFormSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Opening the form source file...
      string filePath = PromptFileChoice("Please select a PDF file to use as form");
      using(var formFile = new File(filePath))
      {
        // 2. Instantiate a new PDF file!
        File file = new File();
        Document document = file.Document;
  
        // 3. Convert the first page of the source file into a form inside the new document!
        XObject form = formFile.Document.Pages[0].ToXObject(document);
  
        // 4. Insert the contents into the new document!
        Populate(document,form);
  
        // 5. Serialize the PDF file!
        Serialize(file, "Page-to-form", "converting a page to a reusable form", "page to form");
      }
    }

    /**
      <summary>Populates a PDF file with contents.</summary>
    */
    private void Populate(
      Document document,
      XObject form
      )
    {
      // 1. Add a page to the document!
      Page page = new Page(document); // Instantiates the page inside the document context.
      document.Pages.Add(page); // Puts the page in the pages collection.

      // 2. Create a content composer for the content stream!
      PrimitiveComposer composer = new PrimitiveComposer(page);

      // 3. Inserting contents...
      SizeF pageSize = page.Size;
      // 3.1. Showing the form on the page...
      {
        SizeF formSize = form.Size;
        // Form 1.
        composer.ShowXObject(
          form,
          new PointF(pageSize.Width/2,pageSize.Height/2),
          GeomUtils.Scale(formSize, new SizeF(300,0)),
          XAlignmentEnum.Center,
          YAlignmentEnum.Middle,
          45
          );
        // Form 2.
        composer.ShowXObject(
          form,
          new PointF(0,pageSize.Height),
          GeomUtils.Scale(formSize, new SizeF(0,300)),
          XAlignmentEnum.Left,
          YAlignmentEnum.Bottom,
          0
          );
        // Form 3.
        composer.ShowXObject(
          form,
          new PointF(pageSize.Width,pageSize.Height),
          new SizeF(80,200),
          XAlignmentEnum.Right,
          YAlignmentEnum.Bottom,
          0
          );
      }
      // 3.2. Showing the comments on the page...
      {
        BlockComposer blockComposer = new BlockComposer(composer);
        RectangleF frame = new RectangleF(
          18,
          18,
          pageSize.Width * .5f,
          pageSize.Height * .5f
          );
        blockComposer.Begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Top);
        StandardType1Font bodyFont = new StandardType1Font(
          document,
          StandardType1Font.FamilyEnum.Courier,
          true,
          false
          );
        composer.SetFont(bodyFont,24);
        blockComposer.ShowText("Page-to-form sample");
        SizeF breakSize = new SizeF(0,8);
        blockComposer.ShowBreak(breakSize);
        composer.SetFont(bodyFont,8);
        blockComposer.ShowText("This sample shows how to convert a page to a reusable form that can be placed multiple times on other pages scaling, rotating, anchoring and aligning it.");
        blockComposer.ShowBreak(breakSize);
        blockComposer.ShowText("On this page you can see some of the above-mentioned transformations:");
        breakSize.Width = 8;
        blockComposer.ShowBreak(breakSize);
        blockComposer.ShowText("1. anchored to the center of the page, rotated by 45 degrees counterclockwise, 300 point wide (preserved proportions);"); blockComposer.ShowBreak(breakSize);
        blockComposer.ShowText("2. anchored to the bottom-left corner of the page, 300 point high (preserved proportions);"); blockComposer.ShowBreak(breakSize);
        blockComposer.ShowText("3. anchored to the bottom-right of the page, 80 point wide and 200 point high (altered proportions).");
        blockComposer.End();
      }

      // 4. Flush the contents into the content stream!
      composer.Flush();
    }
  }
}
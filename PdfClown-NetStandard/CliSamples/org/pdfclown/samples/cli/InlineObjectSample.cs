using org.pdfclown.documents;
using org.pdfclown.documents.contents.composition;
using entities = org.pdfclown.documents.contents.entities;
using org.pdfclown.documents.contents.fonts;
using files = org.pdfclown.files;

using System;
using System.Drawing;
using System.IO;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to embed an image object within a PDF content
    stream.</summary>
    <remarks>
      <para>Inline objects should be used sparingly, as they easily clutter content
      streams.</para>
      <para>The alternative (and preferred) way to insert an image object is via external
      object (XObject); its main advantage is to allow content reuse.</para>
    </remarks>
  */
  public class InlineObjectSample
    : Sample
  {
    private const float Margin = 36;

    public override void Run(
      )
    {
      // 1. PDF file instantiation.
      files::File file = new files::File();
      Document document = file.Document;

      // 2. Content creation.
      Populate(document);

      // 3. Serialize the PDF file!
      Serialize(file, "Inline image", "embedding an image within a content stream", "inline image");
    }

    private void Populate(
      Document document
      )
    {
      Page page = new Page(document);
      document.Pages.Add(page);
      SizeF pageSize = page.Size;

      PrimitiveComposer composer = new PrimitiveComposer(page);
      {
        BlockComposer blockComposer = new BlockComposer(composer);
        blockComposer.Hyphenation = true;
        blockComposer.Begin(
          new RectangleF(
            Margin,
            Margin,
            (float)pageSize.Width - Margin * 2,
            (float)pageSize.Height - Margin * 2
            ),
          XAlignmentEnum.Justify,
          YAlignmentEnum.Top
          );
        StandardType1Font bodyFont = new StandardType1Font(
          document,
          StandardType1Font.FamilyEnum.Courier,
          true,
          false
          );
        composer.SetFont(bodyFont,32);
        blockComposer.ShowText("Inline image sample"); blockComposer.ShowBreak();
        composer.SetFont(bodyFont,16);
        blockComposer.ShowText("Showing the GNU logo as an inline image within the page content stream.");
        blockComposer.End();
      }
      // Showing the 'GNU' image...
      {
        // Instantiate a jpeg image object!
        entities::Image image = entities::Image.Get(GetResourcePath("images" + Path.DirectorySeparatorChar + "gnu.jpg")); // Abstract image (entity).
        // Set the position of the image in the page!
        composer.ApplyMatrix(200,0,0,200,(pageSize.Width-200)/2,(pageSize.Height-200)/2);
        // Show the image!
        image.ToInlineObject(composer); // Transforms the image entity into an inline image within the page.
      }
      composer.Flush();
    }
  }
}
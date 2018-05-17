using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using colors = org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using fonts = org.pdfclown.documents.contents.fonts;
using org.pdfclown.files;

using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to obtain the actual area occupied by text shown in a PDF page.</summary>
  */
  public class TextFrameSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Instantiate a new PDF file!
      File file = new File();
      Document document = file.Document;

      // 2. Insert the contents into the document!
      Populate(document);

      // 3. Serialize the PDF file!
      Serialize(file, "Text frame", "getting the actual bounding box of text shown", "text frames");
    }

    /**
      <summary>Populates a PDF file with contents.</summary>
    */
    private void Populate(
      Document document
      )
    {
      // 1. Add the page to the document!
      Page page = new Page(document); // Instantiates the page inside the document context.
      document.Pages.Add(page); // Puts the page in the pages collection.

      // 2. Create a content composer for the page!
      PrimitiveComposer composer = new PrimitiveComposer(page);

      colors::Color textColor = new colors::DeviceRGBColor(115 / 255d, 164 / 255d, 232 / 255d);
      composer.SetFillColor(textColor);
      composer.SetLineDash(new LineDash(new double[]{10}));
      composer.SetLineWidth(.25);

      BlockComposer blockComposer = new BlockComposer(composer);
      blockComposer.Begin(new RectangleF(300, 400, 200, 100), XAlignmentEnum.Left, YAlignmentEnum.Middle);
      composer.SetFont(new fonts::StandardType1Font(document, fonts::StandardType1Font.FamilyEnum.Times, false, true), 12);
      blockComposer.ShowText("PrimitiveComposer.ShowText(...) methods return the actual bounding box of the text shown, allowing to precisely determine its location on the page.");
      blockComposer.End();

      // 3. Inserting contents...
      // Set the font to use!
      composer.SetFont(new fonts::StandardType1Font(document, fonts::StandardType1Font.FamilyEnum.Courier, true, false), 72);
      composer.DrawPolygon(
        composer.ShowText(
          "Text frame",
          new PointF(150, 360),
          XAlignmentEnum.Left,
          YAlignmentEnum.Middle,
          45
          ).Points
        );
      composer.Stroke();

      composer.SetFont(fonts::Font.Get(document, GetResourcePath("fonts" + System.IO.Path.DirectorySeparatorChar + "Ruritania-Outline.ttf")), 102);
      composer.DrawPolygon(
        composer.ShowText(
          "Text frame",
          new PointF(250, 600),
          XAlignmentEnum.Center,
          YAlignmentEnum.Middle,
          -25
          ).Points
        );
      composer.Stroke();

      // 4. Flush the contents into the page!
      composer.Flush();
    }
  }
}
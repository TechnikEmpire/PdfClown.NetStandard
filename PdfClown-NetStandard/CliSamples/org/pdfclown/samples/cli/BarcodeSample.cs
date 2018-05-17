using org.pdfclown.documents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.entities;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.files;
using org.pdfclown.util.math.geom;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to show bar codes in a PDF document.</summary>
  */
  public class BarcodeSample
    : Sample
  {
    private const float Margin = 36;

    public override void Run(
      )
    {
      // 1. PDF file instantiation.
      File file = new File();
      Document document = file.Document;

      // 2. Content creation.
      Populate(document);

      // 3. Serialize the PDF file!
      Serialize(file, "Barcode", "showing barcodes", "barcodes, creation, EAN13");
    }

    /**
      <summary>Populates a PDF file with contents.</summary>
    */
    private void Populate(
      Document document
      )
    {
      // Get the abstract barcode entity!
      EAN13Barcode barcode = new EAN13Barcode("8012345678901");
      // Create the reusable barcode within the document!
      XObject barcodeXObject = barcode.ToXObject(document);

      Pages pages = document.Pages;
      // Page 1.
      {
        Page page = new Page(document);
        pages.Add(page);
        SizeF pageSize = page.Size;

        PrimitiveComposer composer = new PrimitiveComposer(page);
        {
          BlockComposer blockComposer = new BlockComposer(composer);
          blockComposer.Hyphenation = true;
          blockComposer.Begin(
            new RectangleF(
              Margin,
              Margin,
              pageSize.Width - Margin * 2,
              pageSize.Height - Margin * 2
              ),
            XAlignmentEnum.Left,
            YAlignmentEnum.Top
            );
          StandardType1Font bodyFont = new StandardType1Font(
            document,
            StandardType1Font.FamilyEnum.Courier,
            true,
            false
            );
          composer.SetFont(bodyFont,32);
          blockComposer.ShowText("Barcode sample"); blockComposer.ShowBreak();
          composer.SetFont(bodyFont,16);
          blockComposer.ShowText("Showing the EAN-13 Bar Code on different compositions:"); blockComposer.ShowBreak();
          blockComposer.ShowText("- page 1: on the lower right corner of the page, 100pt wide;"); blockComposer.ShowBreak();
          blockComposer.ShowText("- page 2: on the middle of the page, 1/3-page wide, 25 degree counterclockwise rotated;"); blockComposer.ShowBreak();
          blockComposer.ShowText("- page 3: filled page, 90 degree clockwise rotated."); blockComposer.ShowBreak();
          blockComposer.End();
        }

        // Show the barcode!
        composer.ShowXObject(
          barcodeXObject,
          new PointF(pageSize.Width - Margin, pageSize.Height - Margin),
          GeomUtils.Scale(barcodeXObject.Size, new SizeF(100,0)),
          XAlignmentEnum.Right,
          YAlignmentEnum.Bottom,
          0
          );
        composer.Flush();
      }

      // Page 2.
      {
        Page page = new Page(document);
        pages.Add(page);
        SizeF pageSize = page.Size;

        PrimitiveComposer composer = new PrimitiveComposer(page);
        // Show the barcode!
        composer.ShowXObject(
          barcodeXObject,
          new PointF(pageSize.Width / 2, pageSize.Height / 2),
          GeomUtils.Scale(barcodeXObject.Size, new SizeF(pageSize.Width / 3, 0)),
          XAlignmentEnum.Center,
          YAlignmentEnum.Middle,
          25
          );
        composer.Flush();
      }

      // Page 3.
      {
        Page page = new Page(document);
        pages.Add(page);
        SizeF pageSize = page.Size;

        PrimitiveComposer composer = new PrimitiveComposer(page);
        // Show the barcode!
        composer.ShowXObject(
          barcodeXObject,
          new PointF(pageSize.Width / 2, pageSize.Height / 2),
          new SizeF(pageSize.Height, pageSize.Width),
          XAlignmentEnum.Center,
          YAlignmentEnum.Middle,
          -90
          );
        composer.Flush();
      }
    }
  }
}
package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.entities.EAN13Barcode;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.files.File;
import org.pdfclown.util.math.geom.Dimension;
import org.pdfclown.util.math.geom.GeomUtils;

/**
  This sample demonstrates <b>how to show bar codes</b> in a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.6
  @version 0.1.2, 11/30/12
*/
public class BarcodeSample
  extends Sample
{
  private static final float Margin = 36;

  @Override
  public void run(
    )
  {
    // 1. PDF file instantiation.
    File file = new File();
    Document document = file.getDocument();

    // 2. Content creation.
    populate(document);

    // 3. Serialize the PDF file!
    serialize(file, "Barcode", "showing barcodes", "barcodes, creation, EAN13");
  }
  // </ISample>
  // </public>

  // <private>
  /**
    Populates a PDF file with contents.
  */
  private void populate(
    Document document
    )
  {
    // Get the abstract barcode entity!
    EAN13Barcode barcode = new EAN13Barcode("8012345678901");
    // Create the reusable barcode within the document!
    XObject barcodeXObject = barcode.toXObject(document);

    Pages pages = document.getPages();
    // Page 1.
    {
      Page page = new Page(document);
      pages.add(page);
      Dimension2D pageSize = page.getSize();

      PrimitiveComposer composer = new PrimitiveComposer(page);
      {
        BlockComposer blockComposer = new BlockComposer(composer);
        blockComposer.setHyphenation(true);
        blockComposer.begin(
          new Rectangle2D.Double(
            Margin,
            Margin,
            pageSize.getWidth() - Margin * 2,
            pageSize.getHeight() - Margin * 2
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
        composer.setFont(bodyFont,32);
        blockComposer.showText("Barcode sample"); blockComposer.showBreak();
        composer.setFont(bodyFont,16);
        blockComposer.showText("Showing the EAN-13 Bar Code on different compositions:"); blockComposer.showBreak();
        blockComposer.showText("- page 1: on the lower right corner of the page, 100pt wide;"); blockComposer.showBreak();
        blockComposer.showText("- page 2: on the middle of the page, 1/3-page wide, 25 degree counterclockwise rotated;"); blockComposer.showBreak();
        blockComposer.showText("- page 3: filled page, 90 degree clockwise rotated."); blockComposer.showBreak();
        blockComposer.end();
      }

      // Show the barcode!
      composer.showXObject(
        barcodeXObject,
        new Point2D.Double(pageSize.getWidth() - Margin, pageSize.getHeight() - Margin),
        GeomUtils.scale(barcodeXObject.getSize(), new Dimension(100,0)),
        XAlignmentEnum.Right,
        YAlignmentEnum.Bottom,
        0
        );
      composer.flush();
    }

    // Page 2.
    {
      Page page = new Page(document);
      pages.add(page);
      Dimension2D pageSize = page.getSize();

      PrimitiveComposer composer = new PrimitiveComposer(page);
      // Show the barcode!
      composer.showXObject(
        barcodeXObject,
        new Point2D.Double(pageSize.getWidth() / 2, pageSize.getHeight() / 2),
        GeomUtils.scale(barcodeXObject.getSize(), new Dimension(pageSize.getWidth()/3,0)),
        XAlignmentEnum.Center,
        YAlignmentEnum.Middle,
        25
        );
      composer.flush();
    }

    // Page 3.
    {
      Page page = new Page(document);
      pages.add(page);
      Dimension2D pageSize = page.getSize();

      PrimitiveComposer composer = new PrimitiveComposer(page);
      // Show the barcode!
      composer.showXObject(
        barcodeXObject,
        new Point2D.Double(pageSize.getWidth() / 2, pageSize.getHeight() / 2),
        new Dimension(pageSize.getHeight(),pageSize.getWidth()),
        XAlignmentEnum.Center,
        YAlignmentEnum.Middle,
        -90
        );
      composer.flush();
    }
  }
  // </private>
  // </interface>
  // </dynamic>
}
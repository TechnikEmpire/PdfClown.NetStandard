package org.pdfclown.samples.cli;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to obtain the actual area occupied by text</b>
  shown in a PDF page.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
public class TextFrameSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. Instantiate a new PDF file!
    File file = new File();
    Document document = file.getDocument();

    // 2. Insert the contents into the document!
    populate(document);

    // 3. Serialize the PDF file!
    serialize(file, "Text frame", "getting the actual bounding box of text shown", "text frames");
  }

  /**
    Populates a PDF file with contents.
  */
  private void populate(
    Document document
    )
  {
    // 1. Add the page to the document!
    Page page = new Page(document); // Instantiates the page inside the document context.
    document.getPages().add(page); // Puts the page in the pages collection.

    // 2. Create a content composer for the page!
    PrimitiveComposer composer = new PrimitiveComposer(page);

    Color<?> textColor = new DeviceRGBColor(115 / 255d, 164 / 255d, 232 / 255d);
    composer.setFillColor(textColor);
    composer.setLineDash(new LineDash(new double[]{10}));
    composer.setLineWidth(.25);

    BlockComposer blockComposer = new BlockComposer(composer);
    blockComposer.begin(new Rectangle2D.Double(300, 400, 200, 100), XAlignmentEnum.Left, YAlignmentEnum.Middle);
    composer.setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Times, false, true), 12);
    blockComposer.showText("PrimitiveComposer.showText(...) methods return the actual bounding box of the text shown, allowing to precisely determine its location on the page.");
    blockComposer.end();

    // 3. Inserting contents...
    // Set the font to use!
    composer.setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Courier, true, false), 72);
    composer.drawPolygon(
      composer.showText(
        "Text frame",
        new Point2D.Double(150, 360),
        XAlignmentEnum.Left,
        YAlignmentEnum.Middle,
        45
        ).getPoints()
      );
    composer.stroke();

    composer.setFont(Font.get(document, getResourcePath("fonts" + java.io.File.separator + "Ruritania-Outline.ttf")), 102);
    composer.drawPolygon(
      composer.showText(
        "Text frame",
        new Point2D.Double(250, 600),
        XAlignmentEnum.Center,
        YAlignmentEnum.Middle,
        -25
        ).getPoints()
      );
    composer.stroke();

    // 4. Flush the contents into the page!
    composer.flush();
  }
}
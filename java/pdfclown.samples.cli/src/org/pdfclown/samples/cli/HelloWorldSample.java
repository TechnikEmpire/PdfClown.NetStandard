package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.LineCapEnum;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;

/**
  This sample is a <b>minimalist introduction to the use of PDF Clown</b>.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 03/12/15
*/
public class HelloWorldSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. Instantiate a new PDF file!
    /* NOTE: a File object is the low-level (syntactic) representation of a PDF file. */
    File file = new File();

    // 2. Get its corresponding document!
    /* NOTE: a Document object is the high-level (semantic) representation of a PDF file. */
    Document document = file.getDocument();

    // 3. Insert the contents into the document!
    populate(document);

    // 4. Serialize the PDF file!
    serialize(file, "Hello world", "a simple 'hello world'", "Hello world");
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
    Dimension2D pageSize = page.getSize();

    // 2. Create a content composer for the page!
    PrimitiveComposer composer = new PrimitiveComposer(page);

    // 3. Inserting contents...
    // Set the font to use!
    composer.setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Courier, true, false), 30);
    // Show the text onto the page (along with its box)!
    /*
      NOTE: PrimitiveComposer's showText() method is the most basic way to add text to a page -- see
      BlockComposer for more advanced uses (horizontal and vertical alignment, hyphenation, etc.).
    */
    composer.showText(
      "Hello World!",
      new Point2D.Double(32, 48)
      );
    
    composer.setLineWidth(.25);
    composer.setLineCap(LineCapEnum.Round);
    composer.setLineDash(new LineDash(new double[]{5, 10}));
    composer.setTextLead(1.2);
    composer.drawPolygon(
      composer.showText(
        "This is a primitive example"
          + "\nof centered, rotated multi-"
          + "\nline text."
          + "\n\n\tWe recommend you to use"
          + "\nBlockComposer instead, as it"
          + "\nautomatically manages text"
          + "\nwrapping and alignment with-"
          + "\nin a specified area!",
        new Point2D.Double(pageSize.getWidth() / 2, pageSize.getHeight() / 2),
        XAlignmentEnum.Center,
        YAlignmentEnum.Middle, 
        15
        ).getPoints()
      );
    composer.stroke();

    // 4. Flush the contents into the page!
    composer.flush();
  }
}
package org.pdfclown.samples.cli;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;
import org.pdfclown.tools.PageStamper;

/**
  This sample demonstrates <b>how to stamp the page number</b> on alternated corners
  of an existing PDF document's pages.
  <h3>Remarks</h3>
  <p>Stamping is just one of the several ways PDF contents can be manipulated using PDF Clown:
  contents can be inserted as (raw) data chunks, mid-level content objects, external forms,
  etc.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 1/24/15
*/
public class PageNumberingSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. Opening the PDF file...
    File file;
    {
      String filePath = promptFileChoice("Please select a PDF file");
      try
      {file = new File(filePath);}
      catch(Exception e)
      {throw new RuntimeException(filePath + " file access error.",e);}
    }
    Document document = file.getDocument();

    // 2. Stamp the document!
    stamp(document);

    // 3. Serialize the PDF file!
    serialize(file, "Page numbering", "numbering a document's pages", "page numbering");
  }

  private void stamp(
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
    DeviceRGBColor redColor = DeviceRGBColor.get(Color.RED);
    int margin = 32;
    for(Page page : document.getPages())
    {
      // 2.1. Associate the page to the stamper!
      stamper.setPage(page);

      // 2.2. Stamping the page number on the foreground...
      {
        PrimitiveComposer foreground = stamper.getForeground();

        foreground.setFont(font,16);
        foreground.setFillColor(redColor);

        Dimension2D pageSize = page.getSize();
        int pageNumber = page.getNumber();
        boolean pageIsEven = (pageNumber % 2 == 0);
        foreground.showText(
          Integer.toString(pageNumber),
          new Point2D.Double(
            (pageIsEven
              ? margin
              : pageSize.getWidth() - margin),
            pageSize.getHeight() - margin
            ),
          (pageIsEven
            ? XAlignmentEnum.Left
            : XAlignmentEnum.Right),
          YAlignmentEnum.Bottom,
          0
          );
      }

      // 2.3. End the stamping!
      stamper.flush();
    }
  }
}
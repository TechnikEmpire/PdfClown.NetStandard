package org.pdfclown.samples.cli;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.files.File;
import org.pdfclown.util.io.IOUtils;
import org.pdfclown.util.math.geom.GeomUtils;

/**
  This sample demonstrates <b>how to reuse a PDF page as a form</b> (precisely: form XObject [PDF:1.6:4.9]).
  <p>Form XObjects are a convenient way to represent contents multiple times on multiple pages as
  templates.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.5
  @version 0.1.2.1, 04/08/15
*/
public class PageToFormSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    File formFile = null;
    try
    {
      // 1. Opening the form source file...
      {
        String filePath = promptFileChoice("Please select a PDF file to use as form");
        try
        {formFile = new File(filePath);}
        catch(Exception e)
        {throw new RuntimeException(filePath + " file access error.",e);}
      }

      // 2. Instantiate a new PDF file!
      File file = new File();
      Document document = file.getDocument();

      // 3. Convert the first page of the source file into a form inside the new document!
      XObject form = formFile.getDocument().getPages().get(0).toXObject(document);

      // 4. Insert the contents into the new document!
      populate(document,form);

      // 5. Serialize the PDF file!
      serialize(file, "Page-to-form", "converting a page to a reusable form", "page to form");
    }
    finally
    {
      // 6. Closing the PDF file...
      IOUtils.closeQuietly(formFile);
    }
  }

  /**
    Populates a PDF file with contents.
  */
  private void populate(
    Document document,
    XObject form
    )
  {
    // 1. Add a page to the document!
    Page page = new Page(document); // Instantiates the page inside the document context.
    document.getPages().add(page); // Puts the page in the pages collection.

    // 2. Create a content composer for the content stream!
    PrimitiveComposer composer = new PrimitiveComposer(page);

    // 3. Inserting contents...
    Dimension2D pageSize = page.getSize();
    // 3.1. Showing the form on the page...
    {
      Dimension2D formSize = form.getSize();
      // Form 1.
      composer.showXObject(
        form,
        new Point2D.Double(pageSize.getWidth()/2,pageSize.getHeight()/2),
        GeomUtils.scale(formSize, new Dimension(300,0)),
        XAlignmentEnum.Center,
        YAlignmentEnum.Middle,
        45
        );
      // Form 2.
      composer.showXObject(
        form,
        new Point2D.Double(0,pageSize.getHeight()),
        GeomUtils.scale(formSize, new Dimension(0,300)),
        XAlignmentEnum.Left,
        YAlignmentEnum.Bottom,
        0
        );
      // Form 3.
      composer.showXObject(
        form,
        new Point2D.Double(pageSize.getWidth(),pageSize.getHeight()),
        new Dimension(80,200),
        XAlignmentEnum.Right,
        YAlignmentEnum.Bottom,
        0
        );
    }
    // 3.2. Showing the comments on the page...
    {
      BlockComposer blockComposer = new BlockComposer(composer);
      blockComposer.begin(
        new Rectangle2D.Double(
          18,
          18,
          pageSize.getWidth() * .5,
          pageSize.getHeight() * .5
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
      composer.setFont(bodyFont,24);
      blockComposer.showText("Page-to-form sample");
      Dimension2D breakSize = new Dimension(0,8);
      blockComposer.showBreak(breakSize);
      composer.setFont(bodyFont,8);
      blockComposer.showText("This sample shows how to convert a page to a reusable form that can be placed multiple times on other pages scaling, rotating, anchoring and aligning it.");
      blockComposer.showBreak(breakSize);
      blockComposer.showText("On this page you can see some of the above-mentioned transformations:");
      breakSize.setSize(8,8);
      blockComposer.showBreak(breakSize);
      blockComposer.showText("1. anchored to the center of the page, rotated by 45 degrees counterclockwise, 300 point wide (preserved proportions);"); blockComposer.showBreak(breakSize);
      blockComposer.showText("2. anchored to the bottom-left corner of the page, 300 point high (preserved proportions);"); blockComposer.showBreak(breakSize);
      blockComposer.showText("3. anchored to the bottom-right corner of the page, 80 point wide and 200 point high (altered proportions).");
      blockComposer.end();
    }

    // 4. Flush the contents into the content stream!
    composer.flush();
  }
}
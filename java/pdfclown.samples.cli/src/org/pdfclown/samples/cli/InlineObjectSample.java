package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to embed an image object</b> within a PDF content stream.
  <h3>Remarks</h3>
  <p>Inline objects should be used sparingly, as they easily clutter content streams.</p>
  <p>The alternative (and preferred) way to insert an image object is via external object
  (XObject); its main advantage is to allow content reuse.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 09/24/12
*/
public class InlineObjectSample
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
    serialize(file, "Inline image", "embedding an image within a content stream", "inline image");
  }

  private void populate(
    Document document
    )
  {
    Page page = new Page(document);
    document.getPages().add(page);
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
        XAlignmentEnum.Justify,
        YAlignmentEnum.Top
        );
      StandardType1Font bodyFont = new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Courier,
        true,
        false
        );
      composer.setFont(bodyFont,32);
      blockComposer.showText("Inline image sample"); blockComposer.showBreak();
      composer.setFont(bodyFont,16);
      blockComposer.showText("Showing the GNU logo as an inline image within the page content stream.");
      blockComposer.end();
    }
    // Showing the 'GNU' image...
    {
      // Instantiate the image!
      Image image = Image.get(getResourcePath("images" + java.io.File.separator + "gnu.jpg"));
      // Set the position of the image in the page!
      composer.applyMatrix(200,0,0,200,(pageSize.getWidth()-200)/2,(pageSize.getHeight()-200)/2);
      // Show the image!
      image.toInlineObject(composer); // Transforms the image entity into an inline image within the page.
    }
    composer.flush();
  }
}
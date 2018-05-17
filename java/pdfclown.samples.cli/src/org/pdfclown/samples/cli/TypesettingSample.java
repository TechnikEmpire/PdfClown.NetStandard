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
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.files.File;

/**
  This sample concentrates on proper <b>fitting of styled text</b> within a given PDF page area (block frame),
  from the beginning of "Alice in Wonderland", Chapter 1 ("Down the Rabbit-Hole").

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 11/30/12
*/
public class TypesettingSample
  extends Sample
{
  private static final int Margin_X = 50;
  private static final int Margin_Y = 50;

  @Override
  public void run(
    )
  {
    // 1. PDF file instantiation.
    File file = new File();
    Document document = file.getDocument();

    // 2. Content creation.
    build(document);

    // 3. Serialize the PDF file!
    serialize(file, "Typesetting", "demonstrating how to add style to contents", "typesetting");
  }

  private void build(
    Document document
    )
  {
    // Add a page to the document!
    Page page = new Page(document); // Instantiates the page inside the document context.
    document.getPages().add(page); // Puts the page in the pages collection.

    Dimension2D pageSize = page.getSize();

    // Create a content composer for the page!
    /*
      NOTE: There are several ways to add contents to a content stream:
      - adding content objects directly to the Contents collection;
      - adding content objects through a ContentScanner instance;
      - invoking basic drawing functions through a PrimitiveComposer instance;
      - invoking advanced static-positioning functions through a BlockComposer instance;
      - invoking advanced dynamic-positioning functions through a FlowComposer instance (currently not implemented yet).
    */
    PrimitiveComposer composer = new PrimitiveComposer(page);
    // Wrap the content composer within a block filter!
    /*
      NOTE: The block filter is a basic typesetter. It exposes higher-level graphical
      functionalities (horizontal/vertical alignment, indentation, paragraph composition etc.)
      leveraging the content composer primitives.
      It's important to note that this is just an intermediate abstraction layer of the typesetting
      stack: further abstract levels could sit upon it, allowing the convenient treatment of
      typographic entities like titles, paragraphs, columns, tables, headers, footers etc.
      When such further abstract levels are available, the final user (developer of consuming
      applications) won't care any more of the details you can see here in the following code lines
      (such as bothering to select the first-letter font...).
    */
    BlockComposer blockComposer = new BlockComposer(composer);

    composer.beginLocalState();

    // Define the block frame that will encompass our contents on the page canvas!
    Rectangle2D frame = new Rectangle2D.Double(
      Margin_X,
      Margin_Y,
      pageSize.getWidth() - Margin_X * 2,
      pageSize.getHeight() - Margin_Y * 2
      );
    // Begin the title block!
    blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
    Font decorativeFont = Font.get(
      document,
      getResourcePath("fonts" + java.io.File.separator + "Ruritania-Outline.ttf")
      );
    composer.setFont(decorativeFont,56);
    blockComposer.showText("Chapter 1");
    blockComposer.showBreak();
    composer.setFont(decorativeFont,32);
    blockComposer.showText("Down the Rabbit-Hole");
    // End the title block!
    blockComposer.end();

    // Update the block frame to begin after the title!
    frame = new Rectangle2D.Double(
      blockComposer.getBoundBox().getX(),
      blockComposer.getBoundBox().getY() + blockComposer.getBoundBox().getHeight(),
      blockComposer.getBoundBox().getWidth(),
      pageSize.getHeight() - Margin_Y - (blockComposer.getBoundBox().getY() + blockComposer.getBoundBox().getHeight())
      );
    // Begin the body block!
    blockComposer.begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
    Font bodyFont = Font.get(
      document,
      getResourcePath("fonts" + java.io.File.separator + "TravelingTypewriter.otf")
      );
    composer.setFont(bodyFont,14);
    composer.beginLocalState();
    composer.setFont(decorativeFont,28);
    blockComposer.showText("A");
    composer.end();
    blockComposer.showText("lice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice 'without pictures or conversation?'");
    // Define new-paragraph first-line offset!
    Dimension breakSize = new Dimension(24,8); // Indentation (24pt) and top margin (8pt).
    // Begin a new paragraph!
    blockComposer.showBreak(breakSize);
    blockComposer.showText("So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.");
    // Begin a new paragraph!
    blockComposer.showBreak(breakSize);
    blockComposer.showText("There was nothing so VERY remarkable in that; nor did Alice think it so VERY much out of the way to hear the Rabbit say to itself, 'Oh dear! Oh dear! I shall be late!' (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually TOOK A WATCH OUT OF ITS WAISTCOAT- POCKET, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge.");
    // End the body block!
    blockComposer.end();

    composer.end();

    composer.beginLocalState();
    composer.rotate(
      90,
      new Point2D.Double(
        pageSize.getWidth() - 50,
        pageSize.getHeight() - 25
        )
      );
    blockComposer = new BlockComposer(composer);
    blockComposer.begin(
      new Rectangle2D.Double(0,0,300,50),
      XAlignmentEnum.Left,
      YAlignmentEnum.Middle
      );
    composer.setFont(bodyFont,8);
    blockComposer.showText("Generated by PDF Clown on " + new java.util.Date());
    blockComposer.showBreak();
    blockComposer.showText("For more info, visit http://www.pdfclown.org");
    blockComposer.end();
    composer.end();

    // Flush the contents into the page!
    composer.flush();
  }
}
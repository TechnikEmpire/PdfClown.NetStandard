package org.pdfclown.samples.cli;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.files.EmbeddedFile;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.annotations.Border;
import org.pdfclown.documents.interaction.annotations.BorderEffect;
import org.pdfclown.documents.interaction.annotations.Ellipse;
import org.pdfclown.documents.interaction.annotations.FileAttachment;
import org.pdfclown.documents.interaction.annotations.Line;
import org.pdfclown.documents.interaction.annotations.LineEndStyleEnum;
import org.pdfclown.documents.interaction.annotations.Popup;
import org.pdfclown.documents.interaction.annotations.Scribble;
import org.pdfclown.documents.interaction.annotations.Stamp;
import org.pdfclown.documents.interaction.annotations.StaticNote;
import org.pdfclown.documents.interaction.annotations.StickyNote;
import org.pdfclown.documents.interaction.annotations.TextMarkup;
import org.pdfclown.documents.interaction.annotations.TextMarkup.MarkupTypeEnum;
import org.pdfclown.documents.interaction.annotations.styles.StampAppearanceBuilder;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to insert annotations</b> into a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
public class AnnotationSample
  extends Sample
{
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
    serialize(file, "Annotations", "inserting annotations", "annotations, creation, attachment, sticky notes, callout notes, rubber stamps, markup, highlighting");
  }

  @SuppressWarnings("unchecked")
  private void populate(
    Document document
    )
  {
    Page page = new Page(document);
    document.getPages().add(page);

    PrimitiveComposer composer = new PrimitiveComposer(page);
    StandardType1Font font = new StandardType1Font(document, StandardType1Font.FamilyEnum.Courier, true, false);
    composer.setFont(font, 12);

    // Sticky note.
    composer.showText("Sticky note annotation:", new Point(35, 35));
    new StickyNote(
      page,
      new Point(50, 50),
      "Text of the Sticky note annotation"
      ).withIconType(StickyNote.IconTypeEnum.Note)
       .withColor(DeviceRGBColor.get(Color.YELLOW))
       .withPopup(new Popup(
         page,
         new Rectangle2D.Double(200, 25, 200, 75),
         "Text of the Popup annotation (this text won't be visible as associating popups to markup annotations overrides the former's properties with the latter's)"
         ))
       .withAuthor("Stefano")
       .withSubject("Sticky note")
       .withOpen(true);
    new StickyNote(
      page,
      new Point(80, 50),
      "Text of the Help sticky note annotation"
      ).withIconType(StickyNote.IconTypeEnum.Help)
       .withColor(DeviceRGBColor.get(Color.PINK))
       .withAuthor("Stefano")
       .withSubject("Sticky note")
       .withPopup(new Popup(
         page,
         new Rectangle2D.Double(400, 25, 200, 75),
         "Text of the Popup annotation (this text won't be visible as associating popups to markup annotations overrides the former's properties with the latter's)"
         ));
    new StickyNote(
      page,
      new Point(110, 50),
      "Text of the Comment sticky note annotation"
      ).withIconType(StickyNote.IconTypeEnum.Comment)
       .withColor(DeviceRGBColor.get(Color.GREEN))
       .withAuthor("Stefano")
       .withSubject("Sticky note");
    new StickyNote(
      page,
      new Point(140, 50),
      "Text of the Key sticky note annotation"
      ).withIconType(StickyNote.IconTypeEnum.Key)
       .withColor(DeviceRGBColor.get(Color.BLUE))
       .withAuthor("Stefano")
       .withSubject("Sticky note");

    // Callout.
    composer.showText("Callout note annotation:", new Point(35, 85));
    new StaticNote(
      page,
      new Rectangle(250, 90, 150, 70),
      "Text of the Callout note annotation"
      ).withLine(
         new StaticNote.CalloutLine(
           page,
           new Point(250,125),
           new Point(150,125),
           new Point(100,100)
           )
         )
       .withLineEndStyle(LineEndStyleEnum.OpenArrow)
       .withBorder(new Border(1))
       .withColor(DeviceRGBColor.get(Color.YELLOW));

    // File attachment.
    composer.showText("File attachment annotation:", new Point(35, 135));
    new FileAttachment(
      page,
      new Rectangle(50, 150, 20, 20),
      "Text of the File attachment annotation",
      FileSpecification.get(
        EmbeddedFile.get(document, getResourcePath("images" + java.io.File.separator + "gnu.jpg")),
        "happyGNU.jpg"
        )
      ).withIconType(FileAttachment.IconTypeEnum.PaperClip)
       .withAuthor("Stefano")
       .withSubject("File attachment");
    
    composer.showText("Line annotation:", new Point(35, 185));
    {
      composer.beginLocalState();
      composer.setFont(font, 10);
      
      // Arrow line.
      composer.showText("Arrow:", new Point(50,200));
      new Line(
        page,
        new Point(50, 260),
        new Point(200,210),
        "Text of the Arrow line annotation",
        DeviceRGBColor.get(Color.BLACK)
        ).withStartStyle(LineEndStyleEnum.Circle)
         .withEndStyle(LineEndStyleEnum.ClosedArrow)
         .withCaptionVisible(true)
         .withFillColor(DeviceRGBColor.get(Color.GREEN))
         .withAuthor("Stefano")
         .withSubject("Arrow line");

      // Dimension line.
      composer.showText("Dimension:", new Point(300, 200));
      new Line(
        page,
        new Point(300,220),
        new Point(500,220),
        "Text of the Dimension line annotation",
        DeviceRGBColor.get(Color.BLUE)
        ).withLeaderLineLength(20)
         .withLeaderLineExtensionLength(10)
         .withStartStyle(LineEndStyleEnum.OpenArrow)
         .withEndStyle(LineEndStyleEnum.OpenArrow)
         .withBorder(new Border(1))
         .withCaptionVisible(true)
         .withAuthor("Stefano")
         .withSubject("Dimension line");
      
      composer.end();
    }
    
    // Scribble.
    composer.showText("Scribble annotation:", new Point(35, 285));
    new Scribble(
      page,
      Arrays.asList(
        Arrays.asList(
          (Point2D)new Point(50,320),
          (Point2D)new Point(70,305),
          (Point2D)new Point(110,335),
          (Point2D)new Point(130,320),
          (Point2D)new Point(110,305),
          (Point2D)new Point(70,335),
          (Point2D)new Point(50,320)
          )
        ),
      "Text of the Scribble annotation",
      DeviceRGBColor.get(Color.ORANGE)
      ).withBorder(new Border(1, new LineDash(new double[]{5,2,2,2})))
       .withAuthor("Stefano")
       .withSubject("Scribble");

    // Rectangle.
    composer.showText("Rectangle annotation:", new Point(35, 350));
    new org.pdfclown.documents.interaction.annotations.Rectangle(
      page,
      new Rectangle(50, 370, 100, 30),
      "Text of the Rectangle annotation"
      ).withColor(DeviceRGBColor.get(Color.RED))
       .withBorder(new Border(1, new LineDash(new double[]{5})))
       .withAuthor("Stefano")
       .withSubject("Rectangle")
       .withPopup(new Popup(
         page,
         new Rectangle2D.Double(200, 325, 200, 75),
         "Text of the Popup annotation (this text won't be visible as associating popups to markup annotations overrides the former's properties with the latter's)"
         ));

    // Ellipse.
    composer.showText("Ellipse annotation:", new Point(35, 415));
    new Ellipse(
      page,
      new Rectangle(50, 440, 100, 30),
      "Text of the Ellipse annotation"
      ).withBorderEffect(new BorderEffect(BorderEffect.TypeEnum.Cloudy, 1))
       .withFillColor(DeviceRGBColor.get(Color.CYAN))
       .withColor(DeviceRGBColor.get(Color.BLACK))
       .withAuthor("Stefano")
       .withSubject("Ellipse");

    // Rubber stamp.
    composer.showText("Rubber stamp annotations:", new Point(35,505));
    {
      Font stampFont = Font.get(document, getResourcePath("fonts" + java.io.File.separator + "TravelingTypewriter.otf"));
      new Stamp(
        page,
        new Point(75, 570),
        "This is a round custom stamp",
        new StampAppearanceBuilder(document, StampAppearanceBuilder.TypeEnum.Round, "Done", 50, stampFont)
          .build()
        ).withRotation(-10)
         .withAuthor("Stefano")
         .withSubject("Custom stamp");
      
      new Stamp(
        page,
        new Point(210, 570),
        "This is a squared (and round-cornered) custom stamp",
        new StampAppearanceBuilder(document, StampAppearanceBuilder.TypeEnum.Squared, "Classified", 150, stampFont)
          .withColor(DeviceRGBColor.get(Color.ORANGE))
          .build()
        ).withRotation(15)
         .withAuthor("Stefano")
         .withSubject("Custom stamp");
      
      Font stampFont2 = Font.get(document, getResourcePath("fonts" + java.io.File.separator + "MgOpenCanonicaRegular.ttf"));
      new Stamp(
        page,
        new Point(350, 570),
        "This is a striped custom stamp",
        new StampAppearanceBuilder(document, StampAppearanceBuilder.TypeEnum.Striped, "Out of stock", 100, stampFont2)
          .withColor(DeviceRGBColor.get(Color.GRAY))
          .build()
        ).withRotation(90)
         .withAuthor("Stefano")
         .withSubject("Custom stamp");
      
      // Define the standard stamps template path!
      /*
        NOTE: The PDF specification defines several stamps (aka "standard stamps") whose rendering 
        depends on the support of viewer applications. As such support isn't guaranteed, PDF Clown
        offers smooth, ready-to-use embedding of these stamps through the StampPath property of the
        document configuration: you can decide to point to the stamps directory of your Acrobat
        installation (e.g., in my GNU/Linux system it's located in
        "/opt/Adobe/Reader9/Reader/intellinux/plug_ins/Annotations/Stamps/ENU") or to the collection 
        included in this distribution (std-stamps.pdf).
      */
      document.getConfiguration().setStampPath(new java.io.File(getResourcePath("../../pkg/templates/std-stamps.pdf")));
      
      // Add a standard stamp, rotating it 15 degrees counterclockwise!
      new Stamp(
        page,
        new Point(485, 515),
        null, // Default size is natural size.
        "This is 'Confidential', a standard stamp",
        Stamp.StandardTypeEnum.Confidential
        ).withRotation(15)
         .withAuthor("Stefano")
         .withSubject("Standard stamp");

      // Add a standard stamp, without rotation!
      new Stamp(
        page,
        new Point(485, 580),
        null, // Default size is natural size.
        "This is 'SBApproved', a standard stamp",
        Stamp.StandardTypeEnum.BusinessApproved
        ).withAuthor("Stefano")
         .withSubject("Standard stamp");

      // Add a standard stamp, rotating it 10 degrees clockwise!
      new Stamp(
        page,
        new Point(485, 635),
        new Dimension(0, 40), // This scales the width proportionally to the 40-unit height (you can obviously do also the opposite, defining only the width).
        "This is 'SHSignHere', a standard stamp",
        Stamp.StandardTypeEnum.SignHere
        ).withRotation(-10)
         .withAuthor("Stefano")
         .withSubject("Standard stamp");
    }
    
    composer.showText("Text markup annotations:", new Point(35, 650));
    {
      composer.beginLocalState();
      composer.setFont(font, 8);
      
      new TextMarkup(
        page,
        composer.showText("Highlight annotation", new Point(35, 680)),
        "Text of the Highlight annotation",
        MarkupTypeEnum.Highlight
        ).withAuthor("Stefano")
        .withSubject("An highlight text markup!");
      new TextMarkup(
        page,
        composer.showText("Highlight annotation 2", new Point(35, 695)).inflate(0, 1),
        "Text of the Highlight annotation 2",
        MarkupTypeEnum.Highlight
        ).withColor(DeviceRGBColor.get(Color.MAGENTA));
      new TextMarkup(
        page,
        composer.showText("Highlight annotation 3", new Point(35, 710)).inflate(0, 2),
        "Text of the Highlight annotation 3",
        MarkupTypeEnum.Highlight
        ).withColor(DeviceRGBColor.get(Color.RED));
      
      new TextMarkup(
        page,
        composer.showText("Squiggly annotation", new Point(180, 680)),
        "Text of the Squiggly annotation",
        MarkupTypeEnum.Squiggly
        );
      new TextMarkup(
        page,
        composer.showText("Squiggly annotation 2", new Point(180, 695)).inflate(0, 2.5),
        "Text of the Squiggly annotation 2",
        MarkupTypeEnum.Squiggly
        ).withColor(DeviceRGBColor.get(Color.ORANGE));
      new TextMarkup(
        page,
        composer.showText("Squiggly annotation 3", new Point(180, 710)).inflate(0, 3),
        "Text of the Squiggly annotation 3",
        MarkupTypeEnum.Squiggly
        ).withColor(DeviceRGBColor.get(Color.PINK));
      
      new TextMarkup(
        page,
        composer.showText("Underline annotation", new Point(320, 680)),
        "Text of the Underline annotation",
        MarkupTypeEnum.Underline
        );
      new TextMarkup(
        page,
        composer.showText("Underline annotation 2", new Point(320, 695)).inflate(0, 2.5),
        "Text of the Underline annotation 2",
        MarkupTypeEnum.Underline
        ).withColor(DeviceRGBColor.get(Color.ORANGE));
      new TextMarkup(
        page,
        composer.showText("Underline annotation 3", new Point(320, 710)).inflate(0, 3),
        "Text of the Underline annotation 3",
        MarkupTypeEnum.Underline
        ).withColor(DeviceRGBColor.get(Color.GREEN));
      
      new TextMarkup(
        page,
        composer.showText("StrikeOut annotation", new Point(455, 680)),
        "Text of the StrikeOut annotation",
        MarkupTypeEnum.StrikeOut
        );
      new TextMarkup(
        page,
        composer.showText("StrikeOut annotation 2", new Point(455, 695)).inflate(0, 2.5),
        "Text of the StrikeOut annotation 2",
        MarkupTypeEnum.StrikeOut
        ).withColor(DeviceRGBColor.get(Color.ORANGE));
      new TextMarkup(
        page,
        composer.showText("StrikeOut annotation 3", new Point(455, 710)).inflate(0, 3),
        "Text of the StrikeOut annotation 3",
        MarkupTypeEnum.StrikeOut
        ).withColor(DeviceRGBColor.get(Color.GREEN));
      
      composer.end();
    }
    composer.flush();
  }
}
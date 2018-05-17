using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.entities;
using fonts = org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.files;
using org.pdfclown.documents.interaction;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.documents.interaction.annotations.styles;
using files = org.pdfclown.files;

using System;
using System.Collections.Generic;
using System.Drawing;
using SystemColor = System.Drawing.Color;
using System.IO;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to insert annotations into a PDF document.</summary>
  */
  public class AnnotationSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. PDF file instantiation.
      files::File file = new files::File();
      Document document = file.Document;

      // 2. Content creation.
      Populate(document);

      // 3. Serialize the PDF file!
      Serialize(file, "Annotations", "inserting annotations", "annotations, creation, attachment, sticky notes, callout notes, rubber stamps, markup, highlighting");
    }

    private void Populate(
      Document document
      )
    {
      Page page = new Page(document);
      document.Pages.Add(page);

      PrimitiveComposer composer = new PrimitiveComposer(page);
      fonts::StandardType1Font font = new fonts::StandardType1Font(document, fonts::StandardType1Font.FamilyEnum.Courier, true, false);
      composer.SetFont(font, 12);

      // Sticky note.
      composer.ShowText("Sticky note annotation:", new PointF(35, 35));
      new StickyNote(
        page,
        new PointF(50, 50),
        "Text of the Sticky note annotation"
        )
      {
        IconType = StickyNote.IconTypeEnum.Note,
        Color = DeviceRGBColor.Get(SystemColor.Yellow),
        Popup = new Popup(
          page,
          new RectangleF(200, 25, 200, 75),
          "Text of the Popup annotation (this text won't be visible as associating popups to markup annotations overrides the former's properties with the latter's)"
          ),
        Author = "Stefano",
        Subject = "Sticky note",
        IsOpen = true
      };
      new StickyNote(
        page,
        new PointF(80, 50),
        "Text of the Help sticky note annotation"
        )
      {
        IconType = StickyNote.IconTypeEnum.Help,
        Color = DeviceRGBColor.Get(SystemColor.Pink),
        Author = "Stefano",
        Subject = "Sticky note",
        Popup = new Popup(
          page,
          new RectangleF(400, 25, 200, 75),
          "Text of the Popup annotation (this text won't be visible as associating popups to markup annotations overrides the former's properties with the latter's)"
          )
      };
      new StickyNote(
        page,
        new PointF(110, 50),
        "Text of the Comment sticky note annotation"
        )
      {
        IconType = StickyNote.IconTypeEnum.Comment,
        Color = DeviceRGBColor.Get(SystemColor.Green),
        Author = "Stefano",
        Subject = "Sticky note"
      };
      new StickyNote(
        page,
        new PointF(140, 50),
        "Text of the Key sticky note annotation"
        )
      {
        IconType = StickyNote.IconTypeEnum.Key,
        Color = DeviceRGBColor.Get(SystemColor.Blue),
        Author = "Stefano",
        Subject = "Sticky note"
      };

      // Callout.
      composer.ShowText("Callout note annotation:", new PointF(35, 85));
      new StaticNote(
        page,
        new RectangleF(250, 90, 150, 70),
        "Text of the Callout note annotation"
        )
      {
        Line = new StaticNote.CalloutLine(
          page,
          new Point(250,125),
          new Point(150,125),
          new Point(100,100)
          ),
        LineEndStyle = LineEndStyleEnum.OpenArrow,
        Border = new Border(1),
        Color = DeviceRGBColor.Get(SystemColor.Yellow)
      };

      // File attachment.
      composer.ShowText("File attachment annotation:", new PointF(35, 135));
      new FileAttachment(
        page,
        new RectangleF(50, 150, 20, 20),
        "Text of the File attachment annotation",
        FileSpecification.Get(
          EmbeddedFile.Get(document, GetResourcePath("images" + Path.DirectorySeparatorChar + "gnu.jpg")),
          "happyGNU.jpg"
          )
        )
      {
        IconType = FileAttachment.IconTypeEnum.PaperClip,
        Author = "Stefano",
        Subject = "File attachment"
      };

      composer.ShowText("Line annotation:", new PointF(35, 185));
      {
        composer.BeginLocalState();
        composer.SetFont(font, 10);

        // Arrow line.
        composer.ShowText("Arrow:", new PointF(50,200));
        new Line(
          page,
          new Point(50, 260),
          new Point(200,210),
          "Text of the Arrow line annotation",
          DeviceRGBColor.Get(SystemColor.Black)
          )
        {
          StartStyle = LineEndStyleEnum.Circle,
          EndStyle = LineEndStyleEnum.ClosedArrow,
          CaptionVisible = true,
          FillColor = DeviceRGBColor.Get(SystemColor.Green),
          Author = "Stefano",
          Subject = "Arrow line"
        };

        // Dimension line.
        composer.ShowText("Dimension:", new PointF(300, 200));
        new Line(
          page,
          new PointF(300,220),
          new PointF(500,220),
          "Text of the Dimension line annotation",
          DeviceRGBColor.Get(SystemColor.Blue)
          )
        {
          LeaderLineLength = 20,
          LeaderLineExtensionLength = 10,
          StartStyle = LineEndStyleEnum.OpenArrow,
          EndStyle = LineEndStyleEnum.OpenArrow,
          Border = new Border(1),
          CaptionVisible = true,
          Author = "Stefano",
          Subject = "Dimension line"
        };

        composer.End();
      }

      // Scribble.
      composer.ShowText("Scribble annotation:", new PointF(35, 285));
      new Scribble(
        page,
        new List<IList<PointF>>(
          new List<PointF>[]
          {
            new List<PointF>(
              new PointF[]
              {
                new PointF(50,320),
                new PointF(70,305),
                new PointF(110,335),
                new PointF(130,320),
                new PointF(110,305),
                new PointF(70,335),
                new PointF(50,320)
              }
            )
          }
          ),
        "Text of the Scribble annotation",
        DeviceRGBColor.Get(SystemColor.Orange)
        )
      {
        Border = new Border(1, new LineDash(new double[]{5,2,2,2})),
        Author = "Stefano",
        Subject = "Scribble"
      };

      // Rectangle.
      composer.ShowText("Rectangle annotation:", new PointF(35, 350));
      new org.pdfclown.documents.interaction.annotations.Rectangle(
        page,
        new RectangleF(50, 370, 100, 30),
        "Text of the Rectangle annotation"
        )
      {
        Color = DeviceRGBColor.Get(SystemColor.Red),
        Border = new Border(1, new LineDash(new double[]{5})),
        Author = "Stefano",
        Subject = "Rectangle",
        Popup = new Popup(
           page,
           new RectangleF(200, 325, 200, 75),
           "Text of the Popup annotation (this text won't be visible as associating popups to markup annotations overrides the former's properties with the latter's)"
           )
      };

      // Ellipse.
      composer.ShowText("Ellipse annotation:", new PointF(35, 415));
      new Ellipse(
        page,
        new RectangleF(50, 440, 100, 30),
        "Text of the Ellipse annotation"
        )
      {
        BorderEffect = new BorderEffect(BorderEffect.TypeEnum.Cloudy, 1),
        FillColor = DeviceRGBColor.Get(SystemColor.Cyan),
        Color = DeviceRGBColor.Get(SystemColor.Black),
        Author = "Stefano",
        Subject = "Ellipse"
      };

      // Rubber stamp.
      composer.ShowText("Rubber stamp annotations:", new Point(35, 505));
      {
        fonts::Font stampFont = fonts::Font.Get(document, GetResourcePath("fonts" + Path.DirectorySeparatorChar + "TravelingTypewriter.otf"));
        new Stamp(
          page,
          new PointF(75, 570),
          "This is a round custom stamp",
          new StampAppearanceBuilder(document, StampAppearanceBuilder.TypeEnum.Round, "Done", 50, stampFont)
            .Build()
          )
        {
          Rotation = -10,
          Author = "Stefano",
          Subject = "Custom stamp"
        };

        new Stamp(
          page,
          new PointF(210, 570),
          "This is a squared (and round-cornered) custom stamp",
          new StampAppearanceBuilder(document, StampAppearanceBuilder.TypeEnum.Squared, "Classified", 150, stampFont)
          {Color = DeviceRGBColor.Get(SystemColor.Orange)}.Build()
          )
        {
          Rotation = 15,
          Author = "Stefano",
          Subject = "Custom stamp"
        };

        fonts::Font stampFont2 = fonts::Font.Get(document, GetResourcePath("fonts" + Path.DirectorySeparatorChar + "MgOpenCanonicaRegular.ttf"));
        new Stamp(
          page,
          new PointF(350, 570),
          "This is a striped custom stamp",
          new StampAppearanceBuilder(document, StampAppearanceBuilder.TypeEnum.Striped, "Out of stock", 100, stampFont2)
          {Color = DeviceRGBColor.Get(SystemColor.Gray)}.Build()
          )
        {
          Rotation = 90,
          Author = "Stefano",
          Subject = "Custom stamp"
        };

        // Define the standard stamps template path!
        /*
          NOTE: The PDF specification defines several stamps (aka "standard stamps") whose rendering
          depends on the support of viewer applications. As such support isn't guaranteed, PDF Clown
          offers smooth, ready-to-use embedding of these stamps through the StampPath property of the
          document configuration: you can decide to point to the stamps directory of your Acrobat
          installation (e.g., in my GNU/Linux system it's located in
          "/opt/Adobe/Reader9/Reader/intellinux/plug_ins/Annotations/Stamps/ENU") or to the
          collection included in this distribution (std-stamps.pdf).
        */
        document.Configuration.StampPath = GetResourcePath("../../pkg/templates/std-stamps.pdf");

        // Add a standard stamp, rotating it 15 degrees counterclockwise!
        new Stamp(
          page,
          new PointF(485, 515),
          null, // Default size is natural size.
          "This is 'Confidential', a standard stamp",
          Stamp.StandardTypeEnum.Confidential
          )
        {
          Rotation = 15,
          Author = "Stefano",
          Subject = "Standard stamp"
        };

        // Add a standard stamp, without rotation!
        new Stamp(
          page,
          new PointF(485, 580),
          null, // Default size is natural size.
          "This is 'SBApproved', a standard stamp",
          Stamp.StandardTypeEnum.BusinessApproved
          )
        {
          Author = "Stefano",
          Subject = "Standard stamp"
        };

        // Add a standard stamp, rotating it 10 degrees clockwise!
        new Stamp(
          page,
          new PointF(485, 635),
          new SizeF(0, 40), // This scales the width proportionally to the 40-unit height (you can obviously do also the opposite, defining only the width).
          "This is 'SHSignHere', a standard stamp",
          Stamp.StandardTypeEnum.SignHere
          )
        {
          Rotation = -10,
          Author = "Stefano",
          Subject = "Standard stamp"
        };
      }

      composer.ShowText("Text markup annotations:", new PointF(35, 650));
      {
        composer.BeginLocalState();
        composer.SetFont(font, 8);

        new TextMarkup(
          page,
          composer.ShowText("Highlight annotation", new PointF(35, 680)),
          "Text of the Highlight annotation",
          TextMarkup.MarkupTypeEnum.Highlight
          )
        {
          Author = "Stefano",
          Subject = "An highlight text markup!"
        };
        new TextMarkup(
          page,
          composer.ShowText("Highlight annotation 2", new PointF(35, 695)).Inflate(0, 1),
          "Text of the Highlight annotation 2",
          TextMarkup.MarkupTypeEnum.Highlight
          )
        {Color = DeviceRGBColor.Get(SystemColor.Magenta)};
        new TextMarkup(
          page,
          composer.ShowText("Highlight annotation 3", new PointF(35, 710)).Inflate(0, 2),
          "Text of the Highlight annotation 3",
          TextMarkup.MarkupTypeEnum.Highlight
          )
        {Color = DeviceRGBColor.Get(SystemColor.Red)};

        new TextMarkup(
          page,
          composer.ShowText("Squiggly annotation", new PointF(180, 680)),
          "Text of the Squiggly annotation",
          TextMarkup.MarkupTypeEnum.Squiggly
          );
        new TextMarkup(
          page,
          composer.ShowText("Squiggly annotation 2", new PointF(180, 695)).Inflate(0, 2.5f),
          "Text of the Squiggly annotation 2",
          TextMarkup.MarkupTypeEnum.Squiggly
          )
        {Color = DeviceRGBColor.Get(SystemColor.Orange)};
        new TextMarkup(
          page,
          composer.ShowText("Squiggly annotation 3", new PointF(180, 710)).Inflate(0, 3),
          "Text of the Squiggly annotation 3",
          TextMarkup.MarkupTypeEnum.Squiggly
          )
        {Color = DeviceRGBColor.Get(SystemColor.Pink)};

        new TextMarkup(
          page,
          composer.ShowText("Underline annotation", new PointF(320, 680)),
          "Text of the Underline annotation",
          TextMarkup.MarkupTypeEnum.Underline
          );
        new TextMarkup(
          page,
          composer.ShowText("Underline annotation 2", new PointF(320, 695)).Inflate(0, 2.5f),
          "Text of the Underline annotation 2",
          TextMarkup.MarkupTypeEnum.Underline
          )
        {Color = DeviceRGBColor.Get(SystemColor.Orange)};
        new TextMarkup(
          page,
          composer.ShowText("Underline annotation 3", new PointF(320, 710)).Inflate(0, 3),
          "Text of the Underline annotation 3",
          TextMarkup.MarkupTypeEnum.Underline
          )
        {Color = DeviceRGBColor.Get(SystemColor.Green)};

        new TextMarkup(
          page,
          composer.ShowText("StrikeOut annotation", new PointF(455, 680)),
          "Text of the StrikeOut annotation",
          TextMarkup.MarkupTypeEnum.StrikeOut
          );
        new TextMarkup(
          page,
          composer.ShowText("StrikeOut annotation 2", new PointF(455, 695)).Inflate(0, 2.5f),
          "Text of the StrikeOut annotation 2",
          TextMarkup.MarkupTypeEnum.StrikeOut
          )
        {Color = DeviceRGBColor.Get(SystemColor.Orange)};
        new TextMarkup(
          page,
          composer.ShowText("StrikeOut annotation 3", new PointF(455, 710)).Inflate(0, 3),
          "Text of the StrikeOut annotation 3",
          TextMarkup.MarkupTypeEnum.StrikeOut
          )
        {Color = DeviceRGBColor.Get(SystemColor.Green)};

        composer.End();
      }
      composer.Flush();
    }
  }
}
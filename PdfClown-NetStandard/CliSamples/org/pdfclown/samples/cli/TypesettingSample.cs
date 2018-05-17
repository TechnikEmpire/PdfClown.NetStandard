using org.pdfclown.documents;
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using fonts = org.pdfclown.documents.contents.fonts;
using files = org.pdfclown.files;

using System;
using System.Drawing;
using System.IO;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample concentrates on proper fitting of styled text within a given PDF page area (block
    frame), from the beginning of "Alice in Wonderland", Chapter 1 ("Down the Rabbit-Hole").</summary>
  */
  public class TypesettingSample
    : Sample
  {
    private static readonly int Margin_X = 50;
    private static readonly int Margin_Y = 50;

    public override void Run(
      )
    {
      // 1. PDF file instantiation.
      files::File file = new files::File();
      Document document = file.Document;

      // 2. Content creation.
      Build(document);

      // 3. Serialize the PDF file!
      Serialize(file, "Typesetting", "demonstrating how to add style to contents", "typesetting");
    }

    private void Build(
      Document document
      )
    {
      // Add a page to the document!
      Page page = new Page(document); // Instantiates the page inside the document context.
      document.Pages.Add(page); // Puts the page in the pages collection.

      SizeF pageSize = page.Size;

      // Create a content composer for the content stream!
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

      composer.BeginLocalState();
      // Define the block frame that will constrain our contents on the page canvas!
      RectangleF frame = new RectangleF(
        Margin_X,
        Margin_Y,
        (float)pageSize.Width - Margin_X * 2,
        (float)pageSize.Height - Margin_Y * 2
        );
      // Begin the title block!
      blockComposer.Begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
      fonts::Font decorativeFont = fonts::Font.Get(
        document,
        GetResourcePath("fonts" + Path.DirectorySeparatorChar + "Ruritania-Outline.ttf")
        );
      composer.SetFont(decorativeFont,56);
      blockComposer.ShowText("Chapter 1");
      blockComposer.ShowBreak();
      composer.SetFont(decorativeFont,32);
      blockComposer.ShowText("Down the Rabbit-Hole");
      // End the title block!
      blockComposer.End();
      // Update the block frame in order to begin after the title!
      frame = new RectangleF(
        (float)blockComposer.BoundBox.X,
        (float)blockComposer.BoundBox.Y + blockComposer.BoundBox.Height,
        (float)blockComposer.BoundBox.Width,
        (float)pageSize.Height - Margin_Y - (blockComposer.BoundBox.Y + blockComposer.BoundBox.Height)
        );
      // Begin the body block!
      blockComposer.Begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
      fonts::Font bodyFont = fonts::Font.Get(
        document,
        GetResourcePath("fonts" + Path.DirectorySeparatorChar + "TravelingTypewriter.otf")
        );
      composer.SetFont(bodyFont,14);
      composer.BeginLocalState();
      composer.SetFont(decorativeFont,28);
      blockComposer.ShowText("A");
      composer.End();
      blockComposer.ShowText("lice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice 'without pictures or conversation?'");
      // Define new-paragraph first-line offset!
      SizeF breakSize = new SizeF(24,8); // Indentation (24pt) and top margin (8pt).
      // Begin a new paragraph!
      blockComposer.ShowBreak(breakSize);
      blockComposer.ShowText("So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.");
      // Begin a new paragraph!
      blockComposer.ShowBreak(breakSize);
      blockComposer.ShowText("There was nothing so VERY remarkable in that; nor did Alice think it so VERY much out of the way to hear the Rabbit say to itself, 'Oh dear! Oh dear! I shall be late!' (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually TOOK A WATCH OUT OF ITS WAISTCOAT- POCKET, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge.");
      // End the body block!
      blockComposer.End();
      composer.End();

      composer.BeginLocalState();
      composer.Rotate(
        90,
        new PointF(
          pageSize.Width - 50,
          pageSize.Height - 25
          )
        );
      blockComposer = new BlockComposer(composer);
      blockComposer.Begin(
        new RectangleF(0,0,300,50),
        XAlignmentEnum.Left,
        YAlignmentEnum.Middle
        );
      composer.SetFont(bodyFont,8);
      blockComposer.ShowText("Generated by PDF Clown on " + System.DateTime.Now);
      blockComposer.ShowBreak();
      blockComposer.ShowText("For more info, visit http://www.pdfclown.org");
      blockComposer.End();
      composer.End();

      // Flush the contents into the page!
      composer.Flush();
    }
  }
}
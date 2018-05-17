using bytes = org.pdfclown.bytes;
using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using colorSpaces = org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.documents.contents.composition;
using entities = org.pdfclown.documents.contents.entities;
using fonts = org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.interaction;
using actions = org.pdfclown.documents.interaction.actions;
using org.pdfclown.documents.interaction.navigation.document;
using org.pdfclown.documents.interaction.navigation.page;
using org.pdfclown.documents.interaction.viewer;
using org.pdfclown.documents.interchange.metadata;
using files = org.pdfclown.files;

using System;
using System.Drawing;
using System.IO;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to create a new PDF document populating it with various
    graphics elements.</summary>
    <remarks>
      <para>This implementation features an enlightening example of an embryonic typesetter
      that exploits the new typographic primitives defined in PDF Clown (see BlockComposer
      class in use); this is just a humble experiment -- anybody could develop a typesetter
      sitting upon PDF Clown!</para>
      <para>Anyway, PDF Clown currently lacks support for content flow composition (i.e. paragraphs
      spread across multiple pages): since 0.0.3 release offers a static-composition facility
      (BlockComposer class) that is meant to be the base for more advanced functionalities (such as the
      above-mentioned content flow composition), to be made available in the next releases.</para>
    </remarks>
  */
  public class ComplexTypesettingSample
    : Sample
  {
    private static readonly colorSpaces::Color TextColor_Highlight = new colorSpaces::DeviceRGBColor(255 / 255d, 50 / 255d, 50 / 255d);

    public override void Run(
      )
    {
      // 1. PDF file instantiation.
      files::File file = new files::File();
      Document document = file.Document;
      // Set default page size (A4)!
      document.PageSize = PageFormat.GetSize();

      // 2. Content creation.
      DateTime creationDate = DateTime.Now;
      // 2.1. Template.
      FormXObject template = BuildTemplate(document, creationDate);
      // 2.2. Welcome page.
      BuildWelcomePage(document, template);
      // 2.3. Free Software definition.
      BuildFreeSoftwareDefinitionPages(document, template);
      // 2.4. Bookmarks.
      BuildBookmarks(document);

      // 3. Serialization.
      Serialize(file, "Complex Typesetting", "complex typesetting", "typesetting, bookmarks, hyphenation, block composer, primitive composer, text alignment, image insertion, article threads");
    }

    private void BuildBookmarks(
      Document document
      )
    {
      Pages pages = document.Pages;
      Bookmarks bookmarks = document.Bookmarks;
      Page page = pages[0];
      Bookmark rootBookmark = new Bookmark(
        document,
        "Creation Sample",
        new LocalDestination(page)
        );
      bookmarks.Add(rootBookmark);
      bookmarks = rootBookmark.Bookmarks;
      page = pages[1];
      Bookmark bookmark = new Bookmark(
        document,
        "2nd page (close-up view)",
        new LocalDestination(
          page,
          Destination.ModeEnum.XYZ,
          new PointF(0, 250),
          2
          )
        );
      bookmarks.Add(bookmark);
      bookmark.Bookmarks.Add(
        new Bookmark(
          document,
          "2nd page (mid view)",
          new LocalDestination(
            page,
            Destination.ModeEnum.XYZ,
            new PointF(0, page.Size.Height - 250),
            1
            )
          )
        );
      page = pages[2];
      bookmarks.Add(
        new Bookmark(
          document,
          "3rd page (fit horizontal view)",
          new LocalDestination(
            page,
            Destination.ModeEnum.FitHorizontal,
            0,
            null
            )
          )
        );
      bookmarks.Add(
        bookmark = new Bookmark(
            document,
            "PDF Clown Home Page",
            new actions::GoToURI(document, new Uri("http://www.pdfclown.org"))
            )
          );
      bookmark.Flags = Bookmark.FlagsEnum.Bold | Bookmark.FlagsEnum.Italic;
      bookmark.Color = new colorSpaces::DeviceRGBColor(.5, .5, 1);
      
      document.ViewerPreferences.PageMode = ViewerPreferences.PageModeEnum.Bookmarks;
    }

    private void BuildFreeSoftwareDefinitionPages(
      Document document,
      FormXObject template
      )
    {
      // Add page!
      Page page = new Page(document);
      document.Pages.Add(page);
      SizeF pageSize = page.Size;

      string title = "The Free Software Definition";

      // Create the article thread!
      Article article = new Article(document);
      {
        Information articleInfo = article.Information;
        articleInfo.Title = title;
        articleInfo.Author = "Free Software Foundation, Inc.";
      }
      // Get the article beads collection to populate!
      ArticleElements articleElements = article.Elements;

      PrimitiveComposer composer = new PrimitiveComposer(page);
      // Add the background template!
      composer.ShowXObject(template);
      // Wrap the content composer inside a block composer in order to achieve higher-level typographic control!
      /*
        NOTE: BlockComposer provides block-level typographic features as text and paragraph alignment.
        Flow-level typographic features are currently not supported: block-level typographic features
        are the foundations upon which flow-level typographic features will sit.
      */
      BlockComposer blockComposer = new BlockComposer(composer);
      blockComposer.Hyphenation = true;

      SizeF breakSize = new SizeF(0,10);
      // Add the font to the document!
      fonts::Font font = fonts::Font.Get(document, GetResourcePath("fonts" + Path.DirectorySeparatorChar + "TravelingTypewriter.otf"));

      RectangleF frame = new RectangleF(
        20,
        150,
        (pageSize.Width - 90 - 20) / 2,
        pageSize.Height - 250
        );

      // Showing the 'GNU' image...
      // Instantiate a jpeg image object!
      entities::Image image = entities::Image.Get(GetResourcePath("images" + Path.DirectorySeparatorChar + "gnu.jpg")); // Abstract image (entity).
      // Show the image!
      composer.ShowXObject(
        image.ToXObject(document),
        new PointF(
          (pageSize.Width - 90 - image.Width) / 2 + 20,
          pageSize.Height - 100 - image.Height)
        );

      // Showing the title...
      blockComposer.Begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
      composer.SetFont(font,24);
      blockComposer.ShowText(title);
      blockComposer.End();

      // Showing the copyright note...
      frame = new RectangleF(
        (float)blockComposer.BoundBox.X,
        (float)blockComposer.BoundBox.Y + blockComposer.BoundBox.Height + 32,
        (float)blockComposer.BoundBox.Width,
        (float)(pageSize.Height - 100 - image.Height - 10) - (blockComposer.BoundBox.Y + blockComposer.BoundBox.Height + 32)
        );
      blockComposer.Begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
      composer.SetFont(font,6);
      blockComposer.ShowText("Copyright 2004, 2005, 2006 Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA Verbatim copying and distribution of this entire article are permitted worldwide, without royalty, in any medium, provided this notice is preserved.");

      // Showing the body...
      blockComposer.ShowBreak(breakSize);
      composer.SetFont(font,8.25f);
      RectangleF[] frames = new RectangleF[]
        {
          new RectangleF(
            (float)blockComposer.BoundBox.X,
            (float)pageSize.Height - 100 - image.Height - 10,
            (float)blockComposer.BoundBox.Width-image.Width/2,
            (float)image.Height + 10
            ),
          new RectangleF(
            (float)(20 + 20 + (pageSize.Width - 90 - 20) / 2),
            150,
            (float)(pageSize.Width - 90 - 20) / 2,
            (float)(pageSize.Height - 100 - image.Height - 10) - 150
            ),
          new RectangleF(
            (float)(20 + 20 + (pageSize.Width - 90 - 20) / 2 + image.Width/2),
            (float)pageSize.Height - 100 - image.Height - 10,
            (float)blockComposer.BoundBox.Width-image.Width/2,
            (float)image.Height + 10
            ),
          new RectangleF(
            20,
            150,
            (float)(pageSize.Width - 90 - 20) / 2,
            (float)(pageSize.Height - 100) - 150
            ),
          new RectangleF(
            (float)(20 + 20 + (pageSize.Width - 90 - 20) / 2),
            150,
            (float)(pageSize.Width - 90 - 20) / 2,
            (float)(pageSize.Height - 100) - 150
            )
        };
      YAlignmentEnum[] yAlignments = new YAlignmentEnum[]
        {
          YAlignmentEnum.Top,
          YAlignmentEnum.Bottom,
          YAlignmentEnum.Top,
          YAlignmentEnum.Top,
          YAlignmentEnum.Top
        };
      String[] paragraphs = new String[]
        {
          "We maintain this free software definition to show clearly what must be true about a particular software program for it to be considered free software.",
          "\"Free software\" is a matter of liberty, not price. To understand the concept, you should think of \"free\" as in \"free speech\", not as in \"free beer\".",
          "Free software is a matter of the users' freedom to run, copy, distribute, study, change and improve the software. More precisely, it refers to four kinds of freedom, for the users of the software:",
          "* The freedom to run the program, for any purpose (freedom 0).",
          "* The freedom to study how the program works, and adapt it to your needs (freedom 1). Access to the source code is a precondition for this.",
          "* The freedom to redistribute copies so you can help your neighbor (freedom 2).",
          "* The freedom to improve the program, and release your improvements to the public, so that the whole community benefits (freedom 3). Access to the source code is a precondition for this.",
          "A program is free software if users have all of these freedoms. Thus, you should be free to redistribute copies, either with or without modifications, either gratis or charging a fee for distribution, to anyone anywhere. Being free to do these things means (among other things) that you do not have to ask or pay for permission.",
          "You should also have the freedom to make modifications and use them privately in your own work or play, without even mentioning that they exist. If you do publish your changes, you should not be required to notify anyone in particular, or in any particular way.",
          "The freedom to use a program means the freedom for any kind of person or organization to use it on any kind of computer system, for any kind of overall job, and without being required to communicate subsequently with the developer or any other specific entity.",
          "The freedom to redistribute copies must include binary or executable forms of the program, as well as source code, for both modified and unmodified versions. (Distributing programs in runnable form is necessary for conveniently installable free operating systems.) It is ok if there is no way to produce a binary or executable form for a certain program (since some languages don't support that feature), but you must have the freedom to redistribute such forms should you find or develop a way to make them.",
          "In order for the freedoms to make changes, and to publish improved versions, to be meaningful, you must have access to the source code of the program. Therefore, accessibility of source code is a necessary condition for free software.",
          "In order for these freedoms to be real, they must be irrevocable as long as you do nothing wrong; if the developer of the software has the power to revoke the license, without your doing anything to give cause, the software is not free.",
          "However, certain kinds of rules about the manner of distributing free software are acceptable, when they don't conflict with the central freedoms. For example, copyleft (very simply stated) is the rule that when redistributing the program, you cannot add restrictions to deny other people the central freedoms. This rule does not conflict with the central freedoms; rather it protects them.",
          "You may have paid money to get copies of free software, or you may have obtained copies at no charge. But regardless of how you got your copies, you always have the freedom to copy and change the software, even to sell copies.",
          "\"Free software\" does not mean \"non-commercial\". A free program must be available for commercial use, commercial development, and commercial distribution. Commercial development of free software is no longer unusual; such free commercial software is very important.",
          "Rules about how to package a modified version are acceptable, if they don't substantively block your freedom to release modified versions. Rules that \"if you make the program available in this way, you must make it available in that way also\" can be acceptable too, on the same condition. (Note that such a rule still leaves you the choice of whether to publish the program or not.) It is also acceptable for the license to require that, if you have distributed a modified version and a previous developer asks for a copy of it, you must send one, or that you identify yourself on your modifications.",
          "In the GNU project, we use \"copyleft\" to protect these freedoms legally for everyone. But non-copylefted free software also exists. We believe there are important reasons why it is better to use copyleft, but if your program is non-copylefted free software, we can still use it.",
          "See Categories of Free Software for a description of how \"free software,\" \"copylefted software\" and other categories of software relate to each other.",
          "Sometimes government export control regulations and trade sanctions can constrain your freedom to distribute copies of programs internationally. Software developers do not have the power to eliminate or override these restrictions, but what they can and must do is refuse to impose them as conditions of use of the program. In this way, the restrictions will not affect activities and people outside the jurisdictions of these governments.",
          "Most free software licenses are based on copyright, and there are limits on what kinds of requirements can be imposed through copyright. If a copyright-based license respects freedom in the ways described above, it is unlikely to have some other sort of problem that we never anticipated (though this does happen occasionally). However, some free software licenses are based on contracts, and contracts can impose a much larger range of possible restrictions. That means there are many possible ways such a license could be unacceptably restrictive and non-free.",
          "We can't possibly list all the possible contract restrictions that would be unacceptable. If a contract-based license restricts the user in an unusual way that copyright-based licenses cannot, and which isn't mentioned here as legitimate, we will have to think about it, and we will probably decide it is non-free.",
          "When talking about free software, it is best to avoid using terms like \"give away\" or \"for free\", because those terms imply that the issue is about price, not freedom. Some common terms such as \"piracy\" embody opinions we hope you won't endorse. See Confusing Words and Phrases that are Worth Avoiding for a discussion of these terms. We also have a list of translations of \"free software\" into various languages.",
          "Finally, note that criteria such as those stated in this free software definition require careful thought for their interpretation. To decide whether a specific software license qualifies as a free software license, we judge it based on these criteria to determine whether it fits their spirit as well as the precise words. If a license includes unconscionable restrictions, we reject it, even if we did not anticipate the issue in these criteria. Sometimes a license requirement raises an issue that calls for extensive thought, including discussions with a lawyer, before we can decide if the requirement is acceptable. When we reach a conclusion about a new issue, we often update these criteria to make it easier to see why certain licenses do or don't qualify.",
          "If you are interested in whether a specific license qualifies as a free software license, see our list of licenses. If the license you are concerned with is not listed there, you can ask us about it by sending us email at <licensing@fsf.org>.",
          "If you are contemplating writing a new license, please contact the FSF by writing to that address. The proliferation of different free software licenses means increased work for users in understanding the licenses; we may be able to help you find an existing Free Software license that meets your needs.",
          "If that isn't possible, if you really need a new license, with our help you can ensure that the license really is a Free Software license and avoid various practical problems.",
          "Another group has started using the term \"open source\" to mean something close (but not identical) to \"free software\". We prefer the term \"free software\" because, once you have heard it refers to freedom rather than price, it calls to mind freedom. The word \"open\" never does that."
        };
      int paragraphIndex = 0;
      int paragraphTextIndex = 0;
      int frameIndex = -1;
      for(
        int paragraphCount = paragraphs.Length;
        paragraphIndex < paragraphCount;
        paragraphIndex++
        )
      {
        String paragraph = paragraphs[paragraphIndex];

        paragraphTextIndex = blockComposer.ShowText(paragraph.Substring(paragraphTextIndex)) + paragraphTextIndex;
        if(paragraphTextIndex < paragraph.Length)
        {
          if(++frameIndex < frames.Length)
          {
            blockComposer.End();

            // Add the bead to the article thread!
            articleElements.Add(new ArticleElement(page, blockComposer.BoundBox));

            // New page?
            if(frameIndex == 3)
            {
              // Close current page!
              composer.Flush();

              // Create a new page!
              document.Pages.Add(page = new Page(document));
              composer = new PrimitiveComposer(page);
              // Add the background template!
              composer.ShowXObject(template);
              blockComposer = new BlockComposer(composer);
              blockComposer.Hyphenation = true;
            }

            blockComposer.Begin(frames[frameIndex],XAlignmentEnum.Justify,yAlignments[frameIndex]);
            composer.SetFont(font,8.25f);

            // Come back to complete the interrupted paragraph!
            paragraphIndex--;
          }
          else
          {break;}
        }
        else
        {
          paragraphTextIndex = 0;

          blockComposer.ShowBreak(breakSize);
        }
      }
      blockComposer.End();

      // Add the bead to the article thread!
      articleElements.Add(new ArticleElement(page, blockComposer.BoundBox));

      blockComposer.Begin(frames[frames.Length-1],XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
      composer.SetFont(font,6);
      blockComposer.ShowText("This article was crafted with the nice Traveling_Typewriter font (by Carl Krull, www.carlkrull.dk).");
      blockComposer.End();

      composer.Flush();
    }

    private void BuildWelcomePage(
      Document document,
      FormXObject template
      )
    {
      // Add welcome page to the document!
      Page page = new Page(document); // Instantiates the page inside the document context.
      document.Pages.Add(page); // Puts the page in the pages collection.
      SizeF pageSize = page.Size;

      PrimitiveComposer composer = new PrimitiveComposer(page);
      // Add the background template!
      composer.ShowXObject(template);
      // Wrap the content composer inside a block composer in order to achieve higher-level typographic control!
      /*
        NOTE: BlockComposer provides block-level typographic features as text and paragraph alignment.
        Flow-level typographic features are currently not supported: block-level typographic features
        are the foundations upon which flow-level typographic features will sit.
      */
      BlockComposer blockComposer = new BlockComposer(composer);

      SizeF breakSize = new SizeF(0,20); // Size of a paragraph break.
      // Instantiate the page body's font!
      fonts::Font font = fonts::Font.Get(
        document,
        GetResourcePath("fonts" + Path.DirectorySeparatorChar + "lazyDog.ttf")
        );

      // Showing the page title...
      // Define the box frame to force the page title within!
      RectangleF frame = new RectangleF(
        20,
        150,
        (float)pageSize.Width - 90,
        (float)pageSize.Height - 250
        );
      // Begin the block!
      blockComposer.Begin(frame,XAlignmentEnum.Center,YAlignmentEnum.Top);
      // Set the font to use!
      composer.SetFont(font,56);
      // Set the text rendering mode (outline only)!
      composer.SetTextRenderMode(TextRenderModeEnum.Stroke);
      // Show the page title!
      blockComposer.ShowText("Welcome");
      // End the block!
      blockComposer.End();

      // Showing the clown photo...
      // Instantiate a jpeg image object!
      entities::Image image = entities::Image.Get(GetResourcePath("images" + Path.DirectorySeparatorChar + "Clown.jpg")); // Abstract image (entity).
      PointF imageLocation = new PointF(
        blockComposer.BoundBox.X + blockComposer.BoundBox.Width - image.Width,
        blockComposer.BoundBox.Y + blockComposer.BoundBox.Height + 25
        );
      // Show the image!
      composer.ShowXObject(
        image.ToXObject(document),
        imageLocation
        );

      RectangleF descriptionFrame = new RectangleF(
        imageLocation.X,
        imageLocation.Y + image.Height + 5,
        image.Width,
        20
        );

      frame = new RectangleF(
        blockComposer.BoundBox.X,
        imageLocation.Y,
        blockComposer.BoundBox.Width - image.Width - 20,
        image.Height
        );
      blockComposer.Begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Middle);
      {
        composer.SetFont(font,30);
        blockComposer.ShowText("This is a sample document that merely demonstrates some basic graphics features supported by PDF Clown.");
        blockComposer.ShowBreak(XAlignmentEnum.Center);
        blockComposer.ShowText("Enjoy!");
      }
      blockComposer.End();

      frame = new RectangleF(
        blockComposer.BoundBox.X,
        blockComposer.BoundBox.Y+blockComposer.BoundBox.Height,
        pageSize.Width - 90,
        pageSize.Height - 100 - (blockComposer.BoundBox.Y+blockComposer.BoundBox.Height)
        );
      blockComposer.Begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
      {
        composer.SetFont(font,14);
        blockComposer.ShowText("PS: As promised, since version 0.0.3 PDF Clown has supported");
        // Begin local state!
        /*
          NOTE: Local state is a powerful feature of PDF format as it lets you nest
          multiple graphics contexts on the graphics state stack.
        */
        composer.BeginLocalState();
        {
          composer.SetFillColor(TextColor_Highlight);
          blockComposer.ShowText(" embedded latin OpenFont/TrueType and non-embedded Type 1 fonts");
        }
        composer.End();
        blockComposer.ShowText(" along with");
        composer.BeginLocalState();
        {
          composer.SetFillColor(TextColor_Highlight);
          blockComposer.ShowText(" paragraph construction facilities");
        }
        composer.End();
        blockComposer.ShowText(" through the BlockComposer class.");
        blockComposer.ShowBreak(breakSize);

        blockComposer.ShowText("Since version 0.0.4 the content stream stack has been completed, providing ");
        composer.BeginLocalState();
        {
          composer.SetFillColor(TextColor_Highlight);
          blockComposer.ShowText("fully object-oriented access to the graphics objects that describe the contents on a page.");
        }
        composer.End();
        blockComposer.ShowText(" It's a great step towards a whole bunch of possibilities, such as text extraction/replacement, that next releases will progressively exploit.");
        blockComposer.ShowBreak(breakSize);

        blockComposer.ShowText("Since version 0.0.6 it has supported ");
        composer.BeginLocalState();
        {
          composer.SetFillColor(TextColor_Highlight);
          blockComposer.ShowText("Unicode");
        }
        composer.End();
        blockComposer.ShowText(" for OpenFont/TrueType fonts.");
        blockComposer.ShowBreak(breakSize);

        composer.SetFont(font,8);
        blockComposer.ShowText("This page was crafted with the nice");
        composer.BeginLocalState();
        {
          composer.SetFont(font,10);
          blockComposer.ShowText(" LazyDog font");
        }
        composer.End();
        blockComposer.ShowText(" (by Paul Neave, www.neave.com)");
      }
      blockComposer.End();

      blockComposer.Begin(descriptionFrame,XAlignmentEnum.Right,YAlignmentEnum.Top);
      {
        composer.SetFont(font,8);
        blockComposer.ShowText("Source: http://www.wikipedia.org/");
      }
      blockComposer.End();

      composer.Flush();
    }

    private FormXObject BuildTemplate(
      Document document,
      DateTime creationDate
      )
    {
      // Create a template (form)!
      FormXObject template = new FormXObject(document, document.PageSize.Value);
      SizeF templateSize = template.Size;

      // Get form content stream!
      PrimitiveComposer composer = new PrimitiveComposer(template);

      // Showing the header image inside the common content stream...
      // Instantiate a jpeg image object!
      entities::Image image = entities::Image.Get(GetResourcePath("images" + Path.DirectorySeparatorChar + "mountains.jpg")); // Abstract image (entity).
      // Show the image inside the common content stream!
      composer.ShowXObject(
        image.ToXObject(document),
        new PointF(0,0),
        new SizeF(templateSize.Width - 50, 125)
        );

      // Showing the 'PDFClown' label inside the common content stream...
      composer.BeginLocalState();
      composer.SetFillColor(new colorSpaces::DeviceRGBColor(115f / 255, 164f / 255, 232f / 255));
      // Set the font to use!
      composer.SetFont(
        new fonts::StandardType1Font(
          document,
          fonts::StandardType1Font.FamilyEnum.Times,
          true,
          false
          ),
        120
        );
      // Show the text!
      composer.ShowText(
        "PDFClown",
        new PointF(
          0,
          templateSize.Height - (float)composer.State.Font.GetAscent(composer.State.FontSize)
          )
        );

      // Drawing the side rectangle...
      composer.DrawRectangle(
        new RectangleF(
          (float)templateSize.Width - 50,
          0,
          50,
          (float)templateSize.Height
          )
        );
      composer.Fill();
      composer.End();

      // Showing the side text inside the common content stream...
      composer.BeginLocalState();
      {
        composer.SetFont(
          new fonts::StandardType1Font(
            document,
            fonts::StandardType1Font.FamilyEnum.Helvetica,
            false,
            false
            ),
          8
          );
        composer.SetFillColor(colorSpaces::DeviceRGBColor.White);
        composer.BeginLocalState();
        {
          composer.Rotate(
            90,
            new PointF(
              templateSize.Width - 50,
              templateSize.Height - 25
              )
            );
          BlockComposer blockComposer = new BlockComposer(composer);
          blockComposer.Begin(
            new RectangleF(0,0,300,50),
            XAlignmentEnum.Left,
            YAlignmentEnum.Middle
            );
          {
            blockComposer.ShowText("Generated by PDF Clown on " + creationDate);
            blockComposer.ShowBreak();
            blockComposer.ShowText("For more info, visit http://www.pdfclown.org");
          }
          blockComposer.End();
        }
        composer.End();
      }
      composer.End();

      composer.Flush();

      return template;
    }
  }
}

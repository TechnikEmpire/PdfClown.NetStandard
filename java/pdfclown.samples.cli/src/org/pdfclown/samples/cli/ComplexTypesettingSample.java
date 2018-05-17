package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.EnumSet;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.PageFormat;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.contents.TextRenderModeEnum;
import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.interaction.actions.GoToURI;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmark.FlagsEnum;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.documents.interaction.navigation.page.Article;
import org.pdfclown.documents.interaction.navigation.page.ArticleElement;
import org.pdfclown.documents.interaction.navigation.page.ArticleElements;
import org.pdfclown.documents.interaction.viewer.ViewerPreferences.PageModeEnum;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.files.File;
import org.pdfclown.util.math.geom.Dimension;

/**
  This sample demonstrates <b>how to create a new PDF document populating it with various
  graphics elements</b>.
  <h3>Remarks</h3>
  <p>This implementation features an enlightening example of an embryonic typesetter
  that exploits the new typographic primitives defined in PDF Clown (see BlockComposer
  class in use); this is just a humble experiment -- anybody could develop a typesetter
  sitting upon PDF Clown!</p>
  <p>Anyway, PDF Clown currently lacks support for content flow composition (i.e. paragraphs spread
  across multiple pages): since 0.0.3 release offers a static-composition facility (BlockComposer class)
  that is meant to be the base for more advanced functionalities (such as the above-mentioned
  content flow composition), to be made available in the next releases.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2.1, 05/02/15
*/
public class ComplexTypesettingSample
  extends Sample
{
  private static final Color<?> TextColor_Highlight = new DeviceRGBColor(255 / 255d, 50 / 255d, 50 / 255d);

  @Override
  public void run(
    )
  {
    // 1. PDF file instantiation.
    File file = new File();
    Document document = file.getDocument();
    // Set default page size (A4)!
    document.setPageSize(PageFormat.getSize());

    // 2. Content creation.
    Date creationDate = new Date();
    // 2.1. Template.
    FormXObject template = buildTemplate(document, creationDate);
    // 2.2. Welcome page.
    buildWelcomePage(document, template);
    // 2.3. Free Software definition.
    buildFreeSoftwareDefinitionPages(document, template);
    // 2.4. Bookmarks.
    buildBookmarks(document);

    // 3. Serialization.
    serialize(file, "Complex Typesetting", "complex typesetting", "typesetting, bookmarks, hyphenation, block composer, primitive composer, text alignment, image insertion, article threads");
  }

  private void buildBookmarks(
    Document document
    )
  {
    Pages pages = document.getPages();
    Bookmarks bookmarks = document.getBookmarks();
    Page page = pages.get(0);
    Bookmark rootBookmark = new Bookmark(
      document,
      "Creation Sample",
      new LocalDestination(page)
      );
    bookmarks.add(rootBookmark);
    bookmarks = rootBookmark.getBookmarks();
    page = pages.get(1);
    Bookmark bookmark = new Bookmark(
      document,
      "2nd page (close-up view)",
      new LocalDestination(
        page,
        Destination.ModeEnum.XYZ,
        new Point2D.Double(0, 250),
        2d
        )
      );
    bookmarks.add(bookmark);
    bookmark.getBookmarks().add(
      new Bookmark(
        document,
        "2nd page (mid view)",
        new LocalDestination(
          page,
          Destination.ModeEnum.XYZ,
          new Point2D.Double(0, page.getSize().getHeight() - 250),
          1d
          )
        )
      );
    page = pages.get(2);
    bookmarks.add(
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
    try
    {
      bookmarks.add(
        bookmark = new Bookmark(
          document,
          "PDF Clown Home Page",
          new GoToURI(document, new URI("http://www.pdfclown.org"))
          )
        );
      bookmark.setFlags(EnumSet.of(FlagsEnum.Bold, FlagsEnum.Italic));
      bookmark.setColor(new DeviceRGBColor(.5, .5, 1));
    }
    catch(URISyntaxException e)
    {/* NOOP */}
    
    document.getViewerPreferences().setPageMode(PageModeEnum.Bookmarks);
  }

  private void buildFreeSoftwareDefinitionPages(
    Document document,
    FormXObject template
    )
  {
    // Add page!
    Page page = new Page(document);
    document.getPages().add(page);
    Dimension2D pageSize = page.getSize();

    String title = "The Free Software Definition";

    // Create the article thread!
    Article article = new Article(document);
    {
      Information articleInfo = article.getInformation();
      articleInfo.setTitle(title);
      articleInfo.setAuthor("Free Software Foundation, Inc.");
    }
    // Get the article beads collection to populate!
    ArticleElements articleElements = article.getElements();

    PrimitiveComposer composer = new PrimitiveComposer(page);
    // Add the background template!
    composer.showXObject(template);
    // Wrap the content composer inside a block composer in order to achieve higher-level typographic control!
    /*
      NOTE: BlockComposer provides block-level typographic features as text and paragraph alignment.
      Flow-level typographic features are currently not supported: block-level typographic features
      are the foundations upon which flow-level typographic features will sit.
    */
    BlockComposer blockComposer = new BlockComposer(composer);
    blockComposer.setHyphenation(true);

    Dimension breakSize = new Dimension(0,10);
    // Add the font to the document!
    Font font = Font.get(document, getResourcePath("fonts" + java.io.File.separator + "TravelingTypewriter.otf"));

    Rectangle2D frame = new Rectangle2D.Double(
      20,
      150,
      (pageSize.getWidth() - 90 - 20) / 2,
      pageSize.getHeight() - 250
      );

    // Showing the 'GNU' image...
    // Instantiate a jpeg image object!
    Image image = Image.get(getResourcePath("images" + java.io.File.separator + "gnu.jpg")); // Abstract image (entity).
    // Show the image!
    composer.showXObject(
      image.toXObject(document),
      new Point2D.Double(
        (pageSize.getWidth() - 90 - image.getWidth()) / 2 + 20,
        pageSize.getHeight() - 100 - image.getHeight()
        )
      );

    // Showing the title...
    blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
    composer.setFont(font,24);
    blockComposer.showText(title);
    blockComposer.end();

    // Showing the copyright note...
    frame = new Rectangle2D.Double(
      blockComposer.getBoundBox().getX(),
      blockComposer.getBoundBox().getY() + blockComposer.getBoundBox().getHeight() + 32,
      blockComposer.getBoundBox().getWidth(),
      (pageSize.getHeight() - 100 - image.getHeight() - 10) - (blockComposer.getBoundBox().getY() + blockComposer.getBoundBox().getHeight() + 32)
      );
    blockComposer.begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
    composer.setFont(font,6);
    blockComposer.showText("Copyright 2004, 2005, 2006 Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA Verbatim copying and distribution of this entire article are permitted worldwide, without royalty, in any medium, provided this notice is preserved.");

    // Showing the body...
    blockComposer.showBreak(breakSize);
    composer.setFont(font,8.25f);
    Rectangle2D[] frames = new Rectangle2D[]
      {
        new Rectangle2D.Double(
          blockComposer.getBoundBox().getX(),
          pageSize.getHeight() - 100 - image.getHeight() - 10,
          blockComposer.getBoundBox().getWidth()-image.getWidth()/2,
          image.getHeight() + 10
          ),
        new Rectangle2D.Double(
          20 + 20 + (pageSize.getWidth() - 90 - 20) / 2,
          150,
          (pageSize.getWidth() - 90 - 20) / 2,
          (pageSize.getHeight() - 100 - image.getHeight() - 10) - 150
          ),
        new Rectangle2D.Double(
          20 + 20 + (pageSize.getWidth() - 90 - 20) / 2 + image.getWidth()/2,
          pageSize.getHeight() - 100 - image.getHeight() - 10,
          blockComposer.getBoundBox().getWidth()-image.getWidth()/2,
          image.getHeight() + 10
          ),
        new Rectangle2D.Double(
          20,
          150,
          (pageSize.getWidth() - 90 - 20) / 2,
          (pageSize.getHeight() - 100) - 150
          ),
        new Rectangle2D.Double(
          20 + 20 + (pageSize.getWidth() - 90 - 20) / 2,
          150,
          (pageSize.getWidth() - 90 - 20) / 2,
          (pageSize.getHeight() - 100) - 150
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
      int paragraphCount = paragraphs.length;
      paragraphIndex < paragraphCount;
      paragraphIndex++
      )
    {
      String paragraph = paragraphs[paragraphIndex];

      paragraphTextIndex = blockComposer.showText(paragraph.substring(paragraphTextIndex)) + paragraphTextIndex;
      if(paragraphTextIndex < paragraph.length())
      {
        if(++frameIndex < frames.length)
        {
          blockComposer.end();

          // Add the bead to the article thread!
          articleElements.add(new ArticleElement(page, blockComposer.getBoundBox()));

          // New page?
          if(frameIndex == 3)
          {
            // Close current page!
            composer.flush();

            // Create a new page!
            document.getPages().add(page = new Page(document));
            composer = new PrimitiveComposer(page);
            // Add the background template!
            composer.showXObject(template);
            blockComposer = new BlockComposer(composer);
            blockComposer.setHyphenation(true);
          }

          blockComposer.begin(frames[frameIndex],XAlignmentEnum.Justify,yAlignments[frameIndex]);
          composer.setFont(font,8.25f);

          // Come back to complete the interrupted paragraph!
          paragraphIndex--;
        }
        else
        {break;}
      }
      else
      {
        paragraphTextIndex = 0;

        blockComposer.showBreak(breakSize);
      }
    }
    blockComposer.end();

    // Add the last bead to the article thread!
    articleElements.add(new ArticleElement(page, blockComposer.getBoundBox()));

    blockComposer.begin(frames[frames.length-1],XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
    composer.setFont(font,6);
    blockComposer.showText("This article was crafted with the nice Traveling_Typewriter font (by Carl Krull, www.carlkrull.dk).");
    blockComposer.end();

    composer.flush();
  }

  private void buildWelcomePage(
    Document document,
    FormXObject template
    )
  {
    // Add welcome page to the document!
    Page page = new Page(document); // Instantiates the page inside the document context.
    document.getPages().add(page); // Puts the page in the pages collection.
    Dimension2D pageSize = page.getSize();

    PrimitiveComposer composer = new PrimitiveComposer(page);
    // Add the background template!
    composer.showXObject(template);
    // Wrap the content composer inside a block composer in order to achieve higher-level typographic control!
    /*
      NOTE: BlockComposer provides block-level typographic features as text and paragraph alignment.
      Flow-level typographic features are currently not supported: block-level typographic features
      are the foundations upon which flow-level typographic features will sit.
    */
    BlockComposer blockComposer = new BlockComposer(composer);

    Dimension breakSize = new Dimension(0,20); // Size of a paragraph break.
    // Instantiate the page body's font!
    Font font = Font.get(
      document,
      getResourcePath("fonts" + java.io.File.separator + "lazyDog.ttf")
      );

    // Showing the page title...
    // Define the box frame to force the page title within!
    Rectangle2D frame = new Rectangle2D.Double(
      20,
      150,
      pageSize.getWidth() - 90,
      pageSize.getHeight() - 250
      );
    // Begin the block!
    blockComposer.begin(frame,XAlignmentEnum.Center,YAlignmentEnum.Top);
    // Set the font to use!
    composer.setFont(font,56);
    // Set the text rendering mode (outline only)!
    composer.setTextRenderMode(TextRenderModeEnum.Stroke);
    // Show the page title!
    blockComposer.showText("Welcome");
    // End the block!
    blockComposer.end();

    // Showing the clown photo...
    // Instantiate a jpeg image object!
    Image image = Image.get(getResourcePath("images" + java.io.File.separator + "Clown.jpg")); // Abstract image (entity).
    Point2D imageLocation = new Point2D.Double(
      blockComposer.getBoundBox().getX() + blockComposer.getBoundBox().getWidth() - image.getWidth(),
      blockComposer.getBoundBox().getY() + blockComposer.getBoundBox().getHeight() + 25
      );
    // Show the image!
    composer.showXObject(
      image.toXObject(document),
      imageLocation
      );

    Rectangle2D descriptionFrame = new Rectangle2D.Double(
      imageLocation.getX(),
      imageLocation.getY() + image.getHeight() + 5,
      image.getWidth(),
      20
      );

    frame = new Rectangle2D.Double(
      blockComposer.getBoundBox().getX(),
      imageLocation.getY(),
      blockComposer.getBoundBox().getWidth() - image.getWidth() - 20,
      image.getHeight()
      );
    blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Middle);
    {
      composer.setFont(font,30);
      blockComposer.showText("This is a sample document that merely demonstrates some basic graphics features supported by PDF Clown.");
      blockComposer.showBreak(XAlignmentEnum.Center);
      blockComposer.showText("Enjoy!");
    }
    blockComposer.end();

    frame = new Rectangle2D.Double(
      blockComposer.getBoundBox().getX(),
      blockComposer.getBoundBox().getY()+blockComposer.getBoundBox().getHeight(),
      pageSize.getWidth() - 90,
      pageSize.getHeight() - 100 - (blockComposer.getBoundBox().getY()+blockComposer.getBoundBox().getHeight())
      );
    blockComposer.begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Bottom);
    {
      composer.setFont(font,14);
      blockComposer.showText("PS: As promised, since version 0.0.3 PDF Clown has supported");
      // Begin local state!
      /*
        NOTE: Local state is a powerful feature of PDF format as it lets you nest
        multiple graphics contexts on the graphics state stack.
      */
      composer.beginLocalState();
      {
        composer.setFillColor(TextColor_Highlight);
        blockComposer.showText(" embedded latin OpenFont/TrueType and non-embedded Type 1 fonts");
      }
      composer.end();
      blockComposer.showText(" along with");
      composer.beginLocalState();
      {
        composer.setFillColor(TextColor_Highlight);
        blockComposer.showText(" paragraph construction facilities");
      }
      composer.end();
      blockComposer.showText(" through the BlockComposer class.");
      blockComposer.showBreak(breakSize);

      blockComposer.showText("Since version 0.0.4 the content stream stack has been completed, providing ");
      composer.beginLocalState();
      {
        composer.setFillColor(TextColor_Highlight);
        blockComposer.showText("fully object-oriented access to the graphics objects that describe the contents on a page.");
      }
      composer.end();
      blockComposer.showText(" It's a great step towards a whole bunch of possibilities, such as text extraction/replacement, that next releases will progressively exploit.");
      blockComposer.showBreak(breakSize);

      blockComposer.showText("Since version 0.0.6 it has supported ");
      composer.beginLocalState();
      {
        composer.setFillColor(TextColor_Highlight);
        blockComposer.showText("Unicode");
      }
      composer.end();
      blockComposer.showText(" for OpenFont/TrueType fonts.");
      blockComposer.showBreak(breakSize);

      composer.setFont(font,8);
      blockComposer.showText("This page was crafted with the nice");
      composer.beginLocalState();
      {
        composer.setFont(font,10);
        blockComposer.showText(" LazyDog font");
      }
      composer.end();
      blockComposer.showText(" (by Paul Neave, www.neave.com)");
    }
    blockComposer.end();

    blockComposer.begin(descriptionFrame,XAlignmentEnum.Right,YAlignmentEnum.Top);
    {
      composer.setFont(font,8);
      blockComposer.showText("Source: http://www.wikipedia.org/");
    }
    blockComposer.end();

    composer.flush();
  }

  private FormXObject buildTemplate(
    Document document,
    Date creationDate
    )
  {
    // Create a template (form)!
    FormXObject template = new FormXObject(document, document.getPageSize());
    Dimension2D templateSize = template.getSize();

    // Get form content stream!
    PrimitiveComposer composer = new PrimitiveComposer(template);

    // Showing the header image inside the common content stream...
    // Instantiate a jpeg image object!
    Image image = Image.get(getResourcePath("images" + java.io.File.separator + "mountains.jpg")); // Abstract image (entity).
    // Show the image inside the common content stream!
    composer.showXObject(
      image.toXObject(document),
      new Point2D.Double(0,0),
      new Dimension(templateSize.getWidth() - 50, 125)
      );

    // Showing the 'PDFClown' label inside the common content stream...
    composer.beginLocalState();
    composer.setFillColor(new DeviceRGBColor(115 / 255d, 164 / 255d, 232 / 255d));
    // Set the font to use!
    composer.setFont(
      new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Times,
        true,
        false
        ),
      120
      );
    // Show the text!
    composer.showText(
      "PDFClown",
      new Point2D.Double(
        0,
        templateSize.getHeight() - composer.getState().getFont().getAscent(composer.getState().getFontSize())
        )
      );

    // Drawing the side rectangle...
    composer.drawRectangle(
      new Rectangle2D.Double(
        templateSize.getWidth() - 50,
        0,
        50,
        templateSize.getHeight()
        )
      );
    composer.fill();
    composer.end();

    // Showing the side text inside the common content stream...
    composer.beginLocalState();
    {
      composer.setFont(
        new StandardType1Font(
          document,
          StandardType1Font.FamilyEnum.Helvetica,
          false,
          false
          ),
        8
        );
      composer.setFillColor(DeviceRGBColor.White);
      composer.beginLocalState();
      {
        composer.rotate(
          90,
          new Point2D.Double(
            templateSize.getWidth() - 50,
            templateSize.getHeight() - 25
            )
          );
        BlockComposer blockComposer = new BlockComposer(composer);
        blockComposer.begin(
          new Rectangle2D.Double(0,0,300,50),
          XAlignmentEnum.Left,
          YAlignmentEnum.Middle
          );
        {
          blockComposer.showText("Generated by PDF Clown on " + creationDate);
          blockComposer.showBreak();
          blockComposer.showText("For more info, visit http://www.pdfclown.org");
        }
        blockComposer.end();
      }
      composer.end();
    }
    composer.end();

    composer.flush();

    return template;
  }
}
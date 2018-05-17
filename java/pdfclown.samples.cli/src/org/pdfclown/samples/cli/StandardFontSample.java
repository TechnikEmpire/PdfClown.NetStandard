package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.EnumSet;
import java.util.TreeSet;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.EncodingFallbackEnum;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;
import org.pdfclown.tokens.EncodeException;

/**
  This sample demonstrates <b>how to use of standard Type 1 fonts</b>, which are the14 built-in fonts
  prescribed by the PDF specification to be shipped along with any conformant PDF viewer.
  <p>In particular, this sample displays the complete glyphset of each standard font, iterating through
  character codes and glyph styles (regular, italic, bold).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 04/08/15
*/
public class StandardFontSample
  extends Sample
{
  private static final int FontBaseSize = 20;
  private static final int Margin = 50;

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
    serialize(file, "Standard Type 1 fonts", "applying standard Type 1 fonts", "Standard Type1 fonts");
  }

  private void populate(
    Document document
    )
  {
    Page page = new Page(document);
    document.getPages().add(page);
    Dimension2D pageSize = page.getSize();

    /*
      NOTE: Default fallback behavior on text encoding mismatch is substitution with default 
      character; in this case, we want to force an exception to be thrown so we can explicitly
      handle the issue.
    */
    document.getConfiguration().setEncodingFallback(EncodingFallbackEnum.Exception);
    
    PrimitiveComposer composer = new PrimitiveComposer(page);

    int x = Margin, y = Margin;
    StandardType1Font titleFont = new StandardType1Font(
      document,
      StandardType1Font.FamilyEnum.Times,
      true,
      true
      );
    StandardType1Font font = null;
    // Iterating through the standard Type 1 fonts...
    for(StandardType1Font.FamilyEnum fontFamily
      : EnumSet.allOf(StandardType1Font.FamilyEnum.class))
    {
      // Iterating through the font styles...
      for(int styleIndex = 0; styleIndex < 4; styleIndex++)
      {
        /*
          NOTE: Symbol and Zapf Dingbats are available just as regular fonts (no italic or bold variant).
        */
        if(styleIndex > 0
          && (fontFamily == StandardType1Font.FamilyEnum.Symbol
            || fontFamily == StandardType1Font.FamilyEnum.ZapfDingbats))
            break;

        boolean bold = (styleIndex & 1) > 0;
        boolean italic = (styleIndex & 2) > 0;

        // Define the font used to show its character set!
        font = new StandardType1Font(document, fontFamily, bold, italic);

        if(y > pageSize.getHeight() - Margin)
        {
          composer.flush();

          page = new Page(document);
          document.getPages().add(page);
          pageSize = page.getSize();
          composer = new PrimitiveComposer(page);
          x = Margin;
          y = Margin;
        }

        if(styleIndex == 0)
        {
          composer.drawLine(
            new Point2D.Double(x, y),
            new Point2D.Double(pageSize.getWidth() - Margin, y)
            );
          composer.stroke();
          y += 5;
        }

        composer.setFont(
          titleFont,
          FontBaseSize * (styleIndex == 0 ? 1.5f : 1)
          );
        composer.showText(
          fontFamily.name() + (bold ? " bold" : "") + (italic ? " italic" : ""),
          new Point2D.Double(x, y)
          );

        y += 40;
        // Set the font used to show its character set!
        composer.setFont(font,FontBaseSize);
        // Iterating through the font characters...
        for(int charCode : new TreeSet<Integer>(font.getCodePoints()))
        {
          if(y > pageSize.getHeight() - Margin)
          {
            composer.flush();

            page = new Page(document);
            document.getPages().add(page);
            pageSize = page.getSize();
            composer = new PrimitiveComposer(page);
            x = Margin;
            y = Margin;

            composer.setFont(titleFont,FontBaseSize);
            composer.showText(
              fontFamily.name() + " (continued)",
              new Point2D.Double(pageSize.getWidth() - Margin, y),
              XAlignmentEnum.Right,
              YAlignmentEnum.Top,
              0
              );
            composer.setFont(font,FontBaseSize);
            y += FontBaseSize * 2;
          }

          try
          {
            // Show the character!
            composer.showText(
              String.valueOf((char)charCode),
              new Point2D.Double(x, y)
              );
            x += FontBaseSize;
            if(x > pageSize.getWidth() - Margin)
            {x = Margin; y += 30;}
          }
          catch(EncodeException e)
          {
            /*
              NOOP -- NOTE: document.getConfiguration().setEncodingFallback() allows to customize
              the behavior in case of missing character: we can alternatively catch an exception,
              have the character substituted by a default one (typically '?' symbol) or have the
              character silently removed.
            */
          }
        }

        x = Margin;
        y += Margin;
      }
    }
    composer.flush();
  }
}
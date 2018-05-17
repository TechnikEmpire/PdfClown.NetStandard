package org.pdfclown.samples.cli;

import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.TextChar;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.Text;
import org.pdfclown.documents.contents.objects.XObject;
import org.pdfclown.files.File;
import org.pdfclown.tools.PageStamper;

/**
  This sample demonstrates <b>how to retrieve text content along with its graphic attributes</b>
  (font, font size, text color, text rendering mode, text bounding box...) from a PDF document;
  it also generates a document version decorated by text bounding boxes.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2.1, 04/30/15
*/
public class TextInfoExtractionSample
  extends Sample
{
  private final DeviceRGBColor[] textCharBoxColors = new DeviceRGBColor[]
    {
      new DeviceRGBColor(200 / 255d, 100 / 255d, 100 / 255d),
      new DeviceRGBColor(100 / 255d, 200 / 255d, 100 / 255d),
      new DeviceRGBColor(100 / 255d, 100 / 255d, 200 / 255d)
    };
  private final DeviceRGBColor textStringBoxColor = DeviceRGBColor.Black;

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

    PageStamper stamper = new PageStamper(); // NOTE: Page stamper is used to draw contents on existing pages.

    // 2. Iterating through the document pages...
    for(Page page : document.getPages())
    {
      System.out.println("\nScanning page " + page.getNumber() + "...\n");

      stamper.setPage(page);

      extract(
        new ContentScanner(page), // Wraps the page contents into a scanner.
        stamper.getForeground()
        );

      stamper.flush();
    }

    // 3. Decorated version serialization.
    serialize(file);
  }

  /**
    Scans a content level looking for text.
  */
  /*
    NOTE: Page contents are represented by a sequence of content objects,
    possibly nested into multiple levels.
  */
  private void extract(
    ContentScanner level,
    PrimitiveComposer composer
    )
  {
    if(level == null)
      return;

    while(level.moveNext())
    {
      ContentObject content = level.getCurrent();
      if(content instanceof Text)
      {
        ContentScanner.TextWrapper text = (ContentScanner.TextWrapper)level.getCurrentWrapper();
        int colorIndex = 0;
        for(ContentScanner.TextStringWrapper textString : text.getTextStrings())
        {
          Rectangle2D textStringBox = textString.getBox();
          System.out.println(
            "Text ["
              + "x:" + Math.round(textStringBox.getX()) + ","
              + "y:" + Math.round(textStringBox.getY()) + ","
              + "w:" + Math.round(textStringBox.getWidth()) + ","
              + "h:" + Math.round(textStringBox.getHeight())
              + "] [font size:" + Math.round(textString.getStyle().getFontSize()) + "]: " + textString.getText()
            );

          // Drawing text character bounding boxes...
          colorIndex = (colorIndex + 1) % textCharBoxColors.length;
          composer.setStrokeColor(textCharBoxColors[colorIndex]);
          for(TextChar textChar : textString.getTextChars())
          {
            /*
              NOTE: You can get further text information
              (font, font size, text color, text rendering mode)
              through textChar.style.
            */
            composer.drawRectangle(textChar.getBox());
            composer.stroke();
          }

          // Drawing text string bounding box...
          composer.beginLocalState();
          composer.setLineDash(new LineDash(new double[]{5}));
          composer.setStrokeColor(textStringBoxColor);
          composer.drawRectangle(textString.getBox());
          composer.stroke();
          composer.end();
        }
      }
      else if(content instanceof XObject)
      {
        // Scan the external level!
        extract(
          ((XObject)content).getScanner(level),
          composer
          );
      }
      else if(content instanceof ContainerObject)
      {
        // Scan the inner level!
        extract(
          level.getChildLevel(),
          composer
          );
      }
    }
  }
}

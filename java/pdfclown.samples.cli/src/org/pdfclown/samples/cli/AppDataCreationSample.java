package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.interchange.metadata.AppData;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.objects.PdfTextString;

/**
  This sample demonstrates how to decorate documents and contents with private application data (aka
  page-piece application data dictionary).

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/24/15
*/
public class AppDataCreationSample
  extends Sample
{
  private static PdfName MyAppName = new PdfName(AppDataCreationSample.class.getSimpleName());
  
  @Override
  public void run(
    )
  {
    // 1. Instantiate a new PDF file!
    File file = new File();
    Document document = file.getDocument();

    // 2.1. Page-level private application data.
    {
      Page page = new Page(document);
      document.getPages().add(page);
      
      AppData myAppData = page.getAppData(MyAppName);
      /*
        NOTE: Applications are free to define whatever structure their private data should have. In
        this example, we chose a PdfDictionary populating it with arbitrary entries, including a 
        byte stream.
      */
      PdfStream myStream = new PdfStream(new Buffer("This is just some random characters to feed the stream..."));
      myAppData.setData(
        new PdfDictionary(
          new PdfName("MyPrivateEntry"), PdfBoolean.True,
          new PdfName("MyStreamEntry"), file.register(myStream)
          )
        );
  
      // Add some (arbitrary) graphics content on the page!
      BlockComposer composer = new BlockComposer(new PrimitiveComposer(page));
      composer.getBaseComposer().setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Times, true, false), 14);
      Dimension2D pageSize = page.getSize();
      composer.begin(new Rectangle2D.Double(50, 50, pageSize.getWidth() - 100, pageSize.getHeight() - 100), XAlignmentEnum.Left, YAlignmentEnum.Top);
      composer.showText("This page holds private application data (see PieceInfo entry in its dictionary).");
      composer.end();
      composer.getBaseComposer().flush();
    }
    
    // 2.2. Document-level private application data.
    {
      AppData myAppData = document.getAppData(MyAppName);
      /*
        NOTE: Applications are free to define whatever structure their private data should have. In
        this example, we chose a PdfDictionary populating it with arbitrary entries.
      */
      myAppData.setData(
        new PdfDictionary(
          new PdfName("MyPrivateDocEntry"), new PdfTextString("This is an arbitrary value"),
          new PdfName("AnotherPrivateEntry"), new PdfDictionary(
            new PdfName("SubEntry"), new PdfInteger(1287),
            new PdfName("SomeData"), new PdfArray(
              new PdfReal(282.773),
              new PdfReal(14.28378)
              )
            )
          )
        );
    }
    
    // 3. Serialize the PDF file!
    serialize(file, "Private application data", "editing private application data", "Page-Piece Dictionaries, private application data, metadata");
  }
}
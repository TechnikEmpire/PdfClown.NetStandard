using org.pdfclown.bytes;
using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.interchange.metadata;
using org.pdfclown.files;
using org.pdfclown.objects;

using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to decorate documents and contents with private
    application data (aka page-piece application data dictionary).</summary>
  */
  public class AppDataCreationSample
    : Sample
  {
    private static PdfName MyAppName = new PdfName(typeof(AppDataCreationSample).Name);

    public override void Run(
      )
    {
      // 1. Instantiate a new PDF file!
      File file = new File();
      Document document = file.Document;

      // 2.1. Page-level private application data.
      {
        Page page = new Page(document);
        document.Pages.Add(page);

        AppData myAppData = page.GetAppData(MyAppName);
        /*
          NOTE: Applications are free to define whatever structure their private data should have. In
          this example, we chose a PdfDictionary populating it with arbitrary entries, including a
          byte stream.
        */
        PdfStream myStream = new PdfStream(new Buffer("This is just some random characters to feed the stream..."));
        myAppData.Data = new PdfDictionary(
          new PdfName("MyPrivateEntry"), PdfBoolean.True,
          new PdfName("MyStreamEntry"), file.Register(myStream)
          );

        // Add some (arbitrary) graphics content on the page!
        BlockComposer composer = new BlockComposer(new PrimitiveComposer(page));
        composer.BaseComposer.SetFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Times, true, false), 14);
        SizeF pageSize = page.Size;
        composer.Begin(new RectangleF(50, 50, pageSize.Width - 100, pageSize.Height - 100), XAlignmentEnum.Left, YAlignmentEnum.Top);
        composer.ShowText("This page holds private application data (see PieceInfo entry in its dictionary).");
        composer.End();
        composer.BaseComposer.Flush();
      }

      // 2.2. Document-level private application data.
      {
        AppData myAppData = document.GetAppData(MyAppName);
        /*
          NOTE: Applications are free to define whatever structure their private data should have. In
          this example, we chose a PdfDictionary populating it with arbitrary entries.
        */
        myAppData.Data = new PdfDictionary(
          new PdfName("MyPrivateDocEntry"), new PdfTextString("This is an arbitrary value"),
          new PdfName("AnotherPrivateEntry"), new PdfDictionary(
            new PdfName("SubEntry"), new PdfInteger(1287),
            new PdfName("SomeData"), new PdfArray(
              new PdfReal(282.773),
              new PdfReal(14.28378)
              )
            )
          );
      }

      // 3. Serialize the PDF file!
      Serialize(file, "Private application data", "editing private application data", "Page-Piece Dictionaries, private application data, metadata");
    }
  }
}

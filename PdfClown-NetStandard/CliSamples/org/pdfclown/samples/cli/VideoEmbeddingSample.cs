using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.objects;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.files;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to insert screen annotations to display media clips inside
    a PDF document.</summary>
  */
  public class VideoEmbeddingSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Instantiate the PDF file!
      File file = new File();
      Document document = file.Document;

      // 2. Insert a new page!
      Page page = new Page(document);
      document.Pages.Add(page);

      // 3. Insert a video into the page!
      new Screen(
        page,
        new RectangleF(10, 10, 320, 180),
        "PJ Harvey - Dress (part)",
        GetResourcePath("video" + System.IO.Path.DirectorySeparatorChar + "pj_clip.mp4"),
        "video/mp4"
        );

      // 4. Serialize the PDF file!
      Serialize(file, "Video embedding", "inserting screen annotations to display media clips inside a PDF document", "video embedding");
    }
  }
}
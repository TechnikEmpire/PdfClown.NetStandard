package org.pdfclown.samples.cli;

import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.annotations.Screen;
import org.pdfclown.files.File;

/**
  This sample demonstrates how to insert screen annotations to display media clips inside a PDF
  document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 11/30/12
*/
public class VideoEmbeddingSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. Instantiate the PDF file!
    File file = new File();
    Document document = file.getDocument();

    // 2. Insert a new page!
    Page page = new Page(document);
    document.getPages().add(page);

    // 3. Insert a video into the page!
    new Screen(
      page,
      new Rectangle2D.Double(10, 10, 320, 180),
      "PJ Harvey - Dress (part)",
      getResourcePath("video" + java.io.File.separator + "pj_clip.mp4"),
      "video/mp4"
      );

    // 4. Serialize the PDF file!
    serialize(file, "Video embedding", "inserting screen annotations to display media clips inside a PDF document", "video embedding");
  }
}
package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.files.File;
import org.pdfclown.tools.Renderer;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to render a PDF page as a raster image</b>.
  <p>Note: rendering is currently in pre-alpha stage; therefore this sample is
  nothing but an initial stub (no assumption to work!).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2.1, 04/08/15
*/
public class RenderingSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    File file = null;
    try
    {
      // 1. Opening the PDF file...
      {
        String filePath = promptFileChoice("Please select a PDF file");
        try
        {file = new File(filePath);}
        catch(Exception e)
        {throw new RuntimeException(filePath + " file access error.",e);}
      }
      Document document = file.getDocument();
      Pages pages = document.getPages();

      // 2. Page rasterization.
      int pageIndex = promptPageChoice("Select the page to render", pages.size());
      Page page = pages.get(pageIndex);
      Dimension2D imageSize = page.getSize();
      Renderer renderer = new Renderer();
      BufferedImage image = renderer.render(page, imageSize);

      // 3. Save the page image!
      try
      {ImageIO.write(image,"jpg",new java.io.File(getOutputPath("ContentRenderingSample.jpg")));}
      catch(IOException e)
      {e.printStackTrace();}
    }
    finally
    {
      // 4. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }
}

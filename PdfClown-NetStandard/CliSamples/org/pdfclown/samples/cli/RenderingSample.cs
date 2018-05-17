using org.pdfclown.documents;
using org.pdfclown.files;
using org.pdfclown.tools;

using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to render a PDF page as a raster image.<summary>
    <remarks>Note: rendering is currently in pre-alpha stage; therefore this sample is
    nothing but an initial stub (no assumption to work!).</remarks>
  */
  public class RenderingSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Opening the PDF file...
      string filePath = PromptFileChoice("Please select a PDF file");
      using(var file = new File(filePath))
      {
        Document document = file.Document;
        Pages pages = document.Pages;
  
        // 2. Page rasterization.
        int pageIndex = PromptPageChoice("Select the page to render", pages.Count);
        Page page = pages[pageIndex];
        SizeF imageSize = page.Size;
        Renderer renderer = new Renderer();
        Image image = renderer.Render(page, imageSize);
  
        // 3. Save the page image!
        image.Save(GetOutputPath("ContentRenderingSample.jpg"), ImageFormat.Jpeg);
      }
    }
  }
}
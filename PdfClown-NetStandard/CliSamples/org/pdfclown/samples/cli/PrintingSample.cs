using org.pdfclown.files;
using org.pdfclown.tools;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to print a PDF document.<summary>
    <remarks>Note: printing is currently in pre-alpha stage; therefore this sample is
    nothing but an initial stub (no assumption to work!).</remarks>
  */
  public class PrintingSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Opening the PDF file...
      string filePath = PromptFileChoice("Please select a PDF file");
      using(var file = new File(filePath))
      {
        // 2. Printing the document...
        Renderer renderer = new Renderer();
        bool silent = false;
        if(renderer.Print(file.Document, silent))
        {Console.WriteLine("Print fulfilled.");}
        else
        {Console.WriteLine("Print discarded.");}
      }
    }
  }
}
using org.pdfclown.documents;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.entities;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.documents.interaction.forms;
using org.pdfclown.files;

using System;
using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to inspect the AcroForm fields of a PDF document.</summary>
  */
  public class AcroFormParsingSample
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

        // 2. Get the acroform!
        Form form = document.Form;
        if(!form.Exists())
        {Console.WriteLine("\nNo acroform available (AcroForm dictionary not found).");}
        else
        {
          Console.WriteLine("\nIterating through the fields collection...\n");

          // 3. Showing the acroform fields...
          Dictionary<string,int> objCounters = new Dictionary<string,int>();
          foreach(Field field in form.Fields.Values)
          {
            Console.WriteLine("* Field '" + field.FullName + "' (" + field.BaseObject + ")");

            string typeName = field.GetType().Name;
            Console.WriteLine("    Type: " + typeName);
            Console.WriteLine("    Value: " + field.Value);
            Console.WriteLine("    Data: " + field.BaseDataObject.ToString());

            int widgetIndex = 0;
            foreach(Widget widget in field.Widgets)
            {
              Console.WriteLine("    Widget " + (++widgetIndex) + ":");
              Page widgetPage = widget.Page;
              Console.WriteLine("      Page: " + (widgetPage == null ? "undefined" : widgetPage.Number + " (" + widgetPage.BaseObject + ")"));

              RectangleF widgetBox = widget.Box;
              Console.WriteLine("      Coordinates: {x:" + Math.Round(widgetBox.X) + "; y:" + Math.Round(widgetBox.Y) + "; width:" + Math.Round(widgetBox.Width) + "; height:" + Math.Round(widgetBox.Height) + "}");
            }

            objCounters[typeName] = (objCounters.ContainsKey(typeName) ? objCounters[typeName] : 0) + 1;
          }

          int fieldCount = form.Fields.Count;
          if(fieldCount == 0)
          {Console.WriteLine("No field available.");}
          else
          {
            Console.WriteLine("\nFields partial counts (grouped by type):");
            foreach(KeyValuePair<string,int> entry in objCounters)
            {Console.WriteLine(" " + entry.Key + ": " + entry.Value);}
            Console.WriteLine("Fields total count: " + fieldCount);
          }
        }
      }
    }
  }
}
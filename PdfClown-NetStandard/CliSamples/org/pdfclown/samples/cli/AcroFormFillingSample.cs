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
    <summary>This sample demonstrates how to fill AcroForm fields of a PDF document.</summary>
  */
  public class AcroFormFillingSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Opening the PDF file...
      string filePath = PromptFileChoice("Please select a PDF file");
      File file = new File(filePath);
      Document document = file.Document;

      // 2. Get the acroform!
      Form form = document.Form;
      if(!form.Exists())
      {Console.WriteLine("\nNo acroform available.");}
      else
      {
        // 3. Filling the acroform fields...
        int mode;
        try
        {
          IDictionary<string,string> options = new Dictionary<string,string>();
          options["0"] = "Automatic filling";
          options["1"] = "Manual filling";
          mode = Int32.Parse(PromptChoice(options));
        }
        catch
        {mode = 0;}
        switch(mode)
        {
          case 0: // Automatic filling.
            Console.WriteLine("\nAcroform is being filled with random values...\n");

            foreach(Field field in form.Fields.Values)
            {
              String value;
              if(field is RadioButton)
              {value = field.Widgets[0].Value;} // Selects the first widget in the group.
              else if(field is ChoiceField)
              {value = ((ChoiceField)field).Items[0].Value;} // Selects the first item in the list.
              else
              {value = field.Name;} // Arbitrary value (just to get something to fill with).
              field.Value = value;
            }
            break;
          case 1: // Manual filling.
            Console.WriteLine("\nPlease insert a value for each field listed below (or type 'quit' to end this sample).\n");

            foreach(Field field in form.Fields.Values)
            {
              Console.WriteLine("* " + field.GetType().Name + " '" + field.FullName + "' (" + field.BaseObject + "): ");
              Console.WriteLine("    Current Value:" + field.Value);
              string newValue = PromptChoice("    New Value:");
              if(newValue != null && newValue.Equals("quit"))
                break;

              field.Value = newValue;
            }
            break;
        }
      }

      // 4. Serialize the PDF file!
      Serialize(file);
    }
  }
}
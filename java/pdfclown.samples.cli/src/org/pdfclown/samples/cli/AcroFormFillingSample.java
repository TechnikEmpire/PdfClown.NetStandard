package org.pdfclown.samples.cli;

import java.util.HashMap;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.forms.ChoiceField;
import org.pdfclown.documents.interaction.forms.Field;
import org.pdfclown.documents.interaction.forms.Form;
import org.pdfclown.documents.interaction.forms.RadioButton;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to fill AcroForm fields</b> of a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 03/21/15
*/
public class AcroFormFillingSample
  extends Sample
{
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

    // 2. Get the acroform!
    Form form = document.getForm();
    if(!form.exists())
    {System.out.println("\nNo acroform available.");}
    else
    {
      // 3. Filling the acroform fields...
      int mode;
      try
      {
        Map<String,String> options = new HashMap<String,String>();
        options.put("0", "Automatic filling");
        options.put("1", "Manual filling");
        mode = Integer.parseInt(promptChoice(options));
      }
      catch(Exception e)
      {mode = 0;}
      switch(mode)
      {
        case 0: // Automatic filling.
          System.out.println("\nAcroform is being filled with random values...\n");

          for(Field field : form.getFields().values())
          {
            String value;
            if(field instanceof RadioButton)
            {value = field.getWidgets().get(0).getValue();} // Selects the first widget in the group.
            else if(field instanceof ChoiceField)
            {value = ((ChoiceField)field).getItems().get(0).getValue();} // Selects the first item in the list.
            else
            {value = field.getName();} // Arbitrary value (just to get something to fill with).
            field.setValue(value);
          }
          break;
        case 1: // Manual filling.
          System.out.println("\nPlease insert a value for each field listed below (or type 'quit' to end this sample).\n");

          for(Field field : form.getFields().values())
          {
            System.out.println("* " + field.getClass().getSimpleName() + " '" + field.getFullName() + "' (" + field.getBaseObject() + "): ");
            System.out.println("    Current Value:" + field.getValue());
            String newValue = promptChoice("    New Value:");
            if(newValue != null && newValue.equals("quit"))
              break;

            field.setValue(newValue);
          }
          break;
      }
    }

    // 4. Serialize the PDF file!
    serialize(file);
  }
}

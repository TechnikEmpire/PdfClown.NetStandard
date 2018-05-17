package org.pdfclown.samples.cli;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.interaction.actions.JavaScript;
import org.pdfclown.documents.interaction.annotations.Widget;
import org.pdfclown.documents.interaction.forms.CheckBox;
import org.pdfclown.documents.interaction.forms.ChoiceItems;
import org.pdfclown.documents.interaction.forms.ComboBox;
import org.pdfclown.documents.interaction.forms.FieldActions;
import org.pdfclown.documents.interaction.forms.Fields;
import org.pdfclown.documents.interaction.forms.Form;
import org.pdfclown.documents.interaction.forms.ListBox;
import org.pdfclown.documents.interaction.forms.PushButton;
import org.pdfclown.documents.interaction.forms.RadioButton;
import org.pdfclown.documents.interaction.forms.TextField;
import org.pdfclown.documents.interaction.forms.styles.DefaultStyle;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to insert AcroForm fields</b> into a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
public class AcroFormCreationSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    // 1. PDF file instantiation.
    File file = new File();
    Document document = file.getDocument();

    // 2. Content creation.
    populate(document);

    // 3. Serialize the PDF file!
    serialize(file, "AcroForm", "inserting AcroForm fields", "Acroform, creation, annotations, actions, javascript, button, combo, textbox, radio button");
  }

  private void populate(
    Document document
    )
  {
    /*
      NOTE: In order to insert a field into a document, you have to follow these steps:
      1. Define the form fields collection that will gather your fields (NOTE: the form field collection is global to the document);
      2. Define the pages where to place the fields;
      3. Define the appearance style to render your fields;
      4. Create each field of yours:
        4.1. instantiate your field into the page;
        4.2. apply the appearance style to your field;
        4.3. insert your field into the fields collection.
    */

    // 1. Define the form fields collection!
    Form form = document.getForm();
    Fields fields = form.getFields();

    // 2. Define the page where to place the fields!
    Page page = new Page(document);
    document.getPages().add(page);

    // 3. Define the appearance style to apply to the fields!
    DefaultStyle fieldStyle = new DefaultStyle();
    fieldStyle.setFontSize(12);
    fieldStyle.setGraphicsVisibile(true);

    PrimitiveComposer composer = new PrimitiveComposer(page);
    composer.setFont(
      new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Courier,
        true,
        false
        ),
      14
      );

    // 4. Field creation.
    // 4.a. Push button.
    {
      composer.showText(
        "PushButton:",
        new Point2D.Double(140, 68),
        XAlignmentEnum.Right,
        YAlignmentEnum.Middle,
        0
        );

      Widget fieldWidget = new Widget(
        page,
        new Rectangle(150, 50, 136, 36)
        );
      fieldWidget.getActions().withOnActivate(
        new JavaScript(
          document,
          "app.alert(\"Radio button currently selected: '\" + this.getField(\"myRadio\").value + \"'.\",3,0,\"Activation event\");"
          )
        );
      PushButton field = new PushButton(
        "okButton",
        fieldWidget,
        "Push" // Current value.
        ); // 4.1. Field instantiation.
      fields.add(field); // 4.2. Field insertion into the fields collection.
      fieldStyle.apply(field); // 4.3. Appearance style applied.

      {
        BlockComposer blockComposer = new BlockComposer(composer);
        blockComposer.begin(new Rectangle2D.Double(296,50,page.getSize().getWidth()-336,36),XAlignmentEnum.Left,YAlignmentEnum.Middle);
        composer.setFont(composer.getState().getFont(),7);
        blockComposer.showText("If you click this push button, a javascript action should prompt you an alert box responding to the activation event triggered by your PDF viewer.");
        blockComposer.end();
      }
    }

    // 4.b. Check box.
    {
      composer.showText(
        "CheckBox:",
        new Point2D.Double(140, 118),
        XAlignmentEnum.Right,
        YAlignmentEnum.Middle,
        0
        );
      CheckBox field = new CheckBox(
        "myCheck",
        new Widget(
          page,
          new Rectangle(150, 100, 36, 36)
          ),
        true // Current value.
        ); // 4.1. Field instantiation.
      fieldStyle.apply(field);
      fields.add(field);
      field = new CheckBox(
        "myCheck2",
        new Widget(
          page,
          new Rectangle(200, 100, 36, 36)
          ),
        true // Current value.
        ); // 4.1. Field instantiation.
      fieldStyle.apply(field);
      fields.add(field);
      field = new CheckBox(
        "myCheck3",
        new Widget(
          page,
          new Rectangle(250, 100, 36, 36)
          ),
        false // Current value.
        ); // 4.1. Field instantiation.
      fields.add(field); // 4.2. Field insertion into the fields collection.
      fieldStyle.apply(field); // 4.3. Appearance style applied.
    }

    // 4.c. Radio button.
    {
      composer.showText(
        "RadioButton:",
        new Point2D.Double(140, 168),
        XAlignmentEnum.Right,
        YAlignmentEnum.Middle,
        0
        );
      RadioButton field = new RadioButton(
        "myRadio",
        /*
          NOTE: A radio button field typically combines multiple alternative widgets.
        */
        new Widget[]
        {
          new Widget(
            page,
            new Rectangle(150, 150, 36, 36),
            "first"
            ),
          new Widget(
            page,
            new Rectangle(200, 150, 36, 36),
            "second"
            ),
          new Widget(
            page,
            new Rectangle(250, 150, 36, 36),
            "third"
            )
        },
        "second" // Selected item (it MUST correspond to one of the available widgets' names).
        ); // 4.1. Field instantiation.
      fields.add(field); // 4.2. Field insertion into the fields collection.
      fieldStyle.apply(field); // 4.3. Appearance style applied.
    }

    // 4.d. Text field.
    {
      composer.showText(
        "TextField:",
        new Point2D.Double(140, 218),
        XAlignmentEnum.Right,
        YAlignmentEnum.Middle,
        0
        );
      TextField field = new TextField(
        "myText",
        new Widget(
          page,
          new Rectangle(150, 200, 200, 36)
          ),
        "Carmen Consoli" // Current value.
        ); // 4.1. Field instantiation.
      field.setSpellChecked(false); // Avoids text spell check.
      FieldActions fieldActions = new FieldActions(document);
      field.setActions(fieldActions);
      fieldActions.setOnValidate(
        new JavaScript(
          document,
          "app.alert(\"Text '\" + this.getField(\"myText\").value + \"' has changed!\",3,0,\"Validation event\");"
          )
        );
      fields.add(field); // 4.2. Field insertion into the fields collection.
      fieldStyle.apply(field); // 4.3. Appearance style applied.

      {
        BlockComposer blockComposer = new BlockComposer(composer);
        blockComposer.begin(new Rectangle2D.Double(360,200,page.getSize().getWidth()-400,36),XAlignmentEnum.Left,YAlignmentEnum.Middle);
        composer.setFont(composer.getState().getFont(),7);
        blockComposer.showText("If you leave this text field after changing its content, a javascript action should prompt you an alert box responding to the validation event triggered by your PDF viewer.");
        blockComposer.end();
      }
    }

    // 4.e. Choice fields.
    {
      // Preparing the item list that we'll use for choice fields (a list box and a combo box (see below))...
      ChoiceItems items = new ChoiceItems(document);
      items.add("Tori Amos");
      items.add("Anouk");
      items.add("Joan Baez");
      items.add("Rachele Bastreghi");
      items.add("Anna Calvi");
      items.add("Tracy Chapman");
      items.add("Carmen Consoli");
      items.add("Ani DiFranco");
      items.add("Cristina Dona'");
      items.add("Nathalie Giannitrapani");
      items.add("PJ Harvey");
      items.add("Billie Holiday");
      items.add("Joan As Police Woman");
      items.add("Joan Jett");
      items.add("Janis Joplin");
      items.add("Angelique Kidjo");
      items.add("Patrizia Laquidara");
      items.add("Annie Lennox");
      items.add("Loreena McKennitt");
      items.add("Joni Mitchell");
      items.add("Alanis Morissette");
      items.add("Yael Naim");
      items.add("Noa");
      items.add("Sinead O'Connor");
      items.add("Dolores O'Riordan");
      items.add("Nina Persson");
      items.add("Brisa Roche'");
      items.add("Roberta Sammarelli");
      items.add("Cristina Scabbia");
      items.add("Nina Simone");
      items.add("Skin");
      items.add("Patti Smith");
      items.add("Fatima Spar");
      items.add("Thony (F.V.Caiozzo)");
      items.add("Paola Turci");
      items.add("Sarah Vaughan");
      items.add("Nina Zilli");

      // 4.e1. List box.
      {
        composer.showText(
          "ListBox:",
          new Point2D.Double(140, 268),
          XAlignmentEnum.Right,
          YAlignmentEnum.Middle,
          0
          );
        ListBox field = new ListBox(
          "myList",
          new Widget(
            page,
            new Rectangle(150, 250, 200, 70)
            )
          ); // 4.1. Field instantiation.
        field.setItems(items); // List items assignment.
        field.setMultiSelect(false); // Multiple items may not be selected simultaneously.
        field.setValue("Carmen Consoli"); // Selected item.
        fields.add(field); // 4.2. Field insertion into the fields collection.
        fieldStyle.apply(field); // 4.3. Appearance style applied.
      }

      // 4.e2. Combo box.
      {
        composer.showText(
          "ComboBox:",
          new Point2D.Double(140, 350),
          XAlignmentEnum.Right,
          YAlignmentEnum.Middle,
          0
          );
        ComboBox field = new ComboBox(
          "myCombo",
          new Widget(
            page,
            new Rectangle(150, 334, 200, 36)
            )
          ); // 4.1. Field instantiation.
        field.setItems(items); // Combo items assignment.
        field.setEditable(true); // Text may be edited.
        field.setSpellChecked(false); // Avoids text spell check.
        field.setValue("Carmen Consoli"); // Selected item.
        fields.add(field); // 4.2. Field insertion into the fields collection.
        fieldStyle.apply(field); // 4.3. Appearance style applied.
      }
    }

    composer.flush();
  }
}
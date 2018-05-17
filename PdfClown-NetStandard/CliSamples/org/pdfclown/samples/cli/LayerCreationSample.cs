using org.pdfclown.documents;
using colors = org.pdfclown.documents.contents.colorSpaces;
using entities = org.pdfclown.documents.contents.entities;
using org.pdfclown.documents.contents.composition;
using org.pdfclown.documents.contents.fonts;
using org.pdfclown.documents.contents.layers;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.interaction.actions;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.documents.interaction.viewer;
using org.pdfclown.documents.interchange.access;
using files = org.pdfclown.files;
using org.pdfclown.objects;
using org.pdfclown.util.math;

using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to define layers to control content visibility.</summary>
  */
  public class LayerCreationSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. PDF file instantiation.
      files::File file = new files::File();
      Document document = file.Document;

      // 2. Content creation.
      Populate(document);

      // 3. PDF file serialization.
      Serialize(file, "Layer", "inserting layers", "layers, optional content");
    }

    /**
      <summary>Populates a PDF file with contents.</summary>
    */
    private void Populate(
      Document document
      )
    {
      // Initialize a new page!
      Page page = new Page(document);
      document.Pages.Add(page);

      // Initialize the primitive composer (within the new page context)!
      PrimitiveComposer composer = new PrimitiveComposer(page);
      composer.SetFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Helvetica, true, false), 12);

      // Initialize the block composer (wrapping the primitive one)!
      BlockComposer blockComposer = new BlockComposer(composer);

      // Initialize the document layer configuration!
      LayerDefinition layerDefinition = document.Layer;
      document.ViewerPreferences.PageMode = ViewerPreferences.PageModeEnum.Layers; // Shows the layers tab on document opening.

      // Get the root collection of the layers displayed to the user!
      UILayers uiLayers = layerDefinition.UILayers;

      // Nested layers.
      Layer parentLayer;
      {
        parentLayer = new Layer(document, "Parent layer");
        uiLayers.Add(parentLayer);
        var childLayers = parentLayer.Children;

        var childLayer1 = new Layer(document, "Child layer 1");
        childLayers.Add(childLayer1);

        var childLayer2 = new Layer(document, "Child layer 2");
        childLayers.Add(childLayer2);
        childLayer2.Locked = true;

        /*
          NOTE: Graphical content can be controlled through layers in two ways:
          1) marking content within content streams (that is content within the page body);
          2) associating annotations and external objects (XObject) to the layers.
        */

        XObject imageXObject = entities::Image.Get(GetResourcePath("images" + Path.DirectorySeparatorChar + "gnu.jpg")).ToXObject(document);
        imageXObject.Layer = childLayer1; // Associates the image to the layer.

        composer.ShowXObject(imageXObject, new PointF(200, 75));

        composer.BeginLayer(parentLayer); // Opens a marked block associating its contents with the specified layer.
        composer.ShowText(parentLayer.Title, new PointF(50, 50));
        composer.End(); // Closes the marked block.

        composer.BeginLayer(childLayer1);
        composer.ShowText(childLayer1.Title, new PointF(50, 75));
        composer.End();

        composer.BeginLayer(childLayer2);
        composer.ShowText(childLayer2.Title, new PointF(50, 100));
        composer.End();
      }

      // Simple layer collection (labeled collection of inclusive-state layers).
      Layer simpleLayer1;
      {
        var simpleLayerCollection = new LayerCollection(document, "Simple layer collection");
        uiLayers.Add(simpleLayerCollection);

        simpleLayer1 = new Layer(document, "Simple layer 1");
        simpleLayerCollection.Add(simpleLayer1);

        var simpleLayer2 = new Layer(document, "Simple layer 2 (Design)");
        /*
          NOTE: Intent limits layer use in determining visibility to specific use contexts. In this
          case, we want to mark content as intended to represent a document designer's structural
          organization of artwork, hence it's outside the interactive use by document consumers.
        */
        simpleLayer2.Intents = new HashSet<PdfName>{IntentEnum.Design.Name()};
        simpleLayerCollection.Add(simpleLayer2);

        var simpleLayer3 = new Layer(document, "Simple layer 3");
        simpleLayerCollection.Add(simpleLayer3);

        blockComposer.Begin(new RectangleF(50, 125, 200, 75), XAlignmentEnum.Left, YAlignmentEnum.Middle);

        composer.BeginLayer(simpleLayer1);
        blockComposer.ShowText(simpleLayer1.Title);
        composer.End();

        blockComposer.ShowBreak(new SizeF(0, 10));

        composer.BeginLayer(simpleLayer2);
        blockComposer.ShowText(simpleLayer2.Title);
        composer.End();

        blockComposer.ShowBreak(new SizeF(0, 10));

        composer.BeginLayer(simpleLayer3);
        blockComposer.ShowText(simpleLayer3.Title);
        composer.End();

        blockComposer.End();
      }

      // Radio layer collection (labeled collection of exclusive-state layers).
      Layer radioLayer2;
      {
        var radioLayerCollection = new LayerCollection(document, "Radio layer collection");
        uiLayers.Add(radioLayerCollection);

        var radioLayer1 = new Layer(document, "Radio layer 1")
        {Visible = true};
        radioLayerCollection.Add(radioLayer1);

        radioLayer2 = new Layer(document, "Radio layer 2")
        {Visible = false};
        radioLayerCollection.Add(radioLayer2);

        var radioLayer3 = new Layer(document, "Radio layer 3")
        {Visible = false};
        radioLayerCollection.Add(radioLayer3);

        // Register this option group in the layer configuration!
        var optionGroup = new OptionGroup(document){radioLayer1, radioLayer2, radioLayer3};
        layerDefinition.OptionGroups.Add(optionGroup);

        blockComposer.Begin(new RectangleF(50, 200, 200, 75), XAlignmentEnum.Left, YAlignmentEnum.Middle);

        composer.BeginLayer(radioLayer1);
        blockComposer.ShowText(radioLayer1.Title);
        composer.End();

        blockComposer.ShowBreak(new SizeF(0, 10));

        composer.BeginLayer(radioLayer2);
        blockComposer.ShowText(radioLayer2.Title);
        composer.End();

        blockComposer.ShowBreak(new SizeF(0, 10));

        composer.BeginLayer(radioLayer3);
        blockComposer.ShowText(radioLayer3.Title);
        composer.End();

        blockComposer.End();
      }

      // Layer state action.
      {
        var actionLayer = new Layer(document, "Action layer")
        {Printable = false};

        composer.BeginLayer(actionLayer);
        composer.BeginLocalState();
        composer.SetFillColor(colors::DeviceRGBColor.Get(Color.Blue));
        composer.ShowText(
          "Layer state action:\n * deselect \"" + simpleLayer1.Title + "\"\n   and \"" + radioLayer2.Title + "\"\n * toggle \"" + parentLayer.Title + "\"",
          new PointF(400, 200),
          XAlignmentEnum.Left,
          YAlignmentEnum.Middle,
          0,
          new SetLayerState(
            document,
            new SetLayerState.LayerState(SetLayerState.StateModeEnum.Off, simpleLayer1, radioLayer2),
            new SetLayerState.LayerState(SetLayerState.StateModeEnum.Toggle, parentLayer)
            )
          );
        composer.End();
        composer.End();
      }

      // Zoom-restricted layer.
      {
        var zoomRestrictedLayer = new Layer(document, "Zoom-restricted layer")
        {ZoomRange = new Interval<double>(.75, 1.251)}; // NOTE: Change this interval to test other magnification ranges.

        composer.BeginLayer(zoomRestrictedLayer);
        new TextMarkup(
          page,
          composer.ShowText(zoomRestrictedLayer.Title + ": this text is only visible if zoom between 75% and 125%", new PointF(50, 290)),
          "This is a highlight annotation visible only if zoom is between 75% and 125%",
          TextMarkup.MarkupTypeEnum.Highlight
          )
        {Layer = zoomRestrictedLayer /* Associates the annotation to the layer. */ };
        composer.End();
      }

      // Print-only layer.
      {
        var printOnlyLayer = new Layer(document, "Print-only layer")
        {
          Visible = false,
          Printable = true
        };

        composer.BeginLayer(printOnlyLayer);
        composer.BeginLocalState();
        composer.SetFillColor(colors::DeviceRGBColor.Get(Color.Red));
        composer.ShowText(printOnlyLayer.Title, new PointF(25, 300), XAlignmentEnum.Left, YAlignmentEnum.Top, 90);
        composer.End();
        composer.End();
      }

      // Language-specific layer.
      {
        var languageLayer = new Layer(document, "Language-specific layer")
        {
          Language = new LanguageIdentifier("en-GB"), // NOTE: Change this to test other languages and locales.
          LanguagePreferred = true, // Matches any system locale (e.g. en-US, en-AU...) if layer language (en-GB) doesn't match exactly the system language.
          Printable = false
        };

        blockComposer.Begin(new RectangleF(50, 320, 500, 75), XAlignmentEnum.Left, YAlignmentEnum.Top);

        composer.BeginLayer(languageLayer);
        blockComposer.ShowText(languageLayer.Title + ": this text is visible only if current system language is english (\"en\", any locale (\"en-US\", \"en-GB\", \"en-NZ\", etc.)) and is hidden on print.");
        composer.End();

        blockComposer.End();
      }

      // User-specific layer.
      {
        var userLayer = new Layer(document, "User-specific layer")
        {
          Users = new List<string>{"Lizbeth", "Alice", "Stefano", "Johann"}, // NOTE: Change these entries to test other user names.
          UserType = Layer.UserTypeEnum.Individual
        };

        blockComposer.Begin(new RectangleF(blockComposer.BoundBox.Left, blockComposer.BoundBox.Bottom + 15, blockComposer.BoundBox.Width, 75), XAlignmentEnum.Left, YAlignmentEnum.Top);

        composer.BeginLayer(userLayer);
        blockComposer.ShowText(userLayer.Title + ": this text is visible only to " + String.Join(", ", userLayer.Users) + " (exact match).");
        composer.End();

        blockComposer.End();
      }

      // Layer membership (composite layer visibility).
      {
        var layerMembership = new LayerMembership(document)
        {
          /*
            NOTE: VisibilityExpression is a more flexible alternative to the combination of
            VisibilityPolicy and VisibilityMembers. However, for compatibility purposes is preferable
            to provide both of them (the latter combination will work as a fallback in case of older
            viewer application).
          */
          VisibilityExpression = new VisibilityExpression(
            document,
            VisibilityExpression.OperatorEnum.And,
            new VisibilityExpression(
              document,
              VisibilityExpression.OperatorEnum.Not,
              new VisibilityExpression(
                document,
                VisibilityExpression.OperatorEnum.Or,
                simpleLayer1,
                radioLayer2
                )
              ),
            parentLayer
            ),
          VisibilityPolicy = LayerMembership.VisibilityPolicyEnum.AnyOff,
          VisibilityMembers = new List<Layer>{simpleLayer1, radioLayer2, parentLayer}
        };

        blockComposer.Begin(new RectangleF(blockComposer.BoundBox.Left, blockComposer.BoundBox.Bottom + 15, blockComposer.BoundBox.Width, 75), XAlignmentEnum.Left, YAlignmentEnum.Top);

        composer.BeginLayer(layerMembership);
        blockComposer.ShowText(String.Format("Layer membership: the visibility of this text is computed combining multiple layer states into an expression (\"{0}\" and \"{1}\" must be OFF while \"{2}\" must be ON).", simpleLayer1.Title, radioLayer2.Title, parentLayer.Title));
        composer.End();

        blockComposer.End();
      }

      composer.Flush();
    }
  }
}
package org.pdfclown.samples.cli;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.layers.IntentEnum;
import org.pdfclown.documents.contents.layers.Layer;
import org.pdfclown.documents.contents.layers.LayerCollection;
import org.pdfclown.documents.contents.layers.LayerDefinition;
import org.pdfclown.documents.contents.layers.LayerMembership;
import org.pdfclown.documents.contents.layers.OptionGroup;
import org.pdfclown.documents.contents.layers.UILayers;
import org.pdfclown.documents.contents.layers.VisibilityExpression;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.documents.interaction.actions.SetLayerState;
import org.pdfclown.documents.interaction.annotations.TextMarkup;
import org.pdfclown.documents.interaction.viewer.ViewerPreferences.PageModeEnum;
import org.pdfclown.documents.interchange.access.LanguageIdentifier;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.StringUtils;
import org.pdfclown.util.math.Interval;

/**
  This sample demonstrates how to define layers to control content visibility.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 05/02/15
*/
public class LayerCreationSample
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

    // 3. PDF file serialization.
    serialize(file, "Layer", "inserting layers", "layers, optional content");
  }

  private void populate(
    Document document
    )
  {
    // Initialize a new page!
    Page page = new Page(document);
    document.getPages().add(page);

    // Initialize the primitive composer (within the new page context)!
    PrimitiveComposer composer = new PrimitiveComposer(page);
    composer.setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Helvetica, true, false), 12);

    // Initialize the block composer (wrapping the primitive one)!
    BlockComposer blockComposer = new BlockComposer(composer);

    // Initialize the document layer configuration!
    LayerDefinition layerDefinition = document.getLayer();
    document.getViewerPreferences().setPageMode(PageModeEnum.Layers); // Shows the layers tab on document opening.

    // Get the root collection of the layers displayed to the user!
    UILayers uiLayers = layerDefinition.getUILayers();

    // Nested layers.
    Layer parentLayer;
    {
      parentLayer = new Layer(document, "Parent layer");
      uiLayers.add(parentLayer);
      UILayers childLayers = parentLayer.getChildren();

      Layer childLayer1 = new Layer(document, "Child layer 1");
      childLayers.add(childLayer1);

      Layer childLayer2 = new Layer(document, "Child layer 2");
      childLayers.add(childLayer2);
      childLayer2.setLocked(true);

      /*
        NOTE: Graphical content can be controlled through layers in two ways:
        1) marking content within content streams (that is content within the page body);
        2) associating annotations and external objects (XObject) to the layers.
      */
      
      XObject imageXObject = Image.get(getResourcePath("images" + java.io.File.separator + "gnu.jpg")).toXObject(document);
      imageXObject.setLayer(childLayer1); // Associates the image to the layer.

      composer.showXObject(imageXObject, new Point(200, 75));

      composer.beginLayer(parentLayer); // Opens a marked block associating its contents with the specified layer.
      composer.showText(parentLayer.getTitle(), new Point(50, 50));
      composer.end(); // Closes the marked block.

      composer.beginLayer(childLayer1);
      composer.showText(childLayer1.getTitle(), new Point(50, 75));
      composer.end();

      composer.beginLayer(childLayer2);
      composer.showText(childLayer2.getTitle(), new Point(50, 100));
      composer.end();
    }

    // Simple layer collection (labeled collection of inclusive-state layers).
    Layer simpleLayer1;
    {
      LayerCollection simpleLayerCollection = new LayerCollection(document, "Simple layer collection");
      uiLayers.add(simpleLayerCollection);

      simpleLayer1 = new Layer(document, "Simple layer 1");
      simpleLayerCollection.add(simpleLayer1);
      
      Layer simpleLayer2 = new Layer(document, "Simple layer 2 (Design)");
      /*
        NOTE: Intent limits layer use in determining visibility to specific use contexts. In this 
        case, we want to mark content as intended to represent a document designer's structural 
        organization of artwork, hence it's outside the interactive use by document consumers.
      */
      simpleLayer2.setIntents(new HashSet<PdfName>(Arrays.asList(IntentEnum.Design.getName())));
      simpleLayerCollection.add(simpleLayer2);

      Layer simpleLayer3 = new Layer(document, "Simple layer 3");
      simpleLayerCollection.add(simpleLayer3);

      blockComposer.begin(new Rectangle(50, 125, 200, 75), XAlignmentEnum.Left, YAlignmentEnum.Middle);

      composer.beginLayer(simpleLayer1);
      blockComposer.showText(simpleLayer1.getTitle());
      composer.end();

      blockComposer.showBreak(new Dimension(0, 10));

      composer.beginLayer(simpleLayer2);
      blockComposer.showText(simpleLayer2.getTitle());
      composer.end();

      blockComposer.showBreak(new Dimension(0, 10));

      composer.beginLayer(simpleLayer3);
      blockComposer.showText(simpleLayer3.getTitle());
      composer.end();

      blockComposer.end();
    }

    // Radio layer collection (labeled collection of exclusive-state layers).
    Layer radioLayer2;
    {
      LayerCollection radioLayerCollection = new LayerCollection(document, "Radio layer collection");
      uiLayers.add(radioLayerCollection);

      Layer radioLayer1 = new Layer(document, "Radio layer 1");
      radioLayer1.setVisible(true);
      radioLayerCollection.add(radioLayer1);

      radioLayer2 = new Layer(document, "Radio layer 2");
      radioLayer2.setVisible(false);
      radioLayerCollection.add(radioLayer2);

      Layer radioLayer3 = new Layer(document, "Radio layer 3");
      radioLayer3.setVisible(false);
      radioLayerCollection.add(radioLayer3);

      // Register this option group in the layer configuration!
      OptionGroup optionGroup = new OptionGroup(document);
      optionGroup.add(radioLayer1);
      optionGroup.add(radioLayer2);
      optionGroup.add(radioLayer3);
      layerDefinition.getOptionGroups().add(optionGroup);

      blockComposer.begin(new Rectangle(50, 200, 200, 75), XAlignmentEnum.Left, YAlignmentEnum.Middle);

      composer.beginLayer(radioLayer1);
      blockComposer.showText(radioLayer1.getTitle());
      composer.end();

      blockComposer.showBreak(new Dimension(0, 10));

      composer.beginLayer(radioLayer2);
      blockComposer.showText(radioLayer2.getTitle());
      composer.end();

      blockComposer.showBreak(new Dimension(0, 10));

      composer.beginLayer(radioLayer3);
      blockComposer.showText(radioLayer3.getTitle());
      composer.end();

      blockComposer.end();
    }

    // Layer state action.
    {
      Layer actionLayer = new Layer(document, "Action layer");
      actionLayer.setPrintable(false);

      composer.beginLayer(actionLayer);
      composer.beginLocalState();
      composer.setFillColor(DeviceRGBColor.get(Color.BLUE));
      composer.showText(
        "Layer state action:\n * deselect \"" + simpleLayer1.getTitle() + "\"\n   and \"" + radioLayer2.getTitle() + "\"\n * toggle \"" + parentLayer.getTitle() + "\"",
        new Point(400, 200),
        XAlignmentEnum.Left,
        YAlignmentEnum.Middle,
        0,
        new SetLayerState(
          document,
          new SetLayerState.LayerState(SetLayerState.StateModeEnum.Off, simpleLayer1, radioLayer2),
          new SetLayerState.LayerState(SetLayerState.StateModeEnum.Toggle, parentLayer)
          )
        );
      composer.end();
      composer.end();
    }

    // Zoom-restricted layer.
    {
      Layer zoomRestrictedLayer = new Layer(document, "Zoom-restricted layer");
      zoomRestrictedLayer.setZoomRange(new Interval<Double>(.75, 1.251)); // NOTE: Change this interval to test other magnification ranges.

      composer.beginLayer(zoomRestrictedLayer);
      TextMarkup textMarkup = new TextMarkup(
        page,
        composer.showText(zoomRestrictedLayer.getTitle() + ": this text is only visible if zoom between 75% and 125%", new Point(50, 290)),
        "This is a highlight annotation visible only if zoom is between 75% and 125%",
        TextMarkup.MarkupTypeEnum.Highlight
        );
      textMarkup.setLayer(zoomRestrictedLayer); // Associates the annotation to the layer.
      composer.end();
    }

    // Print-only layer.
    {
      Layer printOnlyLayer = new Layer(document, "Print-only layer");
      printOnlyLayer.setVisible(false);
      printOnlyLayer.setPrintable(true);

      composer.beginLayer(printOnlyLayer);
      composer.beginLocalState();
      composer.setFillColor(DeviceRGBColor.get(Color.RED));
      composer.showText(printOnlyLayer.getTitle(), new Point(25, 300), XAlignmentEnum.Left, YAlignmentEnum.Top, 90);
      composer.end();
      composer.end();
    }

    // Language-specific layer.
    {
      Layer languageLayer = new Layer(document, "Language-specific layer");
      languageLayer.setLanguage(new LanguageIdentifier("en-GB")); // NOTE: Change this to test other languages and locales.
      languageLayer.setLanguagePreferred(true); // Matches any system locale (e.g. en-US, en-AU...) if layer language (en-GB) doesn't match exactly the system language.
      languageLayer.setPrintable(false);

      blockComposer.begin(new Rectangle(50, 320, 500, 75), XAlignmentEnum.Left, YAlignmentEnum.Top);

      composer.beginLayer(languageLayer);
      blockComposer.showText(languageLayer.getTitle() + ": this text is visible only if current system language is english (\"en\", any locale (\"en-US\", \"en-GB\", \"en-NZ\", etc.)) and is hidden on print.");
      composer.end();

      blockComposer.end();
    }

    // User-specific layer.
    {
      Layer userLayer = new Layer(document, "User-specific layer");
      userLayer.setUsers(Arrays.asList("Lizbeth", "Alice", "Stefano", "Johann")); // NOTE: Change these entries to test other user names.
      userLayer.setUserType(Layer.UserTypeEnum.Individual);

      blockComposer.begin(new Rectangle2D.Double(blockComposer.getBoundBox().getX(), blockComposer.getBoundBox().getMaxY() + 15, blockComposer.getBoundBox().getWidth(), 75), XAlignmentEnum.Left, YAlignmentEnum.Top);

      composer.beginLayer(userLayer);
      blockComposer.showText(userLayer.getTitle() + ": this text is visible only to " + StringUtils.join(", ", userLayer.getUsers().toArray(new String[0])) + " (exact match).");
      composer.end();

      blockComposer.end();
    }

    // Layer membership (composite layer visibility).
    {
      LayerMembership layerMembership = new LayerMembership(document);
      /*
        NOTE: VisibilityExpression is a more flexible alternative to the combination of
        VisibilityPolicy and VisibilityMembers. However, for compatibility purposes is preferable
        to provide both of them (the latter combination will work as a fallback in case of older
        viewer application).
      */
      layerMembership.setVisibilityExpression(
        new VisibilityExpression(
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
          )
        );
      layerMembership.setVisibilityPolicy(LayerMembership.VisibilityPolicyEnum.AnyOff);
      layerMembership.setVisibilityMembers(Arrays.asList(simpleLayer1, radioLayer2, parentLayer));

      blockComposer.begin(new Rectangle2D.Double(blockComposer.getBoundBox().getX(), blockComposer.getBoundBox().getMaxY() + 15, blockComposer.getBoundBox().getWidth(), 75), XAlignmentEnum.Left, YAlignmentEnum.Top);

      composer.beginLayer(layerMembership);
      blockComposer.showText(String.format("Layer membership: the visibility of this text is computed combining multiple layer states into an expression (\"%s\" and \"%s\" must be OFF while \"%s\" must be ON).", simpleLayer1.getTitle(), radioLayer2.getTitle(), parentLayer.getTitle()));
      composer.end();

      blockComposer.end();
    }
    
    composer.flush();
  }
}

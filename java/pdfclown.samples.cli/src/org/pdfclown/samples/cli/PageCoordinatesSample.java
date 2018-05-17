package org.pdfclown.samples.cli;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.PageFormat;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.Length;
import org.pdfclown.documents.contents.composition.Length.UnitModeEnum;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.objects.ModifyCTM;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfName;

/**
  This sample shows the <b>effects of the manipulation of the CTM</b> (Current Transformation Matrix),
  that is the logical device which affects the PDF page coordinate system used to place graphics contents
  onto the canvas.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 11/30/12
*/
public class PageCoordinatesSample
  extends Sample
{
  private static final PdfName ResourceName_DefaultFont = new PdfName("default");

  private static double max(
    double... values
    )
  {
    double maxValue = values[0];
    for(double value : values)
    {maxValue = Math.max(maxValue, value);}
    return maxValue;
  }

  @Override
  public void run(
    )
  {
    // 1. Instantiate a new PDF file!
    File file = new File();
    Document document = file.getDocument();

    // 2. Insert the contents into the document!
    buildContent(document);

    // 3. Serialize the PDF file!
    serialize(file, "Page coordinates", "manipulating the CTM", "page coordinates, ctm");
  }

  private void buildContent(
    Document document
    )
  {
    // Set default page size (A4)!
    document.setPageSize(PageFormat.getSize());
    // Add a font to the fonts collection!
    document.getResources().getFonts().put(
      ResourceName_DefaultFont,
      new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Courier,
        true,
        false
        )
      );
    
    // Add a page to the document!
    Page page = new Page(document); // Instantiates the page inside the document context.
    document.getPages().add(page); // Puts the page in the pages collection.

    // Create a content composer for the page content stream!
    PrimitiveComposer composer = new PrimitiveComposer(page);

    String[] steps = new String[5];
    Color<?>[] colors = new Color<?>[5];
    Dimension2D pageSize = page.getSize();

    buildSteps(composer, steps, colors, pageSize);

    buildLegend(composer, steps, colors, pageSize);

    composer.flush();
  }

  private String getStepNote(
    PrimitiveComposer composer,
    String comment
    )
  {
    // Get the CTM!
    AffineTransform ctm = composer.getScanner().getState().getCtm();

    return "CTM (" + comment + "): " + ctm.getScaleX() + ", " + ctm.getShearX() + ", " + ctm.getShearY() + ", " + ctm.getScaleY() + ", " + ctm.getTranslateX() + ", " + ctm.getTranslateY();
  }

  private void buildLegend(
    PrimitiveComposer composer,
    String[] steps,
    Color<?>[] colors,
    Dimension2D pageSize
    )
  {
    double maxCtmInversionApproximation;
    {
      double[] ctmInversionApproximations = new double[6];
      {
        double[] initialCtmValues, finalCtmValues;
        {
          GraphicsState state = composer.getScanner().getState();
          state.getInitialCtm().getMatrix(initialCtmValues = new double[6]);
          state.getCtm().getMatrix(finalCtmValues = new double[6]);
        }
        for(
          int index = 0,
            length = finalCtmValues.length;
          index < length;
          index++
          )
        {ctmInversionApproximations[index] = Math.abs(finalCtmValues[index]) - initialCtmValues[index];}
      }
      maxCtmInversionApproximation = max(ctmInversionApproximations);
    }

    final BlockComposer blockComposer = new BlockComposer(composer);
    blockComposer.setLineSpace(new Length(.25, UnitModeEnum.Relative));

    composer.beginLocalState();
    composer.setFillColor(
      new DeviceRGBColor(115 / 255d, 164 / 255d, 232 / 255d)
      );
    final Rectangle2D frame = new Rectangle2D.Double(
      18,
      18,
      pageSize.getWidth() * .5,
      pageSize.getHeight() * .5
      );
    blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
    composer.setFont(ResourceName_DefaultFont,24);
    blockComposer.showText("Page coordinates sample");
    Dimension2D breakSize = new Dimension(8,4);
    blockComposer.showBreak(breakSize);
    composer.setFont(ResourceName_DefaultFont,8);
    blockComposer.showText(
      "This sample shows the effects of the manipulation of the CTM (Current Transformation Matrix), "
        + "that is the mathematical device which affects the page coordinate system used to place "
        + "graphic contents onto the canvas."
      );
    blockComposer.showBreak(breakSize);
    blockComposer.showText(
      "The following steps represent the operations applied to this page's CTM in order to alter it. "
        + "Each step writes the word \"Step\" at the lower-left corner of the current page frame:"
      );
    blockComposer.showBreak(breakSize);
    for(int i = 0; i < steps.length; i++)
    {
      composer.setFillColor(colors[i]);
      blockComposer.showText("Step " + i + ")");
      composer.setFillColor(
        new DeviceRGBColor(115 / 255d, 164 / 255d, 232 / 255d)
        );
      blockComposer.showText(" " + steps[i]);
      blockComposer.showBreak(breakSize);
    }
    blockComposer.showText("Note that the approximation (" + maxCtmInversionApproximation + ") of the CTM components at step 4 is due to floating point precision limits; their exact values should be 1.0, 0.0, 0.0, 1.0, 0.0, 0.0.");
    blockComposer.end();
    composer.end();
  }

  private void buildSteps(
    PrimitiveComposer composer,
    String[] steps,
    Color<?>[] colors,
    Dimension2D pageSize
    )
  {
    composer.setFont(ResourceName_DefaultFont,32);
    Rectangle2D frame = new Rectangle2D.Double(
      0,
      0,
      pageSize.getWidth(),
      pageSize.getHeight()
      );

    // Step 0.
    {
      colors[0] = new DeviceRGBColor(30 / 255d, 10 / 255d, 0);
      composer.setFillColor(colors[0]);
      composer.setStrokeColor(colors[0]);

      // Draw the page frame!
      composer.drawRectangle(frame);
      composer.stroke();

      // Draw the lower-left corner mark!
      composer.showText(
        "Step 0",
        new Point2D.Double(0,pageSize.getHeight()),
        XAlignmentEnum.Left,
        YAlignmentEnum.Bottom,
        0
        );

      steps[0] = getStepNote(composer,"default");
    }

    // Step 1.
    {
      colors[1] = new DeviceRGBColor(80 / 255d, 25 / 255d, 0);
      composer.setFillColor(colors[1]);
      composer.setStrokeColor(colors[1]);

      // Transform the coordinate space, applying translation!
      composer.translate(72,72);

      // Draw the page frame!
      composer.drawRectangle(frame);
      composer.stroke();

      // Draw the lower-left corner mark!
      composer.showText(
        "Step 1",
        new Point2D.Double(0,pageSize.getHeight()),
        XAlignmentEnum.Left,
        YAlignmentEnum.Bottom,
        0
        );

      steps[1] = getStepNote(composer,"after translate(72,72)");
    }

    // Step 2.
    {
      colors[2] = new DeviceRGBColor(130 / 255d, 45 / 255d, 0);
      composer.setFillColor(colors[2]);
      composer.setStrokeColor(colors[2]);

      // Transform the coordinate space, applying clockwise rotation!
      composer.rotate(-20);

      // Draw the page frame!
      composer.drawRectangle(frame);
      composer.stroke();

      // Draw the coordinate space origin mark!
      composer.showText("Origin 2");

      // Draw the lower-left corner mark!
      composer.showText(
        "Step 2",
        new Point2D.Double(0,pageSize.getHeight()),
        XAlignmentEnum.Left,
        YAlignmentEnum.Bottom,
        0
        );

      steps[2] = getStepNote(composer,"after rotate(-20)");
    }

    // Step 3.
    {
      colors[3] = new DeviceRGBColor(180 / 255d, 60 / 255d, 0);
      composer.setFillColor(colors[3]);
      composer.setStrokeColor(colors[3]);

      // Transform the coordinate space, applying translation and scaling!
      composer.translate(0,72);
      composer.scale(.5f,.5f);

      // Draw the page frame!
      composer.drawRectangle(frame);
      composer.stroke();

      // Draw the lower-left corner mark!
      composer.showText(
        "Step 3",
        new Point2D.Double(0,pageSize.getHeight()),
        XAlignmentEnum.Left,
        YAlignmentEnum.Bottom,
        0
        );

      steps[3] = getStepNote(composer,"after translate(0,72) and scale(.5,.5)");
    }

    // Step 4.
    {
      colors[4] = new DeviceRGBColor(230 / 255d, 75 / 255d, 0);
      composer.setFillColor(colors[4]);
      composer.setStrokeColor(colors[4]);

      // Transform the coordinate space, restoring its initial CTM!
      composer.add(
        ModifyCTM.getResetCTM(
          composer.getScanner().getState()
          )
        );

      // Draw the page frame!
      composer.drawRectangle(frame);
      composer.stroke();

      // Draw the lower-left corner mark!
      composer.showText(
        "Step 4",
        new Point2D.Double(0,pageSize.getHeight()),
        XAlignmentEnum.Left,
        YAlignmentEnum.Bottom,
        0
        );

      steps[4] = getStepNote(composer,"after resetting CTM");
    }
  }
}
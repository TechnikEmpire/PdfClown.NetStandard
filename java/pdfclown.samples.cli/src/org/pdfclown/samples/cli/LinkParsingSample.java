package org.pdfclown.samples.cli;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.PageAnnotations;
import org.pdfclown.documents.contents.ITextString;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.documents.interaction.actions.GoToDestination;
import org.pdfclown.documents.interaction.actions.GoToEmbedded;
import org.pdfclown.documents.interaction.actions.GoToEmbedded.PathElement;
import org.pdfclown.documents.interaction.actions.GoToNonLocal;
import org.pdfclown.documents.interaction.actions.GoToURI;
import org.pdfclown.documents.interaction.annotations.Annotation;
import org.pdfclown.documents.interaction.annotations.Link;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.tools.TextExtractor;
import org.pdfclown.util.io.IOUtils;

/**
  This sample demonstrates <b>how to inspect the links of a PDF document</b>, retrieving
  their associated text along with its graphic attributes (font, font size, text color,
  text rendering mode, text bounding box...).
  <h3>Remarks</h3>
  <p>According to PDF spec, page text and links have no mutual relation (contrary to, for
  example, HTML links), so retrieving the text associated to a link is somewhat tricky
  as we have to infer the overlapping areas between links and their corresponding text.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2.1, 04/08/15
*/
public class LinkParsingSample
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

      // 2. Link extraction from the document pages.
      TextExtractor extractor = new TextExtractor();
      extractor.setAreaTolerance(2); // 2 pt tolerance on area boundary detection.
      boolean linkFound = false;
      for(Page page : document.getPages())
      {
        if(!promptNextPage(page, !linkFound))
        {
          quit();
          break;
        }

        Map<Rectangle2D,List<ITextString>> textStrings = null;
        linkFound = false;

        // Get the page annotations!
        PageAnnotations annotations = page.getAnnotations();
        if(!annotations.exists())
        {
          System.out.println("No annotations here.");
          continue;
        }

        // Iterating through the page annotations looking for links...
        for(Annotation<?> annotation : annotations)
        {
          if(annotation instanceof Link)
          {
            linkFound = true;

            if(textStrings == null)
            {textStrings = extractor.extract(page);}

            Link link = (Link)annotation;
            Rectangle2D linkBox = link.getBox();

            // Text.
            /*
              Extracting text superimposed by the link...
              NOTE: As links have no strong relation to page text but a weak location correspondence,
              we have to filter extracted text by link area.
            */
            StringBuilder linkTextBuilder = new StringBuilder();
            for(ITextString linkTextString : extractor.filter(textStrings,linkBox))
            {linkTextBuilder.append(linkTextString.getText());}
            System.out.println("Link '" + linkTextBuilder + "' ");

            // Position.
            System.out.println(
              "    Position: "
                + "x:" + Math.round(linkBox.getX()) + ","
                + "y:" + Math.round(linkBox.getY()) + ","
                + "w:" + Math.round(linkBox.getWidth()) + ","
                + "h:" + Math.round(linkBox.getHeight())
                );

            // Target.
            System.out.print("    Target: ");
            PdfObjectWrapper<?> target = link.getTarget();
            if(target instanceof Destination)
            {printDestination((Destination)target);}
            else if(target instanceof Action)
            {printAction((Action)target);}
            else if(target == null)
            {System.out.println("[not available]");}
            else
            {System.out.println("[unknown type: " + target.getClass().getSimpleName() + "]");}
          }
        }
        if(!linkFound)
        {
          System.out.println("No links here.");
          continue;
        }
      }
    }
    finally
    {
      // 3. Closing the PDF file...
      IOUtils.closeQuietly(file);
    }
  }

  private void printAction(
    Action action
    )
  {
    System.out.println("Action [" + action.getClass().getSimpleName() + "] " + action.getBaseObject());
    if(action instanceof GoToDestination<?>)
    {
      if(action instanceof GoToNonLocal<?>)
      {
        FileSpecification<?> destinationFile = ((GoToNonLocal<?>)action).getDestinationFile();
        if(destinationFile != null)
        {System.out.println("      Filename: " + destinationFile.getPath());}

        if(action instanceof GoToEmbedded)
        {
          PathElement target = ((GoToEmbedded)action).getDestinationPath();
          System.out.println("      EmbeddedFilename: " + target.getEmbeddedFileName() + " Relation: " + target.getRelation());
        }
      }
      System.out.print("      ");
      printDestination(((GoToDestination<?>)action).getDestination());
    }
    else if(action instanceof GoToURI)
    {System.out.println("      URI: " + ((GoToURI)action).getURI());}
  }

  private void printDestination(
    Destination destination
    )
  {
    System.out.println(destination.getClass().getSimpleName() + " " + destination.getBaseObject());
    System.out.print("        Page ");
    Object pageRef = destination.getPage();
    if(pageRef instanceof Page)
    {
      Page page = (Page)pageRef;
      System.out.println(page.getNumber() + " [ID: " + page.getBaseObject() + "]");
    }
    else
    {System.out.println(((Integer)pageRef+1));}

    Object location = destination.getLocation();
    if(location != null)
    {System.out.println(String.format("        Location %s", location));}

    Double zoom = destination.getZoom();
    if(zoom != null)
    {System.out.println(String.format("        Zoom %s", zoom));}
  }
}

package org.pdfclown.samples.cli;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.NamedDestinations;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.interaction.actions.GoToLocal;
import org.pdfclown.documents.interaction.annotations.Link;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfString;

/**
  This sample demonstrates <b>how to manipulate the named destinations</b> within a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.3, 03/07/13
*/
public class NamedDestinationSample
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
    Pages pages = document.getPages();

    // 2. Inserting page destinations...
    NamedDestinations destinations = document.getNames().getDestinations();
    /*
      NOTE: Here we are registering page 1 multiple times to test tree structure sorting and splitting.
    */
    Destination page1Destination = new LocalDestination(pages.get(0));
    destinations.put(new PdfString("d31e1142"), page1Destination);
    destinations.put(new PdfString("Z1"), page1Destination);
    destinations.put(new PdfString("d38e1142"), page1Destination);
    destinations.put(new PdfString("B84afaba8"), page1Destination);
    destinations.put(new PdfString("z38e1142"), page1Destination);
    destinations.put(new PdfString("d3A8e1142"), page1Destination);
    destinations.put(new PdfString("f38e1142"), page1Destination);
    destinations.put(new PdfString("B84afaba6"), page1Destination);
    destinations.put(new PdfString("d3a8e1142"), page1Destination);
    destinations.put(new PdfString("Z38e1142"), page1Destination);
    if(pages.size() > 1)
    {
      LocalDestination page2Destination = new LocalDestination(pages.get(1), Destination.ModeEnum.FitHorizontal, 0, null);
      destinations.put(new PdfString("N84afaba6"), page2Destination);

      // Let the viewer go to the second page on document opening!
      /*
        NOTE: Any time a named destination is applied, its name is retrieved and used as reference.
      */
      document.getActions().setOnOpen(
        new GoToLocal(
          document,
          page2Destination // Its name ("N84afaba6") is retrieved behind the scenes.
          )
        );
      // Define a link to the second page on the first one!
      new Link(
        pages.get(0),
        new Rectangle(0,0,100,50),
        "Link annotation",
        page2Destination // Its name ("N84afaba6") is retrieved behind the scenes.
        );
      
      if(pages.size() > 2)
      {destinations.put(new PdfString("1845505298"), new LocalDestination(pages.get(2), Destination.ModeEnum.XYZ, new Point2D.Double(50, Double.NaN), null));}
    }

    // 3. Serialize the PDF file!
    serialize(file, "Named destinations", "manipulating named destinations", "named destinations, creation");
  }
}
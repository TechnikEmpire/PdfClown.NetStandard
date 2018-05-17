package org.pdfclown.samples.cli;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.tools.PageManager;
import org.pdfclown.util.io.IOUtils;
import org.pdfclown.util.math.geom.Dimension;

/**
  This sample demonstrates <b>how to manipulate the pages collection</b> within a PDF document,
  to perform page data size calculations, additions, movements, removals, extractions and
  splits of groups of pages.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 04/08/15
*/
public class PageManagementSample
  extends Sample
{
  private enum ActionEnum
  {
    PageDataSizeCalculation,
    BlankPageDetection,
    PageAddition,
    PageMovement,
    PageRemoval,
    PageExtraction,
    DocumentMerge,
    DocumentBurst,
    DocumentSplitByPageIndex,
    DocumentSplitOnMaximumFileSize;

    public String getDescription(
      )
    {
      StringBuilder builder = new StringBuilder();
      for(char c : name().toCharArray())
      {
        if(Character.isUpperCase(c) && builder.length() > 0)
        {builder.append(" ");}

        builder.append(c);
      }
      return builder.toString();
    }
  }

  @Override
  public void run(
    )
  {
    final ActionEnum action = promptAction();

    File mainFile = null;
    try
    {
      // Opening the PDF file...
      final String mainFilePath = promptFileChoice("Please select a PDF file");
      try
      {mainFile = new File(mainFilePath);}
      catch(Exception e)
      {throw new RuntimeException(mainFilePath + " file access error.",e);}
      final Document mainDocument = mainFile.getDocument();
      final Pages mainPages = mainDocument.getPages();
      final int mainPagesCount = mainPages.size();

      switch(action)
      {
        case PageDataSizeCalculation:
        {
          System.out.println("\nThis algorithm calculates the data size (expressed in bytes) of the selected document's pages.");
          System.out.println("Legend:");
          System.out.println(" * full: page data size encompassing all its dependencies (like shared resources) -- this is the size of the page when extracted as a single-page document;");
          System.out.println(" * differential: additional page data size -- this is the extra-content that's not shared with previous pages;");
          System.out.println(" * incremental: data size of the page sublist encompassing all the previous pages and the current one.\n");

          // Calculating the page data sizes...
          Set<PdfReference> visitedReferences = new HashSet<PdfReference>();
          long incrementalDataSize = 0;
          for(Page page : mainPages)
          {
            long pageFullDataSize = PageManager.getSize(page);
            long pageDifferentialDataSize = PageManager.getSize(page, visitedReferences);
            incrementalDataSize += pageDifferentialDataSize;

            System.out.println(
              "Page " + page.getNumber() + ": "
                + pageFullDataSize + " (full); "
                + pageDifferentialDataSize + " (differential); "
                + incrementalDataSize + " (incremental)"
              );
          }
        } break;
        case BlankPageDetection:
        {
          System.out.println(
            "\nThis algorithm makes a simple guess about whether a page should be considered empty:"
            + "\nit evaluates the middle portion (70%) of a page assuming that possible contents"
            + "\noutside this area would NOT qualify as actual (informative) content (such as"
            + "\nredundant patterns like footers and headers). Obviously, this assumption may need"
            + "\nsome fine-tuning as each document features its own layout ratios. Alternatively,"
            + "\nan adaptive algorithm should automatically evaluate the content role based on its"
            + "\ntypographic attributes in relation to the other contents existing in the same page"
            + "\nor document.\n");
          int blankPageCount = 0;
          for(Page page : mainPages)
          {
            Rectangle2D pageBox = page.getBox();
            Dimension margin = new Dimension(pageBox.getWidth() * .15, pageBox.getHeight() * .15);
            Rectangle2D contentBox = new Rectangle2D.Double(margin.getWidth(), margin.getHeight(), pageBox.getWidth() - margin.getWidth() * 2, pageBox.getHeight() - margin.getHeight() * 2);
            if(PageManager.isBlank(page, contentBox))
            {
              blankPageCount++;
              System.out.println("Page " + page.getNumber() + " is blank");
            }
          }
          System.out.println(blankPageCount > 0 ? "Blank pages detected: " + blankPageCount + " of " + mainPages.size() : "No blank pages detected.");
        } break;
        case PageAddition:
        {
          File sourceFile = null;
          try
          {
            // Opening the source file...
            {
              String sourceFilePath = promptFileChoice("Select the source PDF file");
              try
              {sourceFile = new File(sourceFilePath);}
              catch(Exception e)
              {throw new RuntimeException(sourceFilePath + " file access error.",e);}
            }
            // Source page collection.
            Pages sourcePages = sourceFile.getDocument().getPages();
            // Source page count.
            int sourcePagesCount = sourcePages.size();

            // First page to add.
            int fromSourcePageIndex = promptPageChoice("Select the start source page to add", sourcePagesCount);
            // Last page to add.
            int toSourcePageIndex = promptPageChoice("Select the end source page to add", fromSourcePageIndex, sourcePagesCount) + 1;
            // Target position.
            int targetPageIndex = promptPageChoice("Select the position where to insert the source pages", mainPagesCount + 1);

            // Add the chosen page range to the main document!
            new PageManager(mainDocument).add(
              targetPageIndex,
              sourcePages.subList(
                fromSourcePageIndex,
                toSourcePageIndex
                )
              );

            // Serialize the main file!
            serialize(mainFile, action);
          }
          finally
          {
            // Closing the source file...
            IOUtils.closeQuietly(sourceFile);
          }
        } break;
        case PageMovement:
        {
          // First page to move.
          int fromSourcePageIndex = promptPageChoice("Select the start page to move", mainPagesCount);
          // Last page to move.
          int toSourcePageIndex = promptPageChoice("Select the end page to move", fromSourcePageIndex, mainPagesCount) + 1;
          // Target position.
          int targetPageIndex = promptPageChoice("Select the position where to insert the pages", mainPagesCount + 1);

          // Move the chosen page range!
          new PageManager(mainDocument).move(
            fromSourcePageIndex,
            toSourcePageIndex,
            targetPageIndex
            );

          // Serialize the main file!
          serialize(mainFile, action);
        } break;
        case PageRemoval:
        {
          // First page to remove.
          int fromPageIndex = promptPageChoice("Select the start page to remove", mainPagesCount);
          // Last page to remove.
          int toPageIndex = promptPageChoice("Select the end page to remove", fromPageIndex, mainPagesCount) + 1;

          // Remove the chosen page range!
          new PageManager(mainDocument).remove(
            fromPageIndex,
            toPageIndex
            );

          // Serialize the main file!
          serialize(mainFile, action);
        } break;
        case PageExtraction:
        {
          // First page to extract.
          int fromPageIndex = promptPageChoice("Select the start page", mainPagesCount);
          // Last page to extract.
          int toPageIndex = promptPageChoice("Select the end page", fromPageIndex, mainPagesCount) + 1;

          // Extract the chosen page range!
          Document targetDocument = new PageManager(mainDocument).extract(
            fromPageIndex,
            toPageIndex
            );

          // Serialize the target file!
          serialize(targetDocument.getFile(), action);
        } break;
        case DocumentMerge:
        {
          File sourceFile = null;
          try
          {
            // Opening the source file...
            {
              String sourceFilePath = promptFileChoice("Select the source PDF file");
              try
              {sourceFile = new File(sourceFilePath);}
              catch(Exception e)
              {throw new RuntimeException(sourceFilePath + " file access error.",e);}
            }

            // Append the chosen source document to the main document!
            new PageManager(mainDocument).add(sourceFile.getDocument());

            // Serialize the main file!
            serialize(mainFile, action);
          }
          finally
          {
            // Closing the source file...
            IOUtils.closeQuietly(sourceFile);
          }
        } break;
        case DocumentBurst:
        {
          // Split the document into single-page documents!
          List<Document> splitDocuments = new PageManager(mainDocument).split();

          // Serialize the split files!
          int index = 0;
          for(Document splitDocument : splitDocuments)
          {serialize(splitDocument.getFile(), action, ++index);}
        } break;
        case DocumentSplitByPageIndex:
        {
          // Number of splits to apply to the source document.
          int splitCount;
          try
          {splitCount = Integer.parseInt(promptChoice("Number of split positions: "));}
          catch(Exception e)
          {splitCount = 0;}

          // Split positions within the source document.
          int[] splitIndexes = new int[splitCount];
          {
            int prevSplitIndex = 0;
            for(int index = 0; index < splitCount; index++)
            {
              int splitIndex = promptPageChoice("Position " + (index + 1) + " of " + splitCount, prevSplitIndex + 1, mainPagesCount);
              splitIndexes[index] = splitIndex;
              prevSplitIndex = splitIndex;
            }
          }

          // Split the document at the chosen positions!
          List<Document> splitDocuments = new PageManager(mainDocument).split(splitIndexes);

          // Serialize the split files!
          int index = 0;
          for(Document splitDocument : splitDocuments)
          {serialize(splitDocument.getFile(), action, ++index);}
        } break;
        case DocumentSplitOnMaximumFileSize:
        {
          // Maximum file size.
          long maxDataSize;
          {
            long mainFileSize = new java.io.File(mainFilePath).length();
            int kbMaxDataSize;
            do
            {
              try
              {kbMaxDataSize = Integer.parseInt(promptChoice("Max file size (KB): "));}
              catch(Exception e)
              {kbMaxDataSize = 0;}
            } while(kbMaxDataSize == 0);
            maxDataSize = kbMaxDataSize << 10;
            if(maxDataSize > mainFileSize)
            {maxDataSize = mainFileSize;}
          }

          // Split the document on maximum file size!
          List<Document> splitDocuments = new PageManager(mainDocument).split(maxDataSize);

          // Serialize the split files!
          int index = 0;
          for(Document splitDocument : splitDocuments)
          {serialize(splitDocument.getFile(), action, ++index);}
        } break;
      }
    }
    finally
    {
      // Closing the main file...
      IOUtils.closeQuietly(mainFile);
    }
  }

  private ActionEnum promptAction(
    )
  {
    ActionEnum[] actions = ActionEnum.values();
    Map<String,String> options = new HashMap<String,String>();
    for(ActionEnum action : actions)
    {options.put(Integer.toString(action.ordinal()), action.getDescription());}

    try
    {return actions[Integer.parseInt(promptChoice(options))];}
    catch(Exception e)
    {return actions[0];}
  }

  /**
    Serializes the specified PDF file.

    @param file File to serialize.
    @param action Generator.
  */
  private void serialize(
    File file,
    ActionEnum action
    )
  {serialize(file, action, null);}

  /**
    Serializes the specified PDF file.

    @param file File to serialize.
    @param action Generator.
    @param index File index.
  */
  private void serialize(
    File file,
    ActionEnum action,
    Integer index
    )
  {
    serialize(
      file,
      getClass().getSimpleName() + "_" + action.name() + (index != null ? "." + index : ""),
      null,
      action.name(),
      "managing document pages",
      action.name()
      );
  }
}

using org.pdfclown.documents;
using org.pdfclown.files;
using org.pdfclown.objects;
using org.pdfclown.tools;

using System;
using io = System.IO;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to manipulate the pages collection within
    a PDF document, to perform page data size calculations, additions, movements,
    removals, extractions and splits of groups of pages.</summary>
  */
  public class PageManagementSample
    : Sample
  {
    internal enum ActionEnum
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
      DocumentSplitOnMaximumFileSize
    }

    public override void Run(
      )
    {
      ActionEnum action = PromptAction();

      // Opening the PDF file...
      string mainFilePath = PromptFileChoice("Please select a PDF file");
      using(var mainFile = new File(mainFilePath))
      {
        Document mainDocument = mainFile.Document;
        Pages mainPages = mainDocument.Pages;
        int mainPagesCount = mainPages.Count;
  
        switch(action)
        {
          case ActionEnum.PageDataSizeCalculation:
          {
            Console.WriteLine("\nThis algorithm calculates the data size (expressed in bytes) of the selected document's pages.");
            Console.WriteLine("Legend:");
            Console.WriteLine(" * full: page data size encompassing all its dependencies (like shared resources) -- this is the size of the page when extracted as a single-page document;");
            Console.WriteLine(" * differential: additional page data size -- this is the extra-content that's not shared with previous pages;");
            Console.WriteLine(" * incremental: data size of the page sublist encompassing all the previous pages and the current one.\n");
  
            // Calculating the page data sizes...
            HashSet<PdfReference> visitedReferences = new HashSet<PdfReference>();
            long incrementalDataSize = 0;
            foreach(Page page in mainPages)
            {
              long pageFullDataSize = PageManager.GetSize(page);
              long pageDifferentialDataSize = PageManager.GetSize(page, visitedReferences);
              incrementalDataSize += pageDifferentialDataSize;
  
              Console.WriteLine(
                "Page " + page.Number + ": "
                  + pageFullDataSize + " (full); "
                  + pageDifferentialDataSize + " (differential); "
                  + incrementalDataSize + " (incremental)"
                );
            }
          } break;
          case ActionEnum.BlankPageDetection:
          {
            Console.WriteLine(
              "\nThis algorithm makes a simple guess about whether a page should be considered empty:"
              + "\nit evaluates the middle portion (70%) of a page assuming that possible contents"
              + "\noutside this area would NOT qualify as actual (informative) content (such as"
              + "\nredundant patterns like footers and headers). Obviously, this assumption may need"
              + "\nsome fine-tuning as each document features its own layout ratios. Alternatively,"
              + "\nan adaptive algorithm should automatically evaluate the content role based on its"
              + "\ntypographic attributes in relation to the other contents existing in the same page"
              + "\nor document.\n");
            int blankPageCount = 0;
            foreach(Page page in mainPages)
            {
              RectangleF pageBox = page.Box;
              SizeF margin = new SizeF(pageBox.Width * .15f, pageBox.Height * .15f);
              RectangleF contentBox = new RectangleF(margin.Width, margin.Height, pageBox.Width - margin.Width * 2, pageBox.Height - margin.Height * 2);
              if(PageManager.IsBlank(page, contentBox))
              {
                blankPageCount++;
                Console.WriteLine("Page " + page.Number + " is blank");
              }
            }
            Console.WriteLine(blankPageCount > 0 ? "Blank pages detected: " + blankPageCount + " of " + mainPages.Count : "No blank pages detected.");
          } break;
          case ActionEnum.PageAddition:
          {
            // Opening the source file...
            string sourceFilePath = PromptFileChoice("Select the source PDF file");
            using(var sourceFile = new File(sourceFilePath))
            {
              // Source page collection.
              Pages sourcePages = sourceFile.Document.Pages;
              // Source page count.
              int sourcePagesCount = sourcePages.Count;
  
              // First page to add.
              int fromSourcePageIndex = PromptPageChoice("Select the start source page to add", sourcePagesCount);
              // Last page to add.
              int toSourcePageIndex = PromptPageChoice("Select the end source page to add", fromSourcePageIndex, sourcePagesCount) + 1;
              // Target position.
              int targetPageIndex = PromptPageChoice("Select the position where to insert the source pages", mainPagesCount + 1);
  
              // Add the chosen page range to the main document!
              new PageManager(mainDocument).Add(
                targetPageIndex,
                sourcePages.GetSlice(
                  fromSourcePageIndex,
                  toSourcePageIndex
                  )
                );
            }
            // Serialize the main file!
            Serialize(mainFile, action);
          } break;
          case ActionEnum.PageMovement:
          {
            // First page to move.
            int fromSourcePageIndex = PromptPageChoice("Select the start page to move", mainPagesCount);
            // Last page to move.
            int toSourcePageIndex = PromptPageChoice("Select the end page to move", fromSourcePageIndex, mainPagesCount) + 1;
            // Target position.
            int targetPageIndex = PromptPageChoice("Select the position where to insert the pages", mainPagesCount + 1);
  
            // Move the chosen page range!
            new PageManager(mainDocument).Move(
              fromSourcePageIndex,
              toSourcePageIndex,
              targetPageIndex
              );
  
            // Serialize the main file!
            Serialize(mainFile, action);
          } break;
          case ActionEnum.PageRemoval:
          {
            // First page to remove.
            int fromPageIndex = PromptPageChoice("Select the start page to remove", mainPagesCount);
            // Last page to remove.
            int toPageIndex = PromptPageChoice("Select the end page to remove", fromPageIndex, mainPagesCount) + 1;
  
            // Remove the chosen page range!
            new PageManager(mainDocument).Remove(
              fromPageIndex,
              toPageIndex
              );
  
            // Serialize the main file!
            Serialize(mainFile, action);
          } break;
          case ActionEnum.PageExtraction:
          {
            // First page to extract.
            int fromPageIndex = PromptPageChoice("Select the start page", mainPagesCount);
            // Last page to extract.
            int toPageIndex = PromptPageChoice("Select the end page", fromPageIndex, mainPagesCount) + 1;
  
            // Extract the chosen page range!
            Document targetDocument = new PageManager(mainDocument).Extract(
              fromPageIndex,
              toPageIndex
              );
  
            // Serialize the target file!
            Serialize(targetDocument.File, action);
          } break;
          case ActionEnum.DocumentMerge:
          {
            // Opening the source file...
            string sourceFilePath = PromptFileChoice("Select the source PDF file");
            using(var sourceFile = new File(sourceFilePath))
            {
              // Append the chosen source document to the main document!
              new PageManager(mainDocument).Add(sourceFile.Document);
            }
            // Serialize the main file!
            Serialize(mainFile, action);
          } break;
          case ActionEnum.DocumentBurst:
          {
            // Split the document into single-page documents!
            IList<Document> splitDocuments = new PageManager(mainDocument).Split();
  
            // Serialize the split files!
            int index = 0;
            foreach(Document splitDocument in splitDocuments)
            {Serialize(splitDocument.File, action, ++index);}
          } break;
          case ActionEnum.DocumentSplitByPageIndex:
          {
            // Number of splits to apply to the source document.
            int splitCount;
            try
            {splitCount = Int32.Parse(PromptChoice("Number of split positions: "));}
            catch
            {splitCount = 0;}
  
            // Split positions within the source document.
            int[] splitIndexes = new int[splitCount];
            {
              int prevSplitIndex = 0;
              for(int index = 0; index < splitCount; index++)
              {
                int splitIndex = PromptPageChoice("Position " + (index + 1) + " of " + splitCount, prevSplitIndex + 1, mainPagesCount);
                splitIndexes[index] = splitIndex;
                prevSplitIndex = splitIndex;
              }
            }
  
            // Split the document at the chosen positions!
            IList<Document> splitDocuments = new PageManager(mainDocument).Split(splitIndexes);
  
            // Serialize the split files!
            {
              int index = 0;
              foreach(Document splitDocument in splitDocuments)
              {Serialize(splitDocument.File, action, ++index);}
            }
          } break;
          case ActionEnum.DocumentSplitOnMaximumFileSize:
          {
            // Maximum file size.
            long maxDataSize;
            {
              long mainFileSize = new io::FileInfo(mainFilePath).Length;
              int kbMaxDataSize;
              do
              {
                try
                {kbMaxDataSize = Int32.Parse(PromptChoice("Max file size (KB): "));}
                catch
                {kbMaxDataSize = 0;}
              } while(kbMaxDataSize == 0);
              maxDataSize = kbMaxDataSize << 10;
              if(maxDataSize > mainFileSize)
              {maxDataSize = mainFileSize;}
            }
  
            // Split the document on maximum file size!
            IList<Document> splitDocuments = new PageManager(mainDocument).Split(maxDataSize);
  
            // Serialize the split files!
            {
              int index = 0;
              foreach(Document splitDocument in splitDocuments)
              {Serialize(splitDocument.File, action, ++index);}
            }
          } break;
        }
      }
    }

    private ActionEnum PromptAction(
      )
    {
      ActionEnum[] actions = (ActionEnum[])Enum.GetValues(typeof(ActionEnum));
      IDictionary<string,string> options = new Dictionary<string,string>();
      for(
        int actionIndex = 0,
          actionsLength = actions.Length;
        actionIndex < actionsLength;
        actionIndex++
        )
      {options[actionIndex.ToString()] = actions[actionIndex].GetDescription();}

      try
      {return actions[Int32.Parse(PromptChoice(options))];}
      catch
      {return actions[0];}
    }

    /**
      <summary>Serializes the specified PDF file.</summary>
      <param name="file">File to serialize.</param>
      <param name="action">Generator.</param>
    */
    private void Serialize(
      File file,
      ActionEnum action
      )
    {Serialize(file, action, null);}

    /**
      <summary>Serializes the specified PDF file.</summary>
      <param name="file">File to serialize.</param>
      <param name="action">Generator.</param>
      <param name="index">File index.</param>
    */
    private void Serialize(
      File file,
      ActionEnum action,
      int? index
      )
    {
      Serialize(
        file,
        GetType().Name + "_" + action.ToString() + (index.HasValue ? "." + index.Value : ""),
        null,
        action.ToString(),
        "managing document pages",
        action.ToString()
        );
    }
  }

  internal static class ActionEnumExtension
  {
    public static string GetDescription(
      this PageManagementSample.ActionEnum value
      )
    {
      StringBuilder builder = new StringBuilder();
      foreach(char c in value.ToString())
      {
        if(Char.IsUpper(c) && builder.Length > 0)
        {builder.Append(" ");}

        builder.Append(c);
      }
      return builder.ToString();
    }
  }
}
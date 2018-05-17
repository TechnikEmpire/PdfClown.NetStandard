using org.pdfclown.documents;
using org.pdfclown.documents.files;
using actions = org.pdfclown.documents.interaction.actions;
using org.pdfclown.documents.interaction.navigation.document;
using org.pdfclown.files;
using org.pdfclown.objects;

using System;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to inspect the bookmarks of a PDF document.</summary>
  */
  public class BookmarksParsingSample
    : Sample
  {
    public override void Run(
      )
    {
      // 1. Opening the PDF file...
      string filePath = PromptFileChoice("Please select a PDF file");
      using(var file = new File(filePath))
      {
        Document document = file.Document;
  
        // 2. Get the bookmarks collection!
        Bookmarks bookmarks = document.Bookmarks;
        if(!bookmarks.Exists())
        {Console.WriteLine("\nNo bookmark available (Outline dictionary not found).");}
        else
        {
          Console.WriteLine("\nIterating through the bookmarks collection (please wait)...\n");
          // 3. Show the bookmarks!
          PrintBookmarks(bookmarks);
        }
      }
    }

    private void PrintBookmarks(
      Bookmarks bookmarks
      )
    {
      if(bookmarks == null)
        return;

      foreach(Bookmark bookmark in bookmarks)
      {
        // Show current bookmark!
        Console.WriteLine("Bookmark '" + bookmark.Title + "'");
        Console.Write("    Target: ");
        PdfObjectWrapper target = bookmark.Target;
        if(target is Destination)
        {PrintDestination((Destination)target);}
        else if(target is actions::Action)
        {PrintAction((actions::Action)target);}
        else if(target == null)
        {Console.WriteLine("[not available]");}
        else
        {Console.WriteLine("[unknown type: " + target.GetType().Name + "]");}

        // Show child bookmarks!
        PrintBookmarks(bookmark.Bookmarks);
      }
    }

    private void PrintAction(
      actions::Action action
      )
    {
      /*
        NOTE: Here we have to deal with reflection as a workaround
        to the lack of type covariance support in C# (so bad -- any better solution?).
      */
      Console.WriteLine("Action [" + action.GetType().Name + "] " + action.BaseObject);
      if(action.Is(typeof(actions::GoToDestination<>)))
      {
        if(action.Is(typeof(actions::GotoNonLocal<>)))
        {
          FileSpecification destinationFile = (FileSpecification)action.Get("DestinationFile");
          if(destinationFile != null)
          {Console.WriteLine("      Filename: " + destinationFile.Path);}

          if(action is actions::GoToEmbedded)
          {
            actions::GoToEmbedded.PathElement target = ((actions::GoToEmbedded)action).DestinationPath;
            Console.WriteLine("      EmbeddedFilename: " + target.EmbeddedFileName + " Relation: " + target.Relation);
          }
        }
        Console.Write("      ");
        PrintDestination((Destination)action.Get("Destination"));
      }
      else if(action is actions::GoToURI)
      {Console.WriteLine("      URI: " + ((actions::GoToURI)action).URI);}
    }

    private void PrintDestination(
      Destination destination
      )
    {
      Console.WriteLine(destination.GetType().Name + " " + destination.BaseObject);
      Console.Write("        Page ");
      object pageRef = destination.Page;
      if(pageRef is Page)
      {
        Page page = (Page)pageRef;
        Console.WriteLine(page.Number + " [ID: " + page.BaseObject + "]");
      }
      else
      {Console.WriteLine((int)pageRef+1);}

      object location = destination.Location;
      if(location != null)
      {Console.WriteLine("        Location {0}", location);}

      double? zoom = destination.Zoom;
      if(zoom.HasValue)
      {Console.WriteLine("        Zoom {0}", zoom.Value);}
    }
  }
}
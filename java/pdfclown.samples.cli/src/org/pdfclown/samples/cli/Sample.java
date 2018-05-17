package org.pdfclown.samples.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.viewer.ViewerPreferences;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;

/**
  Abstract sample.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2.1, 05/02/15
*/
public abstract class Sample
{
  // <class>
  // <dynamic>
  // <fields>
  private String inputPath;
  private String outputPath;

  private boolean quit;
  // </fields>

  // <interface>
  // <public>
  /**
    Gets whether the sample was exited before its completion.
  */
  public boolean isQuit(
    )
  {return quit;}

  /**
    Executes the sample.

    @return Whether the sample has been completed.
  */
  public abstract void run(
    );
  // </public>

  // <protected>
  protected String getIndentation(
    int level
    )
  {
    StringBuilder indentationBuilder = new StringBuilder();
    for(int i = 0; i < level; i++)
    {indentationBuilder.append(' ');}
    return indentationBuilder.toString();
  }

  /**
    Gets the path used to serialize output files.
  */
  protected String getOutputPath(
    )
  {return getOutputPath(null);}

  /**
    Gets the path used to serialize output files.

    @param fileName Relative output file path.
  */
  protected String getOutputPath(
    String fileName
    )
  {return outputPath + (fileName != null ? java.io.File.separator + fileName : "");}

  /**
    Gets the path to a sample resource.

    @param resourceName Relative resource path.
  */
  protected String getResourcePath(
    String resourceName
    )
  {return inputPath + java.io.File.separator + resourceName;}

  /**
    Prompts a message to the user.

    @param message Text to show.
  */
  protected void prompt(
    String message
    )
  {Utils.prompt(message);}

  /**
    Gets the user's choice from the given request.

    @param message Description of the request to show to the user.
    @return User choice.
  */
  protected String promptChoice(
    String message
    )
  {
    System.out.print(message);
    Scanner in = new Scanner(System.in);
    try
    {return in.nextLine();}
    catch(Exception e)
    {return null;}
  }

  /**
    Gets the user's choice from the given options.

    @param options Available options to show to the user.
    @return Chosen option key.
  */
  protected String promptChoice(
    Map<String,String> options
    )
  {
    System.out.println();
    List<Map.Entry<String,String>> optionEntries = new ArrayList<Map.Entry<String,String>>(options.entrySet());
    Collections.sort(
      optionEntries,
      new Comparator<Map.Entry<String,String>>()
      {
        @Override
        public int compare(Map.Entry<String,String> o1, Map.Entry<String,String> o2)
        {return o1.getKey().compareTo(o2.getKey());};
      }
      );
    for(Map.Entry<String,String> option : optionEntries)
    {
      System.out.println(
          (option.getKey().equals("") ? "ENTER" : "[" + option.getKey() + "]")
            + " " + option.getValue()
          );
    }
    System.out.print("Please select: ");
    Scanner in = new Scanner(System.in);
    try
    {return in.nextLine();}
    catch(Exception e)
    {return null;}
  }

  protected String promptFileChoice(
    String inputDescription
    )
  {
    Scanner in = new Scanner(System.in);

    java.io.File resourceFolder = new java.io.File(inputPath + java.io.File.separator + "pdf");
    try
    {System.out.println("\nAvailable PDF files (" + resourceFolder.getCanonicalPath() + "):");}
    catch(IOException e1)
    {/* NOOP */}

    // Get the list of available PDF files!
    SampleResources resources = new SampleResources(resourceFolder);
    List<String> fileNames = Arrays.asList(resources.filter("pdf"));
    Collections.sort(fileNames);

    // Display files!
    resources.printList((String[])fileNames.toArray());

    while(true)
    {
      // Get the user's choice!
      System.out.print(inputDescription + ": ");
      try
      {return resourceFolder.getPath() + java.io.File.separator + fileNames.get(Integer.parseInt(in.nextLine()));}
      catch(Exception e)
      {/* NOOP */}
    }
  }

  /**
    Prompts the user for advancing to the next page.

    @param page Next page.
    @param skip Whether the prompt has to be skipped.
    @return Whether to advance.
  */
  protected boolean promptNextPage(
    Page page,
    boolean skip
    )
  {
    int pageIndex = page.getIndex();
    if(pageIndex > 0 && !skip)
    {
      Map<String,String> options = new HashMap<String,String>();
      options.put("", "Scan next page");
      options.put("Q", "End scanning");
      if(!promptChoice(options).equals(""))
        return false;
    }

    System.out.println("\nScanning page " + (pageIndex+1) + "...\n");
    return true;
  }

  /**
    Prompts the user for a page index to select.

    @param inputDescription Message prompted to the user.
    @param pageCount Page count.
    @return Selected page index.
  */
  protected int promptPageChoice(
    String inputDescription,
    int pageCount
    )
  {return promptPageChoice(inputDescription, 0, pageCount);}

  /**
    Prompts the user for a page index to select.

    @param inputDescription Message prompted to the user.
    @param startIndex First page index, inclusive.
    @param endIndex Last page index, exclusive.
    @return Selected page index.
  */
  protected int promptPageChoice(
    String inputDescription,
    int startIndex,
    int endIndex
    )
  {
    int pageIndex;
    try
    {pageIndex = Integer.parseInt(promptChoice(inputDescription + " [" + (startIndex + 1) + "-" + endIndex + "]: ")) - 1;}
    catch(Exception e)
    {pageIndex = startIndex;}
    if(pageIndex < startIndex)
    {pageIndex = startIndex;}
    else if(pageIndex >= endIndex)
    {pageIndex = endIndex - 1;}

    return pageIndex;
  }

  /**
    Indicates that the sample was exited before its completion.
  */
  protected void quit(
    )
  {quit = true;}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @return Serialization path.
  */
  protected String serialize(
    File file
    )
  {return serialize(file, null, null, null);}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @param serializationMode Serialization mode.
    @return Serialization path.
  */
  protected String serialize(
    File file,
    SerializationModeEnum serializationMode
    )
  {return serialize(file, serializationMode, null, null, null);}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @param fileName Output file name.
    @return Serialization path.
  */
  protected String serialize(
    File file,
    String fileName
    )
  {return serialize(file, fileName, null, null);}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @param fileName Output file name.
    @param serializationMode Serialization mode.
    @return Serialization path.
  */
  protected String serialize(
    File file,
    String fileName,
    SerializationModeEnum serializationMode
    )
  {return serialize(file, fileName, serializationMode, null, null, null);}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @param title Document title.
    @param subject Document subject.
    @param keywords Document keywords.
    @return Serialization path.
  */
  protected String serialize(
    File file,
    String title,
    String subject,
    String keywords
    )
  {return serialize(file, null, title, subject, keywords);}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @param serializationMode Serialization mode.
    @param title Document title.
    @param subject Document subject.
    @param keywords Document keywords.
    @return Serialization path.
  */
  protected String serialize(
    File file,
    SerializationModeEnum serializationMode,
    String title,
    String subject,
    String keywords
    )
  {return serialize(file, getClass().getSimpleName(), serializationMode, title, subject, keywords);}

  /**
    Serializes the given PDF Clown file object.

    @param file PDF file to serialize.
    @param fileName Output file name.
    @param serializationMode Serialization mode.
    @param title Document title.
    @param subject Document subject.
    @param keywords Document keywords.
    @return Serialization path.
  */
  protected String serialize(
    File file,
    String fileName,
    SerializationModeEnum serializationMode,
    String title,
    String subject,
    String keywords
    )
  {
    applyDocumentSettings(file.getDocument(), title, subject, keywords);

    System.out.println();

    if(serializationMode == null)
    {
      if(file.getReader() == null) // New file.
      {serializationMode = SerializationModeEnum.Standard;}
      else // Existing file.
      {
        Scanner in = new Scanner(System.in);
        System.out.println("[0] Standard serialization");
        System.out.println("[1] Incremental update");
        System.out.print("Please select a serialization mode: ");
        try
        {serializationMode = SerializationModeEnum.values()[Integer.parseInt(in.nextLine())];}
        catch(Exception e)
        {serializationMode = SerializationModeEnum.Standard;}
      }
    }

    java.io.File outputFile = new java.io.File(outputPath + java.io.File.separator + fileName + "." + serializationMode + ".pdf");

    // Save the file!
    /*
      NOTE: You can also save to a generic target stream (see save() method overloads).
    */
    try
    {file.save(outputFile, serializationMode);}
    catch(Exception e)
    {
      System.out.println("File writing failed: " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println("\nOutput: " + outputFile.getPath());

    return outputFile.getPath();
  }
  // </protected>

  // <internal>
  final void initialize(
    String inputPath,
    String outputPath
    )
  {
    this.inputPath = inputPath;
    this.outputPath = outputPath;
  }
  // </internal>

  // <private>
  private void applyDocumentSettings(
    Document document,
    String title,
    String subject,
    String keywords
    )
  {
    if(title == null)
      return;

    // Viewer preferences.
    ViewerPreferences viewerPreferences = document.getViewerPreferences();
    viewerPreferences.setDocTitleDisplayed(true);

    // Document metadata.
    Information info = document.getInformation();
    info.setAuthor("Stefano");
    info.setCreationDate(new Date());
    info.setCreator(getClass().getName());
    info.setTitle("PDF Clown - " + title + " sample");
    info.setSubject("Sample about " + subject + " using PDF Clown");
    info.setKeywords(keywords);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}

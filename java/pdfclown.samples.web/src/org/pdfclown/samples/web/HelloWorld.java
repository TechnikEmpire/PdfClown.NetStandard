package org.pdfclown.samples.web;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.pdfclown.bytes.Buffer;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.PageFormat;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.util.math.geom.Dimension;

/**
  Servlet implementation class HelloWorld
*/
public class HelloWorld extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doPost(
    HttpServletRequest request,
    HttpServletResponse response
    ) throws ServletException, IOException
  {
    if(!ServletFileUpload.isMultipartContent(request))
      return;

    // 1. Parsing the form fields...
    FileItem item = null;
    String comment = null;
    FileItem imageFileFormField = null;
    try
    {
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      List<?> items = upload.parseRequest(request);
      Iterator<?> iter = items.iterator();
      while(iter.hasNext())
      {
          item = (FileItem)iter.next();
          if(item.getFieldName().equals("file"))
          {imageFileFormField = item;}
          if(item.getFieldName().equals("comment"))
          {comment = item.getString();}
      }
    }
    catch(Exception e)
    {
      System.err.println("An exception happened while parsing the form fields: " + e.getMessage());
      e.printStackTrace();
      return;
    }

    // 2. Building the document...
    File file = null;
    try
    {
      // Instantiate a new PDF document!
      file = new File();
      Document document = file.getDocument();

      buildPdf_page(
        document,
        imageFileFormField,
        comment
        );

      buildPdf_metadata(document);
    }
    catch(Exception e)
    {
      System.err.println("An exception happened while building the sample: " + e.getMessage());
      e.printStackTrace();
      return;
    }

    // 3. Serializing the document...
    try
    {
      file.save(
        new org.pdfclown.bytes.OutputStream(
          response.getOutputStream()
          ),
        SerializationModeEnum.Standard
        );
      response.setContentType("application/pdf");
    }
    catch(Exception e)
    {
      System.err.println("An exception happened while serializing the sample: " + e.getMessage());
      e.printStackTrace();
      return;
    }
    finally
    {
      file.close();
    }
  }

  private void buildPdf_metadata(
    Document document
    )
  {
    // Document metadata.
    Information info = new Information(document);
    document.setInformation(info);
    info.setAuthor("Stefano Chizzolini");
    info.setCreationDate(new Date());
    info.setCreator(HelloWorld.class.getName());
    info.setTitle("Sample document");
    info.setSubject("Online PDF creation sample through servlet.");
  }

  private void buildPdf_page(
    Document document,
    FileItem imageFileFormField,
    String comment
    )
  {
    // Set default page size (A4)!
    document.setPageSize(PageFormat.getSize());

    // Add page!
    Page page = new Page(document);
    document.getPages().add(page);
    Dimension2D pageSize = page.getSize();

    PrimitiveComposer composer = new PrimitiveComposer(page);
    // Add the background template!
    composer.showXObject(
      SampleHelper.createTemplate(document)
      );

    // Wrap the content composer inside a block filter in order to achieve higher-level typographic control!
    BlockComposer blockComposer = new BlockComposer(composer);
    blockComposer.setHyphenation(true);

    Rectangle2D.Double frame = new Rectangle2D.Double(
      30,
      150,
      pageSize.getWidth() - 110,
      pageSize.getHeight() - 250
      );

    blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
    StandardType1Font titleFont = new StandardType1Font(
      document,
      StandardType1Font.FamilyEnum.Times,
      true,
      false
      );
    composer.setFont(titleFont,48);
    blockComposer.showText("Welcome"); blockComposer.showBreak();
    StandardType1Font bodyFont = new StandardType1Font(
      document,
      StandardType1Font.FamilyEnum.Times,
      false,
      false
      );
    composer.setFont(bodyFont,16);
    blockComposer.showText("This is an on-the-fly servlet-driven PDF sample document generated by PDF Clown for Java.");
    blockComposer.end();

    // Move past the closed block!
    frame.y = blockComposer.getBoundBox().getMaxY() + 30;
    frame.height -= (blockComposer.getBoundBox().getHeight() + 30);

    // Showing the posted image...
    // Instantiate a jpeg image object!
    Image image = null;
    try
    {
      image = Image.get(
        new Buffer(imageFileFormField.get())
        ); // Abstract image (entity).
    }
    catch(Exception e)
    {/* NOOP. */}
    if(image == null)
    {
      blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
      composer.setFont(bodyFont,12);
      composer.setFillColor(new DeviceRGBColor(1,0,0));
      blockComposer.showText("The file you uploaded wasn't a valid JPEG image!");
      blockComposer.end();

      // Move past the closed block!
      frame.y = blockComposer.getBoundBox().getMaxY() + 20;
      frame.height -= (blockComposer.getBoundBox().getHeight() + 20);
    }
    else
    {
      blockComposer.begin(frame,XAlignmentEnum.Left,YAlignmentEnum.Top);
      composer.setFont(bodyFont,12);
      blockComposer.showText("Here it is the image you uploaded: ");
      blockComposer.end();

      // Move past the closed block!
      frame.y = blockComposer.getBoundBox().getMaxY() + 20;
      frame.height -= (blockComposer.getBoundBox().getHeight() + 20);

      double width = image.getWidth(), height = image.getHeight();
      if(width > frame.getWidth())
      {
        height *= frame.getWidth() / width;
        width = frame.getWidth();
      }
      if(height > frame.getHeight() / 2)
      {
        width *= frame.getHeight() / 2 / height;
        height = frame.getHeight() / 2;
      }
      // Show the image!
      composer.showXObject(
        image.toXObject(document),
        new Point2D.Double(
          (pageSize.getWidth() - 90 - width) / 2 + 20,
          blockComposer.getBoundBox().getMaxY() + 20
          ),
        new Dimension(width,height)
        );
      // Move past the image closed block!
      frame.x = (pageSize.getWidth() - 90 - width) / 2 + 20;
      frame.y += (height + 7);
      frame.height -= (height + 7);
      frame.width = width;
    }

    if (comment != null)
    {
      blockComposer.begin(frame,XAlignmentEnum.Justify,YAlignmentEnum.Top);
      composer.setFont(
        new StandardType1Font(
          document,
          StandardType1Font.FamilyEnum.Courier,
          false,
          false
          ),
        7
        );
      blockComposer.showText(comment);
      blockComposer.end();
    }

    composer.flush();
  }
}

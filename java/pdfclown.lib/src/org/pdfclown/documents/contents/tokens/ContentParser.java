/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)
    * Stephen Cleary (bug reporter [FIX:51], https://sourceforge.net/u/stephencleary/)

  This file should be part of the source code distribution of "PDF Clown library"
  (the Program): see the accompanying README files for more info.

  This Program is free software; you can redistribute it and/or modify it under the terms
  of the GNU Lesser General Public License as published by the Free Software Foundation;
  either version 3 of the License, or (at your option) any later version.

  This Program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  either expressed or implied; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this
  Program (see README files); if not, go to the GNU website (http://www.gnu.org/licenses/).

  Redistribution and use, with or without modification, are permitted provided that such
  redistributions retain the above copyright notice, license and disclaimer, along with
  this list of conditions.
*/

package org.pdfclown.documents.contents.tokens;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.contents.objects.BeginInlineImage;
import org.pdfclown.documents.contents.objects.BeginMarkedContent;
import org.pdfclown.documents.contents.objects.BeginSubpath;
import org.pdfclown.documents.contents.objects.BeginText;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.DrawRectangle;
import org.pdfclown.documents.contents.objects.EndInlineImage;
import org.pdfclown.documents.contents.objects.EndMarkedContent;
import org.pdfclown.documents.contents.objects.EndText;
import org.pdfclown.documents.contents.objects.InlineImage;
import org.pdfclown.documents.contents.objects.InlineImageBody;
import org.pdfclown.documents.contents.objects.InlineImageHeader;
import org.pdfclown.documents.contents.objects.LocalGraphicsState;
import org.pdfclown.documents.contents.objects.MarkedContent;
import org.pdfclown.documents.contents.objects.Operation;
import org.pdfclown.documents.contents.objects.PaintPath;
import org.pdfclown.documents.contents.objects.PaintShading;
import org.pdfclown.documents.contents.objects.PaintXObject;
import org.pdfclown.documents.contents.objects.Path;
import org.pdfclown.documents.contents.objects.RestoreGraphicsState;
import org.pdfclown.documents.contents.objects.SaveGraphicsState;
import org.pdfclown.documents.contents.objects.Shading;
import org.pdfclown.documents.contents.objects.Text;
import org.pdfclown.documents.contents.objects.XObject;
import org.pdfclown.objects.PdfByteString;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.tokens.BaseParser;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.util.parsers.PostScriptParseException;

/**
  Content stream parser [PDF:1.6:3.7.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.2.0, 05/22/15
*/
public final class ContentParser
  extends BaseParser
{
  // <class>
  // <dynamic>
  // <constructors>
  public ContentParser(
    IInputStream stream
    )
  {super(stream);}

  public ContentParser(
    byte[] data
    )
  {super(data);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Parses the next content object [PDF:1.6:4.1].
  */
  public ContentObject parseContentObject(
    )
  {
    final Operation operation = parseOperation();
    if(operation instanceof PaintXObject) // External object.
      return new XObject((PaintXObject)operation);
    else if(operation instanceof PaintShading) // Shading.
      return new Shading((PaintShading)operation);
    else if(operation instanceof BeginSubpath
      || operation instanceof DrawRectangle) // Path.
      return parsePath(operation);
    else if(operation instanceof BeginText) // Text.
      return new Text(
        parseContentObjects()
        );
    else if(operation instanceof SaveGraphicsState) // Local graphics state.
      return new LocalGraphicsState(
        parseContentObjects()
        );
    else if(operation instanceof BeginMarkedContent) // Marked-content sequence.
      return new MarkedContent(
        (BeginMarkedContent)operation,
        parseContentObjects()
        );
    else if(operation instanceof BeginInlineImage) // Inline image.
      return parseInlineImage();
    else // Single operation.
      return operation;
  }

  /**
    Parses the next content objects.
  */
  public List<ContentObject> parseContentObjects(
    )
  {
    final List<ContentObject> contentObjects = new ArrayList<ContentObject>();
    while(moveNext())
    {
      ContentObject contentObject = parseContentObject();
      // Multiple-operation graphics object end?
      if(contentObject instanceof EndText // Text.
        || contentObject instanceof RestoreGraphicsState // Local graphics state.
        || contentObject instanceof EndMarkedContent // End marked-content sequence.
        || contentObject instanceof EndInlineImage) // Inline image.
        return contentObjects;

      contentObjects.add(contentObject);
    }
    return contentObjects;
  }

  /**
    Parses the next operation.
  */
  public Operation parseOperation(
    )
  {
    String operator = null;
    final List<PdfDirectObject> operands = new ArrayList<PdfDirectObject>();
    // Parsing the operation parts...
    do
    {
      switch(getTokenType())
      {
        case Keyword:
          operator = (String)getToken();
          break;
        default:
          operands.add(parsePdfObject());
          break;
      }
    } while(operator == null && moveNext());
    return Operation.get(operator,operands);
  }

  @Override
  public PdfDirectObject parsePdfObject(
    )
  {
    switch(getTokenType())
    {
      case Literal:
        if(getToken() instanceof String)
          return new PdfByteString(Encoding.Pdf.encode((String)getToken()));
        break;
      case Hex:
        return new PdfByteString((String)getToken());
      default:
      {
        /* NOOP */
      }
    }
    return (PdfDirectObject)super.parsePdfObject();
  }
  // </public>

  // <private>
  private InlineImage parseInlineImage(
    )
  {
    InlineImageHeader header;
    {
      final List<PdfDirectObject> operands = new ArrayList<PdfDirectObject>();
      // Parsing the image entries...
      while(moveNext()
        && getTokenType() != TokenTypeEnum.Keyword) // Ends at image body beginning (ID operator).
      {operands.add(parsePdfObject());}
      header = new InlineImageHeader(operands);
    }

    InlineImageBody body;
    try
    {
      // [FIX:51,74] Wrong 'EI' token handling on inline image parsing.
      IInputStream stream = getStream();
      stream.readByte(); // Should be the whitespace following the 'ID' token.
      Buffer data = new Buffer();
      ByteArrayOutputStream endChunkBuffer = new ByteArrayOutputStream(3);
      int endChunkIndex = -1;
      while(true)
      {
        byte curByte = stream.readByte();
        if(endChunkIndex == -1)
        {
          if(isWhitespace(curByte))
          {
            /*
              NOTE: Whitespace characters may announce the beginning of the end image operator.
            */
            endChunkBuffer.write(curByte);
            endChunkIndex++;
          }
          else
          {data.append(curByte);}
        }
        else if(endChunkIndex == 0 && isWhitespace(curByte))
        {
          /*
            NOTE: Only the last whitespace character may announce the beginning of the end image 
            operator.
          */
          data.append(endChunkBuffer.toByteArray());
          endChunkBuffer.reset();
          endChunkBuffer.write(curByte);
        }
        else if((endChunkIndex == 0 && curByte == 'E')
          || (endChunkIndex == 1 && curByte == 'I'))
        {
          /*
            NOTE: End image operator characters.
          */
          endChunkBuffer.write(curByte);
          endChunkIndex++;
        }
        else if(endChunkIndex == 2 && isWhitespace(curByte))
          /*
            NOTE: The whitespace character after the end image operator completes the pattern.
          */
          break;
        else
        {
          if(endChunkIndex > -1)
          {
            data.append(endChunkBuffer.toByteArray());
            endChunkBuffer.reset();
            endChunkIndex = -1;
          }
          data.append(curByte);
        }
      }
      body = new InlineImageBody(data);
    }
    catch(EOFException e)
    {throw new PostScriptParseException("No 'EI' token found to close inline image data stream.", e);}

    return new InlineImage(header, body);
  }

  private Path parsePath(
    Operation beginOperation
    )
  {
    /*
      NOTE: Paths do not have an explicit end operation, so we must infer it
      looking for the first non-painting operation.
    */
    final List<ContentObject> operations = new ArrayList<ContentObject>();
    {
      operations.add(beginOperation);
      long position = getPosition();
      boolean closeable = false;
      while(moveNext())
      {
        Operation operation = parseOperation();
        // Multiple-operation graphics object closeable?
        if(operation instanceof PaintPath) // Painting operation.
        {closeable = true;}
        else if(closeable) // Past end (first non-painting operation).
        {
          seek(position); // Rolls back to the last path-related operation.
          break;
        }

        operations.add(operation);
        position = getPosition();
      }
    }
    return new Path(operations);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
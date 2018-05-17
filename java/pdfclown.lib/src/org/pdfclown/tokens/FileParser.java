/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

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

package org.pdfclown.tokens;

import java.io.EOFException;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.parsers.PostScriptParseException;

/**
  PDF file parser [PDF:1.7:3.2,3.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.2.0, 05/22/15
*/
public final class FileParser
  extends BaseParser
{
  // <class>
  // <classes>
  public class Reference
  {
    private final int generationNumber;
    private final int objectNumber;

    private Reference(
      int objectNumber,
      int generationNumber
      )
    {
      this.objectNumber = objectNumber;
      this.generationNumber = generationNumber;
    }

    public int getGenerationNumber(
      )
    {return generationNumber;}

    public int getObjectNumber(
      )
    {return objectNumber;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final int EOFMarkerChunkSize = 1024; // [PDF:1.6:H.3.18].
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private final File file;
  // </fields>

  // <constructors>
  FileParser(
    IInputStream stream,
    File file
    )
  {
    super(stream);
    this.file = file;
  }
  // </constructors>

  // <interface>
  // <public>
  public File getFile(
    )
  {return file;}

  @Override
  public boolean moveNext(
    )
  {
    boolean moved = super.moveNext();
    if(moved)
    {
      switch(getTokenType())
      {
        case Integer:
        {
          /*
            NOTE: We need to verify whether indirect reference pattern is applicable:
            ref :=  { int int 'R' }
          */
          IInputStream stream = getStream();
          long baseOffset = stream.getPosition(); // Backs up the recovery position.

          // 1. Object number.
          int objectNumber = (Integer)getToken();
          // 2. Generation number.
          super.moveNext();
          if(getTokenType() == TokenTypeEnum.Integer)
          {
            int generationNumber = (Integer)getToken();
            // 3. Reference keyword.
            super.moveNext();
            if(getTokenType() == TokenTypeEnum.Keyword
              && getToken().equals(Keyword.Reference))
            {setToken(new Reference(objectNumber,generationNumber));}
          }
          if(!(getToken() instanceof Reference))
          {
            // Rollback!
            try
            {stream.seek(baseOffset);}
            catch(EOFException e)
            {throw new RuntimeException(e);}
            setToken(objectNumber);
            setTokenType(TokenTypeEnum.Integer);
          }
        } break;
        default:
        {
          /* NOOP */
        }
      }
    }
    return moved;
  }

  @Override
  public PdfDataObject parsePdfObject(
    )
  {
    switch(getTokenType())
    {
      case Keyword:
        if(getToken() instanceof Reference)
        {
          Reference reference = (Reference)getToken();
          return new PdfReference(reference.getObjectNumber(), reference.getGenerationNumber(), file);
        }
        break;
      default:
      {
        /* NOOP */
      }
    }

    PdfDataObject pdfObject = super.parsePdfObject();
    if(pdfObject instanceof PdfDictionary)
    {
      try
      {
        IInputStream stream = getStream();
        long oldOffset = stream.getPosition();
        moveNext();
        // Is this dictionary the header of a stream object [PDF:1.6:3.2.7]?
        if(getTokenType() == TokenTypeEnum.Keyword
          && getToken().equals(Keyword.BeginStream)) // Stream.
        {
          PdfDictionary streamHeader = (PdfDictionary)pdfObject;
  
          // Keep track of current position!
          /*
            NOTE: Indirect reference resolution is an outbound call which affects the stream pointer
            position, so we need to recover our current position after it returns.
          */
          long position = stream.getPosition();
          // Get the stream length!
          int length = ((PdfInteger)streamHeader.resolve(PdfName.Length)).getValue();
          // Move to the stream data beginning!
          stream.seek(position);
          skipEOL();
  
          // Copy the stream data to the instance!
          byte[] data = new byte[length];
          stream.read(data);
  
          moveNext(); // Postcondition (last token should be 'endstream' keyword).
  
          Object streamType = streamHeader.get(PdfName.Type);
          if(PdfName.ObjStm.equals(streamType)) // Object stream [PDF:1.6:3.4.6].
            return new ObjectStream(
              streamHeader,
              new Buffer(data)
              );
          else if(PdfName.XRef.equals(streamType)) // Cross-reference stream [PDF:1.6:3.4.7].
            return new XRefStream(
              streamHeader,
              new Buffer(data)
              );
          else // Generic stream.
            return new PdfStream(
              streamHeader,
              new Buffer(data)
              );
        }
        else // Stand-alone dictionary.
        {stream.seek(oldOffset);} // Restores postcondition (last token should be the dictionary end).
      }
      catch(EOFException e)
      {throw new PostScriptParseException("Malformed stream object", e);}
    }
    return pdfObject;
  }

  /**
    Parses the specified PDF indirect object [PDF:1.6:3.2.9].
    
    @param xrefEntry
      Cross-reference entry of the indirect object to parse.
  */
  public PdfDataObject parsePdfObject(
    XRefEntry xrefEntry
    )
  {
    // Go to the beginning of the indirect object!
    seek(xrefEntry.getOffset());
    // Skip the indirect-object header!
    moveNext(4);
    
    // Empty indirect object?
    if(getTokenType() == TokenTypeEnum.Keyword
        && Keyword.EndIndirectObject.equals(getToken()))
      return null; 
    
    // Get the indirect data object!
    return parsePdfObject();
  }

  /**
    Retrieves the PDF version of the file [PDF:1.6:3.4.1].
  */
  public String retrieveVersion(
    )
  {
    try
    {
      IInputStream stream = getStream();
      stream.seek(0);
      String header = stream.readString(10);
      if(!header.startsWith(Keyword.BOF))
        throw new PostScriptParseException("PDF header not found.", this);
  
      return header.substring(Keyword.BOF.length(),Keyword.BOF.length() + 3);
    }
    catch(EOFException e)
    {throw new RuntimeException(e);}
  }

  /**
    Retrieves the starting position of the last xref-table section [PDF:1.6:3.4.4].
  */
  public long retrieveXRefOffset(
    )
  {
    try
    {
      // [FIX:69] 'startxref' keyword not found (file was corrupted by alien data in the tail).
      IInputStream stream = getStream();
      long streamLength = stream.getLength();
      long position = streamLength;
      int chunkSize = (int)Math.min(streamLength, EOFMarkerChunkSize);
      int index = -1;
      while(index < 0 && position > 0)
      {
        /*
          NOTE: This condition prevents the keyword from being split by the chunk boundary.
        */
        if(position < streamLength)
        {position += Keyword.StartXRef.length();}
        position -= chunkSize;
        if(position < 0)
        {position = 0;}
        stream.seek(position);
  
        // Get 'startxref' keyword position!
        index = stream.readString(chunkSize).lastIndexOf(Keyword.StartXRef);
      }
      if(index < 0)
        throw new PostScriptParseException("'" + Keyword.StartXRef + "' keyword not found.", this);
  
      // Go past the 'startxref' keyword!
      stream.seek(position + index); moveNext();
  
      // Get the xref offset!
      moveNext();
      if(getTokenType() != TokenTypeEnum.Integer)
        throw new PostScriptParseException("'" + Keyword.StartXRef + "' value invalid.", this);
  
      return (Integer)getToken();
    }
    catch(EOFException e)
    {throw new RuntimeException(e);}
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}

/*
  Copyright 2010-2015 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.files.IndirectObjects;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

/**
  PDF file writer implementing compressed cross-reference stream [PDF:1.6:3.4.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2.1, 03/21/15
*/
final class CompressedWriter
  extends Writer
{
  // <class>
  // <static>
  // <fields>
  /**
    Maximum number of objects in individual object streams [PDF:1.7:H:19].
  */
  private static int ObjectStreamMaxEntryCount = 100;
  // </fields>
  // </static>
  
  // <dynamic>
  // <constructors>
  CompressedWriter(
    File file,
    IOutputStream stream
    )
  {super(file, stream);}
  // </constructors>

  // <interface>
  // <protected>
  @Override
  protected void writeIncremental(
    )
  {
    // 1. Original content (header, body and previous trailer).
    FileParser parser = file.getReader().getParser();
    stream.write(parser.getStream());

    // 2. Body update (modified indirect objects insertion).
    XRefEntry xrefStreamEntry;
    {
      // Create the xref stream!
      /*
        NOTE: Incremental xref information structure comprises multiple sections; this update adds a
        new section.
      */
      XRefStream xrefStream = new XRefStream(file);

      // 2.1. Indirect objects.
      IndirectObjects indirectObjects = file.getIndirectObjects();

      // 2.1.1. Modified indirect objects serialization.
      XRefEntry prevFreeEntry = null;
      /*
        NOTE: Any uncompressed indirect object will be compressed.
      */
      ObjectStream objectStream = null;
      /*
        NOTE: Any previously-compressed indirect object will have its original object stream updated
        through a new extension object stream.
      */
      Map<Integer,ObjectStream> extensionObjectStreams = new HashMap<Integer,ObjectStream>();
      int indirectObjectsPrecompressCount = indirectObjects.size();
      for(PdfIndirectObject indirectObject : new ArrayList<PdfIndirectObject>(indirectObjects.getModifiedObjects().values()))
      {
        if(indirectObject.isCompressible())
        {
          if(objectStream == null
            || objectStream.size() >= ObjectStreamMaxEntryCount)
          {file.register(objectStream = new ObjectStream());}

          indirectObject.compress(objectStream);
        }
        
        prevFreeEntry = addXRefEntry(
          indirectObject,
          xrefStream,
          prevFreeEntry,
          extensionObjectStreams
          );
      }
      // 2.1.2. Additional object streams serialization.
      for(int index = indirectObjectsPrecompressCount, limit = indirectObjects.size(); index < limit; index++)
      {
        prevFreeEntry = addXRefEntry(
          indirectObjects.get(index),
          xrefStream,
          prevFreeEntry,
          null
          );
      }
      if(prevFreeEntry != null)
      {
        prevFreeEntry.setOffset(0); // Links back to the first free object. NOTE: The first entry in the table (object number 0) is always free.
      }

      // 2.2. XRef stream.
      updateTrailer(xrefStream.getHeader(), stream);
      xrefStream.getHeader().put(PdfName.Prev, PdfInteger.get((int)parser.retrieveXRefOffset()));
      addXRefEntry(
        /*
          NOTE: This xref stream indirect object is purposely temporary (i.e. not registered into the
          file's indirect objects collection).
        */
        new PdfIndirectObject(
          file,
          xrefStream,
          xrefStreamEntry = new XRefEntry(indirectObjects.size(), 0, (int)stream.getLength(), XRefEntry.UsageEnum.InUse)
          ),
        xrefStream,
        null,
        null
        );
    }

    // 3. Tail.
    writeTail(xrefStreamEntry.getOffset());
  }

  @Override
  protected void writeLinearized(
    )
  {throw new NotImplementedException();}

  @Override
  protected void writeStandard(
    )
  {
    // 1. Header [PDF:1.6:3.4.1].
    writeHeader();

    // 2. Body [PDF:1.6:3.4.2,3,7].
    XRefEntry xrefStreamEntry;
    {
      // Create the xref stream!
      /*
        NOTE: Standard xref information structure comprises just one section; the xref stream is
        generated on-the-fly and kept volatile not to interfere with the existing file structure.
      */
      XRefStream xrefStream = new XRefStream(file);

      // 2.1. Indirect objects.
      IndirectObjects indirectObjects = file.getIndirectObjects();

      // Indirect objects serialization.
      XRefEntry prevFreeEntry = null;
      ObjectStream objectStream = null;
      for(PdfIndirectObject indirectObject : indirectObjects)
      {
        if(indirectObject.isCompressible())
        {
          if(objectStream == null
            || objectStream.size() >= ObjectStreamMaxEntryCount)
          {file.register(objectStream = new ObjectStream());}

          indirectObject.compress(objectStream);
        }
        
        prevFreeEntry = addXRefEntry(
          indirectObject,
          xrefStream,
          prevFreeEntry,
          null
          );
      }
      prevFreeEntry.setOffset(0); // Links back to the first free object. NOTE: The first entry in the table (object number 0) is always free.

      // 2.2. XRef stream.
      updateTrailer(xrefStream.getHeader(), stream);
      addXRefEntry(
        /*
          NOTE: This xref stream indirect object is purposely temporary (i.e. not registered into the
          file's indirect objects collection).
        */
        new PdfIndirectObject(
          file,
          xrefStream,
          xrefStreamEntry = new XRefEntry(indirectObjects.size(), 0, (int)stream.getLength(), XRefEntry.UsageEnum.InUse)
          ),
        xrefStream,
        null,
        null
        );
    }

    // 3. Tail.
    writeTail(xrefStreamEntry.getOffset());
  }
  // </protected>

  // <private>
  /**
    Adds an indirect object entry to the specified xref stream.

    @param indirectObject
      Indirect object.
    @param xrefStream
      XRef stream.
    @param prevFreeEntry
      Previous free xref entry.
    @param extensionObjectStreams
      Object streams used in incremental updates to extend modified ones.
    @return
      Current free xref entry.
  */
  private XRefEntry addXRefEntry(
    PdfIndirectObject indirectObject,
    XRefStream xrefStream,
    XRefEntry prevFreeEntry,
    Map<Integer,ObjectStream> extensionObjectStreams
    )
  {
    XRefEntry xrefEntry = indirectObject.getXrefEntry();
    
    // Add the entry to the xref stream!
    xrefStream.put(xrefEntry.getNumber(), xrefEntry);
    
    // Serialize the entry contents!
    switch(xrefEntry.getUsage())
    {
      case InUse:
      {
        int offset = (int)stream.getLength();
        // Add entry content!
        indirectObject.writeTo(stream, file);
        // Set entry content's offset!
        xrefEntry.setOffset(offset);
      }
        break;
      case InUseCompressed:
        /*
          NOTE: Serialization is delegated to the containing object stream.
        */
        if(extensionObjectStreams != null) // Incremental update.
        {
          int baseStreamNumber = xrefEntry.getStreamNumber();
          PdfIndirectObject baseStreamIndirectObject = file.getIndirectObjects().get(baseStreamNumber);
          if(baseStreamIndirectObject.isOriginal()) // Extension stream needed in order to preserve the original object stream.
          {
            // Get the extension object stream associated to the original object stream!
            ObjectStream extensionObjectStream = extensionObjectStreams.get(baseStreamNumber);
            if(extensionObjectStream == null)
            {
              file.register(extensionObjectStream = new ObjectStream());
              // Link the extension to the base object stream!
              extensionObjectStream.setBaseStream((ObjectStream)baseStreamIndirectObject.getDataObject());
              extensionObjectStreams.put(baseStreamNumber, extensionObjectStream);
            }
            // Insert the data object into the extension object stream!
            extensionObjectStream.put(xrefEntry.getNumber(), indirectObject.getDataObject());
            // Update the data object's xref entry!
            xrefEntry.setStreamNumber(extensionObjectStream.getReference().getObjectNumber());
            xrefEntry.setOffset(XRefEntry.UndefinedOffset); // Internal object index unknown (to set on object stream serialization -- see ObjectStream).
          }
        }
        break;
      case Free:
        if(prevFreeEntry != null)
        {prevFreeEntry.setOffset(xrefEntry.getNumber());} // Object number of the next free object.

        prevFreeEntry = xrefEntry;
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return prevFreeEntry;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
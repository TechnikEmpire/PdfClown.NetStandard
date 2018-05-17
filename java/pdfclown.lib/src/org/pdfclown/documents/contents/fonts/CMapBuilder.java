/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.fonts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;
import org.pdfclown.util.IFunction;
import org.pdfclown.util.NotImplementedException;

/**
  CMap builder [PDF:1.6:5.6.4,5.9.2;CMAP].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/08/15
*/
final class CMapBuilder
{
  // <classes>
  public enum EntryTypeEnum
  {
    BaseFont("bf"),
    CID("cid");
    
    private final String tag;
    
    private EntryTypeEnum(
      String tag
      )
    {this.tag = tag;}
  }
  
  private static class CharEntryBuilder
    extends EntryBuilder<Map.Entry<ByteArray,Integer>>
  {
    public CharEntryBuilder(
      IBuffer buffer,
      IFunction<Entry<ByteArray,Integer>,Integer> outCodeFunction,
      String outCodeFormat
      )
    {super(buffer, outCodeFunction, outCodeFormat);}

    @Override
    public IBuffer apply(
      Map.Entry<ByteArray,Integer> cidChar
      )
    {
      return buffer.append("<").append(ConvertUtils.byteArrayToHex(cidChar.getKey().data)).append("> ")
        .append(String.format(outCodeFormat, outCodeFunction.apply(cidChar))).append("\n");
    }
  }
  
  private abstract static class EntryBuilder<T>
    implements IFunction<T,IBuffer>
  {
    IBuffer buffer;
    String outCodeFormat;
    IFunction<Map.Entry<ByteArray,Integer>,Integer> outCodeFunction;
    
    protected EntryBuilder(
      IBuffer buffer,
      IFunction<Entry<ByteArray,Integer>,Integer> outCodeFunction,
      String outCodeFormat
      )
    {
      this.buffer = buffer;
      this.outCodeFunction = outCodeFunction;
      this.outCodeFormat = outCodeFormat;
    }
  }
  
  private static class RangeEntryBuilder
    extends EntryBuilder<Map.Entry<ByteArray,Integer>[]>
  {
    public RangeEntryBuilder(
      IBuffer buffer,
      IFunction<Entry<ByteArray,Integer>,Integer> outCodeFunction,
      String outCodeFormat
      )
    {super(buffer, outCodeFunction, outCodeFormat);}

    @Override
    public IBuffer apply(
      Map.Entry<ByteArray,Integer>[] cidRange
      )
    {
      return buffer.append("<").append(ConvertUtils.byteArrayToHex(cidRange[0].getKey().data)).append("> <")
        .append(ConvertUtils.byteArrayToHex(cidRange[1].getKey().data)).append("> ")
        .append(String.format(outCodeFormat, outCodeFunction.apply(cidRange[0]))).append("\n");
    }
  }
  // </classes>
  
  // <static>
  // <fields>
  private static final int SubSectionMaxCount = 100;
  // </fields>
  
  // <interface>
  // <public>
  /**
    Builds a CMap according to the specified arguments.
    
    @param entryType
    @param cmapName
      CMap name ({@code null} in case no custom name is needed).
    @param codes
    @param outCodeFunction
    @return
      Buffer containing the serialized CMap.
  */
  @SuppressWarnings("unchecked")
  public static IBuffer build(
    EntryTypeEnum entryType,
    String cmapName,
    SortedMap<ByteArray,Integer> codes,
    IFunction<Map.Entry<ByteArray,Integer>,Integer> outCodeFunction
    )
  {
    IBuffer buffer = new Buffer();

    // Header.
    String outCodeFormat;
    switch(entryType)
    {
      case BaseFont:
      {
        if(cmapName == null)
        {cmapName = "Adobe-Identity-UCS";}
        buffer.append(
          "/CIDInit /ProcSet findresource begin\n"
            + "12 dict begin\n"
            + "begincmap\n"
            + "/CIDSystemInfo\n"
            + "<< /Registry (Adobe)\n"
            + "/Ordering (UCS)\n"
            + "/Supplement 0\n"
            + ">> def\n"
            + "/CMapName /").append(cmapName).append(" def\n"
            + "/CMapVersion 10.001 def\n"
            + "/CMapType 2 def\n"
            + "1 begincodespacerange\n"
            + "<0000> <FFFF>\n"
            + "endcodespacerange\n"
          );
        outCodeFormat = "<%04X>";
        break;
      }
      case CID:
      {
        if(cmapName == null)
        {cmapName = "Custom";}
        buffer.append(
          "%!PS-Adobe-3.0 Resource-CMap\n"
            + "%%DocumentNeededResources: ProcSet (CIDInit)\n"
            + "%%IncludeResource: ProcSet (CIDInit)\n"
            + "%%BeginResource: CMap (").append(cmapName).append(")\n"
            + "%%Title: (").append(cmapName).append(" Adobe Identity 0)\n"
            + "%%Version: 1\n"
            + "%%EndComments\n"
            + "/CIDInit /ProcSet findresource begin\n"
            + "12 dict begin\n"
            + "begincmap\n"
            + "/CIDSystemInfo 3 dict dup begin\n"
            + "/Registry (Adobe) def\n"
            + "/Ordering (Identity) def\n"
            + "/Supplement 0 def\n"
            + "end def\n"
            + "/CMapVersion 1 def\n"
            + "/CMapType 1 def\n"
            + "/CMapName /").append(cmapName).append(" def\n"
            + "/WMode 0 def\n"
            + "1 begincodespacerange\n"
            + "<0000> <FFFF>\n"
            + "endcodespacerange\n"
          );
        outCodeFormat = "%s";
        break;
      }
      default:
        throw new NotImplementedException();
    }
    
    // Entries.
    {
      List<Map.Entry<ByteArray,Integer>> cidChars = new ArrayList<Map.Entry<ByteArray,Integer>>();
      List<Map.Entry<ByteArray,Integer>[]> cidRanges = new ArrayList<Map.Entry<ByteArray,Integer>[]>();
      {
        Map.Entry<ByteArray,Integer> lastCodeEntry = null;
        Map.Entry<ByteArray,Integer>[] lastCodeRange = null;
        for(Map.Entry<ByteArray,Integer> codeEntry : codes.entrySet())
        {
          if(lastCodeEntry != null)
          {
            int codeLength = codeEntry.getKey().data.length;
            if(codeLength == lastCodeEntry.getKey().data.length
              && codeEntry.getKey().data[codeLength - 1] - lastCodeEntry.getKey().data[codeLength - 1] == 1
              && outCodeFunction.apply(codeEntry) - outCodeFunction.apply(lastCodeEntry) == 1) // Contiguous codes.
            {
              if(lastCodeRange == null)
              {lastCodeRange = new Map.Entry[]{lastCodeEntry, null};}
            }
            else // Separated codes.
            {
              addEntry(cidRanges, cidChars, lastCodeEntry, lastCodeRange);
              lastCodeRange = null;
            }
          }
          lastCodeEntry = codeEntry;
        }
        addEntry(cidRanges, cidChars, lastCodeEntry, lastCodeRange);
      }
      // Ranges section.
      buildEntriesSection(buffer, entryType, cidRanges, new RangeEntryBuilder(buffer, outCodeFunction, outCodeFormat), "range");
      // Chars section.
      buildEntriesSection(buffer, entryType, cidChars, new CharEntryBuilder(buffer, outCodeFunction, outCodeFormat), "char");
    }
    
    // Trailer.
    switch(entryType)
    {
      case BaseFont:
        buffer.append(
          "endcmap\n"
            + "CMapName currentdict /CMap defineresource pop\n"
            + "end\n"
            + "end\n"
          );
        break;
      case CID:
        buffer.append(
          "endcmap\n"
            + "CMapName currentdict /CMap defineresource pop\n"
            + "end\n"
            + "end\n"
            + "%%EndResource\n"
            + "%%EOF"
          );
        break;
      default:
        throw new NotImplementedException();
    }
    
    return buffer;
  }
  // </public>
  
  // <private>
  private static void addEntry(
    List<Map.Entry<ByteArray,Integer>[]> cidRanges,
    List<Map.Entry<ByteArray,Integer>> cidChars,
    Map.Entry<ByteArray,Integer> lastEntry,
    Map.Entry<ByteArray,Integer>[] lastRange
    )
  {
    if(lastRange != null) // Range.
    {
      lastRange[1] = lastEntry;
      cidRanges.add(lastRange);
    }
    else // Single character.
    {cidChars.add(lastEntry);}
  }
  
  private static <T> void buildEntriesSection(
    IBuffer buffer,
    EntryTypeEnum entryType,
    List<T> items,
    EntryBuilder<T> entryBuilder,
    String operatorSuffix
    )
  {
    if(items.isEmpty())
      return;
    
    for(int index = 0, count = items.size(); index < count; index++)
    {
      if(index % SubSectionMaxCount == 0)
      {
        if(index > 0)
        {buffer.append("end").append(entryType.tag).append(operatorSuffix).append("\n");}
        buffer.append(Integer.toString(Math.min(count - index, SubSectionMaxCount))).append(" ").append("begin").append(entryType.tag).append(operatorSuffix).append("\n");
      }
      entryBuilder.apply(items.get(index));
    }
    buffer.append("end").append(entryType.tag).append(operatorSuffix).append("\n");
  }
  // </private>
  // </interface>
  // </static>
}
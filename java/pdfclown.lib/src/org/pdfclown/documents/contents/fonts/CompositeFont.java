/*
  Copyright 2009-2015 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.fonts.CMapBuilder.EntryTypeEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.objects.Rectangle;
import org.pdfclown.util.BiMap;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;
import org.pdfclown.util.IFunction;

/**
  Composite font, also called Type 0 font [PDF:1.6:5.6].
  <p>Do not confuse it with {@link Type0Font Type 0 CIDFont}: the latter is
  a composite font descendant describing glyphs based on Adobe Type 1 font format.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.2.0, 05/25/15
*/
@PDF(VersionEnum.PDF12)
public abstract class CompositeFont
  extends Font
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static CompositeFont get(
    Document context,
    IInputStream fontData
    )
  {
    OpenFontParser parser = new OpenFontParser(fontData);
    switch(parser.outlineFormat)
    {
      case PostScript:
        return new Type0Font(context,parser);
      case TrueType:
        return new Type2Font(context,parser);
    }
    throw new UnsupportedOperationException("Unknown composite font format.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  // </fields>

  // <constructors>
  protected CompositeFont(
    Document context,
    OpenFontParser parser
    )
  {
    super(context);

    load(parser);
  }

  protected CompositeFont(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  // </public>

  // <protected>
  /**
    Gets the CIDFont dictionary that is the descendant of this composite font.
  */
  protected PdfDictionary getCIDFontDictionary(
    )
  {return (PdfDictionary)((PdfArray)getBaseDataObject().resolve(PdfName.DescendantFonts)).resolve(0);}

  @Override
  protected PdfDataObject getDescriptorValue(
    PdfName key
    )
  {return ((PdfDictionary)getCIDFontDictionary().resolve(PdfName.FontDescriptor)).resolve(key);}

  protected void loadEncoding(
    )
  {
    PdfDataObject encodingObject = getBaseDataObject().resolve(PdfName.Encoding);

    // CMap [PDF:1.6:5.6.4].
    Map<ByteArray,Integer> cmap = CMap.get(encodingObject);

    // 1. Unicode.
    if(codes == null)
    {
      codes = new BiMap<ByteArray,Integer>();
      if(encodingObject instanceof PdfName
        && !(encodingObject.equals(PdfName.IdentityH)
          || encodingObject.equals(PdfName.IdentityV)))
      {
        /*
          NOTE: According to [PDF:1.6:5.9.1], the fallback method to retrieve
          the character-code-to-Unicode mapping implies getting the UCS2 CMap
          (Unicode value to CID) corresponding to the font's one (character code to CID);
          CIDs are the bridge from character codes to Unicode values.
        */
        BiMap<ByteArray,Integer> ucs2CMap;
        {
          PdfDictionary cidSystemInfo = (PdfDictionary)getCIDFontDictionary().resolve(PdfName.CIDSystemInfo);
          String registry = ((PdfTextString)cidSystemInfo.get(PdfName.Registry)).getValue();
          String ordering = ((PdfTextString)cidSystemInfo.get(PdfName.Ordering)).getValue();
          String ucs2CMapName = registry + "-" + ordering + "-" + "UCS2";
          ucs2CMap = new BiMap<ByteArray,Integer>(CMap.get(ucs2CMapName));
        }
        if(!ucs2CMap.isEmpty())
        {
          for(Map.Entry<ByteArray,Integer> cmapEntry : cmap.entrySet())
          {codes.put(cmapEntry.getKey(),ConvertUtils.byteArrayToInt(ucs2CMap.getKey(cmapEntry.getValue()).data));}
        }
      }
      if(codes.isEmpty())
      {
        /*
          NOTE: In case no clue is available to determine the Unicode resolution map,
          the font is considered symbolic and an identity map is synthesized instead.
         */
        symbolic = true;
        for(Map.Entry<ByteArray,Integer> cmapEntry : cmap.entrySet())
        {codes.put(cmapEntry.getKey(),ConvertUtils.byteArrayToInt(cmapEntry.getKey().data));}
      }
    }

    // 2. Glyph indexes.
    /*
    TODO: gids map for glyph indexes as glyphIndexes is used to map cids!!!
    */
    // Character-code-to-CID mapping [PDF:1.6:5.6.4,5].
    glyphIndexes = new Hashtable<Integer,Integer>();
    for(Map.Entry<ByteArray,Integer> cmapEntry : cmap.entrySet())
    {
      if(!codes.containsKey(cmapEntry.getKey()))
        continue;

      glyphIndexes.put(codes.get(cmapEntry.getKey()),cmapEntry.getValue());
    }
  }

  @Override
  protected void onLoad(
    )
  {
    loadEncoding();

    // Glyph widths.
    {
      glyphWidths = new Hashtable<Integer,Integer>();
      PdfArray glyphWidthObjects = (PdfArray)getCIDFontDictionary().resolve(PdfName.W);
      if(glyphWidthObjects != null)
      {
        for(Iterator<PdfDirectObject> iterator = glyphWidthObjects.iterator(); iterator.hasNext();)
        {
          //TODO: this algorithm is valid only in case cid-to-gid mapping is identity (see cidtogid map)!!
          /*
            NOTE: Font widths are grouped in one of the following formats [PDF:1.6:5.6.3]:
              1. startCID [glyphWidth1 glyphWidth2 ... glyphWidthn]
              2. startCID endCID glyphWidth
          */
          int startCID = ((PdfInteger)iterator.next()).getRawValue();
          PdfDirectObject glyphWidthObject2 = iterator.next();
          if(glyphWidthObject2 instanceof PdfArray) // Format 1: startCID [glyphWidth1 glyphWidth2 ... glyphWidthn].
          {
            int cID = startCID;
            for(PdfDirectObject glyphWidthObject : (PdfArray)glyphWidthObject2)
            {glyphWidths.put(cID++,((PdfNumber<?>)glyphWidthObject).getIntValue());}
          }
          else // Format 2: startCID endCID glyphWidth.
          {
            int endCID = ((PdfInteger)glyphWidthObject2).getRawValue();
            int glyphWidth = ((PdfNumber<?>)iterator.next()).getIntValue();
            for(int cID = startCID; cID <= endCID; cID++)
            {glyphWidths.put(cID,glyphWidth);}
          }
        }
      }
    }
    // Default glyph width.
    {
      PdfInteger defaultWidthObject = (PdfInteger)getBaseDataObject().get(PdfName.DW);
      if(defaultWidthObject != null)
      {setDefaultWidth(defaultWidthObject.getIntValue());}
    }
  }
  // </protected>

  // <private>
  /**
    Loads the font data.
  */
  private void load(
    OpenFontParser parser
    )
  {
    glyphIndexes = parser.glyphIndexes;
    glyphKernings = parser.glyphKernings;
    glyphWidths = parser.glyphWidths;

    PdfDictionary baseDataObject = getBaseDataObject();

    // BaseFont.
    baseDataObject.put(PdfName.BaseFont,new PdfName(parser.fontName));

    // Subtype.
    baseDataObject.put(PdfName.Subtype, PdfName.Type0);

    // Encoding.
    baseDataObject.put(PdfName.Encoding, PdfName.IdentityH); //TODO: this is a simplification (to refine later).

    // Descendant font.
    PdfDictionary cidFontDictionary = new PdfDictionary(
      new PdfName[]{PdfName.Type},
      new PdfDirectObject[]{PdfName.Font}
      ); // CIDFont dictionary [PDF:1.6:5.6.3].
    {
      // Subtype.
      // FIXME: verify proper Type 0 detection.
      cidFontDictionary.put(PdfName.Subtype, PdfName.CIDFontType2);

      // BaseFont.
      cidFontDictionary.put(PdfName.BaseFont, new PdfName(parser.fontName));

      // CIDSystemInfo.
      cidFontDictionary.put(
        PdfName.CIDSystemInfo,
        new PdfDictionary(
          new PdfName[]
          {
            PdfName.Registry,
            PdfName.Ordering,
            PdfName.Supplement
          },
          new PdfDirectObject[]
          {
            PdfTextString.get("Adobe"),
            PdfTextString.get("Identity"),
            PdfInteger.get(0)
          }
          )
        ); // Generic predefined CMap (Identity-H/V (Adobe-Identity-0)) [PDF:1.6:5.6.4].

      // FontDescriptor.
      cidFontDictionary.put(
        PdfName.FontDescriptor,
        load_createFontDescriptor(parser)
        );

      // Encoding.
      load_createEncoding(baseDataObject,cidFontDictionary);
    }
    baseDataObject.put(
      PdfName.DescendantFonts,
      new PdfArray(new PdfDirectObject[]{getFile().register(cidFontDictionary)})
      );

    load();
  }

  /**
    Creates the character code mapping for composite fonts.
  */
  private void load_createEncoding(
    PdfDictionary font,
    PdfDictionary cidFont
    )
  {
    /*
      NOTE: Composite fonts map text shown by content stream strings through a 2-level encoding 
      scheme:
        character code -> CID (character index) -> GID (glyph index)
      This works for rendering purposes, but if we want our text data to be intrinsically meaningful,
      we need a further mapping towards some standard character identification scheme (Unicode):
        Unicode <- character code -> CID -> GID
      Such mapping may be provided by a known CID collection or (in case of custom encodings like
      Identity-H) by an explicit ToUnicode CMap.
      CID -> GID mapping is typically identity, that is CIDS correspond to GIDS, so we don't bother
      about that. Our base encoding is Identity-H, that is character codes correspond to CIDs; 
      however, sometimes a font maps multiple Unicode codepoints to the same GID (for example, the 
      hyphen glyph may be associated to the hyphen (\u2010) and minus (\u002D) symbols), breaking 
      the possibility to recover their original Unicode values once represented as character codes 
      in content stream strings. In this case, we are forced to remap the exceeding codes and 
      generate an explicit CMap (TODO: I tried to emit a differential CMap using the usecmap 
      operator in order to import Identity-H as base encoding, but it failed in several engines
      (including Acrobat, Ghostscript, Poppler, whilst it surprisingly worked with pdf.js), so we
      have temporarily to stick with full CMaps).
    */
    
    // Encoding [PDF:1.7:5.6.1,5.6.4].
    PdfDirectObject encodingObject = PdfName.IdentityH;
    SortedMap<ByteArray,Integer> sortedCodes;
    {
      codes = new BiMap<ByteArray,Integer>(glyphIndexes.size());
      int lastRemappedCharCodeValue = 0;
      for(Iterator<Map.Entry<Integer,Integer>> glyphIndexIterator = glyphIndexes.entrySet().iterator(); glyphIndexIterator.hasNext();)
      {
        Map.Entry<Integer,Integer> glyphIndexEntry = glyphIndexIterator.next();
        int glyphIndex = glyphIndexEntry.getValue();
        ByteArray charCode = new ByteArray(new byte[]
          {
            (byte)((glyphIndex >> 8) & 0xFF),
            (byte)(glyphIndex & 0xFF)
          });

        // Checking for multiple Unicode codepoints which map to the same glyph index...
        /*
          NOTE: In case the same glyph index maps to multiple Unicode codepoints, we are forced to 
          alter the identity encoding creating distinct cmap entries for the exceeding codepoints.
        */
        if(codes.containsKey(charCode))
        {
          if(glyphIndex == 0) // .notdef glyph already mapped.
          {
            glyphIndexIterator.remove();
            continue;
          }
          
          // Assigning the new character code...
          /*
            NOTE: As our base encoding is identity, we have to look for a value that doesn't
            collide with existing glyph indices.
          */
          while(glyphIndexes.containsValue(++lastRemappedCharCodeValue));
          charCode.data[0] = (byte)((lastRemappedCharCodeValue >> 8) & 0xFF);
          charCode.data[1] = (byte)(lastRemappedCharCodeValue & 0xFF);
        }
        else if(glyphIndex == 0) // .notdef glyph.
        {setDefaultCode(glyphIndexEntry.getKey());}
        
        codes.put(charCode, glyphIndexEntry.getKey());
      }
      sortedCodes = new TreeMap<ByteArray,Integer>(codes);
      if(lastRemappedCharCodeValue > 0) // Custom encoding.
      {
        String cmapName = "Custom";
        IBuffer cmapBuffer = CMapBuilder.build(
          EntryTypeEnum.CID,
          cmapName,
          sortedCodes,
          new IFunction<Map.Entry<ByteArray,Integer>,Integer>()
          {
            public Integer apply(Map.Entry<ByteArray,Integer> codeEntry)
            {return glyphIndexes.get(codeEntry.getValue());}
          }
          );
        encodingObject = getFile().register(
          new PdfStream(
            new PdfDictionary(
              new PdfName[]
              {
                PdfName.Type,
                PdfName.CMapName,
                PdfName.CIDSystemInfo
              },
              new PdfDirectObject[]
              {
                PdfName.CMap,
                new PdfName(cmapName),
                new PdfDictionary(
                  new PdfName[]
                  {
                    PdfName.Registry,
                    PdfName.Ordering,
                    PdfName.Supplement
                  },
                  new PdfDirectObject[]
                  {
                    PdfTextString.get("Adobe"),
                    PdfTextString.get("Identity"),
                    PdfInteger.get(0)
                  }
                  )
              }
              ),
            cmapBuffer
            )
          );
      }
    }
    font.put(PdfName.Encoding, encodingObject); // Character-code-to-CID mapping.
    cidFont.put(PdfName.CIDToGIDMap, PdfName.Identity); // CID-to-glyph-index mapping.

    // ToUnicode [PDF:1.6:5.9.2].
    PdfDirectObject toUnicodeObject = null;
    {
      IBuffer toUnicodeBuffer = CMapBuilder.build(
        EntryTypeEnum.BaseFont,
        null,
        sortedCodes,
        new IFunction<Map.Entry<ByteArray,Integer>,Integer>()
        {
          public Integer apply(Map.Entry<ByteArray,Integer> codeEntry)
          {return codeEntry.getValue();}
        }
        );
      toUnicodeObject = getFile().register(new PdfStream(toUnicodeBuffer));
    }
    font.put(PdfName.ToUnicode, toUnicodeObject); // Character-code-to-Unicode mapping.
  
    // Glyph widths.
    PdfArray widthsObject = new PdfArray();
    {
      int lastGlyphIndex = -10;
      PdfArray lastGlyphWidthRangeObject = null;
      for(Integer glyphIndex : new TreeSet<Integer>(glyphIndexes.values()))
      {
        Integer width = glyphWidths.get(glyphIndex);
        if(width == null)
        {width = 0;}
        if(glyphIndex - lastGlyphIndex != 1)
        {
          widthsObject.add(PdfInteger.get(glyphIndex));
          widthsObject.add(lastGlyphWidthRangeObject = new PdfArray());
        }
        lastGlyphWidthRangeObject.add(PdfInteger.get(width));
        lastGlyphIndex = glyphIndex;
      }
    }
    cidFont.put(PdfName.W, widthsObject); // Glyph widths.
  }

  /**
    Creates the font descriptor.
  */
  private PdfReference load_createFontDescriptor(
    OpenFontParser parser
    )
  {
    PdfDictionary fontDescriptor = new PdfDictionary();
    {
      OpenFontParser.FontMetrics metrics = parser.metrics;

      // Type.
      fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);

      // FontName.
      fontDescriptor.put(PdfName.FontName, getBaseDataObject().get(PdfName.BaseFont));

      // Flags [PDF:1.6:5.7.1].
      int flags = 0;
      if(metrics.isFixedPitch)
      {flags |= FlagsEnum.FixedPitch.getCode();}
      if(metrics.isCustomEncoding)
      {flags |= FlagsEnum.Symbolic.getCode();}
      else
      {flags |= FlagsEnum.Nonsymbolic.getCode();}
      fontDescriptor.put(PdfName.Flags, PdfInteger.get(flags));

      // FontBBox.
      fontDescriptor.put(
        PdfName.FontBBox,
        new Rectangle(
          new Point2D.Double(metrics.xMin * metrics.unitNorm, metrics.yMin * metrics.unitNorm),
          new Point2D.Double(metrics.xMax * metrics.unitNorm, metrics.yMax * metrics.unitNorm)
          ).getBaseDataObject()
        );

      // ItalicAngle.
      fontDescriptor.put(PdfName.ItalicAngle, PdfReal.get(metrics.italicAngle));

      // Ascent.
      fontDescriptor.put(
        PdfName.Ascent,
        PdfReal.get(
          metrics.sTypoAscender == 0
            ? metrics.ascender * metrics.unitNorm
            : (metrics.sTypoLineGap == 0 ? metrics.sCapHeight : metrics.sTypoAscender) * metrics.unitNorm
          )
        );

      // Descent.
      fontDescriptor.put(
        PdfName.Descent,
        PdfReal.get(
          metrics.sTypoDescender == 0
            ? metrics.descender * metrics.unitNorm
            : metrics.sTypoDescender * metrics.unitNorm
          )
        );

      // CapHeight.
      fontDescriptor.put(
        PdfName.CapHeight,
        PdfReal.get(metrics.sCapHeight * metrics.unitNorm)
        );

      // StemV.
      /*
        NOTE: '100' is just a rule-of-thumb value, 'cause I've still to solve the
        'cvt' table puzzle (such a harsh headache!) for TrueType fonts...
        TODO:IMPL TrueType and CFF stemv real value to extract!!!
      */
      fontDescriptor.put(PdfName.StemV, PdfInteger.get(100));

      // FontFile.
      fontDescriptor.put(
        PdfName.FontFile2,
        getFile().register(
          new PdfStream(new Buffer(parser.fontData.toByteArray()))
          )
        );
    }
    return getFile().register(fontDescriptor);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
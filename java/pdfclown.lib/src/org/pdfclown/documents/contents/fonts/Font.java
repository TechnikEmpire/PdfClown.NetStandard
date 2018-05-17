/*
  Copyright 2006-2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)
    * Manuel Guilbault (code contributor [FIX:27], manuel.guilbault at gmail.com)

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.FileInputStream;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.tokens.DecodeException;
import org.pdfclown.tokens.EncodeException;
import org.pdfclown.util.BiMap;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.NotImplementedException;

/**
  Abstract font [PDF:1.6:5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 05/25/15
*/
@PDF(VersionEnum.PDF10)
public abstract class Font
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Font descriptor flags [PDF:1.6:5.7.1].
  */
  public enum FlagsEnum
  {
    /**
      All glyphs have the same width.
    */
    FixedPitch(0x1),
    /**
      Glyphs have serifs.
    */
    Serif(0x2),
    /**
      Font contains glyphs outside the Adobe standard Latin character set.
    */
    Symbolic(0x4),
    /**
      Glyphs resemble cursive handwriting.
    */
    Script(0x8),
    /**
      Font uses the Adobe standard Latin character set.
    */
    Nonsymbolic(0x20),
    /**
      Glyphs have dominant vertical strokes that are slanted.
    */
    Italic(0x40),
    /**
      Font contains no lowercase letters.
    */
    AllCap(0x10000),
    /**
      Font contains both uppercase and lowercase letters.
    */
    SmallCap(0x20000),
    /**
      Thicken bold glyphs at small text sizes.
    */
    ForceBold(0x40000);

    /**
      Converts an enumeration set into its corresponding bit mask representation.
    */
    public static int toInt(
      EnumSet<FlagsEnum> flags
      )
    {
      int flagsMask = 0;
      for(FlagsEnum flag : flags)
      {flagsMask |= flag.getCode();}

      return flagsMask;
    }

    /**
      Converts a bit mask into its corresponding enumeration representation.
    */
    public static EnumSet<FlagsEnum> toEnumSet(
      int flagsMask
      )
    {
      EnumSet<FlagsEnum> flags = EnumSet.noneOf(FlagsEnum.class);
      for(FlagsEnum flag : FlagsEnum.values())
      {
        if((flagsMask & flag.getCode()) > 0)
        {flags.add(flag);}
      }

      return flags;
    }

    private final int code;

    private FlagsEnum(
      int code
      )
    {this.code = code;}

    public int getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final int UndefinedDefaultCode = Integer.MIN_VALUE;
  private static final int UndefinedWidth = Integer.MIN_VALUE;
  // </fields>

  // <interface>
  // <public>
  /**
   * Creates the representation of a font.
   */
  public static Font get(
    Document context,
    String path
    )
  {
    try
    {
      return get(
        context,
        new FileInputStream(
          new java.io.RandomAccessFile(path,"r")
          )
        );
    }
    catch(FileNotFoundException e)
    {throw new RuntimeException(e);}
  }

  /**
   * Creates the representation of a font.
   */
  public static Font get(
    Document context,
    java.io.File file
    )
  {return get(context,file.getPath());}

  /**
   * Creates the representation of a font.
   */
  public static Font get(
    Document context,
    IInputStream fontData
    )
  {
    if(OpenFontParser.isOpenFont(fontData))
      return CompositeFont.get(context,fontData);
    else
      throw new NotImplementedException();
   }

  /**
    Gets the scaling factor to be applied to unscaled metrics to get actual measures.
  */
  public static final double getScalingFactor(
    double size
    )
  {return 0.001 * size;}

  /**
    Wraps a font reference into a font object.

    @param baseObject Font base object.
    @return Font object associated to the reference.
  */
  public static final Font wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfReference reference = (PdfReference)baseObject;
    {
      // Has the font been already instantiated?
      /*
        NOTE: Font structures are reified as complex objects, both IO- and CPU-intensive to load.
        So, it's convenient to retrieve them from a common cache whenever possible.
      */
      Hashtable<PdfReference,Object> cache = reference.getIndirectObject().getFile().getDocument().cache;
      if(cache.containsKey(reference))
        return (Font)cache.get(reference);
    }

    PdfDictionary fontDictionary = (PdfDictionary)reference.getDataObject();
    PdfName fontType = (PdfName)fontDictionary.get(PdfName.Subtype);
    if(fontType == null)
      throw new RuntimeException("Font type undefined (reference: " + reference + ")");

    if(fontType.equals(PdfName.Type1)) // Type 1.
    {
      if(!fontDictionary.containsKey(PdfName.FontDescriptor)) // Standard Type 1.
        return new StandardType1Font(reference);
      else // Custom Type 1.
      {
        PdfDictionary fontDescriptor = (PdfDictionary)fontDictionary.resolve(PdfName.FontDescriptor);
        if(fontDescriptor.containsKey(PdfName.FontFile3)
            && ((PdfName)((PdfStream)fontDescriptor.resolve(PdfName.FontFile3)).getHeader().resolve(PdfName.Subtype)).equals(PdfName.OpenType)) // OpenFont/CFF.
          throw new NotImplementedException();
        else // Non-OpenFont Type 1.
          return new Type1Font(reference);
      }
    }
    else if(fontType.equals(PdfName.TrueType)) // TrueType.
      return new TrueTypeFont(reference);
    else if(fontType.equals(PdfName.Type0)) // OpenFont.
    {
      PdfDictionary cidFontDictionary = (PdfDictionary)((PdfArray)fontDictionary.resolve(PdfName.DescendantFonts)).resolve(0);
      PdfName cidFontType = (PdfName)cidFontDictionary.get(PdfName.Subtype);
      if(cidFontType.equals(PdfName.CIDFontType0)) // OpenFont/CFF.
        return new Type0Font(reference);
      else if(cidFontType.equals(PdfName.CIDFontType2)) // OpenFont/TrueType.
        return new Type2Font(reference);
      else
        throw new NotImplementedException("Type 0 subtype " + cidFontType + " not supported yet.");
    }
    else if(fontType.equals(PdfName.Type3)) // Type 3.
      return new Type3Font(reference);
    else if(fontType.equals(PdfName.MMType1)) // MMType1.
      return new MMType1Font(reference);
    else // Unknown.
      throw new UnsupportedOperationException("Unknown font type: " + fontType + " (reference: " + reference + ")");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  /*
    NOTE: In order to avoid nomenclature ambiguities, these terms are used consistently within the
    code:
    * character code: internal codepoint corresponding to a character expressed inside a string
      object of a content stream;
    * unicode: external codepoint corresponding to a character expressed according to the Unicode
      standard encoding;
    * glyph index: internal identifier of the graphical representation of a character.
  */
  /**
    Unicodes by character code.
    <p>When this map is populated, {@link #symbolic} variable shall accordingly be set.</p>
  */
  protected BiMap<ByteArray,Integer> codes;
  /**
    Glyph indexes by unicode.
  */
  protected Map<Integer,Integer> glyphIndexes;
  /**
    Glyph kernings by (left-right) glyph index pairs.
  */
  protected Map<Integer,Integer> glyphKernings;
  /**
    Glyph widths by glyph index.
  */
  protected Map<Integer,Integer> glyphWidths;
  /**
    Whether the font encoding is custom (that is non-Unicode).
  */
  protected boolean symbolic = true;
  /**
    Used unicodes.
  */
  protected Set<Integer> usedCodes;

  /**
    Average glyph width.
  */
  private int averageWidth = UndefinedWidth;
  /**
    Maximum character code byte size.
  */
  private int charCodeMaxLength = 0;
  /**
    Default Unicode for missing characters.
  */
  private int defaultCode = UndefinedDefaultCode;
  /**
    Default glyph width.
  */
  private int defaultWidth = UndefinedWidth;
  // </fields>

  // <constructors>
  /**
    Creates a new font structure within the given document context.
  */
  protected Font(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]{PdfName.Type},
        new PdfDirectObject[]{PdfName.Font}
        )
      );
    initialize();
  }

  /**
    Loads an existing font structure.
  */
  protected Font(
    PdfDirectObject baseObject
    )
  {
    super(baseObject);

    initialize();
    load();
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the text from the given internal representation.

    @param code Internal representation to decode.
    @since 0.0.6
  */
  public final String decode(
    byte[] code
    ) throws DecodeException
  {
    StringBuilder textBuilder = new StringBuilder();
    {
      byte[][] codeBuffers = new byte[charCodeMaxLength+1][];
      for(
        int codeBufferIndex = 0;
        codeBufferIndex <= charCodeMaxLength;
        codeBufferIndex++
        )
      {codeBuffers[codeBufferIndex] = new byte[codeBufferIndex];}
      int index = 0;
      int codeLength = code.length;
      int codeBufferSize = 1;
      while(index < codeLength)
      {
        byte[] codeBuffer = codeBuffers[codeBufferSize];
        System.arraycopy(code, index, codeBuffer, 0, codeBufferSize);
        Integer textChar = codes.get(new ByteArray(codeBuffer));
        if(textChar == null)
        {
          if(codeBufferSize < charCodeMaxLength
            && codeBufferSize < codeLength - index)
          {
            codeBufferSize++;
            continue;
          }
          else // Missing character.
          {
            switch(getDocument().getConfiguration().getEncodingFallback())
            {
              case Exclusion:
                break;
              case Substitution:
                textChar = defaultCode;
                break;
              case Exception:
                throw new DecodeException(code, index);
              default:
                throw new NotImplementedException();
            }
          }
        }
        if(textChar != null)
        {textBuilder.append((char)(int)textChar);}
        index += codeBufferSize;
        codeBufferSize = 1;
      }
    }
    return textBuilder.toString();
  }

  /**
    Gets the internal representation of the given text.

    @param text Text to encode.
    @since 0.0.6
  */
  public final byte[] encode(
    String text
    ) throws EncodeException
  {
    ByteArrayOutputStream encodedStream = new ByteArrayOutputStream();
    try
    {
      for(int index = 0, length = text.length(); index < length; index++)
      {
        int textCode = text.charAt(index);
        if(textCode < 32) // NOTE: Control characters are ignored [FIX:7].
          continue;
        
        ByteArray code = codes.getKey(textCode);
        if(code == null) // Missing glyph.
        {
          switch(getDocument().getConfiguration().getEncodingFallback())
          {
            case Exclusion:
              continue;
            case Substitution:
              code = codes.getKey(defaultCode);
              break;
            case Exception:
              throw new EncodeException(text, index);
            default:
              throw new NotImplementedException();
          }
        }
        
        byte[] charCode = code.data;
        encodedStream.write(charCode);
        usedCodes.add(textCode);
      }
      encodedStream.close();
    }
    catch(IOException e)
    {throw new RuntimeException(e);}

    return encodedStream.toByteArray();
  }

  @Override
  public final boolean equals(
    Object object
    )
  {
    return object != null
      && object.getClass().equals(getClass())
      && ((Font)object).getName().equals(getName());
  }

  /**
    Gets the unscaled vertical offset from the baseline to the ascender line (ascent).
    The value is a positive number.
  */
  public double getAscent(
    )
  {
    PdfNumber<?> ascentObject = (PdfNumber<?>)getDescriptorValue(PdfName.Ascent);
    return ascentObject != null ? ascentObject.getDoubleValue() : 750;
  }

  /**
    Gets the vertical offset from the baseline to the ascender line (ascent),
    scaled to the given font size.
    The value is a positive number.

    @param size Font size.
  */
  public final double getAscent(
    double size
    )
  {return getAscent() * getScalingFactor(size);}

  /**
    Gets the Unicode code-points supported by this font.
  */
  public Set<Integer> getCodePoints(
    )
  {return glyphIndexes.keySet();}
  
  /**
    Gets the Unicode codepoint used to substitute missing characters.
  */
  public int getDefaultCode(
    )
  {return defaultCode;}
  
  /**
    Gets the unscaled vertical offset from the baseline to the descender line (descent).
    The value is a negative number.
  */
  public double getDescent(
    )
  {
    /*
      NOTE: Sometimes font descriptors specify positive descent, therefore normalization is required
      [FIX:27].
    */
    PdfNumber<?> descentObject = (PdfNumber<?>)getDescriptorValue(PdfName.Descent);
    return -Math.abs(descentObject != null ? descentObject.getDoubleValue() : 250);
  }

  /**
    Gets the vertical offset from the baseline to the descender line (descent), scaled to the given font size.
    The value is a negative number.

    @param size Font size.
  */
  public final double getDescent(
    double size
    )
  {return getDescent() * getScalingFactor(size);}

  /**
    Gets the font descriptor flags.
  */
  public EnumSet<FlagsEnum> getFlags(
    )
  {
    PdfInteger flagsObject = (PdfInteger)getDescriptorValue(PdfName.Flags);
    return flagsObject != null ? FlagsEnum.toEnumSet(flagsObject.getRawValue()) : EnumSet.noneOf(FlagsEnum.class);
  }

  private double textHeight = -1; // TODO: temporary until glyph bounding boxes are implemented.
  /**
    Gets the unscaled height of the given character.

    @param textChar
      Character whose height has to be calculated.
  */
  public final double getHeight(
    char textChar
    )
  {
    /*
      TODO: Calculate actual text height through glyph bounding box.
    */
    if(textHeight == -1)
    {textHeight = getAscent() - getDescent();}
    return textHeight;
  }

  /**
    Gets the height of the given character, scaled to the given font size.

    @param textChar
      Character whose height has to be calculated.
    @param size
      Font size.
  */
  public final double getHeight(
    char textChar,
    double size
    )
  {return getHeight(textChar) * getScalingFactor(size);}

  /**
    Gets the unscaled height of the given text.

    @param text
      Text whose height has to be calculated.
  */
  public final double getHeight(
    String text
    )
  {
    double height = 0;
    for(int index = 0, length = text.length(); index < length; index++)
    {
      double charHeight = getHeight(text.charAt(index));
      if(charHeight > height)
      {height = charHeight;}
    }
    return height;
  }

  /**
    Gets the height of the given text, scaled to the given font size.

    @param text Text whose height has to be calculated.
    @param size Font size.
  */
  public final double getHeight(
    String text,
    double size
    )
  {return getHeight(text) * getScalingFactor(size);}

  /**
    Gets the width (kerning inclusive) of the given text, scaled to the given font size.

    @param text Text whose width has to be calculated.
    @param size Font size.
  */
  public final double getKernedWidth(
    String text,
    double size
    ) throws EncodeException
  {return (getWidth(text) + getKerning(text)) * getScalingFactor(size);}

  /**
    Gets the unscaled kerning width between two given characters.

    @param textChar1 Left character.
    @param textChar2 Right character,
  */
  public final int getKerning(
    char textChar1,
    char textChar2
    )
  {
    if(glyphKernings == null)
      return 0;

    Integer textChar1Index = glyphIndexes.get((int)textChar1);
    if(textChar1Index == null)
      return 0;

    Integer textChar2Index = glyphIndexes.get((int)textChar2);
    if(textChar2Index == null)
      return 0;

    Integer kerning = glyphKernings.get(
      textChar1Index << 16 // Left-hand glyph index.
        + textChar2Index // Right-hand glyph index.
      );
    return kerning != null ? kerning : 0;
  }

  /**
    Gets the unscaled kerning width inside the given text.

    @param text Text whose kerning has to be calculated.
  */
  public final int getKerning(
    String text
    )
  {
    int kerning = 0;
    for(int index = 0, length = text.length() - 1; index < length; index++)
    {
      kerning += getKerning(
        text.charAt(index),
        text.charAt(index + 1)
        );
    }
    return kerning;
  }

  /**
    Gets the kerning width inside the given text, scaled to the given font size.

    @param text Text whose kerning has to be calculated.
    @param size Font size.
  */
  public final double getKerning(
    String text,
    double size
    )
  {return getKerning(text) * getScalingFactor(size);}

  /**
    Gets the unscaled line height.
  */
  public double getLineHeight(
    )
  {return getAscent() - getDescent();}

  /**
    Gets the line height, scaled to the given font size.

    @param size Font size.
  */
  public final double getLineHeight(
    double size
    )
  {return getLineHeight() * getScalingFactor(size);}

  /**
    Gets the PostScript name of the font.
  */
  public final String getName(
    )
  {return ((PdfName)getBaseDataObject().get(PdfName.BaseFont)).toString();}

  /**
    Gets the unscaled width of the given character.

    @param textChar Character whose width has to be calculated.
  */
  public int getWidth(
    char textChar
    ) throws EncodeException
  {
    Integer glyphIndex = glyphIndexes.get((int)textChar);
    if(glyphIndex == null)
    {
      switch(getDocument().getConfiguration().getEncodingFallback())
      {
        case Exclusion:
          return 0;
        case Substitution:
          return getDefaultWidth();
        case Exception:
          throw new EncodeException(textChar);
        default:
          throw new NotImplementedException();
      }
    }

    Integer glyphWidth = glyphWidths.get(glyphIndex);
    return glyphWidth != null ? glyphWidth : getDefaultWidth();
  }

  /**
    Gets the width of the given character, scaled to the given font size.

    @param textChar Character whose height has to be calculated.
    @param size Font size.
  */
  public final double getWidth(
    char textChar,
    double size
    ) throws EncodeException
  {return getWidth(textChar) * getScalingFactor(size);}

  /**
    Gets the unscaled width (kerning exclusive) of the given text.

    @param text Text whose width has to be calculated.
  */
  public int getWidth(
    String text
    ) throws EncodeException
  {
    int width = 0;
    for(int index = 0, length = text.length(); index < length; index++)
    {width += getWidth(text.charAt(index));}
    return width;
  }

  /**
    Gets the width (kerning exclusive) of the given text, scaled to the given font size.

    @param text Text whose width has to be calculated.
    @param size Font size.
  */
  public final double getWidth(
    String text,
    double size
    ) throws EncodeException
  {return getWidth(text) * getScalingFactor(size);}

  @Override
  public int hashCode(
    )
  {return getName().hashCode();}

  /**
    Gets whether the font encoding is custom (that is non-Unicode).
  */
  public boolean isSymbolic(
    )
  {return symbolic;}
  
  /**
    @see #getDefaultCode()
    @throws EncodeException
      If the value is not mapped in the font's encoding.
  */
  public void setDefaultCode(
    int value
    ) throws EncodeException
  {
    if(!glyphIndexes.containsKey(value))
      throw new EncodeException((char)value);
    
    defaultCode = value;
  }
  // </public>

  // <protected>
  /**
    Gets the average glyph width.
  */
  protected int getAverageWidth(
    )
  {
    if(averageWidth == UndefinedWidth)
    {
      if(glyphWidths.isEmpty())
      {averageWidth = 1000;}
      else
      {
        averageWidth = 0;
        for(Integer glyphWidth : glyphWidths.values())
        {averageWidth += glyphWidth;}
        averageWidth /= glyphWidths.size();
      }
    }
    return averageWidth;
  }
  
  /**
    Gets the default glyph width.
  */
  protected int getDefaultWidth(
    )
  {
    if(defaultWidth == UndefinedWidth)
    {defaultWidth = getAverageWidth();}
    return defaultWidth;
  }
  
  /**
    Gets the specified font descriptor entry value.
  */
  protected abstract PdfDataObject getDescriptorValue(
    PdfName key
    );

  /**
    Loads font information from existing PDF font structure.
  */
  protected void load(
    )
  {
    if(getBaseDataObject().containsKey(PdfName.ToUnicode)) // To-Unicode explicit mapping.
    {
      PdfStream toUnicodeStream = (PdfStream)getBaseDataObject().resolve(PdfName.ToUnicode);
      @SuppressWarnings("resource")
      CMapParser parser = new CMapParser(toUnicodeStream.getBody());
      codes = new BiMap<ByteArray,Integer>(parser.parse());
      symbolic = false;
    }

    onLoad();

    // Maximum character code length.
    for(ByteArray charCode : codes.keySet())
    {
      if(charCode.data.length > charCodeMaxLength)
      {charCodeMaxLength = charCode.data.length;}
    }
    // Missing character substitute.
    if(defaultCode == UndefinedDefaultCode)
    {
      Set<Integer> codePoints = getCodePoints();
      if(codePoints.contains((int)'?'))
      {setDefaultCode('?');}
      else if(codePoints.contains((int)' '))
      {setDefaultCode(' ');}
      else
      {setDefaultCode(codePoints.iterator().next());}
    }
  }

  /**
    Notifies font information loading from an existing PDF font structure.
  */
  protected abstract void onLoad(
    );
  
  /**
    @see #getAverageWidth()
  */
  protected void setAverageWidth(
    int value
    )
  {averageWidth = value;}
  
  /**
    @see #getDefaultWidth()
  */
  protected void setDefaultWidth(
    int value
    )
  {defaultWidth = value;}
  // </protected>

  // <private>
  private void initialize(
    )
  {
    usedCodes = new HashSet<Integer>();

    // Put the newly instantiated font into the common cache!
    /*
      NOTE: Font structures are reified as complex objects, both IO- and CPU-intensive to load.
      So, it's convenient to put them into a common cache for later reuse.
    */
    getDocument().cache.put((PdfReference)getBaseObject(),this);
  }
  // </private>
  // </interface>
  // </dynamic>
}
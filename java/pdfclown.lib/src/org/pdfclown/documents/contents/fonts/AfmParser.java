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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
  AFM file format parser [AFM:4.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2.1, 05/05/15
*/
final class AfmParser
{
  // <class>
  // <classes>
  /**
    Font header (Global font information).
  */
  static final class FontMetrics
  {
    /**
      Whether the encoding is custom (symbolic font).
    */
    public int ascender;
    public int capHeight;
    public int descender;
    public String fontName;
    public boolean isCustomEncoding;
    public boolean isFixedPitch;
    public float italicAngle;
    public int stemH;
    public int stemV;
    public int underlinePosition;
    public int underlineThickness;
    public String weight;
    public int xHeight;
    public int xMax;
    public int xMin;
    public int yMax;
    public int yMin;
  }
  // </classes>

  // <dynamic>
  // <fields>
  public FontMetrics metrics;

  public Map<Integer,Integer> glyphIndexes;
  public Map<Integer,Integer> glyphKernings;
  public Map<Integer,Integer> glyphWidths;

  public BufferedReader fontData;//TODO: convert to IInputStream
  // </fields>

  // <constructors>
  AfmParser(
    BufferedReader fontData
    )
  {
    this.fontData = fontData;

    load();
  }
  // </constructors>

  // <interface>
  // <private>
  /**
    Loads the font data.
  */
  private void load(
    )
  {
    try
    {
      metrics = new FontMetrics();
      loadFontHeader();
      loadCharMetrics();
      loadKerningData();
    }
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  /**
    Loads the font header [AFM:4.1:3,4.1-4.4].

    @throws IOException
  */
  private void loadFontHeader(
    ) throws IOException
  {
    String line;
    Pattern linePattern = Pattern.compile("(\\S+)\\s+(.+)");
    while((line = fontData.readLine()) != null)
    {
      Matcher lineMatcher = linePattern.matcher(line);
      if(!lineMatcher.find())
        continue;

      String key = lineMatcher.group(1);
      if (key.equals("Ascender"))
      {metrics.ascender = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("CapHeight"))
      {metrics.capHeight = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("Descender"))
      {metrics.descender = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("EncodingScheme"))
      {metrics.isCustomEncoding = lineMatcher.group(2).equals("FontSpecific");}
      else if (key.equals("FontBBox"))
      {
        String[] coordinates = lineMatcher.group(2).split("\\s+");
        metrics.xMin = (int)Float.parseFloat(coordinates[0]);
        metrics.yMin = (int)Float.parseFloat(coordinates[1]);
        metrics.xMax = (int)Float.parseFloat(coordinates[2]);
        metrics.yMax = (int)Float.parseFloat(coordinates[3]);
      }
      else if(key.equals("FontName"))
      {metrics.fontName = lineMatcher.group(2);}
      else if (key.equals("IsFixedPitch"))
      {metrics.isFixedPitch = Boolean.parseBoolean(lineMatcher.group(2));}
      else if (key.equals("ItalicAngle"))
      {metrics.italicAngle = Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("StdHW"))
      {metrics.stemH = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("StdVW"))
      {metrics.stemV = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("UnderlinePosition"))
      {metrics.underlinePosition = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("UnderlineThickness"))
      {metrics.underlineThickness = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("Weight"))
      {metrics.weight = lineMatcher.group(2);}
      else if (key.equals("XHeight"))
      {metrics.xHeight = (int)Float.parseFloat(lineMatcher.group(2));}
      else if (key.equals("StartCharMetrics"))
      {break;}
    }
    if(metrics.ascender == 0)
    {metrics.ascender = metrics.yMax;}
    if(metrics.descender == 0)
    {metrics.descender = metrics.yMin;}
  }

  /**
    Loads individual character metrics [AFM:4.1:3,4,4.4,8].

    @throws IOException
  */
  private void loadCharMetrics(
    ) throws IOException
  {
    glyphIndexes = new Hashtable<Integer, Integer>();
    glyphWidths = new Hashtable<Integer,Integer>();

    String line;
    Pattern linePattern = Pattern.compile("C (\\S+) ; WX (\\S+) ; N (\\S+)");
    int implicitCharCode = Short.MAX_VALUE;
    while((line = fontData.readLine()) != null)
    {
      Matcher lineMatcher = linePattern.matcher(line);
      if(!lineMatcher.find())
      {
        if(line.equals("EndCharMetrics"))
          break;

        continue;
      }

      int charCode = Integer.parseInt(lineMatcher.group(1));
      int width = (int)Float.parseFloat(lineMatcher.group(2));
      String charName = lineMatcher.group(3);
      if(charCode < 0)
      {
        if(charName == null)
          continue;

        charCode = ++implicitCharCode;
      }
      int code = (
        charName == null
            || metrics.isCustomEncoding
          ? charCode
          : GlyphMapping.nameToCode(charName)
        );
      glyphIndexes.put(code,charCode);
      glyphWidths.put(charCode,width);
    }
  }

  /**
    Loads kerning data [AFM:4.1:3,4,4.5,9].

    @throws IOException
  */
  private void loadKerningData(
    ) throws IOException
  {
    glyphKernings = new Hashtable<Integer,Integer>();

    String line;
    while((line = fontData.readLine()) != null)
    {
      if(line.startsWith("StartKernPairs"))
        break;
    }

    Pattern linePattern = Pattern.compile("KPX (\\S+) (\\S+) (\\S+)");
    while((line = fontData.readLine()) != null)
    {
      Matcher lineMatcher = linePattern.matcher(line);
      if(!lineMatcher.find())
      {
        if(line.equals("EndKernPairs"))
          break;

        continue;
      }

      int code1 = GlyphMapping.nameToCode(lineMatcher.group(1));
      int code2 = GlyphMapping.nameToCode(lineMatcher.group(2));
      int pair = code1 << 16 + code2;
      int value = (int)Float.parseFloat(lineMatcher.group(3));

      glyphKernings.put(pair,value);
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
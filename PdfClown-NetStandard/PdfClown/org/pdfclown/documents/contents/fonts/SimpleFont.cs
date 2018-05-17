/*
  Copyright 2009-2015 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library" (the
  Program): see the accompanying README files for more info.

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

using org.pdfclown.objects;
using org.pdfclown.util;

using System;
using System.Collections.Generic;

namespace org.pdfclown.documents.contents.fonts
{
  /**
    <summary>Simple font [PDF:1.6:5.5].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public abstract class SimpleFont
    : Font
  {
    #region constructors
    protected SimpleFont(
      Document context
      ) : base(context)
    {}

    protected SimpleFont(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region protected
    protected override PdfDataObject GetDescriptorValue(
      PdfName key
      )
    {
      PdfDictionary fontDescriptor = (PdfDictionary)BaseDataObject.Resolve(PdfName.FontDescriptor);
      return fontDescriptor != null ? fontDescriptor.Resolve(key) : null;
    }

    protected virtual IDictionary<ByteArray,int> GetBaseEncoding(
      PdfName encodingName
      )
    {
      if(encodingName == null) // Default encoding.
      {
        if(symbolic) // Built-in encoding.
          return Encoding.Get(PdfName.Identity).GetCodes();
        else // Standard encoding.
          return Encoding.Get(PdfName.StandardEncoding).GetCodes();
      }
      else // Predefined encoding.
        return Encoding.Get(encodingName).GetCodes();
    }

    protected void LoadEncoding(
      )
    {
      // Mapping character codes...
      PdfDataObject encodingObject = BaseDataObject.Resolve(PdfName.Encoding);
      FlagsEnum flags = Flags;
      symbolic = (flags & FlagsEnum.Symbolic) != 0;
      if(this.codes == null)
      {
        IDictionary<ByteArray,int> codes;
        if(encodingObject is PdfDictionary) // Derived encoding.
        {
          PdfDictionary encodingDictionary = (PdfDictionary)encodingObject;

          // Base encoding.
          codes = GetBaseEncoding((PdfName)encodingDictionary[PdfName.BaseEncoding]);

          // Differences.
          PdfArray differencesObject = (PdfArray)encodingDictionary.Resolve(PdfName.Differences);
          if(differencesObject != null)
          {
            /*
              NOTE: Each code is the first index in a sequence of character codes to be changed: the
              first character name after a code associates that character to that code; subsequent
              names replace consecutive code indices until the next code appears in the array.
            */
            byte[] charCodeData = new byte[1];
            foreach(PdfDirectObject differenceObject in differencesObject)
            {
              if(differenceObject is PdfInteger) // Subsequence initial code.
              {charCodeData[0] = (byte)(((int)((PdfInteger)differenceObject).Value) & 0xFF);}
              else // Character name.
              {
                ByteArray charCode = new ByteArray(charCodeData);
                string charName = (string)((PdfName)differenceObject).Value;
                if(charName.Equals(".notdef"))
                {codes.Remove(charCode);}
                else
                {
                  int? code = GlyphMapping.NameToCode(charName);
                  codes[charCode] = (code ?? charCodeData[0]);
                }
                charCodeData[0]++;
              }
            }
          }
        }
        else // Predefined encoding.
        {codes = GetBaseEncoding((PdfName)encodingObject);}
        this.codes = new BiDictionary<ByteArray,int>(codes);
      }
      // Purging unused character codes...
      {
        PdfArray glyphWidthObjects = (PdfArray)BaseDataObject.Resolve(PdfName.Widths);
        if(glyphWidthObjects != null)
        {
          ByteArray charCode = new ByteArray(new byte[]{(byte)((PdfInteger)BaseDataObject[PdfName.FirstChar]).IntValue});
          foreach(PdfDirectObject glyphWidthObject in glyphWidthObjects)
          {
            if(((PdfInteger)glyphWidthObject).IntValue == 0)
            {codes.Remove(charCode);}
            charCode.Data[0]++;
          }
        }
      }

      // Mapping glyph indices...
      glyphIndexes = new Dictionary<int, int>();
      foreach(KeyValuePair<ByteArray,int> code in codes)
      {glyphIndexes[code.Value] = (int)code.Key.Data[0] & 0xFF;}
    }

    protected override void OnLoad(
      )
    {
      LoadEncoding();

      // Glyph widths.
      if(glyphWidths == null)
      {
        glyphWidths = new Dictionary<int,int>();
        PdfArray glyphWidthObjects = (PdfArray)BaseDataObject.Resolve(PdfName.Widths);
        if(glyphWidthObjects != null)
        {
          ByteArray charCode = new ByteArray(
            new byte[]
            {(byte)((PdfInteger)BaseDataObject[PdfName.FirstChar]).IntValue}
            );
          foreach(PdfDirectObject glyphWidthObject in glyphWidthObjects)
          {
            int glyphWidth = ((IPdfNumber)glyphWidthObject).IntValue;
            if(glyphWidth > 0)
            {
              int code;
              if(codes.TryGetValue(charCode,out code))
              {glyphWidths[glyphIndexes[code]] = glyphWidth;}
            }
            charCode.Data[0]++;
          }
        }
      }
      // Default glyph width.
      {
        IPdfNumber widthObject = (IPdfNumber)GetDescriptorValue(PdfName.AvgWidth);
        if(widthObject != null)
        {AverageWidth = widthObject.IntValue;}
        widthObject = (IPdfNumber)GetDescriptorValue(PdfName.MissingWidth);
        if(widthObject != null)
        {DefaultWidth = widthObject.IntValue;}
      }
    }
    #endregion
    #endregion
  }
}
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

package org.pdfclown.documents;

import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.documents.interaction.annotations.Stamp;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfString;
import org.pdfclown.util.StringUtils;
import org.pdfclown.util.io.IOUtils;

/**
  Document configuration.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/08/15
*/
public final class DocumentConfiguration
{
  // <dynamic>
  // <fields>
  private CompatibilityModeEnum compatibilityMode = CompatibilityModeEnum.Loose;
  private EncodingFallbackEnum encodingFallback = EncodingFallbackEnum.Substitution;
  private java.io.File stampPath;

  private final Document document;

  private Map<Stamp.StandardTypeEnum,FormXObject> importedStamps;
  // </fields>

  // <constructors>
  DocumentConfiguration(
    Document document
    )
  {this.document = document;}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the document's version compatibility mode.
  */
  public CompatibilityModeEnum getCompatibilityMode(
    )
  {return compatibilityMode;}

  /**
    Gets the document associated with this configuration.
  */
  public Document getDocument(
    )
  {return document;}

  /**
    Gets the encoding behavior in case of missing character mapping.
  */
  public EncodingFallbackEnum getEncodingFallback(
    )
  {return encodingFallback;}

  /**
    Gets the stamp appearance corresponding to the specified stamp type.
    <p>The stamp appearance is retrieved from the {@link #getStampPath() standard stamps 
    path} and embedded in the document.</p>
    
    @param type
      Predefined stamp type whose appearance has to be retrieved.
  */
  public FormXObject getStamp(
    Stamp.StandardTypeEnum type
    )
  {
    if(type == null
      || stampPath == null)
      return null;
    
    FormXObject stamp = null;
    if(importedStamps != null)
    {stamp = importedStamps.get(type);}
    else
    {importedStamps = new HashMap<Stamp.StandardTypeEnum,FormXObject>();}
    if(stamp == null)
    {
      File stampFile = null;
      try
      {
        if(stampPath.isDirectory()) // Acrobat standard stamps directory.
        {
          String stampFileName;
          switch(type)
          {
            case Approved:
            case AsIs:
            case Confidential:
            case Departmental:
            case Draft:
            case Experimental:
            case Expired:
            case Final:
            case ForComment:
            case ForPublicRelease:
            case NotApproved:
            case NotForPublicRelease:
            case Sold:
            case TopSecret:
              stampFileName = "Standard.pdf";
              break;
            case BusinessApproved:
            case BusinessConfidential:
            case BusinessDraft:
            case BusinessFinal:
            case BusinessForComment:
            case BusinessForPublicRelease:
            case BusinessNotApproved:
            case BusinessNotForPublicRelease:
            case BusinessCompleted:
            case BusinessVoid:
            case BusinessPreliminaryResults:
            case BusinessInformationOnly:
              stampFileName = "StandardBusiness.pdf";
              break;
            case Rejected:
            case Accepted:
            case InitialHere:
            case SignHere:
            case Witness: 
              stampFileName = "SignHere.pdf";
              break;
            default:
              throw new UnsupportedOperationException("Unknown stamp type");
          }
          stampFile = new File(new java.io.File(stampPath, stampFileName));
          PdfString stampPageName = new PdfString(type.getCode().getValue() + "=" + StringUtils.join(" ", type.getCode().getValue().substring(2).split("(?=\\p{Upper})")));
          Page stampPage = stampFile.getDocument().resolveName(Page.class, stampPageName);
          importedStamps.put(type, stamp = stampPage.toXObject(getDocument()));
          stamp.setBox(stampPage.getArtBox());
        }
        else // Standard stamps template (std-stamps.pdf).
        {
          stampFile = new File(stampPath);
          FormXObject stampXObject = (FormXObject)stampFile.getDocument().getPages().get(0).getResources().get(XObject.class, type.getCode());
          importedStamps.put(type, stamp = stampXObject.clone(getDocument()));
        }
      }
      catch(FileNotFoundException e)
      {throw new RuntimeException(e);}
      finally
      {IOUtils.closeQuietly(stampFile);}
    }
    return stamp;
  }

  /**
    Gets the path (either Acrobat's standard stamps installation directory or PDF Clown's standard
    stamps collection (std-stamps.pdf)) where standard stamp templates are located.
    <p>In order to ensure consistent and predictable rendering across the systems, the {@link 
    Stamp#Stamp(Page, Rectangle2D, String, org.pdfclown.documents.interaction.annotations.Stamp.StandardTypeEnum) 
    standard stamp annotations} require their appearance to be embedded from the corresponding 
    standard stamp files (Standard.pdf, StandardBusiness.pdf, SignHere.pdf, etc.) shipped with 
    Acrobat: defining this property activates the automatic embedding of such appearances.</p>
  */
  public java.io.File getStampPath(
    )
  {return stampPath;}
  
  /**
    @see #getCompatibilityMode()
  */
  public void setCompatibilityMode(
    CompatibilityModeEnum value
    )
  {compatibilityMode = value;}

  /**
    @see #getEncodingFallback()
  */
  public void setEncodingFallback(
    EncodingFallbackEnum value
    )
  {encodingFallback = value;}

  /**
    @see #getStampPath()
  */
  public void setStampPath(
    java.io.File value
    )
  {
    if(!value.exists())
      throw new IllegalArgumentException(new FileNotFoundException());

    stampPath = value;
  }

  /**
    @see #setCompatibilityMode(CompatibilityModeEnum)
  */
  public DocumentConfiguration withCompatibilityMode(
    CompatibilityModeEnum value
    )
  {
    setCompatibilityMode(value);
    return this;
  }

  /**
    @see #setStampPath(java.io.File)
  */
  public DocumentConfiguration withStampPath(
    java.io.File value
    )
  {
    setStampPath(value);
    return this;
  }
  // </public>
  // </interface>
  // </dynamic>
}
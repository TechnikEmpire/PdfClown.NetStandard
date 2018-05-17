/*
  Copyright 2008-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.DocumentConfiguration;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.util.math.geom.Dimension;
import org.pdfclown.util.math.geom.GeomUtils;

/**
  Rubber stamp annotation [PDF:1.6:8.4.5].
  <p>It displays text or graphics intended to look as if they were stamped
  on the page with a rubber stamp.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/30/15
*/
@PDF(VersionEnum.PDF13)
public final class Stamp
  extends Markup<Stamp>
{
  // <class>
  // <classes>
  /**
    Predefined stamp type [PDF:1.6:8.4.5].
  */
  public enum StandardTypeEnum
  {
    Accepted(PdfName.SHAccepted, 1.14),
    Approved(PdfName.Approved, 3.8),
    AsIs(PdfName.AsIs, 3.8),
    BusinessApproved(PdfName.SBApproved, 3.3),
    BusinessCompleted(PdfName.SBCompleted, 3.55),
    BusinessConfidential(PdfName.SBConfidential, 4.23),
    BusinessDraft(PdfName.SBDraft, 2.27),
    BusinessFinal(PdfName.SBFinal, 1.97),
    BusinessForComment(PdfName.SBForComment, 4.28),
    BusinessForPublicRelease(PdfName.SBForPublicRelease, 5.85),
    BusinessInformationOnly(PdfName.SBInformationOnly, 5.55),
    BusinessNotApproved(PdfName.SBNotApproved, 4.42),
    BusinessNotForPublicRelease(PdfName.SBNotForPublicRelease, 6.98),
    BusinessVoid(PdfName.SBVoid, 1.83),
    BusinessPreliminaryResults(PdfName.SBPreliminaryResults, 6.14),
    Confidential(PdfName.Confidential, 3.8),
    Departmental(PdfName.Departmental, 3.8),
    Draft(PdfName.Draft, 3.8),
    Experimental(PdfName.Experimental, 3.8),
    Expired(PdfName.Expired, 3.8),
    Final(PdfName.Final, 3.8),
    ForComment(PdfName.ForComment, 3.8),
    ForPublicRelease(PdfName.ForPublicRelease, 3.8),
    InitialHere(PdfName.SHInitialHere, 3.29),
    NotApproved(PdfName.NotApproved, 3.8),
    NotForPublicRelease(PdfName.NotForPublicRelease, 3.8),
    Rejected(PdfName.SBRejected, 1.0),
    SignHere(PdfName.SHSignHere, 3.29),
    Sold(PdfName.Sold, 3.8),
    TopSecret(PdfName.TopSecret, 3.8),
    Witness(PdfName.SHWitness, 3.29); 

    /**
      Gets the stamp type corresponding to the given value.
    */
    public static StandardTypeEnum get(
      PdfName value
      )
    {
      for(StandardTypeEnum type : StandardTypeEnum.values())
      {
        if(type.getCode().equals(value))
          return type;
      }
      return null;
    }

    private final double aspect;
    private final PdfName code;

    private StandardTypeEnum(
      PdfName code,
      double aspect
      )
    {
      this.code = code;
      this.aspect = aspect;
    }

    /**
      Gets the aspect ratio of the original Acrobat standard stamp.
    */
    public double getAspect(
      )
    {return aspect;}
    
    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final String CustomTypeName = "Custom";
  private static final StandardTypeEnum DefaultType = StandardTypeEnum.Draft;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new predefined stamp on the specified page.
    
    @param page
      Page where this stamp has to be placed.
    @param location
      Position where this stamp has to be centered.
    @param size
      Dimension of the stamp:
      <ul>
        <li>{@code null} to apply the natural size</li>
        <li>{@code Dimension(0, height)} to scale the width proportionally to the height</li> 
        <li>{@code Dimension(width, 0)} to scale the height proportionally to the width</li>
      </ul> 
    @param text
      Annotation text.
    @param type
      Predefined stamp type.
  */
  public Stamp(
    Page page,
    Point2D location,
    Dimension2D size,
    String text,
    StandardTypeEnum type
    )
  {
    super(
      page,
      PdfName.Stamp,
      GeomUtils.align(
        size != null
          ? new Rectangle2D.Double(0, 0,
            size.getWidth() > 0 ? size.getWidth() : size.getHeight() * type.getAspect(),
            size.getHeight() > 0 ? size.getHeight() : size.getWidth() / type.getAspect()
            )
          : new Rectangle2D.Double(0, 0, 40 * type.getAspect(), 40),
        location,
        new Point(0, 0)
        ),
      text
      );
    setTypeName(type.getCode().getValue());
  }

  /**
    Creates a new custom stamp on the specified page.
    
    @param page
      Page where this stamp has to be placed.
    @param location
      Position where this stamp has to be centered.
    @param text
      Annotation text.
    @param appearance
      Custom appearance.
  */
  public Stamp(
    Page page,
    Point2D location,
    String text,
    FormXObject appearance
    )
  {
    super(
      page,
      PdfName.Stamp,
      GeomUtils.align(
        appearance.getMatrix().createTransformedShape(appearance.getBox()).getBounds2D(),
        location,
        new Point(0, 0)
        ),
      text
      );
    getAppearance().getNormal().put(null, appearance);
    setTypeName(CustomTypeName);
  }

  Stamp(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Stamp clone(
    Document context
    )
  {return (Stamp)super.clone(context);}
  
  /**
    Gets the rotation applied to the stamp.
  */
  public int getRotation(
    )
  {
    PdfNumber<?> rotationObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.Rotate);
    return rotationObject != null ? rotationObject.getIntValue() : 0;
  }

  /**
    Gets the type name of this stamp.
  */
  public String getTypeName(
    )
  {
    PdfName typeNameObject = (PdfName)getBaseDataObject().get(PdfName.Name);
    return typeNameObject != null ? typeNameObject.getValue() : DefaultType.getCode().getValue();
  }
  
  public void setRotation(
    int value
    )
  {
    getBaseDataObject().put(PdfName.Rotate, value != 0 ? new PdfInteger(value) : null);
    
    FormXObject appearance = getAppearance().getNormal().get(null);
    // Custom appearance?
    if(appearance != null)
    {
      /*
        NOTE: Custom appearances are responsible of their proper rotation.
        NOTE: Rotation must preserve the original scale factor.
      */
      Rectangle2D oldBox = getBox();
      Rectangle2D unscaledOldBox = appearance.getMatrix().createTransformedShape(appearance.getBox()).getBounds2D();
      Dimension2D scale = new Dimension(oldBox.getWidth() / unscaledOldBox.getWidth(), oldBox.getHeight() / unscaledOldBox.getHeight());
      
      AffineTransform matrix = new AffineTransform();
      matrix.rotate(Math.toRadians(value));
      appearance.setMatrix(matrix);
      
      Rectangle2D appearanceBox = appearance.getBox();
      appearanceBox.setRect(0, 0, appearanceBox.getWidth() * scale.getWidth(), appearanceBox.getHeight() * scale.getHeight());
      setBox(
        GeomUtils.align(
          appearance.getMatrix().createTransformedShape(appearanceBox).getBounds2D(),
          new Point2D.Double(oldBox.getCenterX(), oldBox.getCenterY()),
          new Point(0, 0)
          )
        );
    }
  }

  /**
    <p>To ensure predictable rendering of the {@link StandardTypeEnum standard stamp types} across the 
    systems, {@link DocumentConfiguration#getStampPath()} must be defined so as to embed the corresponding 
    templates.</p>
    
    @see #getTypeName()
  */
  public void setTypeName(
    String value
    )
  {
    PdfName typeNameObject = PdfName.get(value);
    getBaseDataObject().put(PdfName.Name, typeNameObject != null && !typeNameObject.equals(DefaultType.code) ? typeNameObject : null);
    
    StandardTypeEnum standardType = StandardTypeEnum.get(typeNameObject);
    if(standardType != null)
    {
      /*
        NOTE: Standard stamp types leverage predefined appearances.
      */
      getAppearance().getNormal().put(null, getDocument().getConfiguration().getStamp(standardType));
    }
  }
  
  public Stamp withRotation(
    int value
    )
  {
    setRotation(value);
    return self();
  }
  
  /**
    @see #setTypeName(String)
  */
  public Stamp withTypeName(
    String value
    )
  {
    setTypeName(value);
    return self();
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
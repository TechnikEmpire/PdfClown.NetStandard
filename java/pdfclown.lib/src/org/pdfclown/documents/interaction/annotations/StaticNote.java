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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.JustificationEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Free text annotation [PDF:1.6:8.4.5].
  <p>It displays text directly on the page. Unlike an ordinary text annotation, a free text 
  annotation has no open or closed state; instead of being displayed in a pop-up window, the text is
  always visible.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2.1, 03/21/15
*/
@PDF(VersionEnum.PDF13)
public final class StaticNote
  extends Markup<StaticNote>
{
  // <class>
  // <classes>
  /**
    Callout line [PDF:1.6:8.4.5].
  */
  public static class CalloutLine
    extends PdfObjectWrapper<PdfArray>
  {
    private Page page;

    public CalloutLine(
      Page page,
      Point2D start,
      Point2D end
      )
    {this(page, start, null, end);}

    public CalloutLine(
      Page page,
      Point2D start,
      Point2D knee,
      Point2D end
      )
    {
      super(new PdfArray());
      this.page = page;
      PdfArray baseDataObject = getBaseDataObject();
      {
        double pageHeight = page.getBox().getHeight();
        baseDataObject.add(PdfReal.get(start.getX()));
        baseDataObject.add(PdfReal.get(pageHeight - start.getY()));
        if(knee != null)
        {
          baseDataObject.add(PdfReal.get(knee.getX()));
          baseDataObject.add(PdfReal.get(pageHeight - knee.getY()));
        }
        baseDataObject.add(PdfReal.get(end.getX()));
        baseDataObject.add(PdfReal.get(pageHeight - end.getY()));
      }
    }

    private CalloutLine(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public CalloutLine clone(
      Document context
      )
    {return (CalloutLine)super.clone(context);}

    public Point2D getEnd(
      )
    {
      PdfArray coordinates = getBaseDataObject();
      if(coordinates.size() < 6)
        return new Point2D.Double(
          ((PdfNumber<?>)coordinates.get(2)).getDoubleValue(),
          page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(3)).getDoubleValue()
          );
      else
        return new Point2D.Double(
          ((PdfNumber<?>)coordinates.get(4)).getDoubleValue(),
          page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(5)).getDoubleValue()
          );
    }

    public Point2D getKnee(
      )
    {
      PdfArray coordinates = getBaseDataObject();
      if(coordinates.size() < 6)
        return null;

      return new Point2D.Double(
        ((PdfNumber<?>)coordinates.get(2)).getDoubleValue(),
        page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(3)).getDoubleValue()
        );
    }

    public Point2D getStart(
      )
    {
      PdfArray coordinates = getBaseDataObject();

      return new Point2D.Double(
        ((PdfNumber<?>)coordinates.get(0)).getDoubleValue(),
        page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(1)).getDoubleValue()
        );
    }
  }

  /**
    Note type [PDF:1.6:8.4.5].
  */
  public enum TypeEnum
  {
    /**
      Callout.
    */
    Callout(PdfName.FreeTextCallout),
    /**
      Typewriter.
    */
    TypeWriter(PdfName.FreeTextTypeWriter);

    /**
      Gets the type corresponding to the given value.
    */
    public static TypeEnum get(
      PdfName value
      )
    {
      for(TypeEnum type : TypeEnum.values())
      {
        if(type.getCode().equals(value))
          return type;
      }
      return null;
    }

    private final PdfName code;

    private TypeEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final JustificationEnum DefaultJustification = JustificationEnum.Left;
  private static final LineEndStyleEnum DefaultLineEndStyle = LineEndStyleEnum.None;
  // <fields>
  // </static>
  
  // <dynamic>
  // <constructors>
  public StaticNote(
    Page page,
    Rectangle2D box,
    String text
    )
  {super(page, PdfName.FreeText, box, text);}

  StaticNote(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public StaticNote clone(
    Document context
    )
  {return (StaticNote)super.clone(context);}

  /**
    Gets the border effect.
  */
  @PDF(VersionEnum.PDF16)
  public BorderEffect getBorderEffect(
    )
  {return new BorderEffect(getBaseDataObject().get(PdfName.BE, PdfDictionary.class));}

  /**
    Gets the justification to be used in displaying the annotation's text.
  */
  public JustificationEnum getJustification(
    )
  {return JustificationEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.Q));}

  /**
    Gets the callout line attached to the free text annotation.
  */
  public CalloutLine getLine(
    )
  {
    PdfArray calloutLineObject = (PdfArray)getBaseDataObject().get(PdfName.CL);
    return calloutLineObject != null ? new CalloutLine(calloutLineObject) : null;
  }

  /**
    Gets the style of the ending line ending.
  */
  public LineEndStyleEnum getLineEndStyle(
    )
  {
    PdfArray endstylesObject = (PdfArray)getBaseDataObject().get(PdfName.LE);
    return endstylesObject != null ? LineEndStyleEnum.get((PdfName)endstylesObject.get(1)) : DefaultLineEndStyle;
  }

  /**
    Gets the style of the starting line ending.
  */
  public LineEndStyleEnum getLineStartStyle(
    )
  {
    PdfArray endstylesObject = (PdfArray)getBaseDataObject().get(PdfName.LE);
    return endstylesObject != null ? LineEndStyleEnum.get((PdfName)endstylesObject.get(0)) : DefaultLineEndStyle;
  }

  /**
    @see #getBorderEffect()
  */
  public void setBorderEffect(
    BorderEffect value
    )
  {getBaseDataObject().put(PdfName.BE, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getJustification()
  */
  public void setJustification(
    JustificationEnum value
    )
  {getBaseDataObject().put(PdfName.Q, value != null && value != DefaultJustification ? value.getCode() : null);}

  /**
    @see #getLine()
  */
  public void setLine(
    CalloutLine value
    )
  {
    getBaseDataObject().put(PdfName.CL, PdfObjectWrapper.getBaseObject(value));
    if(value != null)
    {
      /*
        NOTE: To ensure the callout would be properly rendered, we have to declare the corresponding
        intent.
      */
      setType(TypeEnum.Callout);
    }
  }

  /**
    @see #getLineEndStyle()
  */
  public void setLineEndStyle(
    LineEndStyleEnum value
    )
  {ensureLineEndStylesObject().set(1, value.getCode());}

  /**
    @see #getLineStartStyle()
  */
  public void setLineStartStyle(
    LineEndStyleEnum value
    )
  {ensureLineEndStylesObject().set(0,value.getCode());}

  /**
    Popups not supported.
  */
  @Override
  public void setPopup(
    Popup value
    ) throws UnsupportedOperationException
  {throw new UnsupportedOperationException();}
  
  /**
    @see #setBorderEffect(BorderEffect)
  */
  public StaticNote withBorderEffect(
    BorderEffect value
    )
  {
    setBorderEffect(value);
    return self();
  }

  /**
    @see #setJustification(JustificationEnum)
  */
  public StaticNote withJustification(
    JustificationEnum value
    )
  {
    setJustification(value);
    return self();
  }

  /**
    @see #setLine(CalloutLine)
  */
  public StaticNote withLine(
    CalloutLine value
    )
  {
    setLine(value);
    return self();
  }

  /**
    @see #setLineEndStyle(LineEndStyleEnum)
  */
  public StaticNote withLineEndStyle(
    LineEndStyleEnum value
    )
  {
    setLineEndStyle(value);
    return self();
  }

  /**
    @see #setLineStartStyle(LineEndStyleEnum)
  */
  public StaticNote withLineStartStyle(
    LineEndStyleEnum value
    )
  {
    setLineStartStyle(value);
    return self();
  }
  // </public>

  // <private>
  private PdfArray ensureLineEndStylesObject(
    )
  {
    PdfArray endStylesObject = (PdfArray)getBaseDataObject().get(PdfName.LE);
    if(endStylesObject == null)
    {
      getBaseDataObject().put(
        PdfName.LE,
        endStylesObject = new PdfArray(
          new PdfDirectObject[]
          {
            DefaultLineEndStyle.getCode(),
            DefaultLineEndStyle.getCode()
          }
          )
        );
    }
    return endStylesObject;
  }
  
  @SuppressWarnings("unused")
  private TypeEnum getType(
    )
  {return TypeEnum.get(getTypeBase());}
  
  private void setType(
    TypeEnum value
    )
  {setTypeBase(value != null ? value.getCode() : null);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
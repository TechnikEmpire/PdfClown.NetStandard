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

package org.pdfclown.documents.contents.layers;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Visibility expression, used to compute visibility of content based on a set of layers
  [PDF:1.7:4.10.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/20/15
*/
@PDF(VersionEnum.PDF16)
public class VisibilityExpression
  extends PdfObjectWrapper<PdfArray>
{
  // <classes>
  public enum OperatorEnum
  {
    And(PdfName.And),
    Or(PdfName.Or),
    Not(PdfName.Not);

    public static OperatorEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        throw new IllegalArgumentException("Undefined name");

      for(OperatorEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Operator unknown: " + name);
    }
    
    private PdfName name;

    private OperatorEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }

  private static class Operands
    extends Array<PdfObjectWrapper<?>>
  {
    private static class ItemWrapper
      implements IWrapper<PdfObjectWrapper<?>>
    {
      public PdfObjectWrapper<?> wrap(
        PdfDirectObject baseObject
        )
      {
        if(baseObject.resolve() instanceof PdfArray)
          return VisibilityExpression.wrap(baseObject);
        else
          return Layer.wrap(baseObject);
      }
    }

    private static final ItemWrapper Wrapper = new ItemWrapper();

    public Operands(
      PdfDirectObject baseObject
      )
    {super(Wrapper, baseObject);}

    @Override
    public int size(
      )
    {return super.size() - 1;}

    @Override
    public int indexOf(
      Object item
      )
    {
      int index = super.indexOf(item);
      return index > 0 ? index - 1 : -1;
    }

    @Override
    public void add(
      int index,
      PdfObjectWrapper<?> item
      )
    {
      if(PdfName.Not.equals(super.get(0)) && super.size() >=2)
        throw new IllegalArgumentException("'Not' operator requires only one operand.");

      validateItem(item);
      super.add(index + 1, item);
    }

    @Override
    public PdfObjectWrapper<?> remove(
      int index
      )
    {return super.remove(index + 1);}

    @Override
    public PdfObjectWrapper<?> get(
      int index
      )
    {return super.get(index + 1);}
    
    @Override
    public PdfObjectWrapper<?> set(
      int index,
      PdfObjectWrapper<?> item
      )
    {
      validateItem(item);
      return super.set(index + 1, item);
    }

    private void validateItem(
      PdfObjectWrapper<?> item
      )
    {
      if(!(item instanceof VisibilityExpression
        || item instanceof Layer))
        throw new IllegalArgumentException("Operand MUST be either VisibilityExpression or Layer");
    }
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  public static VisibilityExpression wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new VisibilityExpression(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public VisibilityExpression(
    Document context,
    OperatorEnum operator,
    PdfObjectWrapper<?>... operands
    )
  {
    super(context, new PdfArray((PdfDirectObject)null));
    
    setOperator(operator);
    Array<PdfObjectWrapper<?>> operands_ = getOperands();
    for(PdfObjectWrapper<?> operand : operands)
    {operands_.add(operand);}
  }

  protected VisibilityExpression(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  public Array<PdfObjectWrapper<?>> getOperands(
    )
  {return new Operands(getBaseObject());}

  public OperatorEnum getOperator(
    )
  {return OperatorEnum.valueOf((PdfName)getBaseDataObject().get(0));}

  public void setOperator(
    OperatorEnum value
    )
  {
    if(value == OperatorEnum.Not && getBaseDataObject().size() > 2)
      throw new IllegalArgumentException("'Not' operator requires only one operand.");

    getBaseDataObject().set(0, value.getName());
  }
  // </public>
  // </interface>
  // </dynamic>
}

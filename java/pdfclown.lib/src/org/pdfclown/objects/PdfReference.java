/*
  Copyright 2006-2015 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.objects;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Symbol;
import org.pdfclown.util.NotImplementedException;

/**
  PDF indirect reference object [PDF:1.6:3.2.9].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.2.0, 03/10/15
*/
public final class PdfReference
  extends PdfDirectObject
  implements IPdfIndirectObject
{
  // <class>
  // <static>
  private static final int DelegatedReferenceNumber = -1;
  // </static>
  
  // <dynamic>
  // <fields>
  private final int generationNumber;
  private final int objectNumber;
  
  private PdfIndirectObject indirectObject;

  private File file;
  private PdfObject parent;
  private boolean updated;
  // </fields>

  // <constructors>
  PdfReference(
    PdfIndirectObject indirectObject
    )
  {
    this.objectNumber = DelegatedReferenceNumber;
    this.generationNumber = DelegatedReferenceNumber;
    
    this.indirectObject = indirectObject;
  }

  /**
    <span style="color:red">For internal use only.</span>
  */
  public PdfReference(
    int objectNumber,
    int generationNumber,
    File file
    )
  {
    this.objectNumber = objectNumber;
    this.generationNumber = generationNumber;
    
    this.file = file;
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfObject accept(
    IVisitor visitor,
    Object data
    )
  {return visitor.visit(this, data);}

  @Override
  public int compareTo(
    PdfDirectObject object
    )
  {throw new NotImplementedException();}

  @Override
  public boolean equals(
    Object other
    )
  {
    /*
     * NOTE: References are evaluated as "equal" if they are either the same instance or they sport 
     * the same identifier within the same file instance.
     */
    if(super.equals(other))
      return true;
    else if(other == null
        || !other.getClass().equals(getClass()))
      return false;
    
    PdfReference otherReference = (PdfReference)other;
    return otherReference.getFile() == getFile()
        && otherReference.getId().equals(getId());
  }

  @Override
  public File getFile(
    )
  {return file != null ? file : super.getFile();}
  
  /**
    Gets the generation number.
  */
  public int getGenerationNumber(
    )
  {return generationNumber == DelegatedReferenceNumber ? getIndirectObject().getXrefEntry().getGeneration() : generationNumber;}

  /**
    Gets the object identifier.
    <p>This corresponds to the serialized representation of an object identifier within a PDF file.</p>
  */
  public String getId(
    )
  {return ("" + getObjectNumber() + Symbol.Space + getGenerationNumber());}

  /**
    Gets the object reference.
    <p>This corresponds to the serialized representation of a reference within a PDF file.</p>
  */
  public String getIndirectReference(
    )
  {return (getId() + Symbol.Space + Symbol.CapitalR);}

  /**
    Gets the object number.
  */
  public int getObjectNumber(
    )
  {return objectNumber == DelegatedReferenceNumber ? getIndirectObject().getXrefEntry().getNumber() : objectNumber;}

  @Override
  public PdfObject getParent(
    )
  {return parent;}

  @Override
  public int hashCode(
    )
  {
    /*
      NOTE: Uniqueness should be achieved XORring the (local) reference hash-code with the (global)
      file hash-code.
    */
    return getId().hashCode() ^ getFile().hashCode();
  }

  @Override
  public boolean isUpdateable(
    )
  {return getIndirectObject() != null ? indirectObject.isUpdateable() : false;}

  @Override
  public boolean isUpdated(
    )
  {return updated;}

  @Override
  public void setUpdateable(
    boolean value
    )
  {
    /*
      NOTE: Fail fast if the referenced indirect object is undefined.
    */
    getIndirectObject().setUpdateable(value);
  }

  @Override
  public PdfReference swap(
    PdfObject other
    )
  {
    /*
      NOTE: Fail fast if the referenced indirect object is undefined.
    */
    return getIndirectObject().swap(((PdfReference)other).getIndirectObject()).getReference();
  }

  @Override
  public String toString(
    )
  {return getIndirectReference();}

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {stream.write(getIndirectReference());}

  // <IPdfIndirectObject>
  @Override
  public PdfReference clone(
    File context
    )
  {return (PdfReference)super.clone(context);}

  @Override
  public PdfDataObject getDataObject(
    )
  {return getIndirectObject() != null ? indirectObject.getDataObject() : null;}

  /**
    @return
      {@code null}, if the indirect object is undefined.
  */
  @Override
  public PdfIndirectObject getIndirectObject(
    )
  {
    if(indirectObject == null)
    {indirectObject = file.getIndirectObjects().get(objectNumber);}

    return indirectObject;
  }

  @Override
  public PdfReference getReference(
    )
  {return this;}

  @Override
  public void setDataObject(
    PdfDataObject value
    )
  {
    /*
      NOTE: Fail fast if the referenced indirect object is undefined.
    */
    getIndirectObject().setDataObject(value);
  }
  // </IPdfIndirectObject>
  // </public>

  // <protected>
  @Override
  protected boolean isVirtual(
    )
  {return getIndirectObject() != null ? indirectObject.isVirtual() : false;}

  @Override
  protected void setUpdated(
    boolean value
    )
  {updated = value;}

  @Override
  protected void setVirtual(
    boolean value
    )
  {
    /*
      NOTE: Fail fast if the referenced indirect object is undefined.
    */
    getIndirectObject().setVirtual(value);
  }
  // </protected>

  // <internal>
  @Override
  void setParent(
    PdfObject value
    )
  {parent = value;}
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}
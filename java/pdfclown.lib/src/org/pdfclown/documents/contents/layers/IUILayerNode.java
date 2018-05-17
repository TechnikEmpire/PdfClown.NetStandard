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

import org.pdfclown.objects.IPdfObjectWrapper;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;

/**
  Object that can be part of a hierarchical layer structure.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/20/15
*/
public interface IUILayerNode
  extends IPdfObjectWrapper
{
  /**
    Gets the sublayers.
  */
  UILayers getChildren(
    );

  /**
    Gets the text label.
  */
  String getTitle(
    );

  /**
    @see #getTitle()
  */
  void setTitle(
    String value
    );
}

final class UILayerNode
{
  public static IUILayerNode wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfDataObject baseDataObject = baseObject.resolve();
    if(baseDataObject instanceof PdfDictionary)
      return Layer.wrap(baseObject);
    else if(baseDataObject instanceof PdfArray)
      return LayerCollection.wrap(baseObject);
    else
      throw new IllegalArgumentException(baseDataObject.getClass().getSimpleName() + " is NOT a valid layer node.");
  }

  private UILayerNode(
    )
  {}
}
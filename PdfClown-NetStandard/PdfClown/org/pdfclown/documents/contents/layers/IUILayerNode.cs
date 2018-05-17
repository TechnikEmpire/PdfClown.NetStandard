/*
  Copyright 2015 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown.files;
using org.pdfclown.objects;

using System;

namespace org.pdfclown.documents.contents.layers
{
  /**
    <summary>Object that can be part of a hierarchical layer structure.</summary>
  */
  public interface IUILayerNode
    : IPdfObjectWrapper
  {
    /**
      <summary>Gets the sublayers.</summary>
    */
    UILayers Children
    {
      get;
    }

    /**
      <summary>Gets/Sets the text label.</summary>
    */
    string Title
    {
      get;
      set;
    }
  }

  internal sealed class UILayerNode
  {
    public static IUILayerNode Wrap(
      PdfDirectObject baseObject
      )
    {
      if(baseObject == null)
        return null;

      PdfDataObject baseDataObject = baseObject.Resolve();
      if(baseDataObject is PdfDictionary)
        return Layer.Wrap(baseObject);
      else if(baseDataObject is PdfArray)
        return LayerCollection.Wrap(baseObject);
      else
        throw new ArgumentException(baseDataObject.GetType().Name + " is NOT a valid layer node.");
    }

    private UILayerNode(
      )
    {}
  }
}
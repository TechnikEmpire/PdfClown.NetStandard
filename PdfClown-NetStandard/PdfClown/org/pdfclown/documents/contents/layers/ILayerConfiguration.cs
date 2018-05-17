/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

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

using System;
using System.Collections.Generic;

namespace org.pdfclown.documents.contents.layers
{
  /**
    <summary>Optional content configuration interface [PDF:1.7:4.10.3].</summary>
  */
  public interface ILayerConfiguration
    : IPdfObjectWrapper
  {
    /**
      <summary>Gets/Sets the name of the application or feature that created this configuration.
      </summary>
    */
    string Creator
    {
      get;
      set;
    }

    /**
      <summary>Gets/Sets the intended uses of this configuration.</summary>
      <remarks>
        <para>If one or more of a <see cref="Layer.Intents">layer's intents</see> are contained in
        this configuration's intents, the layer is used in determining visibility; otherwise, the
        layer has no effect on visibility.</para>
        <para>If this configuration's intents are empty, no layers are used in determining
        visibility; therefore, all content is considered visible.</para>
      </remarks>
      <returns>Intent collection (it comprises <see cref="IntentEnum"/> names but, for compatibility
      with future versions, unrecognized names are allowed). To apply any subsequent change, it has
      to be assigned back.</returns>
      <seealso cref="IntentEnum"/>
    */
    ISet<PdfName> Intents
    {
      get;
      set;
    }

    /**
      <summary>Gets the groups of layers whose states are intended to follow a radio button paradigm
      (that is exclusive visibility within the same group).</summary>
    */
    Array<OptionGroup> OptionGroups
    {
      get;
    }

    /**
      <summary>Gets/Sets the configuration name.</summary>
    */
    string Title
    {
      get;
      set;
    }

    /**
      <summary>Gets the layer structure displayed to the user.</summary>
    */
    UILayers UILayers
    {
      get;
    }

    /**
      <summary>Gets/Sets the list mode specifying which layers should be displayed to the user.
      </summary>
    */
    UIModeEnum UIMode
    {
      get;
      set;
    }

    /**
      <summary>Gets/Sets whether all the layers in the document are initialize to be visible when
      this configuration is applied.</summary>
    */
    bool? Visible
    {
      get;
      set;
    }
  }
}
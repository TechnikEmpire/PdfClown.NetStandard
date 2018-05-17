/*
  Copyright 2011-2015 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.Set;

import org.pdfclown.objects.Array;
import org.pdfclown.objects.IPdfObjectWrapper;
import org.pdfclown.objects.PdfName;

/**
  Optional content configuration interface [PDF:1.7:4.10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 04/26/15
*/
public interface ILayerConfiguration
  extends IPdfObjectWrapper
{
  /**
    Gets the name of the application or feature that created this configuration.
  */
  String getCreator(
    );

  /**
    Gets the intended uses of this configuration.
    <p>If one or more of a {@link Layer#getIntents() layer's intents} are contained in this 
    configuration's intents, the layer is used in determining visibility; otherwise, the layer has 
    no effect on visibility.</p>
    <p>If this configuration's intents are empty, no layers are used in determining visibility; 
    therefore, all content is considered visible.</p>

    @return
      Intent collection (it comprises {@link IntentEnum} names but, for compatibility with future 
      versions, unrecognized names are allowed). To apply any subsequent change, it has to be 
      assigned back through {@link #setIntents(Set)}.
    @see IntentEnum
  */
  Set<PdfName> getIntents(
    );

  /**
    Gets the groups of layers whose states are intended to follow a radio button paradigm (that is
    exclusive visibility within the same group).
  */
  Array<OptionGroup> getOptionGroups(
    );

  /**
    Gets the configuration name.
  */
  String getTitle(
    );

  /**
    Gets the layer structure displayed to the user.
  */
  UILayers getUILayers(
    );

  /**
    Gets the list mode specifying which layers should be displayed to the user.
  */
  UIModeEnum getUIMode(
    );

  /**
    Gets whether all the layers in the document are initialized to be visible when this configuration
    is applied.
  */
  Boolean isVisible(
    );

  /**
    @see #getCreator()
  */
  void setCreator(
    String value
    );

  /**
    @see #getIntents()
  */
  void setIntents(
    Set<PdfName> value
    );
  
  /**
    @see #getTitle()
  */
  void setTitle(
    String value
    );

  /**
    @see #getUIMode()
  */
  void setUIMode(
    UIModeEnum value
    );

  /**
    @see #isVisible()
  */
  void setVisible(
    Boolean value
    );
}

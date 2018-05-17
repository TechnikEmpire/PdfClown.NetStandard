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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interchange.access.LanguageIdentifier;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfString;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.tools.LayerManager;
import org.pdfclown.util.math.Interval;

/**
  Optional content group [PDF:1.7:4.10.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2.1, 04/26/15
*/
@PDF(VersionEnum.PDF15)
public final class Layer
  extends LayerEntity
  implements IUILayerNode
{
  public enum PageElementTypeEnum
  {
    HeaderFooter(PdfName.HF),
    Foreground(PdfName.FG),
    Background(PdfName.BG),
    Logo(PdfName.L);
    
    public static PageElementTypeEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return null;

      for(PageElementTypeEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Page element type unknown: " + name);
    }

    private PdfName name;

    private PageElementTypeEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }

  public enum UserTypeEnum
  {
    Individual(PdfName.Ind),
    Title(PdfName.Ttl),
    Organization(PdfName.Org);

    public static UserTypeEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return null;

      for(UserTypeEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("User type unknown: " + name);
    }

    private PdfName name;

    private UserTypeEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }

  /**
    Sublayers location within a configuration structure.
  */
  private static class LayersLocation
  {
    /**
      Sublayers ordinal position within the parent sublayers.
    */
    final int index;
    /**
      Parent layer object.
    */
    final PdfDirectObject parentLayerObject;
    /**
      Parent sublayers object.
    */
    final PdfArray parentLayersObject;
    /**
      Upper levels.
    */
    final Stack<Object[]> levels;

    public LayersLocation(
      PdfDirectObject parentLayerObject,
      PdfArray parentLayersObject,
      int index,
      Stack<Object[]> levels
      )
    {
      this.parentLayerObject = parentLayerObject;
      this.parentLayersObject = parentLayersObject;
      this.index = index;
      this.levels = levels;
    }
  }

  /**
    Layer state.
  */
  private enum StateEnum
  {
    /**
      Active.
    */
    On(PdfName.ON, true),
    /**
      Inactive.
    */
    Off(PdfName.OFF, false);

    public static StateEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return StateEnum.On;

      for(StateEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("State unknown: " + name);
    }

    public static StateEnum valueOf(
      Boolean enabled
      )
    {
      if(enabled == null)
        return StateEnum.On;

      for(StateEnum value : values())
      {
        if(enabled.equals(value.isEnabled()))
          return value;
      }
      throw new UnsupportedOperationException();
    }

    private boolean enabled;
    private PdfName name;

    private StateEnum(
      PdfName name,
      boolean enabled
      )
    {
      this.name = name;
      this.enabled = enabled;
    }

    public PdfName getName(
      )
    {return name;}

    public boolean isEnabled(
      )
    {return enabled;}
  }
  // </classes>

  // <static>
  // <fields>
  public static final PdfName TypeName = PdfName.OCG;

  private static final PdfName MembershipName = new PdfName("D-OCMD");
  // </fields>

  // <interface>
  // <public>
  public static Layer wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Layer(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Layer(
    Document context,
    String title
    )
  {
    super(context, PdfName.OCG);
    setTitle(title);

    // Add this layer to the global collection!
    /*
      NOTE: Every layer MUST be included in the global collection [PDF:1.7:4.10.3].
    */
    context.getLayer().getLayers().getBaseDataObject().add(getBaseObject());
  }

  private Layer(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Layer clone(
    Document context
    )
  {return (Layer)super.clone(context);}

  /**
    Deletes this layer, removing also its references from the document (contents included).
  */
  @Override
  public boolean delete(
    )
  {return delete(false);}

  /**
    Deletes this layer, removing also its references from the document.
    
    @param preserveContent
      Whether its contents are to be excluded from the removal.
  */
  public boolean delete(
    boolean preserveContent
    )
  {
    if(getDocument().getLayer().getLayers().contains(this))
    {
      LayerManager layerManager = new LayerManager();
      layerManager.remove(preserveContent, this);
    }
    return super.delete();
  }

  /**
    Gets the name of the type of content controlled by this layer.
  */
  public String getContentType(
    )
  {return (String)PdfSimpleObject.getValue(getUsageEntry(PdfName.CreatorInfo).get(PdfName.Subtype));}

  /**
    Gets the name of the application that created this layer.
  */
  public String getCreator(
    )
  {return (String)PdfSimpleObject.getValue(getUsageEntry(PdfName.CreatorInfo).get(PdfName.Creator));}

  /**
    Gets the dictionary used by the creating application to store application-specific data 
    associated to this layer.
  */
  public PdfDictionary getCreatorInfo(
    )
  {return getUsageEntry(PdfName.CreatorInfo);}

  /**
    Gets the intended uses of this layer.
    <p>For example, many document design applications, such as CAD packages, offer layering features
    for collecting groups of graphics together and selectively hiding or viewing them for the 
    convenience of the author. However, this layering may be different than would be useful to 
    consumers of the document; therefore, it is possible to specify different intents for layers
    within a single document: a given application may decide to use only layers that are of a 
    specific intent.</p>

    @return
      Intent collection (it comprises {@link IntentEnum} names but, for compatibility with future 
      versions, unrecognized names are allowed). To apply any subsequent change, it has to be 
      assigned back through {@link #setIntents(Set)}.
    @see IntentEnum
  */
  public Set<PdfName> getIntents(
    )
  {
    Set<PdfName> intents = new HashSet<PdfName>();
    PdfDataObject intentObject = getBaseDataObject().resolve(PdfName.Intent);
    if(intentObject != null)
    {
      if(intentObject instanceof PdfArray) // Multiple intents.
      {
        for(PdfDirectObject intentItem : (PdfArray)intentObject)
        {intents.add((PdfName)intentItem);}
      }
      else // Single intent.
      {intents.add((PdfName)intentObject);}
    }
    else
    {intents.add(IntentEnum.View.name);}
    return intents;
  }
  
  /**
    Gets the language of the content controlled by this layer.
    <p>The layer whose language matches the current system language is visible.</p>
  */
  public LanguageIdentifier getLanguage(
    )
  {return LanguageIdentifier.wrap(getUsageEntry(PdfName.Language).get(PdfName.Lang));}

  /**
    Gets whether a partial match (that is, the language matches but not the locale) with the current
    system language is enough to keep this layer visible.
  */
  public boolean getLanguagePreferred(
    )
  {return PdfName.ON.equals(getUsageEntry(PdfName.Language).get(PdfName.Preferred));}

  @Override
  public LayerEntity getMembership(
    )
  {
    LayerEntity membership = LayerMembership.wrap(getBaseDataObject().get(MembershipName));
    if(membership == null)
    {
      LayersLocation location = findLayersLocation();
      if(location == null || location.parentLayerObject == null)
      {membership = this;}
      else
      {
        getBaseDataObject().put(MembershipName, (membership = new LayerMembership(getDocument())).getBaseObject());
        membership.setVisibilityPolicy(VisibilityPolicyEnum.AllOn); // NOTE: Forces visibility to depend on all the ascendant layers.
        membership.getVisibilityMembers().add(this);
        membership.getVisibilityMembers().add(new Layer(location.parentLayerObject));
        for(Object[] level : location.levels)
        {
          PdfDirectObject layerObject = (PdfDirectObject)level[2];
          if(layerObject != null)
          {membership.getVisibilityMembers().add(new Layer(layerObject));}
        }
      }
    }
    return membership;
  }

  /**
    Gets the type of pagination artifact this layer contains.
  */
  public PageElementTypeEnum getPageElementType(
    )
  {return PageElementTypeEnum.valueOf((PdfName)getUsageEntry(PdfName.PageElement).get(PdfName.Subtype));}

  /**
    Gets the parent layer.
  */
  public Layer getParent(
    )
  {
    LayersLocation location = findLayersLocation();
    return location != null ? Layer.wrap(location.parentLayerObject) : null;
  }

  /**
    Gets the type of printable content controlled by this layer.
  */
  public String getPrintType(
    )
  {return (String)PdfSimpleObject.getValue(getUsageEntry(PdfName.Print).get(PdfName.Subtype));}

  /**
    Gets the names of the users for whom this layer is primarily intended.
  */
  public List<String> getUsers(
    )
  {
    List<String> users = new ArrayList<String>();
    PdfDirectObject usersObject = getUsageEntry(PdfName.User).get(PdfName.Name);
    if(usersObject instanceof PdfString)
    {users.add(((PdfString)usersObject.resolve()).getStringValue());}
    else if(usersObject instanceof PdfArray)
    {
      for(PdfDirectObject userObject : (PdfArray)usersObject)
      {users.add(((PdfString)userObject.resolve()).getStringValue());}
    }
    return users;
  }

  /**
    Gets the type of the users for whom this layer is primarily intended.
  */
  public UserTypeEnum getUserType(
    )
  {return UserTypeEnum.valueOf((PdfName)getUsageEntry(PdfName.User).get(PdfName.Type));}

  /**
    Default membership's {@link LayerMembership#getVisibilityExpression() VisibilityExpression} is 
    undefined as {@link #getVisibilityMembers()} is used instead.
  */
  @Override
  public VisibilityExpression getVisibilityExpression(
    )
  {return null;}
  
  /**
    Default membership's {@link LayerMembership#getVisibilityMembers() VisibilityMembers} collection
    is immutable as it's expected to represent the hierarchical line of this layer.
  */
  @Override
  public List<Layer> getVisibilityMembers(
    )
  {
    LayerEntity membership = getMembership();
    return Collections.unmodifiableList(membership == this ? Arrays.asList(this) : membership.getVisibilityMembers());
  }

  @Override
  public VisibilityPolicyEnum getVisibilityPolicy(
    )
  {
    LayerEntity membership = getMembership();
    return membership == this ? VisibilityPolicyEnum.AllOn : membership.getVisibilityPolicy();
  }

  /**
    Gets the range of magnifications at which the content in this layer is best viewed in a viewer 
    application.
    
    @return Zoom interval (minimum included, maximum excluded); valid values range from 0 to {@code
    Double.POSITIVE_INFINITY}, where 1 corresponds to 100% magnification.
  */
  public Interval<Double> getZoomRange(
    )
  {
    PdfDictionary zoomDictionary = getUsageEntry(PdfName.Zoom);
    PdfNumber<?> minObject = (PdfNumber<?>)zoomDictionary.resolve(PdfName.min);
    PdfNumber<?> maxObject = (PdfNumber<?>)zoomDictionary.resolve(PdfName.max);
    return new Interval<Double>(
      minObject != null ? minObject.getDoubleValue() : 0,
      maxObject != null ? maxObject.getDoubleValue() : Double.POSITIVE_INFINITY
      );
  }

  /**
    Gets whether this layer is visible when the document is saved by a viewer application to a
    format that does not support layers.
  */
  public Boolean isExportable(
    )
  {
    PdfDirectObject exportableObject = getUsageEntry(PdfName.Export).get(PdfName.ExportState);
    return exportableObject != null ? StateEnum.valueOf((PdfName)exportableObject).isEnabled() : null;
  }

  /**
    Gets whether the default visibility of this layer cannot be changed through the user interface
    of a viewer application.
  */
  public boolean isLocked(
    )
  {return getDefaultConfiguration().getBaseDataObject().resolve(PdfName.Locked, PdfArray.class).contains(getBaseObject());}

  /**
    Gets whether this layer is visible when the document is printed from a viewer application.
  */
  public Boolean isPrintable(
    )
  {
    PdfDirectObject printableObject = getUsageEntry(PdfName.Print).get(PdfName.PrintState);
    return printableObject != null ? StateEnum.valueOf((PdfName)printableObject).isEnabled() : null;
  }

  /**
    Gets whether this layer is visible when the document is opened in a viewer application.
  */
  public Boolean isViewable(
    )
  {
    PdfDirectObject viewableObject = getUsageEntry(PdfName.View).get(PdfName.ViewState);
    return viewableObject != null ? StateEnum.valueOf((PdfName)viewableObject).isEnabled() : null;
  }

  /**
    Gets whether this layer is initially visible in any kind of application.
  */
  public boolean isVisible(
    )
  {return getDefaultConfiguration().isVisible(this);}

  /**
    @see #getContentType()
  */
  public void setContentType(
    String value
    )
  {getUsageEntry(PdfName.CreatorInfo).put(PdfName.Subtype, PdfName.get(value));}

  /**
    @see #getCreator()
  */
  public void setCreator(
    String value
    )
  {getUsageEntry(PdfName.CreatorInfo).put(PdfName.Creator, PdfTextString.get(value));}

  /**
    @see #isExportable()
  */
  public void setExportable(
    Boolean value
    )
  {
    getUsageEntry(PdfName.Export).put(PdfName.ExportState, value != null ? StateEnum.valueOf(value).getName() : null);
    getDefaultConfiguration().setUsageApplication(PdfName.Export, PdfName.Export, this, value != null);
  }
  
  /**
    @see #getIntents()
  */
  public void setIntents(
    Set<PdfName> value
    )
  {
    PdfDirectObject intentObject = null;
    if(value != null 
      && !value.isEmpty())
    {
      if(value.size() == 1) // Single intent.
      {
        intentObject = value.iterator().next();
        if(intentObject.equals(IntentEnum.View.name)) // Default.
        {intentObject = null;}
      }
      else // Multiple intents.
      {
        PdfArray intentArray = new PdfArray();
        for(PdfName valueItem : value)
        {intentArray.add(valueItem);}
      }
    }
    getBaseDataObject().put(PdfName.Intent, intentObject);
  }

  /**
    @see #getLanguage()
  */
  public void setLanguage(
    LanguageIdentifier value
    )
  {
    getUsageEntry(PdfName.Language).put(PdfName.Lang, PdfObjectWrapper.getBaseObject(value));
    getDefaultConfiguration().setUsageApplication(PdfName.View, PdfName.Language, this, value != null);
    getDefaultConfiguration().setUsageApplication(PdfName.Print, PdfName.Language, this, value != null);
    getDefaultConfiguration().setUsageApplication(PdfName.Export, PdfName.Language, this, value != null);
  }
  
  /**
    @see #getLanguagePreferred()
  */
  public void setLanguagePreferred(
    boolean value
    )
  {getUsageEntry(PdfName.Language).put(PdfName.Preferred, value ? PdfName.ON : null);}

  /**
    @see #isLocked()
  */
  public void setLocked(
    boolean value
    )
  {
    PdfArray lockedArrayObject = getDefaultConfiguration().getBaseDataObject().resolve(PdfName.Locked, PdfArray.class);
    if(!lockedArrayObject.contains(getBaseObject()))
    {lockedArrayObject.add(getBaseObject());}
  }

  /**
    @see #getPageElementType()
  */
  public void setPageElementType(
    PageElementTypeEnum value
    )
  {getUsageEntry(PdfName.PageElement).put(PdfName.Subtype, value != null ? value.getName() : null);}

  /**
    @see #isPrintable()
  */
  public void setPrintable(
    Boolean value
    )
  {
    getUsageEntry(PdfName.Print).put(PdfName.PrintState, value != null ? StateEnum.valueOf(value).getName() : null);
    getDefaultConfiguration().setUsageApplication(PdfName.Print, PdfName.Print, this, value != null);
  }

  /**
    @see #getPrintType()
  */
  public void setPrintType(
    String value
    )
  {getUsageEntry(PdfName.Print).put(PdfName.Subtype, PdfName.get(value));}

  /**
    @see #getUsers()
  */
  public void setUsers(
    List<String> value
    )
  {
    PdfDirectObject usersObject = null;
    if(value != null && !value.isEmpty())
    {
      if(value.size() == 1)
      {usersObject = new PdfTextString(value.get(0));}
      else
      {
        PdfArray usersArray = new PdfArray();
        for(String user : value)
        {usersArray.add(new PdfTextString(user));}
        usersObject = usersArray;
      }
    }
    getUsageEntry(PdfName.User).put(PdfName.Name, usersObject);
    getDefaultConfiguration().setUsageApplication(PdfName.View, PdfName.User, this, usersObject != null);
    getDefaultConfiguration().setUsageApplication(PdfName.Print, PdfName.User, this, usersObject != null);
    getDefaultConfiguration().setUsageApplication(PdfName.Export, PdfName.User, this, usersObject != null);
  }
  
  /**
    @see #getUserType()
  */
  public void setUserType(
    UserTypeEnum value
    )
  {getUsageEntry(PdfName.User).put(PdfName.Type, value != null ? value.getName() : null);}

  /**
    @see #isViewable()
  */
  public void setViewable(
    Boolean value
    )
  {
    getUsageEntry(PdfName.View).put(PdfName.ViewState, value != null ? StateEnum.valueOf(value).getName() : null);
    getDefaultConfiguration().setUsageApplication(PdfName.View, PdfName.View, this, value != null);
  }
  
  /**
    @see #getVisibilityExpression()
  */
  @Override
  public void setVisibilityExpression(
    VisibilityExpression value
    )
  {throw new UnsupportedOperationException();}

  /**
    @see #getVisibilityMembers()
  */
  @Override
  public void setVisibilityMembers(
    List<Layer> value
    )
  {throw new UnsupportedOperationException();}

  @Override
  public void setVisibilityPolicy(
    VisibilityPolicyEnum value
    )
  {
    LayerEntity membership = getMembership();
    if(membership != this)
    {membership.setVisibilityPolicy(value);}
  }

  /**
    @see #isVisible()
  */
  public void setVisible(
    boolean value
    )
  {getDefaultConfiguration().setVisible(this, value);}

  public void setZoomRange(
    Interval<Double> value
    )
  {
    if(value != null)
    {
      PdfDictionary zoomDictionary = getUsageEntry(PdfName.Zoom);
      zoomDictionary.put(PdfName.min, value.getLow() != 0 ? PdfReal.get(value.getLow()) : null);
      zoomDictionary.put(PdfName.max, value.getHigh() != Double.POSITIVE_INFINITY ? PdfReal.get(value.getHigh()) : null);
    }
    else
    {getUsage().remove(PdfName.Zoom);}
    getDefaultConfiguration().setUsageApplication(PdfName.View, PdfName.Zoom, this, value != null);
  }

  @Override
  public String toString(
    )
  {return "Layer {\"" + getTitle() + "\" " + getBaseObject() + "}";}

  // <IUILayerNode>
  @Override
  public UILayers getChildren(
    )
  {
    LayersLocation location = findLayersLocation();
    return location != null ? UILayers.wrap(location.parentLayersObject.get(location.index, PdfArray.class)) : null;
  }
  
  @Override
  public String getTitle(
    )
  {return ((PdfTextString)getBaseDataObject().get(PdfName.Name)).getValue();}

  @Override
  public void setTitle(
    String value
    )
  {getBaseDataObject().put(PdfName.Name, new PdfTextString(value));}
  // </IUILayerNode>
  // </public>

  // <private>
  /**
    Finds the location of the sublayers object in the default configuration; in case no sublayers
    object is associated to this object, its virtual position is indicated.
  */
  private LayersLocation findLayersLocation(
    )
  {return findLayersLocation(getDefaultConfiguration());}
  
  /**
    Finds the location of the sublayers object in the specified configuration; in case no sublayers
    object is associated to this object, its virtual position is indicated.

    @param configuration Configuration context.
    @return <code>null</code>, if this layer is outside the specified configuration.
  */
  @SuppressWarnings("unchecked")
  private LayersLocation findLayersLocation(
    LayerConfiguration configuration
    )
  {
    /*
      NOTE: As layers are only weakly tied to configurations, their sublayers have to be sought
      through the configuration structure tree.
    */
    PdfDirectObject levelLayerObject = null;
    PdfArray levelObject = configuration.getUILayers().getBaseDataObject();
    Iterator<PdfDirectObject> levelIterator = levelObject.iterator();
    Stack<Object[]> levelIterators = new Stack<Object[]>();
    PdfDirectObject thisObject = getBaseObject();
    PdfDirectObject currentLayerObject = null;
    while(true)
    {
      if(!levelIterator.hasNext())
      {
        if(levelIterators.isEmpty())
          break;

        Object[] levelItems = levelIterators.pop();
        levelObject = (PdfArray)levelItems[0];
        levelIterator = (Iterator<PdfDirectObject>)levelItems[1];
        levelLayerObject = (PdfDirectObject)levelItems[2];
        currentLayerObject = null;
      }
      else
      {
        PdfDirectObject nodeObject = levelIterator.next();
        PdfDataObject nodeDataObject = PdfObject.resolve(nodeObject);
        if(nodeDataObject instanceof PdfDictionary)
        {
          if(nodeObject.equals(thisObject))
            /*
              NOTE: Sublayers are expressed as an array immediately following the parent layer node.
            */
            return new LayersLocation(levelLayerObject, levelObject, levelObject.indexOf(thisObject) + 1, levelIterators);

          currentLayerObject = nodeObject;
        }
        else if(nodeDataObject instanceof PdfArray)
        {
          levelIterators.push(new Object[]{levelObject, levelIterator, levelLayerObject});
          levelObject = (PdfArray)nodeDataObject;
          levelIterator = levelObject.iterator();
          levelLayerObject = currentLayerObject;
          currentLayerObject = null;
        }
      }
    }
    return null;
  }

  private LayerConfiguration getDefaultConfiguration(
    )
  {return getDocument().getLayer().getDefaultConfiguration();}

  private PdfDictionary getUsage(
    )
  {return getBaseDataObject().resolve(PdfName.Usage, PdfDictionary.class);}

  private PdfDictionary getUsageEntry(
    PdfName key
    )
  {return getUsage().resolve(key, PdfDictionary.class);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
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

using org.pdfclown.files;
using org.pdfclown.documents.interchange.access;
using org.pdfclown.objects;
using org.pdfclown.tools;
using org.pdfclown.util;
using org.pdfclown.util.math;

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;

namespace org.pdfclown.documents.contents.layers
{
  /**
    <summary>Optional content group [PDF:1.7:4.10.1].</summary>
  */
  [PDF(VersionEnum.PDF15)]
  public sealed class Layer
    : LayerEntity,
      IUILayerNode
  {
    #region types
    public enum PageElementTypeEnum
    {
      HeaderFooter,
      Foreground,
      Background,
      Logo
    }

    public enum UserTypeEnum
    {
      Individual,
      Title,
      Organization
    }

    /**
      <summary>Sublayers location within a configuration structure.</summary>
    */
    private class LayersLocation
    {
      /**
        <summary>Sublayers ordinal position within the parent sublayers.</summary>
      */
      public int Index;
      /**
        <summary>Parent layer object.</summary>
      */
      public PdfDirectObject ParentLayerObject;
      /**
        <summary>Parent sublayers object.</summary>
      */
      public PdfArray ParentLayersObject;
      /**
        <summary>Upper levels.</summary>
      */
      public Stack<Object[]> Levels;

      public LayersLocation(
        PdfDirectObject parentLayerObject,
        PdfArray parentLayersObject,
        int index,
        Stack<Object[]> levels
        )
      {
        ParentLayerObject = parentLayerObject;
        ParentLayersObject = parentLayersObject;
        Index = index;
        Levels = levels;
      }
    }

    /**
      <summary>Layer state.</summary>
    */
    internal enum StateEnum
    {
      /**
        <summary>Active.</summary>
      */
      On,
      /**
        <summary>Inactive.</summary>
      */
      Off
    }
    #endregion

    #region static
    #region fields
    public static readonly PdfName TypeName = PdfName.OCG;

    private static readonly PdfName MembershipName = new PdfName("D-OCMD");
    #endregion

    #region interface
    #region public
    public static new Layer Wrap(
      PdfDirectObject baseObject
      )
    {return baseObject != null ? new Layer(baseObject) : null;}
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public Layer(
      Document context,
      string title
      ) : base(context, PdfName.OCG)
    {
      Title = title;

      // Add this layer to the global collection!
      /*
        NOTE: Every layer MUST be included in the global collection [PDF:1.7:4.10.3].
      */
      context.Layer.Layers.BaseDataObject.Add(BaseObject);
    }

    private Layer(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets/Sets the type of content controlled by this layer.</summary>
    */
    public string ContentType
    {
      get
      {return (string)PdfSimpleObject<object>.GetValue(GetUsageEntry(PdfName.CreatorInfo)[PdfName.Subtype]);}
      set
      {GetUsageEntry(PdfName.CreatorInfo)[PdfName.Subtype] = PdfName.Get(value);}
    }

    /**
      <summary>Gets/Sets the name of the application that created this layer.</summary>
    */
    public string Creator
    {
      get
      {return (string)PdfSimpleObject<object>.GetValue(GetUsageEntry(PdfName.CreatorInfo)[PdfName.Creator]);}
      set
      {GetUsageEntry(PdfName.CreatorInfo)[PdfName.Creator] = PdfTextString.Get(value);}
    }

    /**
      <summary>Gets the dictionary used by the creating application to store application-specific
      data associated to this layer.</summary>
    */
    public PdfDictionary CreatorInfo
    {
      get
      {return GetUsageEntry(PdfName.CreatorInfo);}
    }

    /**
      <summary>Deletes this layer, removing also its references from the document (contents included).
      </summary>
    */
    public override bool Delete(
      )
    {return Delete(false);}

    /**
      <summary>Deletes this layer, removing also its references from the document.</summary>
      <param name="preserveContent">Whether its contents are to be excluded from the removal.</param>
    */
    public bool Delete(
      bool preserveContent
      )
    {
      if(Document.Layer.Layers.Contains(this))
      {
        var layerManager = new LayerManager();
        layerManager.Remove(preserveContent, this);
      }
      return base.Delete();
    }

    /**
      <summary>Gets/Sets whether this layer is visible when the document is saved by a viewer
      application to a format that does not support layers.</summary>
    */
    public bool? Exportable
    {
      get
      {
        var exportableObject = GetUsageEntry(PdfName.Export)[PdfName.ExportState];
        return exportableObject != null ? StateEnumExtension.Get((PdfName)exportableObject).IsEnabled() : (bool?)null;
      }
      set
      {
        GetUsageEntry(PdfName.Export)[PdfName.ExportState] = value.HasValue ? StateEnumExtension.Get(value.Value).GetName() : null;
        DefaultConfiguration.SetUsageApplication(PdfName.Export, PdfName.Export, this, value.HasValue);
      }
    }

    /**
      <summary>Gets/Sets the intended uses of this layer.</summary>
      <remarks>For example, many document design applications, such as CAD packages, offer layering
      features for collecting groups of graphics together and selectively hiding or viewing them for
      the convenience of the author. However, this layering may be different than would be useful to
      consumers of the document; therefore, it is possible to specify different intents for layers
      within a single document: a given application may decide to use only layers that are of a
      specific intent.</remarks>
      <returns>Intent collection (it comprises <see cref="IntentEnum"/> names but, for compatibility
      with future versions, unrecognized names are allowed). To apply any subsequent change, it has
      to be assigned back.</returns>
      <seealso cref="IntentEnum"/>
    */
    public ISet<PdfName> Intents
    {
      get
      {
        ISet<PdfName> intents = new HashSet<PdfName>();
        PdfDataObject intentObject = BaseDataObject.Resolve(PdfName.Intent);
        if(intentObject != null)
        {
          if(intentObject is PdfArray) // Multiple intents.
          {
            foreach(PdfDirectObject intentItem in (PdfArray)intentObject)
            {intents.Add((PdfName)intentItem);}
          }
          else // Single intent.
          {intents.Add((PdfName)intentObject);}
        }
        else
        {intents.Add(IntentEnum.View.Name());}
        return intents;
      }
      set
      {
        PdfDirectObject intentObject = null;
        if(value != null
          && value.Count > 0)
        {
          if(value.Count == 1) // Single intent.
          {
            intentObject = value.First();
            if(intentObject.Equals(IntentEnum.View.Name())) // Default.
            {intentObject = null;}
          }
          else // Multiple intents.
          {
            PdfArray intentArray = new PdfArray();
            foreach(PdfName valueItem in value)
            {intentArray.Add(valueItem);}
          }
        }
        BaseDataObject[PdfName.Intent] = intentObject;
      }
    }

    /**
      <summary>Gets/Sets the language of the content controlled by this layer.</summary>
      <remarks>The layer whose language matches the current system language is visible.</remarks>
    */
    public LanguageIdentifier Language
    {
      get
      {return LanguageIdentifier.Wrap(GetUsageEntry(PdfName.Language)[PdfName.Lang]);}
      set
      {
        GetUsageEntry(PdfName.Language)[PdfName.Lang] = PdfObjectWrapper.GetBaseObject(value);
        DefaultConfiguration.SetUsageApplication(PdfName.View, PdfName.Language, this, value != null);
        DefaultConfiguration.SetUsageApplication(PdfName.Print, PdfName.Language, this, value != null);
        DefaultConfiguration.SetUsageApplication(PdfName.Export, PdfName.Language, this, value != null);
      }
    }

    /**
      <summary>Gets/Sets whether a partial match (that is, the language matches but not the locale)
      with the current system language is enough to keep this layer visible.</summary>
    */
    public bool LanguagePreferred
    {
      get
      {return PdfName.ON.Equals(GetUsageEntry(PdfName.Language)[PdfName.Preferred]);}
      set
      {GetUsageEntry(PdfName.Language)[PdfName.Preferred] = value ? PdfName.ON : null;}
    }

    /**
      <summary>Gets/Sets whether the default visibility of this layer cannot be changed through the
      user interface of a viewer application.</summary>
    */
    public bool Locked
    {
      get
      {return DefaultConfiguration.BaseDataObject.Resolve<PdfArray>(PdfName.Locked).Contains(BaseObject);}
      set
      {
        PdfArray lockedArrayObject = DefaultConfiguration.BaseDataObject.Resolve<PdfArray>(PdfName.Locked);
        if(!lockedArrayObject.Contains(BaseObject))
        {lockedArrayObject.Add(BaseObject);}
      }
    }

    public override LayerEntity Membership
    {
      get
      {
        LayerEntity membership = LayerMembership.Wrap(BaseDataObject[MembershipName]);
        if(membership == null)
        {
          var location = FindLayersLocation();
          if(location == null || location.ParentLayerObject == null)
          {membership = this;}
          else
          {
            BaseDataObject[MembershipName] = (membership = new LayerMembership(Document)).BaseObject;
            membership.VisibilityPolicy = VisibilityPolicyEnum.AllOn; // NOTE: Forces visibility to depend on all the ascendant layers.
            membership.VisibilityMembers.Add(this);
            membership.VisibilityMembers.Add(new Layer(location.ParentLayerObject));
            foreach(Object[] level in location.Levels)
            {
              PdfDirectObject layerObject = (PdfDirectObject)level[2];
              if(layerObject != null)
              {membership.VisibilityMembers.Add(new Layer(layerObject));}
            }
          }
        }
        return membership;
      }
    }

    /**
      <summary>Gets/Sets the type of pagination artifact this layer contains.</summary>
    */
    public PageElementTypeEnum? PageElementType
    {
      get
      {return PageElementTypeEnumExtension.Get((PdfName)GetUsageEntry(PdfName.PageElement)[PdfName.Subtype]);}
      set
      {GetUsageEntry(PdfName.PageElement)[PdfName.Subtype] = value.HasValue ? value.Value.GetName() : null;}
    }

    /**
      <summary>Gets the parent layer.</summary>
    */
    public Layer Parent
    {
      get
      {
        var location = FindLayersLocation();
        return location != null ? Layer.Wrap(location.ParentLayerObject) : null;
      }
    }

    /**
      <summary>Gets/Sets whether this layer is visible when the document is printed from a viewer
      application.</summary>
    */
    public bool? Printable
    {
      get
      {
        var printableObject = GetUsageEntry(PdfName.Print)[PdfName.PrintState];
        return printableObject != null ? StateEnumExtension.Get((PdfName)printableObject).IsEnabled() : (bool?)null;
      }
      set
      {
        GetUsageEntry(PdfName.Print)[PdfName.PrintState] = value.HasValue ? StateEnumExtension.Get(value.Value).GetName() : null;
        DefaultConfiguration.SetUsageApplication(PdfName.Print, PdfName.Print, this, value.HasValue);
      }
    }

    /**
      <summary>Gets/Sets the type of printable content controlled by this layer.</summary>
    */
    public string PrintType
    {
      get
      {return (string)PdfSimpleObject<object>.GetValue(GetUsageEntry(PdfName.Print)[PdfName.Subtype]);}
      set
      {GetUsageEntry(PdfName.Print)[PdfName.Subtype] = PdfName.Get(value);}
    }

    public override string ToString(
      )
    {return "Layer {\"" + Title + "\" " + BaseObject + "}";}

    /**
      <summary>Gets/Sets the names of the users for whom this layer is primarily intended.</summary>
    */
    public IList<string> Users
    {
      get
      {
        var users = new List<string>();
        PdfDirectObject usersObject = GetUsageEntry(PdfName.User)[PdfName.Name];
        if(usersObject is PdfString)
        {users.Add(((PdfString)usersObject.Resolve()).StringValue);}
        else if(usersObject is PdfArray)
        {
          foreach(var userObject in (PdfArray)usersObject)
          {users.Add(((PdfString)userObject.Resolve()).StringValue);}
        }
        return users;
      }
      set
      {
        PdfDirectObject usersObject = null;
        if(value != null && value.Count > 0)
        {
          if(value.Count == 1)
          {usersObject = new PdfTextString(value[0]);}
          else
          {
            var usersArray = new PdfArray();
            foreach(var user in value)
            {usersArray.Add(new PdfTextString(user));}
            usersObject = usersArray;
          }
        }
        GetUsageEntry(PdfName.User)[PdfName.Name] = usersObject;
        DefaultConfiguration.SetUsageApplication(PdfName.View, PdfName.User, this, usersObject != null);
        DefaultConfiguration.SetUsageApplication(PdfName.Print, PdfName.User, this, usersObject != null);
        DefaultConfiguration.SetUsageApplication(PdfName.Export, PdfName.User, this, usersObject != null);
      }
    }

    /**
      <summary>Gets/Sets the type of the users for whom this layer is primarily intended.</summary>
    */
    public UserTypeEnum? UserType
    {
      get
      {return UserTypeEnumExtension.Get((PdfName)GetUsageEntry(PdfName.User)[PdfName.Type]);}
      set
      {GetUsageEntry(PdfName.User)[PdfName.Type] = value.HasValue ? value.Value.GetName() : null;}
    }

    /**
      <summary>Gets/Sets whether this layer is visible when the document is opened in a viewer
      application.</summary>
    */
    public bool? Viewable
    {
      get
      {
        var viewableObject = GetUsageEntry(PdfName.View)[PdfName.ViewState];
        return viewableObject != null ? StateEnumExtension.Get((PdfName)viewableObject).IsEnabled() : (bool?)null;
      }
      set
      {
        GetUsageEntry(PdfName.View)[PdfName.ViewState] = value.HasValue ? StateEnumExtension.Get(value.Value).GetName() : null;
        DefaultConfiguration.SetUsageApplication(PdfName.View, PdfName.View, this, value.HasValue);
      }
    }

    /**
      <remarks>Default membership's <see cref="LayerMembership.VisibilityExpression">
      VisibilityExpression</see> is undefined as <see cref="VisibilityMembers"/> is used instead.
      </remarks>
    */
    public override VisibilityExpression VisibilityExpression
    {
      get
      {return null;}
      set
      {throw new NotSupportedException();}
    }

    /**
      <remarks>Default membership's <see cref="LayerMembership.VisibilityMembers">VisibilityMembers
      </see> collection is immutable as it's expected to represent the hierarchical line of this
      layer.</remarks>
    */
    public override IList<Layer> VisibilityMembers
    {
      get
      {
        var membership = Membership;
        return membership == this ? new List<Layer>{this}.AsReadOnly() : new ReadOnlyCollection<Layer>(membership.VisibilityMembers);
      }
      set
      {throw new NotSupportedException();}
    }

    public override VisibilityPolicyEnum VisibilityPolicy
    {
      get
      {
        var membership = Membership;
        return membership == this ? VisibilityPolicyEnum.AllOn : membership.VisibilityPolicy;
      }
      set
      {
        var membership = Membership;
        if(membership != this)
        {membership.VisibilityPolicy = value;}
      }
    }

    /**
      <summary>Gets/Sets whether this layer is initially visible in any kind of application.</summary>
    */
    public bool Visible
    {
      get
      {return DefaultConfiguration.IsVisible(this);}
      set
      {DefaultConfiguration.SetVisible(this, value);}
    }

    /**
      <summary>Gets/Sets the range of magnifications at which the content in this layer is best
      viewed in a viewer application.</summary>
      <returns>Zoom interval (minimum included, maximum excluded); valid values range from 0 to
      <code>double.PositiveInfinity</code>, where 1 corresponds to 100% magnification.</returns>
    */
    public Interval<double> ZoomRange
    {
      get
      {
        PdfDictionary zoomDictionary = GetUsageEntry(PdfName.Zoom);
        IPdfNumber minObject = (IPdfNumber)zoomDictionary.Resolve(PdfName.min);
        IPdfNumber maxObject = (IPdfNumber)zoomDictionary.Resolve(PdfName.max);
        return new Interval<double>(
          minObject != null ? minObject.RawValue : 0,
          maxObject != null ? maxObject.RawValue : double.PositiveInfinity
          );
      }
      set
      {
        if(value != null)
        {
          PdfDictionary zoomDictionary = GetUsageEntry(PdfName.Zoom);
          zoomDictionary[PdfName.min] = (value.Low != 0 ? PdfReal.Get(value.Low) : null);
          zoomDictionary[PdfName.max] = (value.High != double.PositiveInfinity ? PdfReal.Get(value.High) : null);
        }
        else
        {Usage.Remove(PdfName.Zoom);}
        DefaultConfiguration.SetUsageApplication(PdfName.View, PdfName.Zoom, this, value != null);
      }
    }

    #region IUILayerNode
    public UILayers Children
    {
      get
      {
        var location = FindLayersLocation();
        return location != null ? UILayers.Wrap(location.ParentLayersObject.Get<PdfArray>(location.Index)) : null;
      }
    }
    
    public string Title
    {
      get
      {return (string)((PdfTextString)BaseDataObject[PdfName.Name]).Value;}
      set
      {BaseDataObject[PdfName.Name] = new PdfTextString(value);}
    }
    #endregion
    #endregion

    #region private
    private LayerConfiguration DefaultConfiguration
    {
      get
      {return Document.Layer.DefaultConfiguration;}
    }

    /**
      <summary>Finds the location of the sublayers object in the default configuration; in case no
      sublayers object is associated to this object, its virtual position is indicated.</summary>
    */
    private LayersLocation FindLayersLocation(
      )
    {return FindLayersLocation(DefaultConfiguration);}

    /**
      <summary>Finds the location of the sublayers object in the specified configuration; in case no
      sublayers object is associated to this object, its virtual position is indicated.</summary>
      <param name="configuration">Configuration context.</param>
      <returns><code>null</code>, if this layer is outside the specified configuration.</returns>
    */
    private LayersLocation FindLayersLocation(
      LayerConfiguration configuration
      )
    {
      /*
        NOTE: As layers are only weakly tied to configurations, their sublayers have to be sought
        through the configuration structure tree.
      */
      PdfDirectObject levelLayerObject = null;
      PdfArray levelObject = configuration.UILayers.BaseDataObject;
      IEnumerator<PdfDirectObject> levelIterator = levelObject.GetEnumerator();
      Stack<object[]> levelIterators = new Stack<object[]>();
      PdfDirectObject thisObject = BaseObject;
      PdfDirectObject currentLayerObject = null;
      while(true)
      {
        if(!levelIterator.MoveNext())
        {
          if(levelIterators.Count == 0)
            break;

          object[] levelItems = levelIterators.Pop();
          levelObject = (PdfArray)levelItems[0];
          levelIterator = (IEnumerator<PdfDirectObject>)levelItems[1];
          levelLayerObject = (PdfDirectObject)levelItems[2];
          currentLayerObject = null;
        }
        else
        {
          PdfDirectObject nodeObject = levelIterator.Current;
          PdfDataObject nodeDataObject = PdfObject.Resolve(nodeObject);
          if(nodeDataObject is PdfDictionary)
          {
            if(nodeObject.Equals(thisObject))
              /*
                NOTE: Sublayers are expressed as an array immediately following the parent layer node.
              */
              return new LayersLocation(levelLayerObject, levelObject, levelObject.IndexOf(thisObject) + 1, levelIterators);
  
            currentLayerObject = nodeObject;
          }
          else if(nodeDataObject is PdfArray)
          {
            levelIterators.Push(new object[]{levelObject, levelIterator, levelLayerObject});
            levelObject = (PdfArray)nodeDataObject;
            levelIterator = levelObject.GetEnumerator();
            levelLayerObject = currentLayerObject;
            currentLayerObject = null;
          }
        }
      }
      return null;
    }

    private PdfDictionary GetUsageEntry(
      PdfName key
      )
    {return Usage.Resolve<PdfDictionary>(key);}

    private PdfDictionary Usage
    {
      get
      {return BaseDataObject.Resolve<PdfDictionary>(PdfName.Usage);}
    }
    #endregion
    #endregion
    #endregion
  }

  internal static class PageElementTypeEnumExtension
  {
    private static readonly BiDictionary<Layer.PageElementTypeEnum,PdfName> codes;

    static PageElementTypeEnumExtension()
    {
      codes = new BiDictionary<Layer.PageElementTypeEnum,PdfName>();
      codes[Layer.PageElementTypeEnum.Background] = PdfName.BG;
      codes[Layer.PageElementTypeEnum.Foreground] = PdfName.FG;
      codes[Layer.PageElementTypeEnum.HeaderFooter] = PdfName.HF;
      codes[Layer.PageElementTypeEnum.Logo] = PdfName.L;
    }

    public static Layer.PageElementTypeEnum? Get(
      PdfName name
      )
    {
      if(name == null)
        return null;

      Layer.PageElementTypeEnum? pageElementType = codes.GetKey(name);
      if(!pageElementType.HasValue)
        throw new NotSupportedException("Page element type unknown: " + name);

      return pageElementType;
    }

    public static PdfName GetName(
      this Layer.PageElementTypeEnum pageElementType
      )
    {return codes[pageElementType];}
  }

  internal static class StateEnumExtension
  {
    private static readonly BiDictionary<Layer.StateEnum,PdfName> codes;

    static StateEnumExtension()
    {
      codes = new BiDictionary<Layer.StateEnum,PdfName>();
      codes[Layer.StateEnum.On] = PdfName.ON;
      codes[Layer.StateEnum.Off] = PdfName.OFF;
    }

    public static Layer.StateEnum Get(
      PdfName name
      )
    {
      if(name == null)
        return Layer.StateEnum.On;

      Layer.StateEnum? state = codes.GetKey(name);
      if(!state.HasValue)
        throw new NotSupportedException("State unknown: " + name);

      return state.Value;
    }

    public static Layer.StateEnum Get(
      bool? enabled
      )
    {return !enabled.HasValue || enabled.Value ? Layer.StateEnum.On : Layer.StateEnum.Off;}

    public static PdfName GetName(
      this Layer.StateEnum state
      )
    {return codes[state];}

    public static bool IsEnabled(
      this Layer.StateEnum state
      )
    {return state == Layer.StateEnum.On;}
  }

  internal static class UserTypeEnumExtension
  {
    private static readonly BiDictionary<Layer.UserTypeEnum,PdfName> codes;

    static UserTypeEnumExtension()
    {
      codes = new BiDictionary<Layer.UserTypeEnum,PdfName>();
      codes[Layer.UserTypeEnum.Individual] = PdfName.Ind;
      codes[Layer.UserTypeEnum.Organization] = PdfName.Org;
      codes[Layer.UserTypeEnum.Title] = PdfName.Ttl;
    }

    public static Layer.UserTypeEnum? Get(
      PdfName name
      )
    {
      if(name == null)
        return null;

      Layer.UserTypeEnum? userType = codes.GetKey(name);
      if(!userType.HasValue)
        throw new NotSupportedException("User type unknown: " + name);

      return userType;
    }

    public static PdfName GetName(
      this Layer.UserTypeEnum userType
      )
    {return codes[userType];}
  }
}
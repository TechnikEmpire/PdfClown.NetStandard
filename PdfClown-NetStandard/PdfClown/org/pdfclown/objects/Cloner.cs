/*
  Copyright 2012-2013 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown.bytes;
using org.pdfclown.documents;
using org.pdfclown.documents.interaction.forms;
using org.pdfclown.documents.interaction.navigation.document;
using org.pdfclown.files;
using org.pdfclown.tokens;

using System;
using System.Collections.Generic;

namespace org.pdfclown.objects
{
  /**
    <summary>Object cloner.</summary>
  */
  public class Cloner
    : Visitor
  {
    #region types
    public class Filter
    {
      private readonly String name;

      public Filter(
        String name
        )
      {this.name = name;}

      /**
        <summary>Notifies a complete clone operation on an object.</summary>
        <param name="cloner">Object cloner.</param>
        <param name="source">Source object.</param>
        <param name="clone">Clone object.</param>
      */
      public virtual void AfterClone(
        Cloner cloner,
        PdfObject source,
        PdfObject clone
        )
      {/* NOOP */}

      /**
        <summary>Notifies a complete clone operation on a dictionary entry.</summary>
        <param name="cloner">Object cloner.</param>
        <param name="source">Parent source object.</param>
        <param name="clone">Parent clone object.</param>
        <param name="key">Entry key within the parent.</param>
        <param name="value">Clone value.</param>
      */
      public virtual void AfterClone(
        Cloner cloner,
        PdfDictionary source,
        PdfDictionary clone,
        PdfName key,
        PdfDirectObject value
        )
      {/* NOOP */}

      /**
        <summary>Notifies a complete clone operation on an array item.</summary>
        <param name="cloner">Object cloner.</param>
        <param name="source">Parent source object.</param>
        <param name="clone">Parent clone object.</param>
        <param name="index">Item index within the parent.</param>
        <param name="item">Clone item.</param>
      */
      public virtual void AfterClone(
        Cloner cloner,
        PdfArray source,
        PdfArray clone,
        int index,
        PdfDirectObject item
        )
      {/* NOOP */}

      /**
        <summary>Notifies a starting clone operation on a dictionary entry.</summary>
        <param name="cloner">Object cloner.</param>
        <param name="source">Parent source object.</param>
        <param name="clone">Parent clone object.</param>
        <param name="key">Entry key within the parent.</param>
        <param name="value">Source value.</param>
        <returns>Whether the clone operation can be fulfilled.</returns>
      */
      public virtual bool BeforeClone(
        Cloner cloner,
        PdfDictionary source,
        PdfDictionary clone,
        PdfName key,
        PdfDirectObject value
        )
      {return true;}

      /**
        <summary>Notifies a starting clone operation on an array item.</summary>
        <param name="cloner">Object cloner.</param>
        <param name="source">Parent source object.</param>
        <param name="clone">Parent clone object.</param>
        <param name="index">Item index within the parent.</param>
        <param name="item">Source item.</param>
        <returns>Whether the clone operation can be fulfilled.</returns>
      */
      public virtual bool BeforeClone(
        Cloner cloner,
        PdfArray source,
        PdfArray clone,
        int index,
        PdfDirectObject item
        )
      {return true;}

      /**
        <summary>Gets whether this filter can deal with the given object.</summary>
        <param name="cloner">Object cloner.</param>
        <param name="source">Source object.</param>
      */
      public virtual bool Matches(
        Cloner cloner,
        PdfObject source
        )
      {return true;}

      public string Name
      {
        get
        {return name;}
      }

      protected void CloneNamedObject<T>(
        Cloner cloner,
        PdfDirectObject source,
        PdfString name
        ) where T : PdfObjectWrapper
      {
        // Resolve the named object source!
        T namedObjectSource = source.File.Document.ResolveName<T>(name);
        if(namedObjectSource == null)
          return;

        // Clone the named object source into the target document!
        cloner.context.Document.Register(name, (T)namedObjectSource.Clone(cloner));
      }
    }

    private class ActionFilter
      : Filter
    {
      public ActionFilter(
        ) : base("Action")
      {}

      public override void AfterClone(
        Cloner cloner,
        PdfDictionary source,
        PdfDictionary clone,
        PdfName key,
        PdfDirectObject value
        )
      {
        if(PdfName.D.Equals(key))
        {
          PdfDirectObject destObject = clone[PdfName.D];
          if(destObject is PdfString) // Named destination.
          {CloneNamedObject<Destination>(cloner, source, (PdfString)destObject);}
        }
      }

      public override bool Matches(
        Cloner cloner,
        PdfObject source
        )
      {
        if(source is PdfDictionary)
        {
          PdfDictionary dictionary = (PdfDictionary)source;
          return dictionary.ContainsKey(PdfName.S)
            && (!dictionary.ContainsKey(PdfName.Type) || PdfName.Action.Equals(dictionary[PdfName.Type]));
        }
        return false;
      }
    }

    private class AnnotationsFilter
      : Filter
    {
      public AnnotationsFilter(
        ) : base("Annots")
      {}

      public override void AfterClone(
        Cloner cloner,
        PdfArray source,
        PdfArray clone,
        int index,
        PdfDirectObject item
        )
      {
        PdfDictionary annotation = (PdfDictionary)item.Resolve();
        if(annotation.ContainsKey(PdfName.FT))
        {cloner.context.Document.Form.Fields.Add(Field.Wrap(annotation.Reference));}
        else if(annotation.ContainsKey(PdfName.Dest))
        {
          PdfDirectObject destObject = annotation[PdfName.Dest];
          if(destObject is PdfString) // Named destination.
          {CloneNamedObject<Destination>(cloner, source, (PdfString)destObject);}
        }
      }

      public override bool Matches(
        Cloner cloner,
        PdfObject source
        )
      {
        if(source is PdfArray)
        {
          PdfArray array = (PdfArray)source;
          if(array.Count > 0)
          {
            PdfDataObject arrayItem = array.Resolve(0);
            if(arrayItem is PdfDictionary)
            {
              PdfDictionary arrayItemDictionary = (PdfDictionary)arrayItem;
              return arrayItemDictionary.ContainsKey(PdfName.Subtype)
                && arrayItemDictionary.ContainsKey(PdfName.Rect);
            }
          }
        }
        return false;
      }
    }

    private class PageFilter
      : Filter
    {
      public PageFilter(
        ) : base("Page")
      {}

      public override void AfterClone(
        Cloner cloner,
        PdfObject source,
        PdfObject clone
        )
      {
        /*
          NOTE: Inheritable attributes have to be consolidated into the cloned page dictionary in
          order to ensure its consistency.
        */
        PdfDictionary cloneDictionary = (PdfDictionary)clone;
        PdfDictionary sourceDictionary = (PdfDictionary)source;
        foreach(PdfName key in Page.InheritableAttributeKeys)
        {
          if(!sourceDictionary.ContainsKey(key))
          {
            PdfDirectObject sourceValue = Page.GetInheritableAttribute(sourceDictionary, key);
            if(sourceValue != null)
            {cloneDictionary[key] = (PdfDirectObject)sourceValue.Accept(cloner, null);}
          }
        }
      }

      public override bool BeforeClone(
        Cloner cloner,
        PdfDictionary source,
        PdfDictionary clone,
        PdfName key,
        PdfDirectObject value
        )
      {return !PdfName.Parent.Equals(key);}

      public override bool Matches(
        Cloner cloner,
        PdfObject source
        )
      {
        return source is PdfDictionary
          && PdfName.Page.Equals(((PdfDictionary)source)[PdfName.Type]);
      }
    }
    #endregion

    #region static
    #region fields
    private static readonly Filter NullFilter = new Filter("Default");

    private static IList<Filter> commonFilters = new List<Filter>();
    #endregion

    #region constructors
    static Cloner()
    {
      // Page object.
      commonFilters.Add(new PageFilter());
      // Actions.
      commonFilters.Add(new ActionFilter());
      // Annotations.
      commonFilters.Add(new AnnotationsFilter());
    }
    #endregion
    #endregion

    #region dynamic
    #region fields
    private File context;
    private readonly IList<Filter> filters = new List<Filter>(commonFilters);
    #endregion

    #region constructors
    public Cloner(
      File context
      )
    {Context = context;}
    #endregion

    #region interface
    #region public
    public File Context
    {
      get
      {return context;}
      set
      {
        if(value == null)
          throw new ArgumentException("value required");

        context = value;
      }
    }

    public IList<Filter> Filters
    {
      get
      {return filters;}
    }

    public override PdfObject Visit(
      ObjectStream obj,
      object data
      )
    {throw new NotSupportedException();}

    public override PdfObject Visit(
      PdfArray obj,
      object data
      )
    {
      Filter cloneFilter = MatchFilter(obj);
      PdfArray clone = (PdfArray)obj.Clone();
      {
        clone.items = new List<PdfDirectObject>();
        IList<PdfDirectObject> sourceItems = obj.items;
        for(int index = 0, length = sourceItems.Count; index < length; index++)
        {
          PdfDirectObject sourceItem = sourceItems[index];
          if(cloneFilter.BeforeClone(this, obj, clone, index, sourceItem))
          {
            PdfDirectObject cloneItem;
            clone.Add(cloneItem = (PdfDirectObject)(sourceItem != null ? sourceItem.Accept(this, null) : null));
            cloneFilter.AfterClone(this, obj, clone, index, cloneItem);
          }
        }
      }
      cloneFilter.AfterClone(this, obj, clone);
      return clone;
    }

    public override PdfObject Visit(
      PdfDictionary obj,
      object data
      )
    {
      Filter cloneFilter = MatchFilter(obj);
      PdfDictionary clone = (PdfDictionary)obj.Clone();
      {
        clone.entries = new Dictionary<PdfName,PdfDirectObject>();
        foreach(KeyValuePair<PdfName,PdfDirectObject> entry in obj.entries)
        {
          PdfDirectObject sourceValue = entry.Value;
          if(cloneFilter.BeforeClone(this, obj, clone, entry.Key, sourceValue))
          {
            PdfDirectObject cloneValue;
            clone[entry.Key] = cloneValue = (PdfDirectObject)(sourceValue != null ? sourceValue.Accept(this, null) : null);
            cloneFilter.AfterClone(this, obj, clone, entry.Key, cloneValue);
          }
        }
      }
      cloneFilter.AfterClone(this, obj, clone);
      return clone;
    }

    public override PdfObject Visit(
      PdfIndirectObject obj,
      object data
      )
    {return context.IndirectObjects.AddExternal(obj, this);}

    public override PdfObject Visit(
      PdfReference obj,
      object data
      )
    {
      return context == obj.File
        ? (PdfReference)obj.Clone() // Local clone.
        : Visit(obj.IndirectObject, data).Reference; // Alien clone.
    }

    public override PdfObject Visit(
      PdfStream obj,
      object data
      )
    {
      PdfStream clone = (PdfStream)obj.Clone();
      {
        clone.header = (PdfDictionary)Visit(obj.header, data);
        clone.body = obj.body.Clone();
      }
      return clone;
    }

    public override PdfObject Visit(
      XRefStream obj,
      object data
      )
    {throw new NotSupportedException();}
    #endregion

    #region private
    private Filter MatchFilter(
      PdfObject obj
      )
    {
      Filter cloneFilter = NullFilter;
      foreach(Filter filter in filters)
      {
        if(filter.Matches(this, obj))
        {
          cloneFilter = filter;
          break;
        }
      }
      return cloneFilter;
    }
    #endregion
    #endregion
    #endregion
  }
}
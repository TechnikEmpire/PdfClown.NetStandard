/*
  Copyright 2006-2015 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.layers;
using org.pdfclown.documents.interaction.forms;
using org.pdfclown.documents.interaction.navigation.document;
using org.pdfclown.documents.interchange.metadata;
using org.pdfclown.documents.interaction.viewer;
using org.pdfclown.files;
using org.pdfclown.objects;
using org.pdfclown.tokens;
using org.pdfclown.util;
using org.pdfclown.util.io;

using System;
using System.Collections.Generic;
using drawing = System.Drawing;
using System.Drawing.Printing;
using io = System.IO;
using System.Reflection;
using System.Text.RegularExpressions;

namespace org.pdfclown.documents
{
  /**
    <summary>PDF document [PDF:1.6::3.6.1].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public sealed class Document
    : PdfObjectWrapper<PdfDictionary>,
      IAppDataHolder
  {
    #region static
    #region interface
    #region public
    public static T Resolve<T>(
      PdfDirectObject baseObject
      ) where T : PdfObjectWrapper
    {
      if(typeof(Destination).IsAssignableFrom(typeof(T)))
        return Destination.Wrap(baseObject) as T;
      else
        throw new NotSupportedException("Type '" + typeof(T).Name + "' wrapping is not supported.");
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region fields
    internal Dictionary<PdfReference,object> Cache = new Dictionary<PdfReference,object>();

    private DocumentConfiguration configuration;
    #endregion

    #region constructors
    internal Document(
      File context
      ) : base(
        context,
        new PdfDictionary(
          new PdfName[1]{PdfName.Type},
          new PdfDirectObject[1]{PdfName.Catalog}
          )
        )
    {
      configuration = new DocumentConfiguration(this);

      // Attach the document catalog to the file trailer!
      context.Trailer[PdfName.Root] = BaseObject;

      // Pages collection.
      this.Pages = new Pages(this);

      // Default page size.
      PageSize = PageFormat.GetSize();

      // Default resources collection.
      Resources = new Resources(this);
    }

    internal Document(
      PdfDirectObject baseObject // Catalog.
      ) : base(baseObject)
    {configuration = new DocumentConfiguration(this);}
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets/Sets the document's behavior in response to trigger events.</summary>
    */
    [PDF(VersionEnum.PDF14)]
    public DocumentActions Actions
    {
      get
      {return new DocumentActions(BaseDataObject.Get<PdfDictionary>(PdfName.AA));}
      set
      {BaseDataObject[PdfName.AA] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets the article threads.</summary>
    */
    [PDF(VersionEnum.PDF11)]
    public Articles Articles
    {
      get
      {return Articles.Wrap(BaseDataObject.Get<PdfArray>(PdfName.Threads, false));}
      set
      {BaseDataObject[PdfName.Threads] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets/Sets the bookmark collection.</summary>
    */
    public Bookmarks Bookmarks
    {
      get
      {return Bookmarks.Wrap(BaseDataObject.Get<PdfDictionary>(PdfName.Outlines, false));}
      set
      {BaseDataObject[PdfName.Outlines] = PdfObjectWrapper.GetBaseObject(value);}
    }

    public override object Clone(
      Document context
      )
    {throw new NotImplementedException();}

    /**
      <summary>Gets/Sets the configuration of this document.</summary>
    */
    public DocumentConfiguration Configuration
    {
      get
      {return configuration;}
      set
      {configuration = value;}
    }

    /**
      <summary>Deletes the object from this document context.</summary>
    */
    public void Exclude(
      PdfObjectWrapper obj
      )
    {
      if(obj.File != File)
        return;

      obj.Delete();
    }

    /**
      <summary>Deletes the objects from this document context.</summary>
    */
    public void Exclude<T>(
      ICollection<T> objs
      ) where T : PdfObjectWrapper
    {
      foreach(T obj in objs)
      {Exclude(obj);}
    }

    /**
      <summary>Gets/Sets the interactive form (AcroForm).</summary>
    */
    [PDF(VersionEnum.PDF12)]
    public Form Form
    {
      get
      {return Form.Wrap(BaseDataObject.Get<PdfDictionary>(PdfName.AcroForm));}
      set
      {BaseDataObject[PdfName.AcroForm] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets the document size, that is the maximum page dimensions across the whole document.
      </summary>
      <seealso cref="PageSize"/>
    */
    public drawing::SizeF GetSize(
      )
    {
      float height = 0, width = 0;
      foreach(Page page in Pages)
      {
        drawing::SizeF pageSize = page.Size;
        height = Math.Max(height,pageSize.Height);
        width = Math.Max(width,pageSize.Width);
      }
      return new drawing::SizeF(width,height);
    }

    /**
      <summary>Clones the object within this document context.</summary>
    */
    public PdfObjectWrapper Include(
      PdfObjectWrapper obj
      )
    {
      if(obj.File == File)
        return obj;

      return (PdfObjectWrapper)obj.Clone(this);
    }

    /**
      <summary>Clones the collection objects within this document context.</summary>
    */
    public ICollection<T> Include<T>(
      ICollection<T> objs
      ) where T : PdfObjectWrapper
    {
      List<T> includedObjects = new List<T>(objs.Count);
      foreach(T obj in objs)
      {includedObjects.Add((T)Include(obj));}

      return (ICollection<T>)includedObjects;
    }

    /**
      <summary>Gets/Sets common document metadata.</summary>
    */
    public Information Information
    {
      get
      {return Information.Wrap(File.Trailer.Get<PdfDictionary>(PdfName.Info, false));}
      set
      {File.Trailer[PdfName.Info] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets/Sets the optional content properties.</summary>
    */
    [PDF(VersionEnum.PDF15)]
    public LayerDefinition Layer
    {
      get
      {return LayerDefinition.Wrap(BaseDataObject.Get<PdfDictionary>(PdfName.OCProperties));}
      set
      {
        CheckCompatibility("Layer");
        BaseDataObject[PdfName.OCProperties] = PdfObjectWrapper.GetBaseObject(value);
      }
    }

    /**
      <summary>Gets/Sets the name dictionary.</summary>
    */
    [PDF(VersionEnum.PDF12)]
    public Names Names
    {
      get
      {return new Names(BaseDataObject.Get<PdfDictionary>(PdfName.Names));}
      set
      {BaseDataObject[PdfName.Names] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets/Sets the page label ranges.</summary>
    */
    [PDF(VersionEnum.PDF13)]
    public PageLabels PageLabels
    {
      get
      {return new PageLabels(BaseDataObject.Get<PdfDictionary>(PdfName.PageLabels));}
      set
      {
        CheckCompatibility("PageLabels");
        BaseDataObject[PdfName.PageLabels] = PdfObjectWrapper.GetBaseObject(value);
      }
    }

    /**
      <summary>Gets/Sets the page collection.</summary>
    */
    public Pages Pages
    {
      get
      {return new Pages(BaseDataObject[PdfName.Pages]);}
      set
      {BaseDataObject[PdfName.Pages] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets/Sets the default page size [PDF:1.6:3.6.2].</summary>
    */
    public drawing::Size? PageSize
    {
      get
      {
        PdfArray mediaBox = MediaBox;
        return mediaBox != null
          ? new drawing::Size(
            (int)((IPdfNumber)mediaBox[2]).RawValue,
            (int)((IPdfNumber)mediaBox[3]).RawValue
            )
          : (drawing::Size?)null;
      }
      set
      {
        PdfArray mediaBox = MediaBox;
        if(mediaBox == null)
        {
          // Create default media box!
          mediaBox = new org.pdfclown.objects.Rectangle(0,0,0,0).BaseDataObject;
          // Assign the media box to the document!
          ((PdfDictionary)BaseDataObject.Resolve(PdfName.Pages))[PdfName.MediaBox] = mediaBox;
        }
        mediaBox[2] = PdfReal.Get(value.Value.Width);
        mediaBox[3] = PdfReal.Get(value.Value.Height);
      }
    }

    /**
      <summary>Registers a named object.</summary>
      <param name="name">Object name.</param>
      <param name="object">Named object.</param>
      <returns>Registered named object.</returns>
    */
    public T Register<T>(
      PdfString name,
      T @object
      ) where T : PdfObjectWrapper
    {
      PdfObjectWrapper namedObjects = Names.Get(@object.GetType());
      namedObjects.GetType().GetMethod("set_Item", BindingFlags.Public | BindingFlags.Instance).Invoke(namedObjects, new object[]{ name, @object });
      return @object;
    }

    /**
      <summary>Forces a named base object to be expressed as its corresponding high-level
      representation.</summary>
    */
    public T ResolveName<T>(
      PdfDirectObject namedBaseObject
      ) where T : PdfObjectWrapper
    {
      if(namedBaseObject is PdfString) // Named object.
        return Names.Get<T>((PdfString)namedBaseObject);
      else // Explicit object.
        return Resolve<T>(namedBaseObject);
    }

    /**
      <summary>Gets/Sets the default resource collection [PDF:1.6:3.6.2].</summary>
      <remarks>The default resource collection is used as last resort by every page that doesn't
      reference one explicitly (and doesn't reference an intermediate one implicitly).</remarks>
    */
    public Resources Resources
    {
      get
      {return Resources.Wrap(((PdfDictionary)BaseDataObject.Resolve(PdfName.Pages)).Get<PdfDictionary>(PdfName.Resources));}
      set
      {((PdfDictionary)BaseDataObject.Resolve(PdfName.Pages))[PdfName.Resources] = PdfObjectWrapper.GetBaseObject(value);}
    }

    /**
      <summary>Gets/Sets the version of the PDF specification this document conforms to.</summary>
    */
    [PDF(VersionEnum.PDF14)]
    public Version Version
    {
      get
      {
        /*
          NOTE: If the header specifies a later version, or if this entry is absent, the document
          conforms to the version specified in the header.
        */
        Version fileVersion = File.Version;

        PdfName versionObject = (PdfName)BaseDataObject[PdfName.Version];
        if(versionObject == null)
          return fileVersion;

        Version version = Version.Get(versionObject);
        if(File.Reader == null)
          return version;

        return (version.CompareTo(fileVersion) > 0 ? version : fileVersion);
      }
      set
      {BaseDataObject[PdfName.Version] = PdfName.Get(value);}
    }

    /**
      <summary>Gets the way the document is to be presented.</summary>
    */
    public ViewerPreferences ViewerPreferences
    {
      get
      {return ViewerPreferences.Wrap(BaseDataObject.Get<PdfDictionary>(PdfName.ViewerPreferences));}
      set
      {BaseDataObject[PdfName.ViewerPreferences] = PdfObjectWrapper.GetBaseObject(value);}
    }

    #region IAppDataHolder
    public AppDataCollection AppData
    {
      get
      {return AppDataCollection.Wrap(BaseDataObject.Get<PdfDictionary>(PdfName.PieceInfo), this);}
    }

    public AppData GetAppData(
      PdfName appName
      )
    {return AppData.Ensure(appName);}

    public DateTime? ModificationDate
    {
      get
      {return Information.ModificationDate;}
    }

    public void Touch(
      PdfName appName
      )
    {Touch(appName, DateTime.Now);}

    public void Touch(
      PdfName appName,
      DateTime modificationDate
      )
    {
      GetAppData(appName).ModificationDate = modificationDate;
      Information.ModificationDate = modificationDate;
    }
    #endregion
    #endregion

    #region private
    /**
      <summary>Gets the default media box.</summary>
    */
    private PdfArray MediaBox
    {
      get
      {
        /*
          NOTE: Document media box MUST be associated with the page-tree root node in order to be
          inheritable by all the pages.
        */
        return (PdfArray)((PdfDictionary)BaseDataObject.Resolve(PdfName.Pages)).Resolve(PdfName.MediaBox);
      }
    }
    #endregion
    #endregion
    #endregion
  }
}
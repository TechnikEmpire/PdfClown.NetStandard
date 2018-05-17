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

using org.pdfclown.documents;
using org.pdfclown.files;
using org.pdfclown.objects;
using org.pdfclown.util;
using org.pdfclown.util.math;

using System;
using System.Collections.Generic;

namespace org.pdfclown.documents.interaction.viewer
{
  /**
    <summary>Viewer preferences [PDF:1.7:8.1].</summary>
  */
  [PDF(VersionEnum.PDF12)]
  public sealed class ViewerPreferences
    : PdfObjectWrapper<PdfDictionary>
  {
    #region types
    /**
      <summary>Predominant reading order for text [PDF:1.7:8.1].</summary>
    */
    [PDF(VersionEnum.PDF13)]
    public enum DirectionEnum
    {
      /**
        <summary>Left to right.</summary>
      */
      LeftToRight,
      /**
        <summary>Right to left (including vertical writing systems, such as Chinese, Japanese, and
        Korean).</summary>
      */
      RightToLeft
    };

    /**
      <summary>Page layout to be used when the document is opened [PDF:1.7:3.6.1].</summary>
    */
    [PDF(VersionEnum.PDF10)]
    public enum PageLayoutEnum
    {
      /**
        <summary>Displays one page at a time.</summary>
      */
      SinglePage,
      /**
        <summary>Displays the pages in one column.</summary>
      */
      OneColumn,
      /**
        <summary>Displays the pages in two columns, with odd-numbered pages on the left.</summary>
      */
      TwoColumnLeft,
      /**
        <summary>Displays the pages in two columns, with odd-numbered pages on the right.</summary>
      */
      TwoColumnRight,
      /**
        <summary>Displays the pages two at a time, with odd-numbered pages on the left.</summary>
      */
      [PDF(VersionEnum.PDF15)]
      TwoPageLeft,
      /**
        <summary>Displays the pages two at a time, with odd-numbered pages on the right.</summary>
      */
      [PDF(VersionEnum.PDF15)]
      TwoPageRight
    };

    /**
      <summary>Page mode specifying how the document should be displayed when opened [PDF:1.7:3.6.1].
      </summary>
    */
    [PDF(VersionEnum.PDF10)]
    public enum PageModeEnum
    {
      /**
        <summary>Neither document outline nor thumbnail images visible.</summary>
      */
      Simple,
      /**
        <summary>Document outline visible.</summary>
      */
      Bookmarks,
      /**
        <summary>Thumbnail images visible.</summary>
      */
      Thumbnails,
      /**
        <summary>Full-screen mode, with no menu bar, window controls, or any other window visible.
        </summary>
      */
      FullScreen,
      /**
        <summary>Optional content group panel visible.</summary>
      */
      [PDF(VersionEnum.PDF15)]
      Layers,
      /**
        <summary>Attachments panel visible.</summary>
      */
      [PDF(VersionEnum.PDF16)]
      Attachments
    };

    /**
      <summary>Paper handling option to use when printing the file from the print dialog
      [PDF:1.7:8.1].</summary>
    */
    [PDF(VersionEnum.PDF17)]
    public enum PaperModeEnum
    {
      /**
        <summary>Print single-sided.</summary>
      */
      Simplex,
      /**
        <summary>Duplex and flip on the short edge of the sheet.</summary>
      */
      DuplexShortEdge,
      /**
        <summary>Duplex and flip on the long edge of the sheet.</summary>
      */
      DuplexLongEdge
    };
    #endregion

    #region static
    #region fields
    private static readonly DirectionEnum DefaultDirection = DirectionEnum.LeftToRight;
    private static readonly bool DefaultFlag = false;
    private static readonly PageLayoutEnum DefaultPageLayout = PageLayoutEnum.SinglePage;
    private static readonly PageModeEnum DefaultPageMode = PageModeEnum.Simple;
    private static readonly int DefaultPrintCount = 1;
    private static readonly PdfName DefaultPrintScaledObject = PdfName.AppDefault;
    #endregion

    #region interface
    #region public
    public static ViewerPreferences Wrap(
      PdfDirectObject baseObject
      )
    {return baseObject != null ? new ViewerPreferences(baseObject) : null;}
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public ViewerPreferences(
      Document context
      ) : base(context, new PdfDictionary())
    {}

    private ViewerPreferences(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    /**
      <summary>Gets/Sets the predominant reading order for text.</summary>
    */
    [PDF(VersionEnum.PDF13)]
    public DirectionEnum Direction
    {
      get
      {return ViewerPreferencesDirectionEnumExtension.Get((PdfName)BaseDataObject[PdfName.Direction], DefaultDirection).Value;}
      set
      {BaseDataObject[PdfName.Direction] = (value != DefaultDirection ? value.Code() : null);}
    }

    /**
      <summary>Gets/Sets whether the window's title bar should display the <see
      cref="Information.Title">document title</see> (or the name of the PDF file instead).</summary>
    */
    [PDF(VersionEnum.PDF14)]
    public bool DocTitleDisplayed
    {
      get
      {return (bool)Get(PdfName.DisplayDocTitle, DefaultFlag);}
      set
      {BaseDataObject[PdfName.DisplayDocTitle] = (value != DefaultFlag ? PdfBoolean.Get(value) : null);}
    }

    /**
      <summary>Gets/Sets whether the viewer application's menu bar is visible when the document is
      active.</summary>
    */
    public bool MenubarVisible
    {
      get
      {return !(bool)Get(PdfName.HideMenubar, DefaultFlag);}
      set
      {BaseDataObject[PdfName.HideMenubar] = (value != !DefaultFlag ? PdfBoolean.Get(!value) : null);}
    }

    /**
      <summary>Gets/Sets the normal page mode, that is how the document should be displayed on
      exiting full-screen mode.</summary>
    */
    public PageModeEnum NormalPageMode
    {
      get
      {return ViewerPreferencesPageModeEnumExtension.Get((PdfName)BaseDataObject[PdfName.NonFullScreenPageMode], DefaultPageMode).Value;}
      set
      {BaseDataObject[PdfName.NonFullScreenPageMode] = (value != DefaultPageMode ? value.Code() : null);}
    }

    /**
      <summary>Gets/Sets the page layout to be used when the document is opened [PDF:1.7:3.6.1].
      </summary>
    */
    [PDF(VersionEnum.PDF10)]
    public PageLayoutEnum PageLayout
    {
      get
      {return ViewerPreferencesPageLayoutEnumExtension.Get((PdfName)Document.BaseDataObject[PdfName.PageLayout], DefaultPageLayout).Value;}
      set
      {Document.BaseDataObject[PdfName.PageLayout] = (value != DefaultPageLayout ? value.Code() : null);}
    }

    /**
      <summary>Gets/Sets the page mode, that is how the document should be displayed when is opened
      [PDF:1.7:3.6.1].</summary>
    */
    [PDF(VersionEnum.PDF10)]
    public PageModeEnum PageMode
    {
      get
      {return ViewerPreferencesPageModeEnumExtension.Get((PdfName)Document.BaseDataObject[PdfName.PageMode], DefaultPageMode).Value;}
      set
      {Document.BaseDataObject[PdfName.PageMode] = (value != DefaultPageMode ? value.Code() : null);}
    }

    /**
      <summary>Gets/Sets the paper handling option to use when printing the file from the print
      dialog.</summary>
    */
    [PDF(VersionEnum.PDF17)]
    public PaperModeEnum? PaperMode
    {
      get
      {return ViewerPreferencesPaperModeEnumExtension.Get((PdfName)BaseDataObject[PdfName.Duplex]);}
      set
      {BaseDataObject[PdfName.Duplex] = (value.HasValue ? value.Value.Code() : null);}
    }

    /**
      <summary>Gets/Sets whether the page size is used to select the input paper tray, as defined
      through the print dialog presented by the viewer application.</summary>
    */
    [PDF(VersionEnum.PDF17)]
    public bool PaperTraySelected
    {
      get
      {return (bool)Get(PdfName.PickTrayByPDFSize, DefaultFlag);}
      set
      {BaseDataObject[PdfName.PickTrayByPDFSize] = (value != DefaultFlag ? PdfBoolean.Get(value) : null);}
    }

    /**
      <summary>Gets/Sets the number of copies to be printed when the print dialog is opened for this
      file.</summary>
    */
    [PDF(VersionEnum.PDF17)]
    public int PrintCount
    {
      get
      {return (int)Get(PdfName.NumCopies, DefaultPrintCount);}
      set
      {
        /*
          NOTE: Supported values range from 1 to 5; values outside this range are ignored.
        */
        if(value < 1)
        {value = 1;}
        else if(value > 5)
        {value = 5;}
        BaseDataObject[PdfName.NumCopies] = (value != DefaultPrintCount ? PdfInteger.Get(value) : null);
      }
    }

    /**
      <summary>Gets/Sets the page numbers used to initialize the print dialog box when the file is
      printed.</summary>
      <remarks>Page numbers are 1-based.</remarks>
    */
    [PDF(VersionEnum.PDF17)]
    public IList<Interval<int>> PrintPageRanges
    {
      get
      {
        PdfArray printPageRangesObject = (PdfArray)BaseDataObject.Resolve(PdfName.PrintPageRange);
        if(printPageRangesObject == null
          || printPageRangesObject.Count == 0
          || printPageRangesObject.Count % 2 != 0)
          return null;

        var printPageRanges = new List<Interval<int>>();
        for(int index = 0, length = printPageRangesObject.Count; index < length;)
        {
          printPageRanges.Add(
            new Interval<int>(
              ((PdfInteger)printPageRangesObject[index++]).IntValue,
              ((PdfInteger)printPageRangesObject[index++]).IntValue
              )
            );
        }
        return printPageRanges;
      }
      set
      {
        PdfArray printPageRangesObject = null;
        if(value != null && value.Count > 0)
        {
          printPageRangesObject = new PdfArray();
          int pageCount = Document.Pages.Count;
          foreach(Interval<int> printPageRange in value)
          {
            int low = printPageRange.Low,
              high = printPageRange.High;
            if(low < 1)
              throw new ArgumentException(String.Format("Page number {0} is out of range (page numbers are 1-based).", low));
            else if(high > pageCount)
              throw new ArgumentException(String.Format("Page number {0} is out of range (document pages are {1}).", high, pageCount));
            else if(low > high)
              throw new ArgumentException(String.Format("Last page ({0}) can't be less than first one ({1}).", high, low));

            printPageRangesObject.Add(PdfInteger.Get(low));
            printPageRangesObject.Add(PdfInteger.Get(high));
          }
        }
        BaseDataObject[PdfName.PrintPageRange] = printPageRangesObject;
      }
    }

    /**
      <summary>Gets/Sets whether the viewer application should use the current print scaling when a
      print dialog is displayed for this document.</summary>
    */
    [PDF(VersionEnum.PDF16)]
    public bool PrintScaled
    {
      get
      {
        PdfDirectObject printScaledObject = BaseDataObject[PdfName.PrintScaling];
        return printScaledObject == null || printScaledObject.Equals(DefaultPrintScaledObject);
      }
      set
      {BaseDataObject[PdfName.PrintScaling] = (!value ? PdfName.None : null);}
    }

    /**
      <summary>Gets/Sets whether the viewer application's tool bars are visible when the document is
      active.</summary>
    */
    public bool ToolbarVisible
    {
      get
      {return !(bool)Get(PdfName.HideToolbar, DefaultFlag);}
      set
      {BaseDataObject[PdfName.HideToolbar] = (value != !DefaultFlag ? PdfBoolean.Get(!value) : null);}
    }

    /**
      <summary>Gets/Sets whether to position the document's window in the center of the screen.
      </summary>
    */
    public bool WindowCentered
    {
      get
      {return (bool)Get(PdfName.CenterWindow, DefaultFlag);}
      set
      {BaseDataObject[PdfName.CenterWindow] = (value != DefaultFlag ? PdfBoolean.Get(value) : null);}
    }

    /**
      <summary>Gets/Sets whether to resize the document's window to fit the size of the first
      displayed page.</summary>
    */
    public bool WindowFitted
    {
      get
      {return (bool)Get(PdfName.FitWindow, DefaultFlag);}
      set
      {BaseDataObject[PdfName.FitWindow] = (value != DefaultFlag ? PdfBoolean.Get(value) : null);}
    }

    /**
      <summary>Gets/Sets whether user interface elements in the document's window (such as scroll
      bars and navigation controls) are visible when the document is active.</summary>
    */
    public bool WindowUIVisible
    {
      get
      {return !(bool)Get(PdfName.HideWindowUI, DefaultFlag);}
      set
      {BaseDataObject[PdfName.HideWindowUI] = (value != !DefaultFlag ? PdfBoolean.Get(!value) : null);}
    }
    #endregion

    #region private
    private object Get(
      PdfName key,
      object defaultValue
      )
    {return PdfSimpleObject<object>.GetValue(BaseDataObject[key], defaultValue);}
    #endregion
    #endregion
    #endregion
  }

  internal static class ViewerPreferencesDirectionEnumExtension
  {
    private static readonly BiDictionary<ViewerPreferences.DirectionEnum,PdfName> codes;

    static ViewerPreferencesDirectionEnumExtension()
    {
      codes = new BiDictionary<ViewerPreferences.DirectionEnum,PdfName>();
      codes[ViewerPreferences.DirectionEnum.LeftToRight] = PdfName.L2R;
      codes[ViewerPreferences.DirectionEnum.RightToLeft] = PdfName.R2L;
    }

    public static ViewerPreferences.DirectionEnum? Get(
      PdfName code
      )
    {return Get(code, null);}

    public static ViewerPreferences.DirectionEnum? Get(
      PdfName code,
      ViewerPreferences.DirectionEnum? defaultValue
      )
    {
      if(code == null)
        return defaultValue;

      ViewerPreferences.DirectionEnum? value = codes.GetKey(code);
      if(!value.HasValue)
        throw new ArgumentException(code.ToString());

      return value.Value;
    }

    public static PdfName Code(
      this ViewerPreferences.DirectionEnum value
      )
    {return codes[value];}
  }

  internal static class ViewerPreferencesPageLayoutEnumExtension
  {
    private static readonly BiDictionary<ViewerPreferences.PageLayoutEnum,PdfName> codes;

    static ViewerPreferencesPageLayoutEnumExtension()
    {
      codes = new BiDictionary<ViewerPreferences.PageLayoutEnum,PdfName>();
      codes[ViewerPreferences.PageLayoutEnum.SinglePage] = PdfName.SinglePage;
      codes[ViewerPreferences.PageLayoutEnum.OneColumn] = PdfName.OneColumn;
      codes[ViewerPreferences.PageLayoutEnum.TwoColumnLeft] = PdfName.TwoColumnLeft;
      codes[ViewerPreferences.PageLayoutEnum.TwoColumnRight] = PdfName.TwoColumnRight;
      codes[ViewerPreferences.PageLayoutEnum.TwoPageLeft] = PdfName.TwoPageLeft;
      codes[ViewerPreferences.PageLayoutEnum.TwoPageRight] = PdfName.TwoPageRight;
    }

    public static ViewerPreferences.PageLayoutEnum? Get(
      PdfName code
      )
    {return Get(code, null);}

    public static ViewerPreferences.PageLayoutEnum? Get(
      PdfName code,
      ViewerPreferences.PageLayoutEnum? defaultValue
      )
    {
      if(code == null)
        return defaultValue;

      ViewerPreferences.PageLayoutEnum? value = codes.GetKey(code);
      if(!value.HasValue)
        throw new ArgumentException(code.ToString());

      return value.Value;
    }

    public static PdfName Code(
      this ViewerPreferences.PageLayoutEnum value
      )
    {return codes[value];}
  }

  internal static class ViewerPreferencesPageModeEnumExtension
  {
    private static readonly BiDictionary<ViewerPreferences.PageModeEnum,PdfName> codes;

    static ViewerPreferencesPageModeEnumExtension()
    {
      codes = new BiDictionary<ViewerPreferences.PageModeEnum,PdfName>();
      codes[ViewerPreferences.PageModeEnum.Simple] = PdfName.UseNone;
      codes[ViewerPreferences.PageModeEnum.Bookmarks] = PdfName.UseOutlines;
      codes[ViewerPreferences.PageModeEnum.Thumbnails] = PdfName.UseThumbs;
      codes[ViewerPreferences.PageModeEnum.FullScreen] = PdfName.FullScreen;
      codes[ViewerPreferences.PageModeEnum.Layers] = PdfName.UseOC;
      codes[ViewerPreferences.PageModeEnum.Attachments] = PdfName.UseAttachments;
    }

    public static ViewerPreferences.PageModeEnum? Get(
      PdfName code
      )
    {return Get(code, null);}

    public static ViewerPreferences.PageModeEnum? Get(
      PdfName code,
      ViewerPreferences.PageModeEnum? defaultValue
      )
    {
      if(code == null)
        return defaultValue;

      ViewerPreferences.PageModeEnum? value = codes.GetKey(code);
      if(!value.HasValue)
        throw new ArgumentException(code.ToString());

      return value.Value;
    }

    public static PdfName Code(
      this ViewerPreferences.PageModeEnum value
      )
    {return codes[value];}
  }

  internal static class ViewerPreferencesPaperModeEnumExtension
  {
    private static readonly BiDictionary<ViewerPreferences.PaperModeEnum,PdfName> codes;

    static ViewerPreferencesPaperModeEnumExtension()
    {
      codes = new BiDictionary<ViewerPreferences.PaperModeEnum,PdfName>();
      codes[ViewerPreferences.PaperModeEnum.Simplex] = PdfName.Simplex;
      codes[ViewerPreferences.PaperModeEnum.DuplexShortEdge] = PdfName.DuplexFlipShortEdge;
      codes[ViewerPreferences.PaperModeEnum.DuplexLongEdge] = PdfName.DuplexFlipLongEdge;
    }

    public static ViewerPreferences.PaperModeEnum? Get(
      PdfName code
      )
    {return Get(code, null);}

    public static ViewerPreferences.PaperModeEnum? Get(
      PdfName code,
      ViewerPreferences.PaperModeEnum? defaultValue
      )
    {
      if(code == null)
        return defaultValue;

      ViewerPreferences.PaperModeEnum? value = codes.GetKey(code);
      if(!value.HasValue)
        throw new ArgumentException(code.ToString());

      return value.Value;
    }

    public static PdfName Code(
      this ViewerPreferences.PaperModeEnum value
      )
    {return codes[value];}
  }
}
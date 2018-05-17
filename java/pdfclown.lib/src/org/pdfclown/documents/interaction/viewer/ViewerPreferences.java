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

package org.pdfclown.documents.interaction.viewer;

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.util.math.Interval;

/**
  Viewer preferences [PDF:1.7:8.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2.1, 05/02/15
*/
@PDF(VersionEnum.PDF12)
public final class ViewerPreferences
  extends PdfObjectWrapper<PdfDictionary>
{
  // <classes>
  /**
    Predominant reading order for text [PDF:1.7:8.1].
  */
  @PDF(VersionEnum.PDF13)
  public enum DirectionEnum
  {
    /**
      Left to right.
    */
    LeftToRight(PdfName.L2R),
    /**
      Right to left (including vertical writing systems, such as Chinese, Japanese, and Korean).
    */
    RightToLeft(PdfName.R2L);

    public static DirectionEnum valueOf(
      PdfName code
      )
    {return valueOf(code, null);}
    
    public static DirectionEnum valueOf(
      PdfName code,
      DirectionEnum defaultValue
      )
    {
      if(code == null)
        return defaultValue;
      
      for(DirectionEnum direction : DirectionEnum.values())
      {
        if(direction.getCode().equals(code))
          return direction;
      }
      throw new IllegalArgumentException(code.toString());
    }

    private final PdfName code;

    private DirectionEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }

  /**
    Page layout to be used when the document is opened [PDF:1.7:3.6.1].
  */
  @PDF(VersionEnum.PDF10)
  public enum PageLayoutEnum
  {
    /**
      Displays one page at a time.
    */
    SinglePage(PdfName.SinglePage),
    /**
      Displays the pages in one column.
    */
    OneColumn(PdfName.OneColumn),
    /**
      Displays the pages in two columns, with odd-numbered pages on the left.
    */
    TwoColumnLeft(PdfName.TwoColumnLeft),
    /**
      Displays the pages in two columns, with odd-numbered pages on the right.
    */
    TwoColumnRight(PdfName.TwoColumnRight),
    /**
      Displays the pages two at a time, with odd-numbered pages on the left.
    */
    @PDF(VersionEnum.PDF15)
    TwoPageLeft(PdfName.TwoPageLeft),
    /**
      Displays the pages two at a time, with odd-numbered pages on the right.
    */
    @PDF(VersionEnum.PDF15)
    TwoPageRight(PdfName.TwoPageRight);
  
    public static PageLayoutEnum valueOf(
      PdfName code
      )
    {return valueOf(code, null);}
    
    public static PageLayoutEnum valueOf(
      PdfName code,
      PageLayoutEnum defaultValue
      )
    {
      if(code == null)
        return defaultValue;
  
      for(PageLayoutEnum value : values())
      {
        if(value.getCode().equals(code))
          return value;
      }
      throw new IllegalArgumentException(code.toString());
    }
  
    private PdfName code;
  
    private PageLayoutEnum(
      PdfName code
      )
    {this.code = code;}
  
    public PdfName getCode(
      )
    {return code;}
  }

  /**
    Page mode specifying how the document should be displayed when opened [PDF:1.7:3.6.1].
  */
  @PDF(VersionEnum.PDF10)
  public enum PageModeEnum
  {
    /**
      Neither document outline nor thumbnail images visible.
    */
    Simple(PdfName.UseNone),
    /**
      Document outline visible.
    */
    Bookmarks(PdfName.UseOutlines),
    /**
      Thumbnail images visible.
    */
    Thumbnails(PdfName.UseThumbs),
    /**
      Full-screen mode, with no menu bar, window controls, or any other window visible.
    */
    FullScreen(PdfName.FullScreen),
    /**
      Optional content group panel visible.
    */
    @PDF(VersionEnum.PDF15)
    Layers(PdfName.UseOC),
    /**
      Attachments panel visible.
    */
    @PDF(VersionEnum.PDF16)
    Attachments(PdfName.UseAttachments);
  
    public static PageModeEnum valueOf(
      PdfName code
      )
    {return valueOf(code, null);}
    
    public static PageModeEnum valueOf(
      PdfName code,
      PageModeEnum defaultValue
      )
    {
      if(code == null)
        return defaultValue;
      
      for(PageModeEnum value : values())
      {
        if(value.getCode().equals(code))
          return value;
      }
      throw new IllegalArgumentException(code.toString());
    }
  
    private PdfName code;
  
    private PageModeEnum(
      PdfName code
      )
    {this.code = code;}
  
    public PdfName getCode(
      )
    {return code;}
  }

  /**
    Paper handling option to use when printing the file from the print dialog [PDF:1.7:8.1].
  */
  @PDF(VersionEnum.PDF17)
  public enum PaperModeEnum
  {
    /**
      Print single-sided.
    */
    Simplex(PdfName.Simplex),
    /**
      Duplex and flip on the short edge of the sheet.
    */
    DuplexShortEdge(PdfName.DuplexFlipShortEdge),
    /**
      Duplex and flip on the long edge of the sheet.
    */
    DuplexLongEdge(PdfName.DuplexFlipLongEdge);
  
    public static PaperModeEnum valueOf(
      PdfName code
      )
    {return valueOf(code, null);}
    
    public static PaperModeEnum valueOf(
      PdfName code,
      PaperModeEnum defaultValue
      )
    {
      if(code == null)
        return defaultValue;

      for(PaperModeEnum paperMode : PaperModeEnum.values())
      {
        if(paperMode.getCode().equals(code))
          return paperMode;
      }
      throw new IllegalArgumentException(code.toString());
    }
  
    private final PdfName code;
  
    private PaperModeEnum(
      PdfName code
      )
    {this.code = code;}
  
    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final DirectionEnum DefaultDirection = DirectionEnum.LeftToRight;
  private static final boolean DefaultFlag = false;
  private static final PageLayoutEnum DefaultPageLayout = PageLayoutEnum.SinglePage;
  private static final PageModeEnum DefaultPageMode = PageModeEnum.Simple;
  private static final int DefaultPrintCount = 1;
  private static final PdfName DefaultPrintScaledObject = PdfName.AppDefault;
  // </fields>
  
  // <interface>
  // <public>
  public static ViewerPreferences wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new ViewerPreferences(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public ViewerPreferences(
    Document context
    )
  {super(context, new PdfDictionary());}

  private ViewerPreferences(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ViewerPreferences clone(
    Document context
    )
  {return (ViewerPreferences)super.clone(context);}

  /**
    Gets the predominant reading order for text.
  */
  @PDF(VersionEnum.PDF13)
  public DirectionEnum getDirection(
    )
  {return DirectionEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.Direction), DefaultDirection);}

  /**
    Gets the normal page mode, that is how the document should be displayed on exiting full-screen 
    mode.
  */
  public PageModeEnum getNormalPageMode(
    )
  {return PageModeEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.NonFullScreenPageMode), DefaultPageMode);}

  /**
    Gets the page layout to be used when the document is opened [PDF:1.7:3.6.1].
  */
  @PDF(VersionEnum.PDF10)
  public PageLayoutEnum getPageLayout(
    )
  {return PageLayoutEnum.valueOf((PdfName)getDocument().getBaseDataObject().get(PdfName.PageLayout), DefaultPageLayout);}

  /**
    Gets the page mode, that is how the document should be displayed when is opened [PDF:1.7:3.6.1].
  */
  @PDF(VersionEnum.PDF10)
  public PageModeEnum getPageMode(
    )
  {return PageModeEnum.valueOf((PdfName)getDocument().getBaseDataObject().get(PdfName.PageMode), DefaultPageMode);}

  /**
    Gets the paper handling option to use when printing the file from the print dialog.
  */
  @PDF(VersionEnum.PDF17)
  public PaperModeEnum getPaperMode(
    )
  {return PaperModeEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.Duplex));}

  /**
    Gets the number of copies to be printed when the print dialog is opened for this file.
  */
  @PDF(VersionEnum.PDF17)
  public int getPrintCount(
    )
  {return (Integer)get(PdfName.NumCopies, DefaultPrintCount);}

  /**
    Gets the page numbers used to initialize the print dialog box when the file is printed.
    <p>Page numbers are 1-based.</p>
  */
  @PDF(VersionEnum.PDF17)
  public List<Interval<Integer>> getPrintPageRanges(
    )
  {
    PdfArray printPageRangesObject = (PdfArray)getBaseDataObject().resolve(PdfName.PrintPageRange);
    if(printPageRangesObject == null 
      || printPageRangesObject.isEmpty() 
      || printPageRangesObject.size() % 2 != 0)
      return null;
    
    List<Interval<Integer>> printPageRanges = new ArrayList<Interval<Integer>>();
    for(int index = 0, length = printPageRangesObject.size(); index < length;)
    {
      printPageRanges.add(
        new Interval<Integer>(
          ((PdfInteger)printPageRangesObject.get(index++)).getIntValue(),
          ((PdfInteger)printPageRangesObject.get(index++)).getIntValue()
          )
        );
    }
    return printPageRanges;
  }

  /**
    Gets whether the window's title bar should display the {@link Information#getTitle() document 
    title} (or the name of the PDF file instead).
  */
  @PDF(VersionEnum.PDF14)
  public boolean isDocTitleDisplayed(
    )
  {return (Boolean)get(PdfName.DisplayDocTitle, DefaultFlag);}

  /**
    Gets whether the viewer application's menu bar is visible when the document is active.
  */
  public boolean isMenubarVisible(
    )
  {return !(Boolean)get(PdfName.HideMenubar, DefaultFlag);}

  /**
    Gets whether the page size is used to select the input paper tray, as defined through the print 
    dialog presented by the viewer application.
  */
  @PDF(VersionEnum.PDF17)
  public boolean isPaperTraySelected(
    )
  {return (Boolean)get(PdfName.PickTrayByPDFSize, DefaultFlag);}

  /**
    Gets whether the viewer application should use the current print scaling when a print dialog is 
    displayed for this document.
  */
  @PDF(VersionEnum.PDF16)
  public boolean isPrintScaled(
    )
  {
    PdfDirectObject printScaledObject = getBaseDataObject().get(PdfName.PrintScaling);
    return printScaledObject == null || printScaledObject.equals(DefaultPrintScaledObject);
  }

  /**
    Gets whether the viewer application's tool bars are visible when the document is active.
  */
  public boolean isToolbarVisible(
    )
  {return !(Boolean)get(PdfName.HideToolbar, DefaultFlag);}

  /**
    Gets whether to position the document's window in the center of the screen.
  */
  public boolean isWindowCentered(
    )
  {return (Boolean)get(PdfName.CenterWindow, DefaultFlag);}

  /**
    Gets whether to resize the document's window to fit the size of the first displayed page.
  */
  public boolean isWindowFitted(
    )
  {return (Boolean)get(PdfName.FitWindow, DefaultFlag);}

  /**
    Gets whether user interface elements in the document's window (such as scroll bars and 
    navigation controls) are visible when the document is active.
  */
  public boolean isWindowUIVisible(
    )
  {return !(Boolean)get(PdfName.HideWindowUI, DefaultFlag);}

  /**
    @see #getDirection()
  */
  public void setDirection(
    DirectionEnum value
    )
  {getBaseDataObject().put(PdfName.Direction, value != null && value != DefaultDirection ? value.getCode() : null);}

  /**
    @see #isDocTitleDisplayed()
  */
  public void setDocTitleDisplayed(
    boolean value
    )
  {getBaseDataObject().put(PdfName.DisplayDocTitle, value != DefaultFlag ? PdfBoolean.get(value) : null);}

  /**
    @see #isMenubarVisible()
  */
  public void setMenubarVisible(
    boolean value
    )
  {getBaseDataObject().put(PdfName.HideMenubar, value != !DefaultFlag ? PdfBoolean.get(!value) : null);}

  /**
    @see #getNormalPageMode()
  */
  public void setNormalPageMode(
    PageModeEnum value
    )
  {getBaseDataObject().put(PdfName.NonFullScreenPageMode, value != null && value != DefaultPageMode ? value.getCode() : null);}

  /**
    @see #getPageLayout()
  */
  public void setPageLayout(
    PageLayoutEnum value
    )
  {getDocument().getBaseDataObject().put(PdfName.PageLayout, value != null && value != DefaultPageLayout ? value.getCode() : null);}

  /**
    @see #getPageMode()
  */
  public void setPageMode(
    PageModeEnum value
    )
  {getDocument().getBaseDataObject().put(PdfName.PageMode, value != null && value != DefaultPageMode ? value.getCode() : null);}

  /**
    @see #getPaperMode()
  */
  public void setPaperMode(
    PaperModeEnum value
    )
  {getBaseDataObject().put(PdfName.Duplex, value != null ? value.getCode() : null);}

  /**
    @see #isPaperTraySelected()
  */
  public void setPaperTraySelected(
    boolean value
    )
  {getBaseDataObject().put(PdfName.PickTrayByPDFSize, value != DefaultFlag ? PdfBoolean.get(value) : null);}

  /**
    @see #getPrintCount()
  */
  public void setPrintCount(
    int value
    )
  {
    /*
      NOTE: Supported values range from 1 to 5; values outside this range are ignored.
    */
    if(value < 1)
    {value = 1;}
    else if(value > 5)
    {value = 5;}
    getBaseDataObject().put(PdfName.NumCopies, value != DefaultPrintCount ? PdfInteger.get(value) : null);
  }
  
  /**
    @see #getPrintPageRanges()
  */
  public void setPrintPageRanges(
    List<Interval<Integer>> value
    )
  {
    PdfArray printPageRangesObject = null;
    if(value != null && !value.isEmpty())
    {
      printPageRangesObject = new PdfArray();
      int pageCount = getDocument().getPages().size();
      for(Interval<Integer> printPageRange : value)
      {
        int low = printPageRange.getLow(),
          high = printPageRange.getHigh();
        if(low < 1)
          throw new IllegalArgumentException(String.format("Page number %s is out of range (page numbers are 1-based).", low));
        else if(high > pageCount)
          throw new IllegalArgumentException(String.format("Page number %s is out of range (document pages are %s).", high, pageCount));
        else if(low > high)
          throw new IllegalArgumentException(String.format("Last page (%s) can't be less than first one (%s).", high, low));
        
        printPageRangesObject.add(PdfInteger.get(low));
        printPageRangesObject.add(PdfInteger.get(high));
      }
    }
    getBaseDataObject().put(PdfName.PrintPageRange, printPageRangesObject);
  }

  /**
    @see #isPrintScaled()
  */
  public void setPrintScaled(
    boolean value
    )
  {getBaseDataObject().put(PdfName.PrintScaling, !value ? PdfName.None : null);}
  
  /**
    @see #isToolbarVisible()
  */
  public void setToolbarVisible(
    boolean value
    )
  {getBaseDataObject().put(PdfName.HideToolbar, value != !DefaultFlag ? PdfBoolean.get(!value) : null);}

  /**
    @see #isWindowCentered()
  */
  public void setWindowCentered(
    boolean value
    )
  {getBaseDataObject().put(PdfName.CenterWindow, value != DefaultFlag ? PdfBoolean.get(value) : null);}

  /**
    @see #isWindowFitted()
  */
  public void setWindowFitted(
    boolean value
    )
  {getBaseDataObject().put(PdfName.FitWindow, value != DefaultFlag ? PdfBoolean.get(value) : null);}

  /**
    @see #isWindowUIVisible()
  */
  public void setWindowUIVisible(
    boolean value
    )
  {getBaseDataObject().put(PdfName.HideWindowUI, value != !DefaultFlag ? PdfBoolean.get(!value) : null);}
  // </public>

  // <private>
  private Object get(
    PdfName key,
    Object defaultValue
    )
  {return PdfSimpleObject.getValue(getBaseDataObject().get(key), defaultValue);}
  // </private>
  // </interface>
  // </dynamic>
}
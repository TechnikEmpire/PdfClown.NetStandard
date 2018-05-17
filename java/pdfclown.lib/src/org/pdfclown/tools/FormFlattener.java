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

package org.pdfclown.tools;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.PageAnnotations;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.interaction.annotations.Annotation.FlagsEnum;
import org.pdfclown.documents.interaction.annotations.Widget;
import org.pdfclown.documents.interaction.forms.Field;
import org.pdfclown.documents.interaction.forms.Fields;
import org.pdfclown.documents.interaction.forms.Form;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.math.geom.Dimension;

/**
  Tool to flatten Acroforms.
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.2.0
  @version 0.2.0, 1/26/15
*/
public class FormFlattener
{
  private boolean hiddenRendered;
  private boolean nonPrintableRendered;
  
  /**
    Replaces the Acroform fields with their corresponding graphics representation.
    
    @param document Document to flatten.
  */
  public void flatten(
    Document document
    )
  {
    Map<Page,PageStamper> pageStampers = new HashMap<Page,PageStamper>();
    Form form = document.getForm();
    Fields formFields = form.getFields();
    for(Field field : formFields.values())
    {
      for(Widget widget : field.getWidgets())
      {
        Page widgetPage = widget.getPage();
        EnumSet<FlagsEnum> flags = widget.getFlags();
        // Is the widget to be rendered?
        if((!flags.contains(FlagsEnum.Hidden) || hiddenRendered)
          && (flags.contains(FlagsEnum.Print) || nonPrintableRendered))
        {
          // Stamping the current state appearance of the widget...
          PdfName widgetCurrentState = (PdfName)widget.getBaseDataObject().get(PdfName.AS);
          FormXObject widgetCurrentAppearance = widget.getAppearance().getNormal().get(widgetCurrentState);
          if(widgetCurrentAppearance != null)
          {
            PageStamper widgetStamper = pageStampers.get(widgetPage);
            if(widgetStamper == null)
            {pageStampers.put(widgetPage, widgetStamper = new PageStamper(widgetPage));}
            
            Rectangle2D widgetBox = widget.getBox();
            widgetStamper.getForeground().showXObject(widgetCurrentAppearance, new Point2D.Double(widgetBox.getX(), widgetBox.getY()), new Dimension(widgetBox.getWidth(), widgetBox.getHeight()));
          }
        }
        
        // Removing the widget from the page annotations...
        PageAnnotations widgetPageAnnotations = widgetPage.getAnnotations();
        widgetPageAnnotations.remove(widget);
        if(widgetPageAnnotations.isEmpty())
        {
          widgetPage.setAnnotations(null);
          widgetPageAnnotations.delete();
        }
        
        // Removing the field references relating the widget...
        PdfDictionary fieldPartDictionary = widget.getBaseDataObject();
        while (fieldPartDictionary != null)
        {
          PdfDictionary parentFieldPartDictionary = (PdfDictionary)fieldPartDictionary.resolve(PdfName.Parent);
          
          PdfArray kidsArray;
          if(parentFieldPartDictionary != null)
          {kidsArray = (PdfArray)parentFieldPartDictionary.resolve(PdfName.Kids);}
          else
          {kidsArray = formFields.getBaseDataObject();}
          
          kidsArray.remove(fieldPartDictionary.getReference());
          fieldPartDictionary.delete();
          if(!kidsArray.isEmpty())
            break;
          
          fieldPartDictionary = parentFieldPartDictionary;
        }
      }
    }
    if(formFields.isEmpty())
    {
      // Removing the form root...
      document.setForm(null);
      form.delete();
    }
    for(PageStamper pageStamper : pageStampers.values())
    {pageStamper.flush();}
  }
  
  /**
    Gets whether hidden fields have to be rendered.
  */
  public boolean isHiddenRendered(
    )
  {return hiddenRendered;}
  
  /**
    Gets whether non-printable fields have to be rendered.
  */
  public boolean isNonPrintableRendered(
    )
  {return nonPrintableRendered;}
  
  /**
    @see #isHiddenRendered()
  */
  public FormFlattener setHiddenRendered(
    boolean value
    )
  {
    hiddenRendered = value;
    return this;
  }
  
  /**
    @see #isNonPrintableRendered()
  */
  public FormFlattener setNonPrintableRendered(
    boolean value
    )
  {
    nonPrintableRendered = value;
    return this;
  }
}

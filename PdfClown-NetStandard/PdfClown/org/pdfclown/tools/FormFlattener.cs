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

using org.pdfclown.documents;
using org.pdfclown.documents.contents.xObjects;
using org.pdfclown.documents.interaction.annotations;
using org.pdfclown.documents.interaction.forms;
using org.pdfclown.objects;

using System.Collections.Generic;
using System.Drawing;

namespace org.pdfclown.tools
{
  /**
    <summary>Tool to flatten Acroforms.</summary>
  */
  public sealed class FormFlattener
  {
    private bool hiddenRendered;
    private bool nonPrintableRendered;

    /**
      <summary>Replaces the Acroform fields with their corresponding graphics representation.</summary>
      <param name="document">Document to flatten.</param>
    */
    public void Flatten(
      Document document
      )
    {
      Dictionary<Page,PageStamper> pageStampers = new Dictionary<Page, PageStamper>();
      Form form = document.Form;
      Fields formFields = form.Fields;
      foreach(Field field in formFields.Values)
      {
        foreach(Widget widget in field.Widgets)
        {
          Page widgetPage = widget.Page;
          Annotation.FlagsEnum flags = widget.Flags;
          // Is the widget to be rendered?
          if(((flags & Annotation.FlagsEnum.Hidden) == 0 || hiddenRendered)
            && ((flags & Annotation.FlagsEnum.Print) > 0 || nonPrintableRendered))
          {
            // Stamping the current state appearance of the widget...
            PdfName widgetCurrentState = (PdfName)widget.BaseDataObject[PdfName.AS];
            FormXObject widgetCurrentAppearance = widget.Appearance.Normal[widgetCurrentState];
            if(widgetCurrentAppearance != null)
            {
              PageStamper widgetStamper;
              if(!pageStampers.TryGetValue(widgetPage, out widgetStamper))
              {pageStampers[widgetPage] = widgetStamper = new PageStamper(widgetPage);}

              RectangleF widgetBox = widget.Box;
              widgetStamper.Foreground.ShowXObject(widgetCurrentAppearance, widgetBox.Location, widgetBox.Size);
            }
          }

          // Removing the widget from the page annotations...
          PageAnnotations widgetPageAnnotations = widgetPage.Annotations;
          widgetPageAnnotations.Remove(widget);
          if(widgetPageAnnotations.Count == 0)
          {
            widgetPage.Annotations = null;
            widgetPageAnnotations.Delete();
          }

          // Removing the field references relating the widget...
          PdfDictionary fieldPartDictionary = widget.BaseDataObject;
          while (fieldPartDictionary != null)
          {
            PdfDictionary parentFieldPartDictionary = (PdfDictionary)fieldPartDictionary.Resolve(PdfName.Parent);

            PdfArray kidsArray;
            if(parentFieldPartDictionary != null)
            {kidsArray = (PdfArray)parentFieldPartDictionary.Resolve(PdfName.Kids);}
            else
            {kidsArray = formFields.BaseDataObject;}

            kidsArray.Remove(fieldPartDictionary.Reference);
            fieldPartDictionary.Delete();
            if(kidsArray.Count > 0)
              break;

            fieldPartDictionary = parentFieldPartDictionary;
          }
        }
      }
      if(formFields.Count == 0)
      {
        // Removing the form root...
        document.Form = null;
        form.Delete();
      }
      foreach(PageStamper pageStamper in pageStampers.Values)
      {pageStamper.Flush();}
    }

    /**
      <summary>Gets/Sets whether hidden fields have to be rendered.</summary>
    */
    public bool HiddenRendered
    {
      get
      {return hiddenRendered;}
      set
      {hiddenRendered = value;}
    }

    /**
      <summary>Gets/Sets whether non-printable fields have to be rendered.</summary>
    */
    public bool NonPrintableRendered
    {
      get
      {return nonPrintableRendered;}
      set
      {nonPrintableRendered = value;}
    }
  }
}

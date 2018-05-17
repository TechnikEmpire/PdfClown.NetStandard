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

using org.pdfclown.bytes;
using org.pdfclown.files;
using org.pdfclown.objects;
using org.pdfclown.tokens;

using System;
using System.Collections.Generic;
using System.Text;

namespace org.pdfclown.documents.contents.objects
{
  /**
    <summary>Content stream instruction [PDF:1.6:3.7.1].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public abstract class Operation
    : ContentObject
  {
    #region static
    #region interface
    #region public
    /**
      <summary>Gets an operation.</summary>
      <param name="@operator">Operator.</param>
      <param name="operands">List of operands.</param>
    */
    public static Operation Get(
      string @operator,
      IList<PdfDirectObject> operands
      )
    {
      if(@operator == null)
        return null;

      if(@operator.Equals(SaveGraphicsState.OperatorKeyword))
        return SaveGraphicsState.Value;
      else if(@operator.Equals(SetFont.OperatorKeyword))
        return new SetFont(operands);
      else if(@operator.Equals(SetStrokeColor.OperatorKeyword)
        || @operator.Equals(SetStrokeColor.ExtendedOperatorKeyword))
        return new SetStrokeColor(@operator, operands);
      else if(@operator.Equals(SetStrokeColorSpace.OperatorKeyword))
        return new SetStrokeColorSpace(operands);
      else if(@operator.Equals(SetFillColor.OperatorKeyword)
        || @operator.Equals(SetFillColor.ExtendedOperatorKeyword))
        return new SetFillColor(@operator, operands);
      else if(@operator.Equals(SetFillColorSpace.OperatorKeyword))
        return new SetFillColorSpace(operands);
      else if(@operator.Equals(SetDeviceGrayStrokeColor.OperatorKeyword))
        return new SetDeviceGrayStrokeColor(operands);
      else if(@operator.Equals(SetDeviceGrayFillColor.OperatorKeyword))
        return new SetDeviceGrayFillColor(operands);
      else if(@operator.Equals(SetDeviceRGBStrokeColor.OperatorKeyword))
        return new SetDeviceRGBStrokeColor(operands);
      else if(@operator.Equals(SetDeviceRGBFillColor.OperatorKeyword))
        return new SetDeviceRGBFillColor(operands);
      else if(@operator.Equals(SetDeviceCMYKStrokeColor.OperatorKeyword))
        return new SetDeviceCMYKStrokeColor(operands);
      else if(@operator.Equals(SetDeviceCMYKFillColor.OperatorKeyword))
        return new SetDeviceCMYKFillColor(operands);
      else if(@operator.Equals(RestoreGraphicsState.OperatorKeyword))
        return RestoreGraphicsState.Value;
      else if(@operator.Equals(BeginSubpath.OperatorKeyword))
        return new BeginSubpath(operands);
      else if(@operator.Equals(CloseSubpath.OperatorKeyword))
        return CloseSubpath.Value;
      else if(@operator.Equals(PaintPath.CloseStrokeOperatorKeyword))
        return PaintPath.CloseStroke;
      else if(@operator.Equals(PaintPath.FillOperatorKeyword)
        || @operator.Equals(PaintPath.FillObsoleteOperatorKeyword))
        return PaintPath.Fill;
      else if(@operator.Equals(PaintPath.FillEvenOddOperatorKeyword))
        return PaintPath.FillEvenOdd;
      else if(@operator.Equals(PaintPath.StrokeOperatorKeyword))
        return PaintPath.Stroke;
      else if(@operator.Equals(PaintPath.FillStrokeOperatorKeyword))
        return PaintPath.FillStroke;
      else if(@operator.Equals(PaintPath.FillStrokeEvenOddOperatorKeyword))
        return PaintPath.FillStrokeEvenOdd;
      else if(@operator.Equals(PaintPath.CloseFillStrokeOperatorKeyword))
        return PaintPath.CloseFillStroke;
      else if(@operator.Equals(PaintPath.CloseFillStrokeEvenOddOperatorKeyword))
        return PaintPath.CloseFillStrokeEvenOdd;
      else if(@operator.Equals(PaintPath.EndPathNoOpOperatorKeyword))
        return PaintPath.EndPathNoOp;
      else if(@operator.Equals(ModifyClipPath.NonZeroOperatorKeyword))
        return ModifyClipPath.NonZero;
      else if(@operator.Equals(ModifyClipPath.EvenOddOperatorKeyword))
        return ModifyClipPath.EvenOdd;
      else if(@operator.Equals(TranslateTextToNextLine.OperatorKeyword))
        return TranslateTextToNextLine.Value;
      else if(@operator.Equals(ShowSimpleText.OperatorKeyword))
        return new ShowSimpleText(operands);
      else if(@operator.Equals(ShowTextToNextLine.SimpleOperatorKeyword)
        || @operator.Equals(ShowTextToNextLine.SpaceOperatorKeyword))
        return new ShowTextToNextLine(@operator, operands);
      else if(@operator.Equals(ShowAdjustedText.OperatorKeyword))
        return new ShowAdjustedText(operands);
      else if(@operator.Equals(TranslateTextRelative.SimpleOperatorKeyword)
        || @operator.Equals(TranslateTextRelative.LeadOperatorKeyword))
        return new TranslateTextRelative(@operator, operands);
      else if(@operator.Equals(SetTextMatrix.OperatorKeyword))
        return new SetTextMatrix(operands);
      else if(@operator.Equals(ModifyCTM.OperatorKeyword))
        return new ModifyCTM(operands);
      else if(@operator.Equals(PaintXObject.OperatorKeyword))
        return new PaintXObject(operands);
      else if(@operator.Equals(PaintShading.OperatorKeyword))
        return new PaintShading(operands);
      else if(@operator.Equals(SetCharSpace.OperatorKeyword))
        return new SetCharSpace(operands);
      else if(@operator.Equals(SetLineCap.OperatorKeyword))
        return new SetLineCap(operands);
      else if(@operator.Equals(SetLineDash.OperatorKeyword))
        return new SetLineDash(operands);
      else if(@operator.Equals(SetLineJoin.OperatorKeyword))
        return new SetLineJoin(operands);
      else if(@operator.Equals(SetLineWidth.OperatorKeyword))
        return new SetLineWidth(operands);
      else if(@operator.Equals(SetMiterLimit.OperatorKeyword))
        return new SetMiterLimit(operands);
      else if(@operator.Equals(SetTextLead.OperatorKeyword))
        return new SetTextLead(operands);
      else if(@operator.Equals(SetTextRise.OperatorKeyword))
        return new SetTextRise(operands);
      else if(@operator.Equals(SetTextScale.OperatorKeyword))
        return new SetTextScale(operands);
      else if(@operator.Equals(SetTextRenderMode.OperatorKeyword))
        return new SetTextRenderMode(operands);
      else if(@operator.Equals(SetWordSpace.OperatorKeyword))
        return new SetWordSpace(operands);
      else if(@operator.Equals(DrawLine.OperatorKeyword))
        return new DrawLine(operands);
      else if(@operator.Equals(DrawRectangle.OperatorKeyword))
        return new DrawRectangle(operands);
      else if(@operator.Equals(DrawCurve.FinalOperatorKeyword)
        || @operator.Equals(DrawCurve.FullOperatorKeyword)
        || @operator.Equals(DrawCurve.InitialOperatorKeyword))
        return new DrawCurve(@operator, operands);
      else if(@operator.Equals(EndInlineImage.OperatorKeyword))
        return EndInlineImage.Value;
      else if(@operator.Equals(BeginText.OperatorKeyword))
        return BeginText.Value;
      else if(@operator.Equals(EndText.OperatorKeyword))
        return EndText.Value;
      else if(@operator.Equals(BeginMarkedContent.SimpleOperatorKeyword)
        || @operator.Equals(BeginMarkedContent.PropertyListOperatorKeyword))
        return new BeginMarkedContent(@operator, operands);
      else if(@operator.Equals(EndMarkedContent.OperatorKeyword))
        return EndMarkedContent.Value;
      else if(@operator.Equals(MarkedContentPoint.SimpleOperatorKeyword)
        || @operator.Equals(MarkedContentPoint.PropertyListOperatorKeyword))
        return new MarkedContentPoint(@operator, operands);
      else if(@operator.Equals(BeginInlineImage.OperatorKeyword))
        return BeginInlineImage.Value;
      else if(@operator.Equals(EndInlineImage.OperatorKeyword))
        return EndInlineImage.Value;
      else if(@operator.Equals(ApplyExtGState.OperatorKeyword))
        return new ApplyExtGState(operands);
      else // No explicit operation implementation available.
        return new GenericOperation(@operator, operands);
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region fields
    protected string @operator;
    protected IList<PdfDirectObject> operands;
    #endregion

    #region constructors
    protected Operation(
      string @operator
      )
    {this.@operator = @operator;}

    protected Operation(
      string @operator,
      PdfDirectObject operand
      )
    {
      this.@operator = @operator;

      this.operands = new List<PdfDirectObject>();
      this.operands.Add(operand);
    }

    protected Operation(
      string @operator,
      params PdfDirectObject[] operands
      )
    {
      this.@operator = @operator;
      this.operands = new List<PdfDirectObject>(operands);
    }

    protected Operation(
      string @operator,
      IList<PdfDirectObject> operands
      )
    {
      this.@operator = @operator;
      this.operands = operands;
    }
    #endregion

    #region interface
    #region public
    public string Operator
    {get{return @operator;}}

    public IList<PdfDirectObject> Operands
    {get{return operands;}}

    public override string ToString(
      )
    {
      StringBuilder buffer = new StringBuilder();

      // Begin.
      buffer.Append("{");

      // Operator.
      buffer.Append(@operator);

      // Operands.
      if(operands != null)
      {
        buffer.Append(" [");
        for(
          int i = 0, count = operands.Count;
          i < count;
          i++
          )
        {
          if(i > 0)
          {buffer.Append(", ");}

          buffer.Append(operands[i].ToString());
        }
        buffer.Append("]");
      }

      // End.
      buffer.Append("}");

      return buffer.ToString();
    }

    public override void WriteTo(
      IOutputStream stream,
      Document context
      )
    {
      if(operands != null)
      {
        File fileContext = context.File;
        foreach(PdfDirectObject operand in operands)
        {operand.WriteTo(stream, fileContext); stream.Write(Chunk.Space);}
      }
      stream.Write(@operator); stream.Write(Chunk.LineFeed);
    }
    #endregion
    #endregion
    #endregion
  }
}
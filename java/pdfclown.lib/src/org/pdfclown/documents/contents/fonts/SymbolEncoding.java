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

package org.pdfclown.documents.contents.fonts;

/**
  Symbol encoding [PDF:1.7:D.4].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/16/15
*/
final class SymbolEncoding
  extends Encoding
{
  public SymbolEncoding(
    )
  {
    put(0040, "space");
    put(0041, "exclam");
    put(0042, "universal");
    put(0043, "numbersign");
    put(0044, "existential");
    put(0045, "percent");
    put(0046, "ampersand");
    put(0047, "suchthat");
    put(0050, "parenleft");
    put(0051, "parenright");
    put(0052, "asteriskmath");
    put(0053, "plus");
    put(0054, "comma");
    put(0055, "minus");
    put(0056, "period");
    put(0057, "slash");
    put(0060, "zero");
    put(0061, "one");
    put(0062, "two");
    put(0063, "three");
    put(0064, "four");
    put(0065, "five");
    put(0066, "six");
    put(0067, "seven");
    put(0070, "eight");
    put(0071, "nine");
    put(0072, "colon");
    put(0073, "semicolon");
    put(0074, "less");
    put(0075, "equal");
    put(0076, "greater");
    put(0077, "question");
    put(0100, "congruent");
    put(0101,"Alpha");
    put(0102, "Beta");
    put(0103, "Chi");
    put(0104, "Delta");
    put(0105, "Epsilon");
    put(0106, "Phi");
    put(0107, "Gamma");
    put(0110, "Eta");
    put(0111, "Iota");
    put(0112, "theta1");
    put(0113, "Kappa");
    put(0114, "Lambda");
    put(0115, "Mu");
    put(0116, "Nu");
    put(0117, "Omicron");
    put(0120, "Pi");
    put(0121, "Theta");
    put(0122, "Rho");
    put(0123, "Sigma");
    put(0124, "Tau");
    put(0125, "Upsilon");
    put(0126, "sigma1");
    put(0127, "Omega");
    put(0130, "Xi");
    put(0131, "Psi");
    put(0132, "Zeta");
    put(0133, "bracketleft");
    put(0134, "therefore");
    put(0135, "bracketright");
    put(0136, "perpendicular");
    put(0137, "underscore");
    put(0140, "radicalex");
    put(0141, "alpha");
    put(0142, "beta");
    put(0143, "chi");
    put(0144, "delta");
    put(0145, "epsilon");
    put(0146, "phi");
    put(0147, "gamma");
    put(0150, "eta");
    put(0151, "iota");
    put(0152, "phi1");
    put(0153, "kappa");
    put(0154, "lambda");
    put(0155, "mu");
    put(0156, "nu");
    put(0157, "omicron");
    put(0160, "pi");
    put(0161, "theta");
    put(0162, "rho");
    put(0163, "sigma");
    put(0164, "tau");
    put(0165, "upsilon");
    put(0166, "omega1");
    put(0167, "omega");
    put(0170, "xi");
    put(0171, "psi");
    put(0172, "zeta");
    put(0173, "braceleft");
    put(0174, "bar");
    put(0175, "braceright");
    put(0176, "similar");
    put(0240, "Euro");
    put(0241, "Upsilon1");
    put(0242, "minute");
    put(0243, "lessequal");
    put(0244, "fraction");
    put(0245, "infinity");
    put(0246, "florin");
    put(0247, "club");
    put(0250, "diamond");
    put(0251, "heart");
    put(0252, "spade");
    put(0253, "arrowboth");
    put(0254, "arrowleft");
    put(0255, "arrowup");
    put(0256, "arrowright");
    put(0257, "arrowdown");
    put(0260, "degree");
    put(0261, "plusminus");
    put(0262, "second");
    put(0263, "greaterequal");
    put(0264, "multiply");
    put(0265, "proportional");
    put(0266, "partialdiff");
    put(0267, "bullet");
    put(0270, "divide");
    put(0271, "notequal");
    put(0272, "equivalence");
    put(0273, "approxequal");
    put(0274, "ellipsis");
    put(0275, "arrowvertex");
    put(0276, "arrowhorizex");
    put(0277, "carriagereturn");
    put(0300, "aleph");
    put(0301, "Ifraktur");
    put(0302, "Rfraktur");
    put(0303, "weierstrass");
    put(0304, "circlemultiply");
    put(0305, "circleplus");
    put(0306, "emptyset");
    put(0307, "intersection");
    put(0310, "union");
    put(0311, "propersuperset");
    put(0312, "reflexsuperset");
    put(0313, "notsubset");
    put(0314, "propersubset");
    put(0315, "reflexsubset");
    put(0316, "element");
    put(0317, "notelement");
    put(0320, "angle");
    put(0321, "gradient");
    put(0322, "registerserif");
    put(0323, "copyrightserif");
    put(0324, "trademarkserif");
    put(0325, "product");
    put(0326, "radical");
    put(0327, "dotmath");
    put(0330, "logicalnot");
    put(0331, "logicaland");
    put(0332, "logicalor");
    put(0333, "arrowdblboth");
    put(0334, "arrowdblleft");
    put(0335, "arrowdblup");
    put(0336, "arrowdblright");
    put(0337, "arrowdbldown");
    put(0340, "lozenge");
    put(0341, "angleleft");
    put(0342, "registersans");
    put(0343, "copyrightsans");
    put(0344, "trademarksans");
    put(0345, "summation");
    put(0346, "parenlefttp");
    put(0347, "parenleftex");
    put(0350, "parenleftbt");
    put(0351, "bracketlefttp");
    put(0352, "bracketleftex");
    put(0353, "bracketleftbt");
    put(0354, "bracelefttp");
    put(0355, "braceleftmid");
    put(0356, "braceleftbt");
    put(0357, "braceex");
    put(0361, "angleright");
    put(0362, "integral");
    put(0363, "integraltp");
    put(0364, "integralex");
    put(0365, "integralbt");
    put(0366, "parenrighttp");
    put(0367, "parenrightex");
    put(0370, "parenrightbt");
    put(0371, "bracketrighttp");
    put(0372, "bracketrightex");
    put(0373, "bracketrightbt");
    put(0374, "bracerighttp");
    put(0375, "bracerightmid");
    put(0376, "bracerightbt");
  }
}

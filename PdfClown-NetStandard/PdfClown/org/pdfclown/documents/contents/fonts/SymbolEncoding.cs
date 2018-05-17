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

namespace org.pdfclown.documents.contents.fonts
{
  /**
    <summary>Symbol encoding [PDF:1.7:D.4].</summary>
  */
  internal sealed class SymbolEncoding
    : Encoding
  {
    public SymbolEncoding(
      )
    {
      Put(32, "space");
      Put(33, "exclam");
      Put(34, "universal");
      Put(35, "numbersign");
      Put(36, "existential");
      Put(37, "percent");
      Put(38, "ampersand");
      Put(39, "suchthat");
      Put(40, "parenleft");
      Put(41, "parenright");
      Put(42, "asteriskmath");
      Put(43, "plus");
      Put(44, "comma");
      Put(45, "minus");
      Put(46, "period");
      Put(47, "slash");
      Put(48, "zero");
      Put(49, "one");
      Put(50, "two");
      Put(51, "three");
      Put(52, "four");
      Put(53, "five");
      Put(54, "six");
      Put(55, "seven");
      Put(56, "eight");
      Put(57, "nine");
      Put(58, "colon");
      Put(59, "semicolon");
      Put(60, "less");
      Put(61, "equal");
      Put(62, "greater");
      Put(63, "question");
      Put(64, "congruent");
      Put(65,"Alpha");
      Put(66, "Beta");
      Put(67, "Chi");
      Put(68, "Delta");
      Put(69, "Epsilon");
      Put(70, "Phi");
      Put(71, "Gamma");
      Put(72, "Eta");
      Put(73, "Iota");
      Put(74, "theta1");
      Put(75, "Kappa");
      Put(76, "Lambda");
      Put(77, "Mu");
      Put(78, "Nu");
      Put(79, "Omicron");
      Put(80, "Pi");
      Put(81, "Theta");
      Put(82, "Rho");
      Put(83, "Sigma");
      Put(84, "Tau");
      Put(85, "Upsilon");
      Put(86, "sigma1");
      Put(87, "Omega");
      Put(88, "Xi");
      Put(89, "Psi");
      Put(90, "Zeta");
      Put(91, "bracketleft");
      Put(92, "therefore");
      Put(93, "bracketright");
      Put(94, "perpendicular");
      Put(95, "underscore");
      Put(96, "radicalex");
      Put(97, "alpha");
      Put(98, "beta");
      Put(99, "chi");
      Put(100, "delta");
      Put(101, "epsilon");
      Put(102, "phi");
      Put(103, "gamma");
      Put(104, "eta");
      Put(105, "iota");
      Put(106, "phi1");
      Put(107, "kappa");
      Put(108, "lambda");
      Put(109, "mu");
      Put(110, "nu");
      Put(111, "omicron");
      Put(112, "pi");
      Put(113, "theta");
      Put(114, "rho");
      Put(115, "sigma");
      Put(116, "tau");
      Put(117, "upsilon");
      Put(118, "omega1");
      Put(119, "omega");
      Put(120, "xi");
      Put(121, "psi");
      Put(122, "zeta");
      Put(123, "braceleft");
      Put(124, "bar");
      Put(125, "braceright");
      Put(126, "similar");
      Put(160, "Euro");
      Put(161, "Upsilon1");
      Put(162, "minute");
      Put(163, "lessequal");
      Put(164, "fraction");
      Put(165, "infinity");
      Put(166, "florin");
      Put(167, "club");
      Put(168, "diamond");
      Put(169, "heart");
      Put(170, "spade");
      Put(171, "arrowboth");
      Put(172, "arrowleft");
      Put(173, "arrowup");
      Put(174, "arrowright");
      Put(175, "arrowdown");
      Put(176, "degree");
      Put(177, "plusminus");
      Put(178, "second");
      Put(179, "greaterequal");
      Put(180, "multiply");
      Put(181, "proportional");
      Put(182, "partialdiff");
      Put(183, "bullet");
      Put(184, "divide");
      Put(185, "notequal");
      Put(186, "equivalence");
      Put(187, "approxequal");
      Put(188, "ellipsis");
      Put(189, "arrowvertex");
      Put(190, "arrowhorizex");
      Put(191, "carriagereturn");
      Put(192, "aleph");
      Put(193, "Ifraktur");
      Put(194, "Rfraktur");
      Put(195, "weierstrass");
      Put(196, "circlemultiply");
      Put(197, "circleplus");
      Put(198, "emptyset");
      Put(199, "intersection");
      Put(200, "union");
      Put(201, "propersuperset");
      Put(202, "reflexsuperset");
      Put(203, "notsubset");
      Put(204, "propersubset");
      Put(205, "reflexsubset");
      Put(206, "element");
      Put(207, "notelement");
      Put(208, "angle");
      Put(209, "gradient");
      Put(210, "registerserif");
      Put(211, "copyrightserif");
      Put(212, "trademarkserif");
      Put(213, "product");
      Put(214, "radical");
      Put(215, "dotmath");
      Put(216, "logicalnot");
      Put(217, "logicaland");
      Put(218, "logicalor");
      Put(219, "arrowdblboth");
      Put(220, "arrowdblleft");
      Put(221, "arrowdblup");
      Put(222, "arrowdblright");
      Put(223, "arrowdbldown");
      Put(224, "lozenge");
      Put(225, "angleleft");
      Put(226, "registersans");
      Put(227, "copyrightsans");
      Put(228, "trademarksans");
      Put(229, "summation");
      Put(230, "parenlefttp");
      Put(231, "parenleftex");
      Put(232, "parenleftbt");
      Put(233, "bracketlefttp");
      Put(234, "bracketleftex");
      Put(235, "bracketleftbt");
      Put(236, "bracelefttp");
      Put(237, "braceleftmid");
      Put(238, "braceleftbt");
      Put(239, "braceex");
      Put(241, "angleright");
      Put(242, "integral");
      Put(243, "integraltp");
      Put(244, "integralex");
      Put(245, "integralbt");
      Put(246, "parenrighttp");
      Put(247, "parenrightex");
      Put(248, "parenrightbt");
      Put(249, "bracketrighttp");
      Put(250, "bracketrightex");
      Put(251, "bracketrightbt");
      Put(252, "bracerighttp");
      Put(253, "bracerightmid");
      Put(254, "bracerightbt");
    }
  }
}
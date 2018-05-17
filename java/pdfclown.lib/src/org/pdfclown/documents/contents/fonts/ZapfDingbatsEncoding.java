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
  ZapfDingbats encoding [PDF:1.7:D.5].
  
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2.1
  @version 0.1.2.1, 04/16/15
*/
final class ZapfDingbatsEncoding
  extends Encoding
{
  public ZapfDingbatsEncoding(
    )
  {
    put(0040, '\u0020');
    put(0041, '\u2701');
    put(0042, '\u2702');
    put(0043, '\u2703');
    put(0044, '\u2704');
    put(0045, '\u260E');
    put(0046, '\u2706');
    put(0047, '\u2707');
    put(0050, '\u2708');
    put(0051, '\u2709');
    put(0052, '\u261B');
    put(0053, '\u261E');
    put(0054, '\u270C');
    put(0055, '\u270D');
    put(0056, '\u270E');
    put(0057, '\u270F');
    put(0060, '\u2710');
    put(0061, '\u2711');
    put(0062, '\u2712');
    put(0063, '\u2713');
    put(0064, '\u2714');
    put(0065, '\u2715');
    put(0066, '\u2716');
    put(0067, '\u2717');
    put(0070, '\u2718');
    put(0071, '\u2719');
    put(0072, '\u271A');
    put(0073, '\u271B');
    put(0074, '\u271C');
    put(0075, '\u271D');
    put(0076, '\u271E');
    put(0077, '\u271F');
    put(0100, '\u2720');
    put(0101, '\u2721');
    put(0102, '\u2722');
    put(0103, '\u2723');
    put(0104, '\u2724');
    put(0105, '\u2725');
    put(0106, '\u2726');
    put(0107, '\u2727');
    put(0110, '\u2605');
    put(0111, '\u2729');
    put(0112, '\u272A');
    put(0113, '\u272B');
    put(0114, '\u272C');
    put(0115, '\u272D');
    put(0116, '\u272E');
    put(0117, '\u272F');
    put(0120, '\u2730');
    put(0121, '\u2731');
    put(0122, '\u2732');
    put(0123, '\u2733');
    put(0124, '\u2734');
    put(0125, '\u2735');
    put(0126, '\u2736');
    put(0127, '\u2737');
    put(0130, '\u2738');
    put(0131, '\u2739');
    put(0132, '\u273A');
    put(0133, '\u273B');
    put(0134, '\u273C');
    put(0135, '\u273D');
    put(0136, '\u273E');
    put(0137, '\u273F');
    put(0140, '\u2740');
    put(0141, '\u2741');
    put(0142, '\u2742');
    put(0143, '\u2743');
    put(0144, '\u2744');
    put(0145, '\u2745');
    put(0146, '\u2746');
    put(0147, '\u2747');
    put(0150, '\u2748');
    put(0151, '\u2749');
    put(0152, '\u274A');
    put(0153, '\u274B');
    put(0154, '\u25CF');
    put(0155, '\u274D');
    put(0156, '\u25A0');
    put(0157, '\u274F');
    put(0160, '\u2750');
    put(0161, '\u2751');
    put(0162, '\u2752');
    put(0163, '\u25B2');
    put(0164, '\u25BC');
    put(0165, '\u25C6');
    put(0166, '\u2756');
    put(0167, '\u25D7');
    put(0170, '\u2759');
    put(0171, '\u2758');
    put(0172, '\u275A');
    put(0173, '\u275B');
    put(0174, '\u275C');
    put(0175, '\u275D');
    put(0176, '\u275E');

    // BEGIN: Undocumented range (parenthesis).
    put(0200, '\u2768');
    put(0201, '\u2769');
    put(0202, '\u276A');
    put(0203, '\u276B');
    put(0204, '\u276C');
    put(0205, '\u276D');
    put(0206, '\u276E');
    put(0207, '\u276F');
    put(0210, '\u2770');
    put(0211, '\u2771');
    put(0212, '\u2772');
    put(0213, '\u2773');
    put(0214, '\u2774');
    put(0215, '\u2775');
    // END: Undocumented range (parenthesis).
    
    put(0241, '\u2761');
    put(0242, '\u2762');
    put(0243, '\u2763');
    put(0244, '\u2764');
    put(0245, '\u2765');
    put(0246, '\u2766');
    put(0247, '\u2767');
    put(0250, '\u2663');
    put(0251, '\u2666');
    put(0252, '\u2665');
    put(0253, '\u2660');
    put(0254, '\u2460');
    put(0255, '\u2461');
    put(0256, '\u2462');
    put(0257, '\u2463');
    put(0260, '\u2464');
    put(0261, '\u2465');
    put(0262, '\u2466');
    put(0263, '\u2467');
    put(0264, '\u2468');
    put(0265, '\u2469');
    put(0266, '\u2776');
    put(0267, '\u2777');
    put(0270, '\u2778');
    put(0271, '\u2779');
    put(0272, '\u277A');
    put(0273, '\u277B');
    put(0274, '\u277C');
    put(0275, '\u277D');
    put(0276, '\u277E');
    put(0277, '\u277F');
    put(0300, '\u2780');
    put(0301, '\u2781');
    put(0302, '\u2782');
    put(0303, '\u2783');
    put(0304, '\u2784');
    put(0305, '\u2785');
    put(0306, '\u2786');
    put(0307, '\u2787');
    put(0310, '\u2788');
    put(0311, '\u2789');
    put(0312, '\u278A');
    put(0313, '\u278B');
    put(0314, '\u278C');
    put(0315, '\u278D');
    put(0316, '\u278E');
    put(0317, '\u278F');
    put(0320, '\u2790');
    put(0321, '\u2791');
    put(0322, '\u2792');
    put(0323, '\u2793');
    put(0324, '\u2794');
    put(0325, '\u2192');
    put(0326, '\u2194');
    put(0327, '\u2195');
    put(0330, '\u2798');
    put(0331, '\u2799');
    put(0332, '\u279A');
    put(0333, '\u279B');
    put(0334, '\u279C');
    put(0335, '\u279D');
    put(0336, '\u279E');
    put(0337, '\u279F');
    put(0340, '\u27A0');
    put(0341, '\u27A1');
    put(0342, '\u27A2');
    put(0343, '\u27A3');
    put(0344, '\u27A4');
    put(0345, '\u27A5');
    put(0346, '\u27A6');
    put(0347, '\u27A7');
    put(0350, '\u27A8');
    put(0351, '\u27A9');
    put(0352, '\u27AA');
    put(0353, '\u27AB');
    put(0354, '\u27AC');
    put(0355, '\u27AD');
    put(0356, '\u27AE');
    put(0357, '\u27AF');
    put(0361, '\u27B1');
    put(0362, '\u27B2');
    put(0363, '\u27B3');
    put(0364, '\u27B4');
    put(0365, '\u27B5');
    put(0366, '\u27B6');
    put(0367, '\u27B7');
    put(0370, '\u27B8');
    put(0371, '\u27B9');
    put(0372, '\u27BA');
    put(0373, '\u27BB');
    put(0374, '\u27BC');
    put(0375, '\u27BD');
    put(0376, '\u27BE');
  }
}

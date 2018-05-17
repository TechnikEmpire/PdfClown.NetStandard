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
    <summary>ZapfDingbats encoding [PDF:1.7:D.5].</summary>
  */
  internal sealed class ZapfDingbatsEncoding
    : Encoding
  {
    public ZapfDingbatsEncoding(
      )
    {
      Put(32, '\u0020');
      Put(33, '\u2701');
      Put(34, '\u2702');
      Put(35, '\u2703');
      Put(36, '\u2704');
      Put(37, '\u260E');
      Put(38, '\u2706');
      Put(39, '\u2707');
      Put(40, '\u2708');
      Put(41, '\u2709');
      Put(42, '\u261B');
      Put(43, '\u261E');
      Put(44, '\u270C');
      Put(45, '\u270D');
      Put(46, '\u270E');
      Put(47, '\u270F');
      Put(48, '\u2710');
      Put(49, '\u2711');
      Put(50, '\u2712');
      Put(51, '\u2713');
      Put(52, '\u2714');
      Put(53, '\u2715');
      Put(54, '\u2716');
      Put(55, '\u2717');
      Put(56, '\u2718');
      Put(57, '\u2719');
      Put(58, '\u271A');
      Put(59, '\u271B');
      Put(60, '\u271C');
      Put(61, '\u271D');
      Put(62, '\u271E');
      Put(63, '\u271F');
      Put(64, '\u2720');
      Put(65, '\u2721');
      Put(66, '\u2722');
      Put(67, '\u2723');
      Put(68, '\u2724');
      Put(69, '\u2725');
      Put(70, '\u2726');
      Put(71, '\u2727');
      Put(72, '\u2605');
      Put(73, '\u2729');
      Put(74, '\u272A');
      Put(75, '\u272B');
      Put(76, '\u272C');
      Put(77, '\u272D');
      Put(78, '\u272E');
      Put(79, '\u272F');
      Put(80, '\u2730');
      Put(81, '\u2731');
      Put(82, '\u2732');
      Put(83, '\u2733');
      Put(84, '\u2734');
      Put(85, '\u2735');
      Put(86, '\u2736');
      Put(87, '\u2737');
      Put(88, '\u2738');
      Put(89, '\u2739');
      Put(90, '\u273A');
      Put(91, '\u273B');
      Put(92, '\u273C');
      Put(93, '\u273D');
      Put(94, '\u273E');
      Put(95, '\u273F');
      Put(96, '\u2740');
      Put(97, '\u2741');
      Put(98, '\u2742');
      Put(99, '\u2743');
      Put(100, '\u2744');
      Put(101, '\u2745');
      Put(102, '\u2746');
      Put(103, '\u2747');
      Put(104, '\u2748');
      Put(105, '\u2749');
      Put(106, '\u274A');
      Put(107, '\u274B');
      Put(108, '\u25CF');
      Put(109, '\u274D');
      Put(110, '\u25A0');
      Put(111, '\u274F');
      Put(112, '\u2750');
      Put(113, '\u2751');
      Put(114, '\u2752');
      Put(115, '\u25B2');
      Put(116, '\u25BC');
      Put(117, '\u25C6');
      Put(118, '\u2756');
      Put(119, '\u25D7');
      Put(120, '\u2759');
      Put(121, '\u2758');
      Put(122, '\u275A');
      Put(123, '\u275B');
      Put(124, '\u275C');
      Put(125, '\u275D');
      Put(126, '\u275E');

      // BEGIN: Undocumented range (parenthesis).
      Put(128, '\u2768');
      Put(129, '\u2769');
      Put(130, '\u276A');
      Put(131, '\u276B');
      Put(132, '\u276C');
      Put(133, '\u276D');
      Put(134, '\u276E');
      Put(135, '\u276F');
      Put(136, '\u2770');
      Put(137, '\u2771');
      Put(138, '\u2772');
      Put(139, '\u2773');
      Put(140, '\u2774');
      Put(141, '\u2775');
      // END: Undocumented range (parenthesis).

      Put(161, '\u2761');
      Put(162, '\u2762');
      Put(163, '\u2763');
      Put(164, '\u2764');
      Put(165, '\u2765');
      Put(166, '\u2766');
      Put(167, '\u2767');
      Put(168, '\u2663');
      Put(169, '\u2666');
      Put(170, '\u2665');
      Put(171, '\u2660');
      Put(172, '\u2460');
      Put(173, '\u2461');
      Put(174, '\u2462');
      Put(175, '\u2463');
      Put(176, '\u2464');
      Put(177, '\u2465');
      Put(178, '\u2466');
      Put(179, '\u2467');
      Put(180, '\u2468');
      Put(181, '\u2469');
      Put(182, '\u2776');
      Put(183, '\u2777');
      Put(184, '\u2778');
      Put(185, '\u2779');
      Put(186, '\u277A');
      Put(187, '\u277B');
      Put(188, '\u277C');
      Put(189, '\u277D');
      Put(190, '\u277E');
      Put(191, '\u277F');
      Put(192, '\u2780');
      Put(193, '\u2781');
      Put(194, '\u2782');
      Put(195, '\u2783');
      Put(196, '\u2784');
      Put(197, '\u2785');
      Put(198, '\u2786');
      Put(199, '\u2787');
      Put(200, '\u2788');
      Put(201, '\u2789');
      Put(202, '\u278A');
      Put(203, '\u278B');
      Put(204, '\u278C');
      Put(205, '\u278D');
      Put(206, '\u278E');
      Put(207, '\u278F');
      Put(208, '\u2790');
      Put(209, '\u2791');
      Put(210, '\u2792');
      Put(211, '\u2793');
      Put(212, '\u2794');
      Put(213, '\u2192');
      Put(214, '\u2194');
      Put(215, '\u2195');
      Put(216, '\u2798');
      Put(217, '\u2799');
      Put(218, '\u279A');
      Put(219, '\u279B');
      Put(220, '\u279C');
      Put(221, '\u279D');
      Put(222, '\u279E');
      Put(223, '\u279F');
      Put(224, '\u27A0');
      Put(225, '\u27A1');
      Put(226, '\u27A2');
      Put(227, '\u27A3');
      Put(228, '\u27A4');
      Put(229, '\u27A5');
      Put(230, '\u27A6');
      Put(231, '\u27A7');
      Put(232, '\u27A8');
      Put(233, '\u27A9');
      Put(234, '\u27AA');
      Put(235, '\u27AB');
      Put(236, '\u27AC');
      Put(237, '\u27AD');
      Put(238, '\u27AE');
      Put(239, '\u27AF');
      Put(241, '\u27B1');
      Put(242, '\u27B2');
      Put(243, '\u27B3');
      Put(244, '\u27B4');
      Put(245, '\u27B5');
      Put(246, '\u27B6');
      Put(247, '\u27B7');
      Put(248, '\u27B8');
      Put(249, '\u27B9');
      Put(250, '\u27BA');
      Put(251, '\u27BB');
      Put(252, '\u27BC');
      Put(253, '\u27BD');
      Put(254, '\u27BE');
    }
  }
}
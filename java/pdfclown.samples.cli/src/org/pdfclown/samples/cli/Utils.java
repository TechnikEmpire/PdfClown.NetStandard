package org.pdfclown.samples.cli;

import java.util.Scanner;

class Utils
{
  public static void prompt(String message)
  {
    System.out.println("\n" + message);
    System.out.println("Press ENTER to continue");
    try
    {
      Scanner in = new Scanner(System.in);
      in.nextLine();
    }
    catch(Exception e)
    {}
  }
}

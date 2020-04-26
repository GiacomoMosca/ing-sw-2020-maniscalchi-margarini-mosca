package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.cli.CLI;

import java.util.Scanner;


public class Client
{
  private static UI userInterface;

  public static void main( String[] args )
  {
    Scanner scanner = new Scanner(System.in);
    System.out.println("1. CLI, 2. GUI\n");
    int choice = scanner.nextInt();
    if (choice == 1)
      userInterface = new CLI();
    else {
      //userInterface = new GUI();
      System.out.println("Coming soon :)");
      userInterface = new CLI();
    }
    userInterface.start();
  }
}

package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.GatheringByteChannel;
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
    else
      userInterface = new GUI();
    userInterface.start();
  }
}

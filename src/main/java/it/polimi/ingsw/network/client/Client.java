package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.GUI;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private static UI userInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. CLI, 2. GUI\n");
        int choice = -1;
        while (true) {
            try {
                choice = scanner.nextInt();
                while (choice != 1 && choice != 2) {
                    System.out.println("Invalid input. \n");
                    choice = scanner.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. \n");
                scanner.nextLine();
            }
        }
        if (choice == 1)
            userInterface = new CLI();
        else
            userInterface = new GUI();

        userInterface.run();
    }

}

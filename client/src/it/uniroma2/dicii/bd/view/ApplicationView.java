package it.uniroma2.dicii.bd.view;

import java.util.Scanner;

public class ApplicationView {

    public int showMenu() {
        System.out.println("*********************************");
        System.out.println("*          TAXI SERVICE         *");
        System.out.println("*********************************\n");
        System.out.println("1) Login");
        System.out.println("2) Registra cliente");
        System.out.println("3) Registra tassista");
        System.out.println("4) Esci");

        Scanner input = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.print("Seleziona un'opzione: ");
            try {
                choice = Integer.parseInt(input.nextLine());
                if (choice >= 1 && choice <= 4) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // Si ripete la richiesta, come thin client testuale.
            }

            System.out.println("Opzione non valida.");
        }
    }

    public void showError(String message) {
        System.out.println("Errore: " + message + "\n");
    }
}

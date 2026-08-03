package it.uniroma2.dicii.bd.view;

import it.uniroma2.dicii.bd.model.dto.CreaRichiestaRequest;

import java.util.Scanner;

public class ClienteView {

    public int showMenu() {
        System.out.println("*********************************");
        System.out.println("*        CLIENTE DASHBOARD      *");
        System.out.println("*********************************\n");
        System.out.println("1) Crea richiesta");
        System.out.println("2) Esci");

        Scanner input = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.print("Seleziona un'opzione: ");
            try {
                choice = Integer.parseInt(input.nextLine());
                if (choice >= 1 && choice <= 2) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // Si ripete la richiesta.
            }

            System.out.println("Opzione non valida.");
        }
    }

    public CreaRichiestaRequest askCreateRequest(String usernameSessione) {
        Scanner input = new Scanner(System.in);

        System.out.println("\n--- Nuova richiesta taxi ---");

        System.out.print("Indirizzo di partenza: ");
        String indirizzoPartenza = input.nextLine();

        System.out.print("Indirizzo di destinazione: ");
        String indirizzoDestinazione = input.nextLine();

        return new CreaRichiestaRequest(usernameSessione, indirizzoPartenza, indirizzoDestinazione);
    }

    public void showCreateRequestResult(int codiceRichiesta) {
        System.out.println("Richiesta creata correttamente. Codice richiesta: " + codiceRichiesta + "\n");
    }

    public void showError(String message) {
        System.out.println("Errore: " + message + "\n");
    }
}

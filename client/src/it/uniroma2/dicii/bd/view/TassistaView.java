package it.uniroma2.dicii.bd.view;

import it.uniroma2.dicii.bd.model.domain.RichiestaAttiva;
import it.uniroma2.dicii.bd.model.dto.AccettaRichiestaRequest;
import it.uniroma2.dicii.bd.model.dto.RegistraFineCorsaRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class TassistaView {

    public int showMenu() {
        System.out.println("*********************************");
        System.out.println("*       TASSISTA DASHBOARD      *");
        System.out.println("*********************************\n");
        System.out.println("1) Lista richieste attive");
        System.out.println("2) Accetta richiesta");
        System.out.println("3) Registra fine corsa");
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
                // Si ripete la richiesta.
            }

            System.out.println("Opzione non valida.");
        }
    }

    public void showActiveRequests(List<RichiestaAttiva> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("Nessuna richiesta attiva disponibile.\n");
            return;
        }

        System.out.println("\n--- Richieste attive ---");
        for (RichiestaAttiva request : requests) {
            System.out.println(request);
        }
        System.out.println();
    }

    public AccettaRichiestaRequest askAcceptRequest(String usernameSessione) {
        Scanner input = new Scanner(System.in);

        System.out.print("Codice richiesta da accettare: ");
        try {
            int codiceRichiesta = Integer.parseInt(input.nextLine());
            return new AccettaRichiestaRequest(usernameSessione, codiceRichiesta);
        } catch (NumberFormatException e) {
            System.out.println("Valore numerico non valido.\n");
            return null;
        }
    }

    public RegistraFineCorsaRequest askRideEndRequest(String usernameSessione) {
        Scanner input = new Scanner(System.in);

        System.out.print("Codice richiesta della corsa: ");
        try {
            int codiceRichiesta = Integer.parseInt(input.nextLine());

            System.out.print("Importo tassametro: ");
            BigDecimal importo = new BigDecimal(input.nextLine());

            return new RegistraFineCorsaRequest(usernameSessione, codiceRichiesta, importo);
        } catch (NumberFormatException e) {
            System.out.println("Valore numerico non validok.\n");
            return null;
        }
    }

    public void showAcceptResult() {
        System.out.println("Richiesta accettata correttamente.\n");
    }

    public void showRideEndResult() {
        System.out.println("Fine corsa registrata correttamente.\n");
    }

    public void showError(String message) {
        System.out.println("Errore: " + message + "\n");
    }
}

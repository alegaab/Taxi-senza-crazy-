package it.uniroma2.dicii.bd.view;

import it.uniroma2.dicii.bd.model.domain.ReportTassista;
import it.uniroma2.dicii.bd.model.dto.SegnaRiscossioneRequest;

import java.util.List;
import java.util.Scanner;

public class GestoreView {

    public int showMenu() {
        System.out.println("*********************************");
        System.out.println("*        GESTORE DASHBOARD      *");
        System.out.println("*********************************\n");
        System.out.println("1) Genera report tassisti");
        System.out.println("2) Segna riscossione commissione");
        System.out.println("3) Esci");

        Scanner input = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.print("Seleziona un'opzione: ");
            try {
                choice = Integer.parseInt(input.nextLine());
                if (choice >= 1 && choice <= 3) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // Si ripete la richiesta.
            }

            System.out.println("Opzione non valida.");
        }
    }

    public void showReport(List<ReportTassista> report) {
        if (report == null || report.isEmpty()) {
            System.out.println("Nessun dato disponibile per il report.\n");
            return;
        }

        System.out.println("\n--- Report tassisti ---");
        for (ReportTassista row : report) {
            System.out.println(row);
        }
        System.out.println();
    }

    public SegnaRiscossioneRequest askCommissionCollectionRequest() {
        Scanner input = new Scanner(System.in);

        System.out.print("Codice richiesta della corsa da riscuotere: ");
        try {
            int codiceRichiesta = Integer.parseInt(input.nextLine());
            return new SegnaRiscossioneRequest(codiceRichiesta);
        } catch (NumberFormatException e) {
            System.out.println("Valore numerico non valido.\n");
            return null;
        }
    }

    public void showCommissionCollectionResult() {
        System.out.println("Riscossione registrata correttamente.\n");
    }

    public void showError(String message) {
        System.out.println("Errore: " + message + "\n");
    }
}

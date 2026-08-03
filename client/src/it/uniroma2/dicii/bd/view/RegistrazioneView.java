package it.uniroma2.dicii.bd.view;

import it.uniroma2.dicii.bd.model.dto.RegistraClienteRequest;
import it.uniroma2.dicii.bd.model.dto.RegistraTassistaRequest;

import java.util.Scanner;

public class RegistrazioneView {

    public RegistraClienteRequest askClientRegistrationRequest() {
        Scanner input = new Scanner(System.in);

        System.out.println("\n--- Registrazione cliente ---");

        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        System.out.print("Nome: ");
        String nome = input.nextLine();

        System.out.print("Cognome: ");
        String cognome = input.nextLine();

        System.out.print("Telefono: ");
        String telefono = input.nextLine();

        System.out.print("Carta di credito: ");
        String cc = input.nextLine();

        return new RegistraClienteRequest(username, password, nome, cognome, telefono, cc);
    }

    public RegistraTassistaRequest askDriverRegistrationRequest() {
        Scanner input = new Scanner(System.in);

        System.out.println("\n--- Registrazione tassista ---");

        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        System.out.print("Patente: ");
        String patente = input.nextLine();

        System.out.print("Nome: ");
        String nome = input.nextLine();

        System.out.print("Cognome: ");
        String cognome = input.nextLine();

        System.out.print("Carta di credito: ");
        String cc = input.nextLine();

        System.out.print("Targa veicolo: ");
        String targa = input.nextLine();

        System.out.print("Capacità veicolo: ");
        try {
            int capacita = Integer.parseInt(input.nextLine());
            return new RegistraTassistaRequest(username, password, patente, nome, cognome, cc, targa, capacita);
        } catch (NumberFormatException e) {
            System.out.println("Valore numerico non valido.\n");
            return null;
        }
    }

    public void showClientRegistrationResult(int idCliente) {
        System.out.println("Cliente registrato correttamente. ID cliente: " + idCliente);
        System.out.println("Ora è possibile effettuare il login.\n");
    }

    public void showDriverRegistrationResult() {
        System.out.println("Tassista registrato correttamente.");
        System.out.println("Ora è possibile effettuare il login.\n");
    }
}

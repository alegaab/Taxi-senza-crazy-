package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.model.domain.Credentials;
import it.uniroma2.dicii.bd.view.ApplicationView;

public class ApplicationController implements Controller {
    private final ApplicationView view = new ApplicationView();

    @Override
    public void start() throws ApplicationException {
        while (true) {
            int choice = view.showMenu();

            try {
                switch (choice) {
                    case 1 -> login();
                    case 2 -> new RegistrazioneController().registerClient();
                    case 3 -> new RegistrazioneController().registerDriver();
                    case 4 -> System.exit(0);
                    default -> throw new ControllerException("Scelta non valida");
                }
            } catch (ControllerException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void login() throws ApplicationException {
        LoginController loginController = new LoginController();
        loginController.start();

        Credentials cred = loginController.getCred();

        if (cred == null || cred.getRole() == null) {
            throw new ControllerException("Credenziali non valide");
        }

        switch (cred.getRole()) {
            case CLIENTE -> new ClienteController(cred).start();
            case TASSISTA -> new TassistaController(cred).start();
            case GESTORE -> new GestoreController().start();
            default -> throw new ControllerException("Ruolo non valido");
        }
    }
}

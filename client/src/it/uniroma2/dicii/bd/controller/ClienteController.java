package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.CreaRichiestaProcedureDAO;
import it.uniroma2.dicii.bd.model.domain.Credentials;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.dto.CreaRichiestaRequest;
import it.uniroma2.dicii.bd.view.ClienteView;

import java.sql.SQLException;

public class ClienteController implements Controller {
    private final Credentials cred;
    private final ClienteView view = new ClienteView();

    public ClienteController(Credentials cred) {
        this.cred = cred;
    }

    @Override
    public void start() throws ApplicationException {
        try {
            ConnectionFactory.changeRole(Role.CLIENTE);
        } catch (SQLException e) {
            throw new ApplicationException(e.getMessage(), e);
        }

        while (true) {
            int choice = view.showMenu();

            try {
                switch (choice) {
                    case 1 -> createRequest();
                    case 2 -> System.exit(0);
                    default -> throw new ControllerException("Scelta non valida");
                }
            } catch (ControllerException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void createRequest() throws ControllerException {
        CreaRichiestaRequest request = view.askCreateRequest(cred.getUsername());
        try {
            int codiceRichiesta = new CreaRichiestaProcedureDAO().execute(request);
            view.showCreateRequestResult(codiceRichiesta);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }
}

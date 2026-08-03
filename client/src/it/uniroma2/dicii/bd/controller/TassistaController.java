package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.AccettaRichiestaProcedureDAO;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.ListaRichiesteAttiveProcedureDAO;
import it.uniroma2.dicii.bd.model.dao.RegistraFineCorsaProcedureDAO;
import it.uniroma2.dicii.bd.model.domain.Credentials;
import it.uniroma2.dicii.bd.model.domain.RichiestaAttiva;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.dto.AccettaRichiestaRequest;
import it.uniroma2.dicii.bd.model.dto.RegistraFineCorsaRequest;
import it.uniroma2.dicii.bd.view.TassistaView;

import java.sql.SQLException;
import java.util.List;

public class TassistaController implements Controller {
    private final Credentials cred;
    private final TassistaView view = new TassistaView();

    public TassistaController(Credentials cred) {
        this.cred = cred;
    }

    @Override
    public void start() throws ApplicationException {
        try {
            ConnectionFactory.changeRole(Role.TASSISTA);
        } catch (SQLException e) {
            throw new ApplicationException(e.getMessage(), e);
        }

        while (true) {
            int choice = view.showMenu();

            try {
                switch (choice) {
                    case 1 -> listActiveRequests();
                    case 2 -> acceptRequest();
                    case 3 -> registerRideEnd();
                    case 4 -> System.exit(0);
                    default -> throw new ControllerException("Scelta non valida");
                }
            } catch (ControllerException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void listActiveRequests() throws ControllerException {
        try {
            List<RichiestaAttiva> requests = new ListaRichiesteAttiveProcedureDAO().execute(null);
            view.showActiveRequests(requests);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private void acceptRequest() throws ControllerException {
        AccettaRichiestaRequest request = view.askAcceptRequest(cred.getUsername());
        if (request == null) {
            return;
        }

        try {
            new AccettaRichiestaProcedureDAO().execute(request);
            view.showAcceptResult();
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private void registerRideEnd() throws ControllerException {
        RegistraFineCorsaRequest request = view.askRideEndRequest(cred.getUsername());
        if (request == null) {
            return;
        }

        try {
            new RegistraFineCorsaProcedureDAO().execute(request);
            view.showRideEndResult();
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }
}

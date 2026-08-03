package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.ReportTassistiProcedureDAO;
import it.uniroma2.dicii.bd.model.dao.SegnaRiscossioneProcedureDAO;
import it.uniroma2.dicii.bd.model.domain.ReportTassista;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.dto.SegnaRiscossioneRequest;
import it.uniroma2.dicii.bd.view.GestoreView;

import java.sql.SQLException;
import java.util.List;

public class GestoreController implements Controller {
    private final GestoreView view = new GestoreView();

    @Override
    public void start() throws ApplicationException {
        try {
            ConnectionFactory.changeRole(Role.GESTORE);
        } catch (SQLException e) {
            throw new ApplicationException(e.getMessage(), e);
        }

        while (true) {
            int choice = view.showMenu();

            try {
                switch (choice) {
                    case 1 -> generateReport();
                    case 2 -> markCommissionCollected();
                    case 3 -> System.exit(0);
                    default -> throw new ControllerException("Scelta non valida");
                }
            } catch (ControllerException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void generateReport() throws ControllerException {
        try {
            List<ReportTassista> report = new ReportTassistiProcedureDAO().execute(null);
            view.showReport(report);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private void markCommissionCollected() throws ControllerException {
        SegnaRiscossioneRequest request = view.askCommissionCollectionRequest();
        if (request == null) {
            return;
        }

        try {
            new SegnaRiscossioneProcedureDAO().execute(request);
            view.showCommissionCollectionResult();
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }
}

package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.RegistraClienteProcedureDAO;
import it.uniroma2.dicii.bd.model.dao.RegistraTassistaProcedureDAO;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.dto.RegistraClienteRequest;
import it.uniroma2.dicii.bd.model.dto.RegistraTassistaRequest;
import it.uniroma2.dicii.bd.view.RegistrazioneView;

import java.sql.SQLException;

public class RegistrazioneController {
    private final RegistrazioneView view = new RegistrazioneView();

    public void registerClient() throws ControllerException {
        try {
            ConnectionFactory.changeRole(Role.REGISTRAZIONE);
        } catch (SQLException e) {
            throw new ControllerException(e.getMessage(), e);
        }

        RegistraClienteRequest request = view.askClientRegistrationRequest();

        try {
            int idCliente = new RegistraClienteProcedureDAO().execute(request);
            view.showClientRegistrationResult(idCliente);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public void registerDriver() throws ControllerException {
        try {
            ConnectionFactory.changeRole(Role.REGISTRAZIONE);
        } catch (SQLException e) {
            throw new ControllerException(e.getMessage(), e);
        }

        RegistraTassistaRequest request = view.askDriverRegistrationRequest();
        if (request == null) {
            return;
        }

        try {
            new RegistraTassistaProcedureDAO().execute(request);
            view.showDriverRegistrationResult();
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }
}

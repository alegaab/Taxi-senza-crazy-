package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.LoginProcedureDAO;
import it.uniroma2.dicii.bd.model.domain.Credentials;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.dto.LoginRequest;
import it.uniroma2.dicii.bd.view.LoginView;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController implements Controller {
    private Credentials cred;

    @Override
    public void start() throws ControllerException {
        try {
            ConnectionFactory.changeRole(Role.LOGIN);
        } catch (SQLException e) {
            throw new ControllerException(e.getMessage(), e);
        }

        try {
            cred = LoginView.authenticate();
        } catch (IOException e) {
            throw new ControllerException("Errore nella lettura delle credenziali", e);
        }

        LoginRequest loginRequest = new LoginRequest(cred.getUsername(), cred.getPassword());

        try {
            cred = new LoginProcedureDAO().execute(loginRequest);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public Credentials getCred() {
        return cred;
    }
}

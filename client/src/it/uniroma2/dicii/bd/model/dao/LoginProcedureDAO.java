package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.Credentials;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.dto.LoginRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginProcedureDAO implements GenericProcedureDAO<LoginRequest, Credentials> {

    @Override
    public Credentials execute(LoginRequest input) throws DAOException {
        String username = input.username();
        String password = input.password();

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_login(?,?,?)}");

            cs.setString(1, username);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.INTEGER);

            cs.execute();

            int role = cs.getInt(3);

            return new Credentials(username, password, Role.fromInt(role));
        } catch (SQLException e) {
            throw new DAOException("Login error: " + e.getMessage());
        }
    }
}

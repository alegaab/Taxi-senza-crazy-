package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.RegistraClienteRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class RegistraClienteProcedureDAO implements GenericProcedureDAO<RegistraClienteRequest, Integer> {

    @Override
    public Integer execute(RegistraClienteRequest input) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_registra_cliente(?,?,?,?,?,?,?)}");

            cs.setString(1, input.username());
            cs.setString(2, input.password());
            cs.setString(3, input.nome());
            cs.setString(4, input.cognome());
            cs.setString(5, input.telefono());
            cs.setString(6, input.cc());
            cs.registerOutParameter(7, Types.INTEGER);

            cs.execute();

            return cs.getInt(7);
        } catch (SQLException e) {
            throw new DAOException("Registrazione cliente error: " + e.getMessage());
        }
    }
}

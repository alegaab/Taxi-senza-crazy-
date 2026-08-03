package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.RegistraTassistaRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class RegistraTassistaProcedureDAO implements GenericProcedureDAO<RegistraTassistaRequest, Boolean> {

    @Override
    public Boolean execute(RegistraTassistaRequest input) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_registra_tassista(?,?,?,?,?,?,?,?)}");

            cs.setString(1, input.username());
            cs.setString(2, input.password());
            cs.setString(3, input.patente());
            cs.setString(4, input.nome());
            cs.setString(5, input.cognome());
            cs.setString(6, input.cc());
            cs.setString(7, input.targa());
            cs.setInt(8, input.capacita());

            cs.execute();

            return true;
        } catch (SQLException e) {
            throw new DAOException("Registrazione tassista error: " + e.getMessage());
        }
    }
}

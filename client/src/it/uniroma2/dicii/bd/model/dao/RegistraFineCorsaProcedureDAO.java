package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.RegistraFineCorsaRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class RegistraFineCorsaProcedureDAO implements GenericProcedureDAO<RegistraFineCorsaRequest, Boolean> {

    @Override
    public Boolean execute(RegistraFineCorsaRequest input) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_registra_fine_corsa(?,?,?)}");

            cs.setString(1, input.usernameSessione());
            cs.setInt(2, input.codiceRichiesta());
            cs.setBigDecimal(3, input.importo());

            cs.execute();

            return true;
        } catch (SQLException e) {
            throw new DAOException("Fine corsa error: " + e.getMessage());
        }
    }
}

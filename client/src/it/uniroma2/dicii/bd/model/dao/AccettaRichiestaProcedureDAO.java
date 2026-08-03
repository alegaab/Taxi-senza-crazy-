package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.AccettaRichiestaRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AccettaRichiestaProcedureDAO implements GenericProcedureDAO<AccettaRichiestaRequest, Boolean> {

    @Override
    public Boolean execute(AccettaRichiestaRequest input) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_accetta_richiesta(?,?)}");

            cs.setString(1, input.usernameSessione());
            cs.setInt(2, input.codiceRichiesta());

            cs.execute();

            return true;
        } catch (SQLException e) {
            throw new DAOException("Accettazione richiesta error: " + e.getMessage());
        }
    }
}

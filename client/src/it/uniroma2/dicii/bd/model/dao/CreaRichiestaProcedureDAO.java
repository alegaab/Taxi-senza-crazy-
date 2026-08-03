package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.CreaRichiestaRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class CreaRichiestaProcedureDAO implements GenericProcedureDAO<CreaRichiestaRequest, Integer> {

    @Override
    public Integer execute(CreaRichiestaRequest input) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_crea_richiesta(?,?,?,?)}");

            cs.setString(1, input.usernameSessione());
            cs.setString(2, input.indirizzoPartenza());
            cs.setString(3, input.indirizzoDestinazione());
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();

            return cs.getInt(4);
        } catch (SQLException e) {
            throw new DAOException("Creazione richiesta error: " + e.getMessage());
        }
    }
}

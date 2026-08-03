package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.SegnaRiscossioneRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class SegnaRiscossioneProcedureDAO implements GenericProcedureDAO<SegnaRiscossioneRequest, Boolean> {

    @Override
    public Boolean execute(SegnaRiscossioneRequest input) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_segna_riscossione(?)}");

            cs.setInt(1, input.codiceRichiesta());
            cs.execute();

            return true;
        } catch (SQLException e) {
            throw new DAOException("Riscossione error: " + e.getMessage());
        }
    }
}

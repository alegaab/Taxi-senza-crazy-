package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.RichiestaAttiva;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ListaRichiesteAttiveProcedureDAO implements GenericProcedureDAO<Void, List<RichiestaAttiva>> {

    @Override
    public List<RichiestaAttiva> execute(Void input) throws DAOException {
        List<RichiestaAttiva> requests = new ArrayList<>();

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_lista_richieste_attive()}");

            boolean status = cs.execute();
            if (status) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    Timestamp tsRichiesta = rs.getTimestamp(4);
                    requests.add(new RichiestaAttiva(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            tsRichiesta.toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Lista richieste attive error: " + e.getMessage());
        }

        return requests;
    }
}

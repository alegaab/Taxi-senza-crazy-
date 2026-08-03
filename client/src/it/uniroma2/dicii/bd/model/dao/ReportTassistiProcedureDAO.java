package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.ReportTassista;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportTassistiProcedureDAO implements GenericProcedureDAO<Void, List<ReportTassista>> {

    @Override
    public List<ReportTassista> execute(Void input) throws DAOException {
        List<ReportTassista> report = new ArrayList<>();

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call sp_genera_report_tassisti()}");

            boolean status = cs.execute();
            if (status) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    report.add(new ReportTassista(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getBigDecimal(5),
                            rs.getBigDecimal(6)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Report tassisti error: " + e.getMessage());
        }

        return report;
    }
}

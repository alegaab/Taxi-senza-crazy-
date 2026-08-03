package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.model.domain.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static Connection connection;

    private ConnectionFactory() {}

    static {
        try {
            changeRole(Role.LOGIN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            throw new SQLException("Connessione al database non inizializzata");
        }

        return connection;
    }

    public static void changeRole(Role role) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        try (InputStream input = new FileInputStream("resources/db.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String connectionUrl = properties.getProperty("CONNECTION_URL");
            String user = properties.getProperty(role.name() + "_USER");
            String pass = properties.getProperty(role.name() + "_PASS");

            connection = DriverManager.getConnection(connectionUrl, user, pass);
        } catch (IOException e) {
            throw new SQLException("Errore nella lettura del file db.properties", e);
        }
    }
}

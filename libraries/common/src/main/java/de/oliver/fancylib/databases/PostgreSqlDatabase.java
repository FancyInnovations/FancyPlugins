package de.oliver.fancylib.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class PostgreSqlDatabase implements Database {

    protected final String host;
    protected final String port;
    protected final String database;
    protected final String username;
    protected final String password;
    protected Connection connection;

    public PostgreSqlDatabase(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.connection = null;
    }

    @Override
    public boolean connect() {
        try {
            if (isConnected()) {
                return true;
            }

//            Class.forName("jdbc:postgresql/.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, username, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean close() {
        try {
            if (!isConnected()) {
                return true;
            }

            connection.close();
            connection = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean executeNonQuery(String sql) {
        try {
            connection.createStatement().execute(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet executeQuery(String query) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

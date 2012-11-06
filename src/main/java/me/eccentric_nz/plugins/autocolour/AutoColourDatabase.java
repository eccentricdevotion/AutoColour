package me.eccentric_nz.plugins.autocolour;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AutoColourDatabase {

    private static AutoColourDatabase instance = new AutoColourDatabase();
    public Connection connection = null;
    public Statement statement;
    private AutoColour plugin;

    public static synchronized AutoColourDatabase getInstance() {
        return instance;
    }

    public void setConnection(String path) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTables() {
        try {
            statement = connection.createStatement();
            String queryAutoColour = "CREATE TABLE IF NOT EXISTS autocolour (ac_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, find TEXT, colour TEXT, world TEXT DEFAULT '*')";
            statement.executeUpdate(queryAutoColour);
            ResultSet rsFirst = statement.executeQuery("SELECT * FROM autocolour");
            if (!rsFirst.next()) {
                String queryAddDefault = "INSERT INTO autocolour (find, colour) VALUES ('eccentric_nz', '6')";
                statement.executeUpdate(queryAddDefault);
            }
            rsFirst.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println(AutoColourConstants.MY_PLUGIN_NAME + " Create table error: " + e);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}

package de.oliver.fancylib.databases;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.DriverManager;

public class SqliteDatabase extends MySqlDatabase {

    protected final String path;
    protected final File file;

    public SqliteDatabase(String path) {
        super(null, null, null, null, null);
        this.path = path;
        this.file = new File(path);
    }

    @Override
    public boolean connect() {
        try {
            if (isConnected()) {
                return true;
            }

            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Bukkit.getLogger().warning("Could not create database file.");
                    return false;
                }
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

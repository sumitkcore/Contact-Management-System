package com.cms.dao;

import com.cms.exception.DatabaseException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton Database Connection Manager for JDBC connectivity.
 * Demonstrates:
 * - Singleton Design Pattern
 * - JDBC Driver Loading & Connection Factory
 * - Failover / Graceful Connection Testing & Auto-creation of database
 */
public final class DatabaseConnectionManager {

    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();

    private String url = "jdbc:mysql://localhost:3306/java_fullstack?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String username = "root";
    private String password = "MyNewPrivatePassword_2026!";
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
    private boolean driverLoaded = false;

    private DatabaseConnectionManager() {
        loadProperties();
        try {
            Class.forName(driverClassName);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("Notice: MySQL JDBC driver not found on classpath: " + e.getMessage());
            driverLoaded = false;
        }
    }

    public static DatabaseConnectionManager getInstance() {
        return INSTANCE;
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                this.url = props.getProperty("spring.datasource.url", props.getProperty("db.url", url));
                this.username = props.getProperty("spring.datasource.username", props.getProperty("db.username", username));
                this.password = props.getProperty("spring.datasource.password", props.getProperty("db.password", password));
                this.driverClassName = props.getProperty("spring.datasource.driver-class-name", driverClassName);
            }
        } catch (Exception e) {
            System.err.println("Using default database configuration: " + e.getMessage());
        }
    }

    /**
     * Ensures target database exists on MySQL server prior to schema execution.
     * @return true if database exists or was created, false otherwise.
     */
    public synchronized boolean ensureDatabaseExists() {
        if (!driverLoaded) return false;
        try {
            int qIndex = url.indexOf('?');
            String baseUrl = (qIndex != -1) ? url.substring(0, qIndex) : url;
            int slashIndex = baseUrl.lastIndexOf('/');
            if (slashIndex != -1 && baseUrl.length() > slashIndex + 1) {
                String dbName = baseUrl.substring(slashIndex + 1);
                String rootUrl = baseUrl.substring(0, slashIndex + 1) + (qIndex != -1 ? url.substring(qIndex) : "");
                try (Connection conn = DriverManager.getConnection(rootUrl, username, password);
                     Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    return true;
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Tests if MySQL connection can be successfully established with current credentials.
     */
    public boolean testConnection() {
        return testConnection(url, username, password);
    }

    /**
     * Tests arbitrary MySQL credentials.
     */
    public boolean testConnection(String testUrl, String testUser, String testPass) {
        if (!driverLoaded) return false;
        try (Connection conn = DriverManager.getConnection(testUrl, testUser, testPass)) {
            return conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtains a new JDBC Connection.
     * @throws DatabaseException if connection fails.
     */
    public Connection getConnection() throws DatabaseException {
        if (!driverLoaded) {
            throw new DatabaseException("MySQL JDBC Driver [" + driverClassName + "] is not loaded.");
        }
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL database at " + url + ": " + e.getMessage(), e);
        }
    }

    public synchronized void setConnectionConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDriverLoaded() {
        return driverLoaded;
    }
}

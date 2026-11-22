package com.cms.dao;

/**
 * Factory Pattern implementation to supply the appropriate ContactDAO instance.
 * Demonstrates:
 * - Factory Design Pattern
 * - Dynamic Storage Provider Selection (MySQL JDBC vs Local Persistent Engine)
 */
public class DAOFactory {

    public enum StorageType {
        FILE_PERSISTENT("Local Persistent Engine (Serializable / Zero Config)"),
        MYSQL_DATABASE("MySQL 8.0 JDBC Database");

        private final String description;

        StorageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private static StorageType currentStorageType = StorageType.FILE_PERSISTENT;
    private static ContactDAO activeDaoInstance = null;

    public static synchronized ContactDAO getContactDAO() {
        if (activeDaoInstance == null) {
            activeDaoInstance = createDAO(currentStorageType);
        }
        return activeDaoInstance;
    }

    public static synchronized void setStorageType(StorageType type) {
        if (currentStorageType != type) {
            currentStorageType = type;
            activeDaoInstance = createDAO(type);
        }
    }

    public static StorageType getCurrentStorageType() {
        return currentStorageType;
    }

    private static ContactDAO createDAO(StorageType type) {
        if (type == StorageType.MYSQL_DATABASE) {
            DatabaseConnectionManager connMgr = DatabaseConnectionManager.getInstance();
            if (connMgr.testConnection()) {
                System.out.println("Connecting to MySQL Database DAO...");
                return new MySQLContactDAOImpl();
            } else {
                System.out.println("Notice: MySQL connection not active. Seamlessly defaulting to Local Persistent Engine.");
                currentStorageType = StorageType.FILE_PERSISTENT;
                return new FileContactDAOImpl();
            }
        }
        return new FileContactDAOImpl();
    }
}

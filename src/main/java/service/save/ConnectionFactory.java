package service.save;

import service.property.loader.SystemSettingsLoader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class checks the database if the database is not available,
 * it tries to create it. Similar checks for all
 * necessary tables, and their creation. If it is not
 * possible to create a database or spreadsheet exception
 * occurs emissions
 * @author Gladush Ivan
 * @since 21.03.16.
 */
public class ConnectionFactory {
    private static final String DATABASE_NAME = SystemSettingsLoader.getDataBaseName();
    private static final String SHOW_DATABASE = SystemSettingsLoader.getShowDatabas();
    private static final String DB_DRIVER = SystemSettingsLoader.getDbDriver();
    private static final String DB_USER_NAME = SystemSettingsLoader.getDbUserName();
    private static final String DB_PASSWORD = SystemSettingsLoader.getDbPassword();
    private static final String DB_URL_ADDRESS = SystemSettingsLoader.getDbUrlAddress();
    private static final String CREATE_DB = SystemSettingsLoader.getCreateDataBaseQuery();

    /**
     * Method returns a connection to the database
     */
    public static Connection getConnection() {
        try {
            Class.forName(DB_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL_ADDRESS, initProperties());
            Statement sql = con.createStatement();
            createDbIfNotExist(sql);

            Connection connection = createConnection();
            sql = connection.createStatement();

            createTablesIfNotExist(sql);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("I can't init db" + e.getMessage());
        }

    }

    private static void createTablesIfNotExist(Statement sql) throws SQLException {
        String[] needTables = SystemSettingsLoader.getNeedTables();
        ResultSet resultSet = sql.executeQuery("Show Tables;");
        List<String> existTables = new ArrayList<>();
        while (resultSet.next()) {
            existTables.add(resultSet.getString(1));
        }
        for (String s : needTables) {
            if (!existTables.contains(s)) {
                sql.execute(SystemSettingsLoader.getProperty("create." + s));
            }
        }
    }

    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_ADDRESS + DATABASE_NAME, DB_USER_NAME, DB_PASSWORD);
    }

    private static java.util.Properties initProperties() {
        java.util.Properties connectionProperties = new java.util.Properties();
        connectionProperties.put("driver", DB_DRIVER);
        connectionProperties.put("user", DB_USER_NAME);
        connectionProperties.put("password", DB_PASSWORD);
        return connectionProperties;
    }

    private static void createDbIfNotExist(Statement sql) throws SQLException {
        ResultSet resultSet = sql.executeQuery(SHOW_DATABASE);
        while (resultSet.next()) {
            if (DATABASE_NAME.equals(resultSet.getString(1))) {
                return;
            }
        }
        sql.execute(CREATE_DB);

    }
}

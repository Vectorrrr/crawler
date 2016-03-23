package service.storage;

import service.property.loader.PropertyLoader;

import java.sql.*;


/**
 * This class checks the database if the database is not available,
 * it tries to create it. If it is not
 * possible to create a database or spreadsheet exception
 * occurs emissions
 * @author Gladush Ivan
 * @since 21.03.16.
 */
public class ConnectionFactory {
    private static final String DATABASE_NAME = PropertyLoader.getProperty("database.name");
    private static final String SHOW_DATABASE = PropertyLoader.getProperty("show.database.query");
    private static final String DB_DRIVER = PropertyLoader.getProperty("db.driver.name");
    private static final String DB_USER_NAME = PropertyLoader.getProperty("db.user.name");
    private static final String DB_PASSWORD = PropertyLoader.getProperty("db.user.password");
    private static final String DB_URL_ADDRESS = PropertyLoader.getProperty("db.url.address");
    private static final String CREATE_DB = PropertyLoader.getProperty("create.database.query");
    private static Connection connectionToDataBase;

    //todo perhaps worth making the lazy loading
    static {
        try {
            Class.forName(DB_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL_ADDRESS, initProperties());
            createDbIfNotExist(connection);
            connection.close();
            connectionToDataBase = createConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("I can't init db" + e.getMessage());
        }
    }

    /**
     * Method returns a connection to the database
     */
    public static Connection getConnection() {

        return connectionToDataBase;

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

    private static void createDbIfNotExist(Connection connection) throws SQLException {
        Statement sql = connection.createStatement();
        ResultSet resultSet = sql.executeQuery(SHOW_DATABASE);
        while (resultSet.next()) {
            if (DATABASE_NAME.equals(resultSet.getString(1))) {
                return;
            }
        }
        sql.execute(CREATE_DB);
    }
}
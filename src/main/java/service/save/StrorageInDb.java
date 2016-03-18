package service.save;

import service.PropertiesLoader;

import java.sql.*;

/**
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public class StrorageInDb {
    private static final String DATABASE_NAME = PropertiesLoader.getDataBaseName();

    /**
     * This block create connection to db
     * and
     * */
    static {
        String driver = PropertiesLoader.getDbDriver();
        String user = PropertiesLoader.getDbUserName();
        String password = PropertiesLoader.getDbPassword();
        String dbUrl = PropertiesLoader.getDbUrlAddress();
        java.util.Properties connectionProperties = new java.util.Properties();
        connectionProperties.put("driver", driver);
        connectionProperties.put("user", user);
        connectionProperties.put("password", password);


        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(dbUrl, connectionProperties);
            Statement sql = con.createStatement();
            String queryCheckExistDb = "SHOW DATABASES";
            ResultSet rset = sql.executeQuery(queryCheckExistDb);

            boolean dbExist=false;
            while(rset.next()) {
                if (DATABASE_NAME.equals(rset.getString(1))) {
                    dbExist = true;
                    break;
                }
            }
            if(!dbExist){
                sql.execute("CREATE DATABASE Crawler");
            }
            connectionProperties=DriverManager.getConnection(dbUrl+DATABASE_NAME,user,password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package service.save;

import service.property.loader.SqlQueryLoader;

import java.sql.*;


/**
 * Class allow save download page with link in db
 * @author Gladush Ivan
 * @since 18.03.16.
 */

public class StorageInDb implements Storage {
    private static final String EXCEPTION_CLOSE_CONNECTION = "I can't close connection";
    private static final String EXCEPTION_DO_SQL = "I can't do sql query ";
    private static final String QUOTE = "'";
    private static final String EMPTY_STRING = "";
    private static final String SELECT_ID_ROOT_PAGE = SqlQueryLoader.selectIdRootPage();
    private static final String INSERT_LINK = SqlQueryLoader.insertLink();
    private static final String INSERT_LINK_IN_PAGE = SqlQueryLoader.insertLinkInPage();
    private static final String INSERT_ROOT_PAGE = SqlQueryLoader.insertRootPage();
    private static final String INSERT_CONTENT = SqlQueryLoader.writeContent();
    private static final String SELECT_MAX_ID_LINK = SqlQueryLoader.selectMaxIdLink();
    private static Connection connection = ConnectionFactory.getConnection();

    /**
     * method  closes the connection to the database
     * if you call a method on one instance this class,
     * all classes will cease the ability to save
     */

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException(EXCEPTION_CLOSE_CONNECTION + e.getMessage());
        }
    }

    /**
     * The method checks for this link in the database,
     * if it is not it creates a record in the database
     * and returns the primary key of the record
     */
    @Override
    public String writePage(String siteURL, String pageName) {
        try {
            String string = getResultSet(siteURL);
            if (!EMPTY_STRING.equals(string)) {
                return string;
            }
            insertPage(siteURL);
            return getResultSet(siteURL);
        } catch (SQLException e) {
            throw new IllegalArgumentException(EXCEPTION_DO_SQL + e.getMessage());
        }

    }

    /**
     * Method retains all references to a particular
     * page received in the database
     */
    @Override
    public void writeLinks(String rootPage, String[] value) {
        try (Statement statement = connection.createStatement()) {
            int t = getLastLinkId();
            for (String s : value) {
                statement.executeUpdate(String.format(INSERT_LINK, s));
                statement.executeUpdate(String.format(INSERT_LINK_IN_PAGE, rootPage, ++t));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(EXCEPTION_DO_SQL + e.getMessage());
        }
    }

    /**
     * Method stores the content received from the particular page
     */
    @Override
    public void writeContent(String rootPage, String content) {
        if (content.length() < 1000) {
            content = content.replaceAll("'", "\\\\" + QUOTE);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(String.format(INSERT_CONTENT, content, Integer.valueOf(rootPage)));
            } catch (SQLException e) {
                System.out.println(content);
                throw new IllegalStateException(EXCEPTION_DO_SQL + e.getMessage());
            }
        }
    }

    /**
     * We support a database so this method
     * does not require the implementation of
     */
    @Override
    public String createDir(String path) {
        return EMPTY_STRING;
    }


    private String getResultSet(String siteURL) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format(SELECT_ID_ROOT_PAGE, siteURL));
            return resultSet.next() ? resultSet.getString(1) : EMPTY_STRING;
        }
    }

    private void insertPage(String siteURL) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format(INSERT_ROOT_PAGE, siteURL));

        }
    }

    private int getLastLinkId() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return Integer.valueOf(statement.executeQuery(SELECT_MAX_ID_LINK).getString(1));
        }
    }

}
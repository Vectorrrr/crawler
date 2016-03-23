package service.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static service.property.loader.PropertyLoader.*;


/**
 * Class allow save download page with link in db
 * @author Gladush Ivan
 * @since 18.03.16.
 */

public class StorageInDb implements Storage {
    private static final String EXCEPTION_CLOSE_CONNECTION = "I can't close connection";
    private static final String EXCEPTION_DO_SQL = "I can't do sql query ";
    private static final String EXCEPTION_CREATE_STATEMENT = "I can't create statement";
    private static final String EXCEPTION_CREATE_TABLES="I can't create tables";
    private static final String QUOTE = "'";
    private static final String EMPTY_STRING = "";
    private static Connection connection = ConnectionFactory.getConnection();

    private PreparedStatement insertInLink;
    private PreparedStatement insertInLinkInPage;
    private PreparedStatement insertInContents;
    private PreparedStatement selectIdRootPage;
    private PreparedStatement insertInRootPages;
    private PreparedStatement selectMaxIdLink;

    /**
     * gets a connection to the database and
     * verifies that all required tables, if
     * the table is not present, create it.
     * Upon receipt of the connection or create
     * a database may IllegalArgumentException
     * */
    static {
        try (Statement sql = connection.createStatement()) {
            ResultSet resultSet = sql.executeQuery(getProperty("show.tables.query"));
            List<String> existTables = new ArrayList<>();
            while (resultSet.next()) {
                existTables.add(resultSet.getString(1));
            }
            for (String s : getNeedTables()) {
                if (!existTables.contains(s)) {
                    sql.execute(getProperty("create." + s));
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(EXCEPTION_CREATE_TABLES);
        }
    }


    /**
     * method  closes the connection to the database
     * if you call a method on one instance this class,
     * all classes will cease the ability to save
     */
    public StorageInDb() {
        createStatements();
    }

    /**
     * method creates a state for all queries
     * implemented in this class
     * */
    private void createStatements() {
        try {
            insertInRootPages = connection.prepareStatement(getProperty("insert.root.page"));
            insertInLink = connection.prepareStatement(getProperty("insert.link.query"));
            insertInLinkInPage = connection.prepareStatement(getProperty("insert.link.in.page"));
            insertInContents = connection.prepareStatement(getProperty("insert.root.content"));
            selectIdRootPage = connection.prepareStatement(getProperty("select.id.root.page"));
            selectMaxIdLink = connection.prepareStatement(getProperty("select.max.id.link"));
        } catch (SQLException e) {
            throw new IllegalStateException(EXCEPTION_CREATE_STATEMENT + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            closeStatement();
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
        try {
            int t = getLastLinkId();
            for (String s : value) {
                if (s.length() > 50) {
                    setInsertInLink(s);
                    insertInLink.executeUpdate();

                    t = setInsertLinkInPage(rootPage, t);
                    insertInLinkInPage.executeUpdate();
                }
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
            try {
                setInsertInContents(rootPage, content);
                insertInContents.executeUpdate();
            } catch (SQLException e) {
                System.out.println(content);
                throw new IllegalStateException(EXCEPTION_DO_SQL + e.getMessage());
            }
        }
    }


    private String getResultSet(String siteURL) throws SQLException {
        setSelectInRootPage(siteURL);
        ResultSet resultSet = selectIdRootPage.executeQuery();
        return resultSet.next() ? resultSet.getString(1) : EMPTY_STRING;
    }

    private void insertPage(String siteURL) throws SQLException {
        setInsertInRootPage(siteURL);
        insertInRootPages.executeUpdate();
    }

    private int getLastLinkId() throws SQLException {
        ResultSet resultSet = selectMaxIdLink.executeQuery();
        resultSet.next();
        String value = resultSet.getString(1);
        return value != null ? Integer.valueOf(value) + 1 : 1;
    }

    /**
     * method close a state for all queries
     * implemented in this class
     * */
    private void closeStatement() throws SQLException {
        insertInLink.close();
        insertInLinkInPage.close();
        insertInContents.close();
        selectIdRootPage.close();
        insertInRootPages.close();
        selectMaxIdLink.close();
    }

    private void setInsertInLink(String s) throws SQLException {
        insertInLink.setString(1, s);
    }

    private int setInsertLinkInPage(String rootPage, int t) throws SQLException {
        insertInLinkInPage.setString(1, rootPage);
        insertInLinkInPage.setInt(2, t++);
        return t;
    }

    private void setInsertInContents(String rootPage, String content) throws SQLException {
        content = content.replaceAll("'", "\\\\" + QUOTE);
        insertInContents.setString(1, content);
        insertInContents.setInt(2, Integer.valueOf(rootPage));
    }

    private void setSelectInRootPage(String siteURL) throws SQLException {
        selectIdRootPage.setString(1, siteURL);
    }

    private void setInsertInRootPage(String siteURL) throws SQLException {
        insertInRootPages.setString(1, siteURL);
    }

}
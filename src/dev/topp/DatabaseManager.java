package dev.topp;

import dev.topp.LoggingManager.LoggingLevel;
import org.apache.http.NameValuePair;

import java.sql.*;
import java.util.List;

public class DatabaseManager implements AutoCloseable {
    private Connection conn = null;
    private Statement stmt = null;

    public DatabaseManager(String dbHost, String dbPort, String dbName, String dbUser, String dbPass) {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Open a connection
            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName, dbUser, dbPass);

            stmt = conn.createStatement();
        } catch (Exception ex) {
            LoggingManager.log(LoggingLevel.CRITICAL, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "DatabaseManager(String, String, String, String, String)", "DatabaseManager");
        }
    }

    @Override
    public void close() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            LoggingManager.log(LoggingLevel.WARNING, "Could not close Statement", "close()", "DatabaseManager");
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            LoggingManager.log(LoggingLevel.WARNING, "Could not close Connection", "close()", "DatabaseManager");
        }
    }

    public void beginTransaction() {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            LoggingManager.log(LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "beginTransaction()", "DatabaseManager");
        }
    }

    public void setTransactionSuccessful() {
        try {
            if (!conn.getAutoCommit())
                conn.commit();
        } catch (SQLException ex) {
            LoggingManager.log(LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "setTransactionSuccessful()", "DatabaseManager");
        }
    }

    public void endTransaction() {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.WARNING, e.getMessage(), "endTransaction()", "DatabaseManager");
        }
    }

    public boolean insert(String tableName, List<NameValuePair> nameValuePairs) {
        String sql = "";
        try {
            sql = "INSERT INTO " + tableName + " (";
            for (NameValuePair nameValuePair : nameValuePairs)
                sql += nameValuePair.getName() + ", ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += ") VALUES (";
            for (NameValuePair nameValuePair : nameValuePairs)
                sql += "'" + nameValuePair.getValue() + "', ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += ");";

            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "insert(String, List<NameValuePair>)", "DatabaseManager");
        }
        return false;
    }

    public boolean update(String tableName, List<NameValuePair> updateValuePairs, List<NameValuePair> whereValuePairs) {
        String sql = "";
        try {
            sql = "UPDATE " + tableName + " SET ";
            for (NameValuePair nameValuePair : updateValuePairs)
                sql += nameValuePair.getName() + " = '" + nameValuePair.getValue() + "', ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            if (whereValuePairs != null) {
                sql += " WHERE ";
                for (NameValuePair nameValuePair : whereValuePairs)
                    sql += nameValuePair.getName() + " = '" + nameValuePair.getValue() + "' AND ";
                sql = sql.substring(0, sql.lastIndexOf(" AND"));
            }
            sql += ";";

            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "update(String, List<NameValuePair>), List<NameValuePair>)", "DatabaseManager");
        }
        return false;
    }

    public boolean insertOrUpdate(String tableName, String primaryKey, List<NameValuePair> nameValuePairs) {
        String sql = "";
        try {
            sql = "INSERT INTO " + tableName + " (";
            for (NameValuePair nameValuePair : nameValuePairs)
                sql += nameValuePair.getName() + ", ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += ") VALUES (";
            for (NameValuePair nameValuePair : nameValuePairs)
                sql += "'" + nameValuePair.getValue() + "', ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += ") ON DUPLICATE KEY UPDATE ";
            for (NameValuePair nameValuePair : nameValuePairs)
                if (!nameValuePair.getName().equals(primaryKey))
                    sql += nameValuePair.getName() + " = VALUES(" + nameValuePair.getName() + "), ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += ";";

            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "insertOrUpdate(String, String, List<NameValuePair>", "DatabaseManager");
        }
        return false;
    }

    public ResultSet select(String tableName, String[] selectStrings, List<NameValuePair> whereValuePairs) {
        String sql = "";

        Statement stmt;
        try {
            stmt = conn.createStatement();
            sql = "SELECT ";
            for (String selectString : selectStrings)
                sql += selectString + ", ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += " FROM " + tableName;
            if (whereValuePairs != null) {
                sql += " WHERE ";
                for (NameValuePair nameValuePair : whereValuePairs)
                    sql += nameValuePair.getName() + " = '" + nameValuePair.getValue() + "' AND ";
                sql = sql.substring(0, sql.lastIndexOf(" AND"));
            }
            sql += ";";

            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "select(String, String[], List<NameValuePair>)", "DatabaseManager");
        }
        return null;
    }

    public boolean createTable(String tableName, List<NameValuePair> nameValuePairs) {
        String sql = "";
        try {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
            for (NameValuePair nameValuePair : nameValuePairs)
                sql += nameValuePair.getName() + " " + nameValuePair.getValue() + ", ";
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql += ");";

            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "createTable(String, List<NameValuePair>)", "DatabaseManager");
        }
        return false;
    }

    public boolean dropTable(String tableName) {
        String sql = "";
        try {
            sql = "DROP TABLE IF EXISTS " + tableName + ";";

            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "dropTable(String)", "DatabaseManager");
        }
        return false;
    }

    public boolean customQuery(String sql) {
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "customQuery(String)", "DatabaseManager");
        }
        return false;
    }

    public ResultSet customSelect(String sql) {
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            LoggingManager.log(LoggingLevel.CRITICAL, e.getMessage() + "->" + sql, "customSelect(String)", "DatabaseManager");
        }
        return null;
    }
}

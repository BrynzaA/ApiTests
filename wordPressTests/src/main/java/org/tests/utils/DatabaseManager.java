package org.tests.utils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseManager {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    ConfigReader.getDbUrl(),
                    ConfigReader.getDbUsername(),
                    ConfigReader.getDbPassword()
            );
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Map<String, Object> getPostById(Integer postId) throws SQLException {
        String query = "SELECT * FROM wp_posts WHERE ID = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> postData = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = null;
                    try {
                        value = rs.getObject(i);
                        if (rs.wasNull()) {
                            value = null;
                        }
                    } catch (SQLException e) {
                        if (e.getMessage().contains("Zero date value prohibited")) {
                            value = null;
                        } else {
                            throw e;
                        }
                    }
                    postData.put(columnName, value);
                }
                return postData;
            }
        }
        return null;
    }

    public static boolean isPostExists(int postId) throws SQLException {
        String query = "SELECT COUNT(*) FROM wp_posts WHERE ID = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public static void cleanupTestData() throws SQLException {
        String query = "DELETE FROM wp_posts WHERE post_title LIKE 'Test Post%' OR post_content LIKE '%test content%'";
        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate(query);
        }
    }
}

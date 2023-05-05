package edu.ita.honorio.facerecognitionserver;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database implements AutoCloseable {
    private Connection m_connection = null;
    
    public Database(String jarPath) throws SQLException {
        String path = Paths.get(jarPath, Enums.SRV_RESOURCES_KEY, Enums.DB_DATABASE_KEY).toString();
        Properties prop = new Properties();
        prop.setProperty(Enums.DB_JOURNAL_MODE_KEY, Enums.DB_WAL_KEY);
        m_connection = DriverManager.getConnection("jdbc:sqlite:" + path, prop);
    }
    
    @Override
    public void close() throws SQLException {
        m_connection.close();
    }
    
    public void create() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + Enums.DB_TABLE_KEY + " (\n"
                + Enums.DB_FIELD_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + Enums.DB_FIELD_KEY_KEY + " TEXT NOT NULL,\n"
                + Enums.DB_FIELD_FIRST_NAME_KEY + " TEXT NOT NULL,\n"
                + Enums.DB_FIELD_LAST_NAME_KEY + " TEXT NOT NULL,\n"
                + Enums.DB_FIELD_SECOND_LAST_NAME_KEY + " TEXT NOT NULL,\n"
                + Enums.DB_FIELD_TEMPLATE_KEY + " TEXT NOT NULL,\n"
                + Enums.DB_FIELD_THUMBNAIL_KEY + " TEXT NOT NULL);";
        try (Statement stmt = m_connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    public boolean exists(String key) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM " 
                + Enums.DB_TABLE_KEY + " WHERE " 
                + Enums.DB_FIELD_KEY_KEY + " = ?";
        try (PreparedStatement pstmt = m_connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                throw new SQLException(Enums.DB_UNEXPECTED_EMPTY_KEY);
            }
        }
    }
    
    public void insert(EnrollRequest request, EnrollResponseNative response) throws SQLException {
        String sql = "INSERT INTO " + Enums.DB_TABLE_KEY + "("
                + Enums.DB_FIELD_KEY_KEY + ", " 
                + Enums.DB_FIELD_FIRST_NAME_KEY + ", " 
                + Enums.DB_FIELD_LAST_NAME_KEY + ", " 
                + Enums.DB_FIELD_SECOND_LAST_NAME_KEY + ", " 
                + Enums.DB_FIELD_TEMPLATE_KEY + ", "
                + Enums.DB_FIELD_THUMBNAIL_KEY + ") VALUES(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = m_connection.prepareStatement(sql)) {
            pstmt.setString(1, request.key);
            pstmt.setString(2, request.firstName);
            pstmt.setString(3, request.lastName);
            pstmt.setString(4, request.secondLastName);
            pstmt.setString(5, response.template);
            pstmt.setString(6, response.thumbnail);
            pstmt.executeUpdate();
        }
    }
    
    public List<Record> selectAll() throws SQLException {
        List<Record> records = new ArrayList<>();
        try (Statement stmt = m_connection.createStatement()) {
            String sql = "SELECT " 
                    + Enums.DB_FIELD_ID_KEY + ", " 
                    + Enums.DB_FIELD_TEMPLATE_KEY + " FROM " 
                    + Enums.DB_TABLE_KEY;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    records.add(new Record(rs.getInt(1), rs.getString(2)));
                }
            }
        }
        return records;
    }
    
    public RecognizeResponse select(int id, String message, float score) throws SQLException {
        String sql = "SELECT " 
                + Enums.DB_FIELD_KEY_KEY + ", " 
                + Enums.DB_FIELD_FIRST_NAME_KEY + ", " 
                + Enums.DB_FIELD_LAST_NAME_KEY + ", " 
                + Enums.DB_FIELD_SECOND_LAST_NAME_KEY + ", " 
                + Enums.DB_FIELD_THUMBNAIL_KEY + " FROM " 
                + Enums.DB_TABLE_KEY + " WHERE " 
                + Enums.DB_FIELD_ID_KEY + " = ?";
        try (PreparedStatement pstmt = m_connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RecognizeResponse(message, 
                                                 score, 
                                                 rs.getString(1), 
                                                 rs.getString(2), 
                                                 rs.getString(3), 
                                                 rs.getString(4), 
                                                 rs.getString(5));
                }
                throw new SQLException(Enums.DB_UNEXPECTED_EMPTY_KEY);
            }
        }
    }
}

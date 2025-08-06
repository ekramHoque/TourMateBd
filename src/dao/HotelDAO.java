package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private DBConnection dbConnection;

    public HotelDAO() {
        this.dbConnection = new DBConnection();
    }

    public boolean saveHotel(int tourPlaceId, String name, String location, String description, String contact) {
        String sql = "INSERT INTO hotels (tour_place_id, name, location, description, contact, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tourPlaceId);
            stmt.setString(2, name);
            stmt.setString(3, location);
            stmt.setString(4, description);
            stmt.setString(5, contact);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving hotel: " + e.getMessage());
            return false;
        }
    }

    public String[] getHotels() {
        List<String> hotels = new ArrayList<>();
        String sql = "SELECT name FROM hotels";
        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hotels.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving hotels: " + e.getMessage());
        }
        return hotels.toArray(new String[0]);
    }

    public int getHotelId(String name) {
        String sql = "SELECT id FROM hotels WHERE name = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving hotel ID: " + e.getMessage());
        }
        return -1;
    }

    public String[] getHotelsByTourPlaceId(int tourPlaceId) {
        List<String> hotels = new ArrayList<>();
        String sql = "SELECT name FROM hotels WHERE tour_place_id = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tourPlaceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hotels.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving hotels by tour place ID: " + e.getMessage());
            e.printStackTrace();
        }
        return hotels.toArray(new String[0]);
    }
}
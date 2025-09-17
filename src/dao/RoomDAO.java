package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private DBConnection dbConnection;

    public RoomDAO() {
        this.dbConnection = new DBConnection();
    }

    public boolean saveRoom(int hotelId, String roomType, String price, String capacity) {
        String sql = "INSERT INTO rooms (hotel_id, room_type, price, capacity, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            stmt.setString(2, roomType);
            stmt.setString(3, price);
            stmt.setString(4, capacity);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving room: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> getRoomsByHotelId(int hotelId) {
        List<Object[]> rooms = new ArrayList<>();
        String sql = "SELECT id, room_type, price, capacity, availability FROM rooms WHERE hotel_id = ? AND availability = TRUE";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] room = {rs.getInt("id"), rs.getString("room_type") + " - " + rs.getString("price") + " - " + rs.getString("capacity"),
                            rs.getBoolean("availability")};
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    public int getRoomIdByFullName(String fullName) {
        String sql = "SELECT id FROM rooms WHERE CONCAT(room_type, ' - ', price, ' - ', capacity) = ? AND availability = TRUE";
        System.out.println("Looking up room ID for fullName: " + fullName);
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    System.out.println("Found room ID: " + id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving room ID for " + fullName + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("No room ID found for " + fullName);
        return -1;
    }
}
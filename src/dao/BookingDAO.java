package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    public List<Object[]> getBookingsByUserId(int userId) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT id, user_id, tour_place_id, hotel_id, room_id, status, booking_date FROM bookings WHERE user_id = ?";
        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] booking = {rs.getInt("id"), rs.getInt("user_id"), rs.getInt("tour_place_id"),
                            rs.getInt("hotel_id"), rs.getInt("room_id"), rs.getString("status"), rs.getTimestamp("booking_date")};
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings for user: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Object[]> searchBookingsByUserId(int userId, String searchTerm) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT id, user_id, tour_place_id, hotel_id, room_id, status, booking_date FROM bookings WHERE user_id = ? " +
                "AND (hotel_id LIKE ? OR room_id LIKE ? OR status LIKE ?)";
        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + searchTerm + "%");
            stmt.setString(3, "%" + searchTerm + "%");
            stmt.setString(4, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] booking = {rs.getInt("id"), rs.getInt("user_id"), rs.getInt("tour_place_id"),
                            rs.getInt("hotel_id"), rs.getInt("room_id"), rs.getString("status"), rs.getTimestamp("booking_date")};
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Object[]> getAllBookings(String filter) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT id, user_id, tour_place_id, hotel_id, room_id, status, booking_date FROM bookings";
        System.out.println("Executing SQL: " + sql);
        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] booking = {rs.getInt("id"), rs.getInt("user_id"), rs.getInt("tour_place_id"),
                            rs.getInt("hotel_id"), rs.getInt("room_id"), rs.getString("status"), rs.getTimestamp("booking_date")};
                    bookings.add(booking);
                    System.out.println("Found booking: ID=" + rs.getInt("id") + ", User ID=" + rs.getInt("user_id") +
                            ", Tour Place ID=" + rs.getInt("tour_place_id") + ", Hotel ID=" + rs.getInt("hotel_id") +
                            ", Room ID=" + rs.getInt("room_id") + ", Status=" + rs.getString("status") +
                            ", Booking Date=" + rs.getTimestamp("booking_date"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bookings: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Total bookings retrieved: " + bookings.size());
        return bookings;
    }

    public boolean createBooking(int userId, int tourPlaceId, int hotelId, int roomId, String bookingDate, String status) {
        String sql = "INSERT INTO bookings (user_id, tour_place_id, hotel_id, room_id, booking_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("Executing booking SQL: " + sql + " with values: " + userId + ", " + tourPlaceId + ", " + hotelId + ", " + roomId + ", " + bookingDate + ", " + status);
        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, tourPlaceId);
            stmt.setInt(3, hotelId);
            stmt.setInt(4, roomId);
            stmt.setString(5, bookingDate);
            stmt.setString(6, status);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Booking execution result: " + (rowsAffected > 0 ? "Success" : "Failed") + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookingStatus(int bookingId, String newStatus) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        System.out.println("Executing status update SQL: " + sql + " with values: " + newStatus + ", " + bookingId);
        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Status update result: " + (rowsAffected > 0 ? "Success" : "Failed") + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
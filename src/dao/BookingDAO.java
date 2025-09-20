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


    public List<Object[]> getAllBookings(String filter) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT id, user_id, tour_place_id, hotel_id, room_id, status, booking_date FROM bookings";

        // Add WHERE clause if filter is not "All"
        if (!"All".equalsIgnoreCase(filter)) {
            sql += " WHERE status = ?";
        }

        System.out.println("Executing SQL: " + sql);

        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (!"All".equalsIgnoreCase(filter)) {
                stmt.setString(1, filter); // set status parameter
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] booking = {
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("tour_place_id"),
                            rs.getInt("hotel_id"),
                            rs.getInt("room_id"),
                            rs.getString("status"),
                            rs.getTimestamp("booking_date")
                    };
                    bookings.add(booking);
                    System.out.println("Found booking: ID=" + rs.getInt("id") + ", User ID=" + rs.getInt("user_id") +
                            ", Status=" + rs.getString("status"));
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

    public List<Object[]> getAllBookingsDetailed(String filter) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT b.id, u.name, u.email, tp.name, h.name, r.room_type, b.status, b.booking_date " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN tour_places tp ON b.tour_place_id = tp.id " +
                "JOIN hotels h ON b.hotel_id = h.id " +
                "JOIN rooms r ON b.room_id = r.id";

        if (!"All".equalsIgnoreCase(filter)) {
            sql += " WHERE b.status = ?";
        }

        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (!"All".equalsIgnoreCase(filter)) {
                stmt.setString(1, filter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] booking = {
                            rs.getInt(1),     // booking_id
                            rs.getString(2),  // user_name
                            rs.getString(3),  // user_email
                            rs.getString(4),  // tour_place_name
                            rs.getString(5),  // hotel_name
                            rs.getString(6),  // room_type
                            rs.getString(7),  // status
                            rs.getTimestamp(8) // booking_date
                    };
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }


    public List<Object[]> getBookingsByUserIdDetailed(int userId) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT b.id, tp.name, h.name, r.room_type, b.status, b.booking_date " +
                "FROM bookings b " +
                "JOIN tour_places tp ON b.tour_place_id = tp.id " +
                "JOIN hotels h ON b.hotel_id = h.id " +
                "JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.user_id = ?";

        try (Connection conn = new DBConnection().connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] booking = {
                            rs.getInt(1),     // booking_id
                            rs.getString(2),  // tour_place_name
                            rs.getString(3),  // hotel_name
                            rs.getString(4),  // room_type
                            rs.getString(5),  // status
                            rs.getTimestamp(6) // booking_date
                    };
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }



}
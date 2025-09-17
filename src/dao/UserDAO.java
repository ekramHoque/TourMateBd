package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private DBConnection dbConnection;

    public UserDAO() {
        this.dbConnection = new DBConnection();
    }

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (name, email, password, is_admin, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdmin());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Duplicate entry for name/email: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        System.out.println("Executing query for email: " + (email != null ? email : "null")); // Log the input
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (email != null) {
                System.out.println("Setting parameter 1 to: " + email); // Log before setting
                stmt.setString(1, email); // Explicitly set the parameter
                System.out.println("Parameter set, executing query...");
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"));
                        user.setAdmin(rs.getBoolean("is_admin"));
                        System.out.println("User retrieved: ID = " + user.getId() + ", Email = " + user.getEmail() +
                                ", Password = " + user.getPassword() + ", isAdmin = " + user.isAdmin());
                        return user;
                    }
                    System.out.println("No user found for email: " + email);
                }
            } else {
                System.err.println("Email parameter is null");
            }
        } catch (SQLException e) {
            System.err.println("SQL error retrieving user: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers(String filter) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                if (filter.equals("All") || (filter.equals("Admin") && isAdmin) || (filter.equals("User") && !isAdmin)) {
                    User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"));
                    user.setAdmin(isAdmin);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public int getUserId(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 if user not found or error occurs
    }
}
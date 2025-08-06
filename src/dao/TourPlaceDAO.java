package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourPlaceDAO {
    private DBConnection dbConnection;

    public TourPlaceDAO() {
        this.dbConnection = new DBConnection();
    }

    public boolean saveTourPlace(String name, String description, String address, String latitude, String longitude) {
        String sql = "INSERT INTO tour_places (name, description, address, latitude, longitude, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, address);
            stmt.setString(4, latitude);
            stmt.setString(5, longitude);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving tour place: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean savePhotos(int tourPlaceId, String[] photoUrls) {
        String sql = "INSERT INTO photos (tour_place_id, photo_url, created_at) VALUES (?, ?, NOW())";
        try (Connection conn = dbConnection.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (String url : photoUrls) {
                    if (!url.isEmpty()) {
                        stmt.setInt(1, tourPlaceId);
                        stmt.setString(2, url);
                        stmt.addBatch();
                    }
                }
                int[] rowsAffected = stmt.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error saving photos: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error saving photos: " + e.getMessage());
            return false;
        }
    }

    public String[] getTourPlaces() {
        List<String> places = new ArrayList<>();
        String sql = "SELECT name FROM tour_places";
        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                places.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tour places: " + e.getMessage());
            e.printStackTrace();
        }
        return places.toArray(new String[0]);
    }

    public int getTourPlaceId(String name) {
        String sql = "SELECT id FROM tour_places WHERE name = ?";
        System.out.println("Attempting to get ID for name: '" + name + "'");
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (name != null && !name.trim().isEmpty()) {
                stmt.setString(1, name);
                System.out.println("Executing query with name: " + name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            } else {
                System.out.println("Name is null or empty, returning -1");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tour place ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Object[]> getAllTourPlacesWithPhotos() {
        List<Object[]> tourPlaces = new ArrayList<>();
        String sql = "SELECT tp.id, tp.name, tp.description, tp.address, tp.latitude, tp.longitude, p.photo_url " +
                "FROM tour_places tp LEFT JOIN photos p ON tp.id = p.tour_place_id " +
                "ORDER BY tp.created_at DESC";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int currentId = -1;
            Object[] currentPlace = null;
            List<String> photos = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                if (currentId != id) {
                    if (currentPlace != null) {
                        currentPlace[6] = photos.toArray(new String[0]); // Store up to 3 photos
                        tourPlaces.add(currentPlace);
                    }
                    currentPlace = new Object[7]; // id, name, description, address, latitude, longitude, photos[]
                    currentPlace[0] = id;
                    currentPlace[1] = rs.getString("name");
                    currentPlace[2] = rs.getString("description");
                    currentPlace[3] = rs.getString("address");
                    currentPlace[4] = rs.getString("latitude");
                    currentPlace[5] = rs.getString("longitude");
                    photos = new ArrayList<>();
                    currentId = id;
                }
                String photoUrl = rs.getString("photo_url");
                if (photoUrl != null && photos.size() < 3) {
                    photos.add(photoUrl);
                }
            }
            if (currentPlace != null) {
                currentPlace[6] = photos.toArray(new String[0]);
                tourPlaces.add(currentPlace);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all tour places with photos: " + e.getMessage());
            e.printStackTrace();
        }
        return tourPlaces;
    }

    public List<Object[]> searchTourPlaces(String searchTerm) {
        List<Object[]> tourPlaces = new ArrayList<>();
        String sql = "SELECT id, name, address FROM tour_places WHERE name LIKE ? OR address LIKE ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] place = {rs.getInt("id"), rs.getString("name"), rs.getString("address")};
                    tourPlaces.add(place);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching tour places: " + e.getMessage());
            e.printStackTrace();
        }
        return tourPlaces;
    }
}
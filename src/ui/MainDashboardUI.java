package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import dao.*;

public class MainDashboardUI extends JFrame {
    private JPanel contentPanel;
    private JButton selectedButton = null;
    private UserDAO userDAO;
    private BookingDAO bookingDAO;
    private TourPlaceDAO tourPlaceDAO;
    private HotelDAO hotelDAO;
    private RoomDAO roomDAO;
    private int currentUserId; // Store logged-in user ID

    public MainDashboardUI(int userId) {
        this.currentUserId = userId;
        this.userDAO = new UserDAO();
        this.bookingDAO = new BookingDAO();
        this.tourPlaceDAO = new TourPlaceDAO();
        this.hotelDAO = new HotelDAO();
        this.roomDAO = new RoomDAO();
        initializeUI();
    }

    public void displayDashboard() {
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("User Dashboard - TourMateBD");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(3, 252, 236));
        mainPanel.setLayout(new BorderLayout());

        // Left Panel (Menu)
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTH;

        // Dashboard label
        JLabel dashboardLabel = new JLabel("User Dashboard", SwingConstants.CENTER);
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 20));
        dashboardLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(dashboardLabel, gbc);

        // Buttons
        String[] options = {
                "View Tour Places", "Manage Bookings", "View Bookings", "Back to Main Dashboard", "Back to HomePageUI"
        };
        JButton[] buttons = new JButton[options.length];
        for (int i = 0; i < options.length; i++) {
            buttons[i] = new JButton(options[i]);
            buttons[i].setBackground(new Color(0, 0, 255));
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 16));
            buttons[i].setPreferredSize(new Dimension(200, 40));
            buttons[i].setFocusPainted(false);
            buttons[i].setBorder(BorderFactory.createEmptyBorder());
            gbc.gridy = i + 1;
            int finalI = i;
            buttons[i].addActionListener(e -> handleOptionClick(buttons[finalI], options[finalI]));
            menuPanel.add(buttons[i], gbc);
        }

        // Right Panel (Content)
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints contentGbc = new GridBagConstraints();
        contentGbc.insets = new Insets(10, 10, 10, 10);
        contentGbc.gridx = 0;
        contentGbc.gridy = 0;
        contentGbc.anchor = GridBagConstraints.CENTER;
        contentGbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Select an option to begin", SwingConstants.CENTER), contentGbc);

        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void handleOptionClick(JButton button, String option) {
        if (selectedButton != null) {
            selectedButton.setBackground(new Color(0, 0, 255));
        }
        button.setBackground(new Color(0, 51, 102));
        selectedButton = button;

        contentPanel.removeAll();
        GridBagConstraints contentGbc = new GridBagConstraints();
        contentGbc.insets = new Insets(10, 10, 10, 10);
        contentGbc.gridx = 0;
        contentGbc.gridy = 0;
        contentGbc.anchor = GridBagConstraints.CENTER;
        contentGbc.fill = GridBagConstraints.NONE;
        switch (option) {
            case "View Tour Places":
                showTourPlaces(contentGbc);
                break;
            case "Manage Bookings":
                showManageBookings(contentGbc);
                break;
            case "View Bookings":
                showBookings(contentGbc);
                break;
            case "Back to Main Dashboard":
                dispose();
                new MainDashboardUI(currentUserId).displayDashboard();
                break;
            case "Back to HomePageUI":
                dispose();
                new HomePageUI().setVisible(true);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTourPlaces(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // Dropdown for all tour places
        String[] placeNames = tourPlaceDAO.getTourPlaces();
        JComboBox<String> placeCombo = new JComboBox<>(placeNames.length > 0 ? placeNames : new String[]{"No places available"});
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.BLACK);
        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.insets = new Insets(5, 5, 5, 5);
        innerGbc.gridx = 0;

        innerGbc.gridy = 0;
        wrapper.add(new JLabel("Select Tour Place:"), innerGbc);
        innerGbc.gridy = 1;
        wrapper.add(placeCombo, innerGbc);

        // Default display (latest added)
        List<Object[]> latestPlaces = tourPlaceDAO.getAllTourPlacesWithPhotos();
        if (!latestPlaces.isEmpty()) {
            displayPlaceDetails(detailsPanel, latestPlaces.get(0), new GridBagConstraints());
        } else {
            detailsPanel.add(new JLabel("No tour places available"), new GridBagConstraints());
        }
        innerGbc.gridy = 2;
        wrapper.add(detailsPanel, innerGbc);

        // Update details on selection
        placeCombo.addActionListener(e -> {
            System.out.println("Place combo action triggered. Selected: " + placeCombo.getSelectedItem());
            SwingUtilities.invokeLater(() -> {
                String selectedPlace = (String) placeCombo.getSelectedItem();
                detailsPanel.removeAll();
                if (selectedPlace != null && !selectedPlace.equals("No places available")) {
                    int tourPlaceId = tourPlaceDAO.getTourPlaceId(selectedPlace);
                    System.out.println("Tour place ID for " + selectedPlace + ": " + tourPlaceId);
                    if (tourPlaceId != -1) {
                        List<Object[]> places = tourPlaceDAO.getAllTourPlacesWithPhotos();
                        for (Object[] place : places) {
                            if ((int) place[0] == tourPlaceId) {
                                displayPlaceDetails(detailsPanel, place, new GridBagConstraints());
                                System.out.println("Displayed details for: " + place[1]);
                                break;
                            }
                        }
                    } else {
                        detailsPanel.add(new JLabel("Invalid tour place ID"), new GridBagConstraints());
                    }
                } else {
                    detailsPanel.add(new JLabel("No details available"), new GridBagConstraints());
                }
                detailsPanel.revalidate();
                detailsPanel.repaint();
                wrapper.revalidate();
                wrapper.repaint();
            });
        });

        wrapper.add(new JScrollPane(), gbc);
        contentPanel.add(wrapper, gbc);
    }

    private void displayPlaceDetails(JPanel panel, Object[] place, GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        int y = 0;
        String[] photos = (String[]) place[6];
        for (int i = 0; i < Math.min(3, photos.length); i++) {
            JLabel imageLabel = new JLabel();
            try {
                if (photos[i] != null && !photos[i].isEmpty()) {
                    java.net.URL imgURL = getClass().getClassLoader().getResource(photos[i].trim());
                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(400, 100, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(img));
                        System.out.println("Successfully loaded image from resource: " + photos[i]);
                    } else {
                        imageLabel.setText("Image not found in resources: " + photos[i]);
                        System.err.println("Resource not found for path: " + photos[i]);
                    }
                } else {
                    imageLabel.setText("No Image " + (i + 1));
                }
            } catch (Exception e) {
                imageLabel.setText("Image load error: " + e.getMessage());
                System.err.println("Error loading image " + photos[i] + ": " + e.getMessage());
            }
            gbc.gridy = y++;
            panel.add(imageLabel, gbc);
        }
        gbc.gridy = y++;
        JLabel nameLabel = new JLabel("Name: " + place[1]);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger size and bold
        nameLabel.setForeground(Color.white); // Change to blue
        panel.add(nameLabel, gbc);

        gbc.gridy = y++;
        JLabel descLabel = new JLabel("Description: " + place[2]);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 16)); // Larger size and italic
        descLabel.setForeground(Color.white); // Change to green
        panel.add(descLabel, gbc);

        gbc.gridy = y++;
        JLabel addrLabel = new JLabel("Address: " + place[3]);
        addrLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger size
        addrLabel.setForeground(Color.white); // Change to black
        panel.add(addrLabel, gbc);

        gbc.gridy = y++;
        JLabel latLabel = new JLabel("Latitude: " + place[4]);
        latLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger size
        latLabel.setForeground(Color.white); // Change to gray
        panel.add(latLabel, gbc);

        gbc.gridy = y++;
        JLabel longLabel = new JLabel("Longitude: " + place[5]);
        longLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger size
        longLabel.setForeground(Color.white); // Change to gray
        panel.add(longLabel, gbc);
    }

    private void showBookings(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JTextArea bookingsArea = new JTextArea(10, 40);
        bookingsArea.setEditable(false);
        List<Object[]> bookings = bookingDAO.getBookingsByUserId(currentUserId);
        for (Object[] booking : bookings) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = booking.length > 6 && booking[6] != null ? sdf.format(booking[6]) : "N/A";
            bookingsArea.append("ID: " + booking[0] + ", Tour Place ID: " + booking[2] + ", Hotel ID: " + booking[3] +
                    ", Room ID: " + booking[4] + ", Status: " + booking[5] + ", Date: " + dateStr + "\n");
        }
        if (bookings.isEmpty()) {
            bookingsArea.append("No bookings found.");
        }
        wrapper.add(new JScrollPane(bookingsArea), gbc);
        contentPanel.add(wrapper, gbc);
    }

    private void showManageBookings(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.BLACK);
        wrapper.setOpaque(false);

        // Tour Place selection
        JLabel placeLabel = new JLabel("Select Tour Place:");
        String[] placeNames = tourPlaceDAO.getTourPlaces();
        JComboBox<String> placeCombo = new JComboBox<>(placeNames.length > 0 ? placeNames : new String[]{"No places available"});
        // Hotel selection
        JLabel hotelLabel = new JLabel("Select Hotel:");
        JComboBox<String> hotelCombo = new JComboBox<>();
        // Room selection
        JLabel roomLabel = new JLabel("Select Room: TYPE - PRICE PER NIGHT - CAPACITY");
        JComboBox<String> roomCombo = new JComboBox<>();
        // Date selection
        JLabel dateLabel = new JLabel("Select Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField(10);
        // Book button
        JButton bookButton = new JButton("Book");
        bookButton.setBackground(Color.BLUE);
        bookButton.setForeground(Color.WHITE);

        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.insets = new Insets(5, 5, 5, 5);
        innerGbc.gridx = 0;

        innerGbc.gridy = 0;
        wrapper.add(placeLabel, innerGbc);
        innerGbc.gridy = 1;
        wrapper.add(placeCombo, innerGbc);
        innerGbc.gridy = 2;
        wrapper.add(hotelLabel, innerGbc);
        innerGbc.gridy = 3;
        wrapper.add(hotelCombo, innerGbc);
        innerGbc.gridy = 4;
        wrapper.add(roomLabel, innerGbc);
        innerGbc.gridy = 5;
        wrapper.add(roomCombo, innerGbc);
        innerGbc.gridy = 6;
        wrapper.add(dateLabel, innerGbc);
        innerGbc.gridy = 7;
        wrapper.add(dateField, innerGbc);
        innerGbc.gridy = 8;
        wrapper.add(bookButton, innerGbc);

        // Add listeners
        placeCombo.addActionListener(e -> {
            System.out.println("Place combo action triggered. Selected: " + placeCombo.getSelectedItem());
            SwingUtilities.invokeLater(() -> {
                String selectedPlace = (String) placeCombo.getSelectedItem();
                hotelCombo.removeAllItems();
                roomCombo.removeAllItems();
                if (selectedPlace != null && !selectedPlace.equals("No places available")) {
                    int tourPlaceId = tourPlaceDAO.getTourPlaceId(selectedPlace);
                    System.out.println("Tour place ID for " + selectedPlace + ": " + tourPlaceId);
                    if (tourPlaceId != -1) {
                        String[] hotels = hotelDAO.getHotelsByTourPlaceId(tourPlaceId);
                        System.out.println("Found " + hotels.length + " hotels for tour place ID " + tourPlaceId);
                        for (String hotel : hotels) {
                            hotelCombo.addItem(hotel);
                        }
                    }
                    if (hotelCombo.getItemCount() == 0) hotelCombo.addItem("No hotels available");
                } else {
                    hotelCombo.addItem("No hotels available");
                }
                wrapper.revalidate();
                wrapper.repaint();
            });
        });

        hotelCombo.addActionListener(e -> {
            System.out.println("Hotel combo action triggered. Selected: " + hotelCombo.getSelectedItem());
            SwingUtilities.invokeLater(() -> {
                String selectedHotel = (String) hotelCombo.getSelectedItem();
                roomCombo.removeAllItems();
                if (selectedHotel != null && !selectedHotel.equals("No hotels available")) {
                    int hotelId = hotelDAO.getHotelId(selectedHotel);
                    System.out.println("Hotel ID for " + selectedHotel + ": " + hotelId);
                    if (hotelId != -1) {
                        List<Object[]> rooms = roomDAO.getRoomsByHotelId(hotelId);
                        System.out.println("Found " + rooms.size() + " rooms for hotel ID " + hotelId);
                        for (Object[] room : rooms) {
                            roomCombo.addItem((String) room[1]); // room_type - price - capacity
                        }
                    }
                    if (roomCombo.getItemCount() == 0) roomCombo.addItem("No rooms available");
                } else {
                    roomCombo.addItem("No rooms available");
                }
                wrapper.revalidate();
                wrapper.repaint();
            });
        });

        bookButton.addActionListener(e -> {
            String place = (String) placeCombo.getSelectedItem();
            String hotel = (String) hotelCombo.getSelectedItem();
            String room = (String) roomCombo.getSelectedItem();
            String date = dateField.getText().trim();
            System.out.println("Booking attempt - Place: " + place + ", Hotel: " + hotel + ", Room: " + room + ", Date: " + date);
            if (place == null || place.equals("No places available") || hotel == null || hotel.equals("No hotels available") ||
                    room == null || room.equals("No rooms available") || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields with valid data", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int tourPlaceId = tourPlaceDAO.getTourPlaceId(place);
            int hotelId = hotelDAO.getHotelId(hotel);
            int roomId = roomDAO.getRoomIdByFullName(room);
            System.out.println("Resolved IDs - TourPlace: " + tourPlaceId + ", Hotel: " + hotelId + ", Room: " + roomId);
            if (tourPlaceId == -1 || hotelId == -1 || roomId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid selection", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            try {
                sdf.parse(date);
            } catch (java.text.ParseException pe) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean success = bookingDAO.createBooking(currentUserId, tourPlaceId, hotelId, roomId, date + " 00:00:00", "Pending");
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed", "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Booking failed for user " + currentUserId + ", details: " + place + ", " + hotel + ", " + room + ", " + date);
            }
        });

        wrapper.add(new JScrollPane(), gbc);
        contentPanel.add(wrapper, gbc);
    }

    private void updateHotelCombo(JComboBox<String> placeCombo, JComboBox<String> hotelCombo) {
        hotelCombo.removeAllItems();
        String place = (String) placeCombo.getSelectedItem();
        if (place != null && !place.equals("No places available")) {
            int tourPlaceId = tourPlaceDAO.getTourPlaceId(place);
            if (tourPlaceId != -1) {
                String[] hotels = hotelDAO.getHotelsByTourPlaceId(tourPlaceId);
                for (String hotel : hotels) {
                    hotelCombo.addItem(hotel);
                }
            }
            if (hotelCombo.getItemCount() == 0) hotelCombo.addItem("No hotels available");
        } else {
            hotelCombo.addItem("No hotels available");
        }
    }

    private void updateRoomCombo(JComboBox<String> hotelCombo, JComboBox<String> roomCombo) {
        roomCombo.removeAllItems();
        String hotel = (String) hotelCombo.getSelectedItem();
        if (hotel != null && !hotel.equals("No hotels available")) {
            int hotelId = hotelDAO.getHotelId(hotel);
            if (hotelId != -1) {
                List<Object[]> rooms = roomDAO.getRoomsByHotelId(hotelId);
                for (Object[] room : rooms) {
                    roomCombo.addItem((String) room[1]); // room_type - price - capacity
                }
            }
            if (roomCombo.getItemCount() == 0) roomCombo.addItem("No rooms available");
        } else {
            roomCombo.addItem("No rooms available");
        }
    }




}
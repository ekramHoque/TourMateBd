package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import dao.*;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class AdminPanelUI extends JFrame {
    private JPanel contentPanel;
    private JButton selectedButton = null;
    private UserDAO userDAO;
    private TourPlaceDAO tourPlaceDAO;
    private HotelDAO hotelDAO;
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;

    public void displayAdminPanel() {
        setVisible(true);
    }

    public AdminPanelUI() {
        this.userDAO = new UserDAO();
        this.tourPlaceDAO = new TourPlaceDAO();
        this.hotelDAO = new HotelDAO();
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Panel - TourMateBD");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(3, 252, 236));
        mainPanel.setLayout(new BorderLayout());

        // Left Panel
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTH;


        JLabel adminLabel = new JLabel("Admin Menu", SwingConstants.CENTER);
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        adminLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(adminLabel, gbc);


        String[] options = {
                "Add Tour Places", "Add Hotel", "Add Room",
                "View/Manage Users", "View/Manage Bookings",
                "Back to Admin Panel", "Back to HomePageUI"
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

        // Right Panel
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints contentGbc = new GridBagConstraints();
        contentGbc.insets = new Insets(10, 10, 10, 10);
        contentGbc.gridx = 0;
        contentGbc.gridy = 0;
        contentGbc.anchor = GridBagConstraints.CENTER;
        contentGbc.fill = GridBagConstraints.NONE; // Prevent stretching
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
            case "Add Tour Places":
                showAddTourPlacesForm(contentGbc);
                break;
            case "Add Hotel":
                showAddHotelForm(contentGbc);
                break;
            case "Add Room":
                showAddRoomForm(contentGbc);
                break;
            case "View/Manage Users":
                showManageUsers(contentGbc);
                break;
            case "View/Manage Bookings":
                showManageBookings(contentGbc);
                break;
            case "Back to Admin Panel":
                dispose();
                AdminPanelUI adminPanel = new AdminPanelUI();
                adminPanel.displayAdminPanel();
                break;
            case "Back to HomePageUI":
                dispose();
                new HomePageUI().setVisible(true);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAddTourPlacesForm(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField addressField = new JTextField(20);
        JTextField latitudeField = new JTextField(20);
        JTextField longitudeField = new JTextField(20);
        JTextField photo1Field = new JTextField(20);
        JTextField photo2Field = new JTextField(20);
        JTextField photo3Field = new JTextField(20);
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 0, 255));
        saveButton.setForeground(Color.WHITE);

        formGbc.gridx = 0;
        formGbc.gridy = 0;
        form.add(new JLabel("Name:"), formGbc);
        formGbc.gridy = 1;
        form.add(new JLabel("Description:"), formGbc);
        formGbc.gridy = 2;
        form.add(new JLabel("Address:"), formGbc);
        formGbc.gridy = 3;
        form.add(new JLabel("Latitude:"), formGbc);
        formGbc.gridy = 4;
        form.add(new JLabel("Longitude:"), formGbc);
        formGbc.gridy = 5;
        form.add(new JLabel("Photo 1 URL:"), formGbc);
        formGbc.gridy = 6;
        form.add(new JLabel("Photo 2 URL:"), formGbc);
        formGbc.gridy = 7;
        form.add(new JLabel("Photo 3 URL:"), formGbc);

        formGbc.gridx = 1;
        formGbc.gridy = 0;
        form.add(nameField, formGbc);
        formGbc.gridy = 1;
        form.add(new JScrollPane(descriptionArea), formGbc);
        formGbc.gridy = 2;
        form.add(addressField, formGbc);
        formGbc.gridy = 3;
        form.add(latitudeField, formGbc);
        formGbc.gridy = 4;
        form.add(longitudeField, formGbc);
        formGbc.gridy = 5;
        form.add(photo1Field, formGbc);
        formGbc.gridy = 6;
        form.add(photo2Field, formGbc);
        formGbc.gridy = 7;
        form.add(photo3Field, formGbc);
        formGbc.gridy = 8;
        form.add(saveButton, formGbc);

        saveButton.addActionListener(e -> {
            if (tourPlaceDAO.saveTourPlace(nameField.getText(), descriptionArea.getText(), addressField.getText(),
                    latitudeField.getText(), longitudeField.getText())) {
                int tourPlaceId = tourPlaceDAO.getTourPlaceId(nameField.getText());
                if (tourPlaceId != -1) {
                    String[] photoUrls = {photo1Field.getText(), photo2Field.getText(), photo3Field.getText()};
                    tourPlaceDAO.savePhotos(tourPlaceId, photoUrls);
                }
                JOptionPane.showMessageDialog(this, "Tour Place added successfully!");
                contentPanel.removeAll();
                contentPanel.add(new JLabel("Tour Place added successfully!", SwingConstants.CENTER), gbc);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding tour place.");
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(form, wrapperGbc);
        contentPanel.add(wrapper, gbc);
    }

    private void showAddHotelForm(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> tourPlaceCombo = new JComboBox<>(tourPlaceDAO.getTourPlaces());
        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField contactField = new JTextField(20);
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 0, 255));
        saveButton.setForeground(Color.WHITE);

        formGbc.gridx = 0;
        formGbc.gridy = 0;
        form.add(new JLabel("Tour Place:"), formGbc);
        formGbc.gridy = 1;
        form.add(new JLabel("Name:"), formGbc);
        formGbc.gridy = 2;
        form.add(new JLabel("Location:"), formGbc);
        formGbc.gridy = 3;
        form.add(new JLabel("Description:"), formGbc);
        formGbc.gridy = 4;
        form.add(new JLabel("Contact:"), formGbc);

        formGbc.gridx = 1;
        formGbc.gridy = 0;
        form.add(tourPlaceCombo, formGbc);
        formGbc.gridy = 1;
        form.add(nameField, formGbc);
        formGbc.gridy = 2;
        form.add(locationField, formGbc);
        formGbc.gridy = 3;
        form.add(new JScrollPane(descriptionArea), formGbc);
        formGbc.gridy = 4;
        form.add(contactField, formGbc);
        formGbc.gridy = 5;
        form.add(saveButton, formGbc);

        saveButton.addActionListener(e -> {
            int tourPlaceId = tourPlaceDAO.getTourPlaceId((String) tourPlaceCombo.getSelectedItem());
            if (tourPlaceId != -1 && hotelDAO.saveHotel(tourPlaceId, nameField.getText(), locationField.getText(),
                    descriptionArea.getText(), contactField.getText())) {
                JOptionPane.showMessageDialog(this, "Hotel added successfully!");
                contentPanel.removeAll();
                contentPanel.add(new JLabel("Hotel added successfully!", SwingConstants.CENTER), gbc);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding hotel.");
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(form, wrapperGbc);
        contentPanel.add(wrapper, gbc);
    }

    private void showAddRoomForm(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> hotelCombo = new JComboBox<>(hotelDAO.getHotels());
        JTextField roomTypeField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField capacityField = new JTextField(20);
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 0, 255));
        saveButton.setForeground(Color.WHITE);

        formGbc.gridx = 0;
        formGbc.gridy = 0;
        form.add(new JLabel("Hotel:"), formGbc);
        formGbc.gridy = 1;
        form.add(new JLabel("Room Type:"), formGbc);
        formGbc.gridy = 2;
        form.add(new JLabel("Price:"), formGbc);
        formGbc.gridy = 3;
        form.add(new JLabel("Capacity:"), formGbc);

        formGbc.gridx = 1;
        formGbc.gridy = 0;
        form.add(hotelCombo, formGbc);
        formGbc.gridy = 1;
        form.add(roomTypeField, formGbc);
        formGbc.gridy = 2;
        form.add(priceField, formGbc);
        formGbc.gridy = 3;
        form.add(capacityField, formGbc);
        formGbc.gridy = 4;
        form.add(saveButton, formGbc);

        saveButton.addActionListener(e -> {
            int hotelId = hotelDAO.getHotelId((String) hotelCombo.getSelectedItem());
            if (hotelId != -1 && roomDAO.saveRoom(hotelId, roomTypeField.getText(), priceField.getText(), capacityField.getText())) {
                JOptionPane.showMessageDialog(this, "Room added successfully!");
                contentPanel.removeAll();
                contentPanel.add(new JLabel("Room added successfully!", SwingConstants.CENTER), gbc);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding room.");
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(form, wrapperGbc);
        contentPanel.add(wrapper, gbc);
    }

    private void showManageUsers(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Admin", "User"});
        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setBackground(new Color(0, 0, 255));
        exportPdfButton.setForeground(Color.WHITE);
        JTextArea usersArea = new JTextArea(10, 40);
        usersArea.setEditable(false);

        filterCombo.addActionListener(e -> updateUsersList(usersArea, (String) filterCombo.getSelectedItem()));
        exportPdfButton.addActionListener(e -> exportToPdf(usersArea.getText(), "users_report"));

        panel.add(filterCombo, BorderLayout.NORTH);
        panel.add(new JScrollPane(usersArea), BorderLayout.CENTER);
        panel.add(exportPdfButton, BorderLayout.SOUTH);

        updateUsersList(usersArea, "All");
        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(panel, wrapperGbc);
        contentPanel.add(wrapper, gbc);
    }

    private void showManageBookings(GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Pending", "Confirmed", "Canceled"});
        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setBackground(new Color(0, 0, 255));
        exportPdfButton.setForeground(Color.WHITE);
        JTextArea bookingsArea = new JTextArea(10, 40);
        bookingsArea.setEditable(false);

        filterCombo.addActionListener(e -> updateBookingsList(bookingsArea, (String) filterCombo.getSelectedItem()));
        exportPdfButton.addActionListener(e -> exportToPdf(bookingsArea.getText(), "bookings_report"));

        // Add status update UI
        JPanel statusPanel = new JPanel(new FlowLayout());
        JLabel bookingIdLabel = new JLabel("Booking ID:");
        JTextField bookingIdField = new JTextField(5);
        JLabel statusLabel = new JLabel("New Status:");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "Confirmed", "Canceled"});
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.setBackground(new Color(0, 0, 255));
        updateStatusButton.setForeground(Color.WHITE);
        updateStatusButton.addActionListener(e -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
                if (bookingId <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid Booking ID greater than 0.");
                    return;
                }
                String newStatus = (String) statusCombo.getSelectedItem();
                if (bookingDAO.updateBookingStatus(bookingId, newStatus)) {
                    JOptionPane.showMessageDialog(this, "Booking status updated to " + newStatus + "!");
                    updateBookingsList(bookingsArea, (String) filterCombo.getSelectedItem());
                    bookingIdField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating booking status. Check if the Booking ID exists.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric Booking ID.");
            }
        });

        statusPanel.add(bookingIdLabel);
        statusPanel.add(bookingIdField);
        statusPanel.add(statusLabel);
        statusPanel.add(statusCombo);
        statusPanel.add(updateStatusButton);

        panel.add(filterCombo, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookingsArea), BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);
        panel.add(exportPdfButton, BorderLayout.EAST);

        updateBookingsList(bookingsArea, "All");
        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(panel, wrapperGbc);
        contentPanel.add(wrapper, gbc);
    }

    private void updateUsersList(JTextArea area, String filter) {
        area.setText("");
        List<User> users = userDAO.getAllUsers(filter);
        for (User user : users) {
            area.append("ID: " + user.getId() + ", Name: " + user.getName() +
                    ", Email: " + user.getEmail() + ", Admin: " + user.isAdmin() + "\n");
        }
    }

    private void updateBookingsList(JTextArea area, String filter) {
        area.setText("");
        List<Object[]> bookings = bookingDAO.getAllBookings(filter);
        System.out.println("Retrieved " + bookings.size() + " bookings with filter: " + filter);
        if (bookings.isEmpty()) {
            area.append("No bookings found.\n");
            System.out.println("No bookings found in the result set.");
        } else {
            for (Object[] booking : bookings) {
                area.append("ID: " + booking[0] + ", User ID: " + booking[1] + ", Tour Place ID: " + booking[2] +
                        ", Hotel ID: " + booking[3] + ", Room ID: " + booking[4] + ", Status: " + booking[5] +
                        ", Booking Date: " + booking[6] + "\n");
            }
        }
    }

    private void exportToPdf(String content, String fileName) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);

            String[] lines = content.split("\n");
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -15);
            }
            contentStream.endText();
            contentStream.close();

            document.save(fileName + ".pdf");
            document.close();

            JOptionPane.showMessageDialog(this, "PDF exported as " + fileName + ".pdf");
            try (Connection conn = new DBConnection().connect()) {
                String sql = "INSERT INTO pdf_logs (user_id, file_name, generated_at) VALUES (?, ?, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, 1); // Placeholder for current admin ID
                stmt.setString(2, fileName + ".pdf");
                stmt.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
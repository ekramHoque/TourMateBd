package ui.handlers.admin;

import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import ui.handlers.DashboardActionHandler;
import dao.BookingDAO;
import dao.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ShowManageBookingsHandler implements DashboardActionHandler {
    private final BookingDAO bookingDAO;

    public ShowManageBookingsHandler(BookingDAO dao) {
        this.bookingDAO = dao;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel panel = new JPanel(new BorderLayout());
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Pending", "Confirmed", "Canceled"});
        JTextArea bookingsArea = new JTextArea(10, 40);
        bookingsArea.setEditable(false);

        // Status update UI
        JPanel statusPanel = new JPanel(new FlowLayout());
        JTextField bookingIdField = new JTextField(5);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "Confirmed", "Canceled"});
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.setBackground(Color.BLUE);
        updateStatusButton.setForeground(Color.WHITE);

        updateStatusButton.addActionListener(e -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
                String newStatus = (String) statusCombo.getSelectedItem();
                if (bookingDAO.updateBookingStatus(bookingId, newStatus)) {
                    JOptionPane.showMessageDialog(panel, "Booking status updated to " + newStatus);
                    updateBookings(bookingsArea, (String) filterCombo.getSelectedItem());
                    bookingIdField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "Error updating booking. Check Booking ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter a valid numeric Booking ID.");
            }
        });

        statusPanel.add(new JLabel("Booking ID:"));
        statusPanel.add(bookingIdField);
        statusPanel.add(new JLabel("New Status:"));
        statusPanel.add(statusCombo);
        statusPanel.add(updateStatusButton);

        // PDF export button
        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setBackground(Color.BLUE);
        exportPdfButton.setForeground(Color.WHITE);
        exportPdfButton.addActionListener(e -> exportToPdf(bookingsArea.getText(), "bookings_report"));

        filterCombo.addActionListener(e -> updateBookings(bookingsArea, (String) filterCombo.getSelectedItem()));

        panel.add(filterCombo, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookingsArea), BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);
        panel.add(exportPdfButton, BorderLayout.EAST);

        updateBookings(bookingsArea, "All");
        contentPanel.add(panel, gbc);
    }

    private void updateBookings(JTextArea area, String filter) {
        area.setText("");
        // Use the detailed DAO method
        List<Object[]> bookings = bookingDAO.getAllBookingsDetailed(filter);

        if (bookings.isEmpty()) {
            area.append("No bookings found.\n");
        } else {
            for (Object[] b : bookings) {
                // b[0]=booking_id, b[1]=user_name, b[2]=user_email, b[3]=tour_place_name
                // b[4]=hotel_name, b[5]=room_type, b[6]=status, b[7]=booking_date
                area.append(
                        "Booking ID: " + b[0] + "\n" +
                                "User Name: " + b[1] + ", Email: " + b[2] + "\n" +
                                "Tour Place: " + b[3] + "\n" +
                                "Hotel: " + b[4] + ", Room: " + b[5] + "\n" +
                                "Status: " + b[6] + ", Date: " + b[7] + "\n" +
                                "-------------------------------------------\n"
                );
            }
        }
    }


    private void exportToPdf(String content, String fileName) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            cs.beginText();
            cs.newLineAtOffset(50, 700);
            for (String line : content.split("\n")) {
                cs.showText(line);
                cs.newLineAtOffset(0, -15);
            }
            cs.endText();
            cs.close();

            document.save(fileName + ".pdf");
            document.close();
            JOptionPane.showMessageDialog(null, "PDF exported as " + fileName + ".pdf");

            // Log to database
            try (Connection conn = new DBConnection().connect()) {
                String sql = "INSERT INTO pdf_logs (user_id, file_name, generated_at) VALUES (?, ?, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, 1); // current admin ID placeholder
                stmt.setString(2, fileName + ".pdf");
                stmt.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error exporting PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package ui.handlers.admin;

import ui.handlers.DashboardActionHandler;
import dao.UserDAO;
import model.User;
import dao.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ShowManageUsersHandler implements DashboardActionHandler {
    private final UserDAO userDAO;

    public ShowManageUsersHandler(UserDAO dao) {
        this.userDAO = dao;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel panel = new JPanel(new BorderLayout());
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Admin", "User"});
        JTextArea usersArea = new JTextArea(10, 40);
        usersArea.setEditable(false);

        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setBackground(Color.BLUE);
        exportPdfButton.setForeground(Color.WHITE);
        exportPdfButton.addActionListener(e -> exportToPdf(usersArea.getText(), "users_report"));

        filterCombo.addActionListener(e -> updateUsers(usersArea, (String) filterCombo.getSelectedItem()));

        panel.add(filterCombo, BorderLayout.NORTH);
        panel.add(new JScrollPane(usersArea), BorderLayout.CENTER);
        panel.add(exportPdfButton, BorderLayout.SOUTH);

        updateUsers(usersArea, "All");
        contentPanel.add(panel, gbc);
    }

    private void updateUsers(JTextArea area, String filter) {
        area.setText("");
        List<User> users = userDAO.getAllUsers(filter);
        if (users.isEmpty()) {
            area.append("No users found.\n");
        } else {
            for (User u : users) {
                area.append("ID: " + u.getId() + ", Name: " + u.getName() +
                        ", Email: " + u.getEmail() + ", Admin: " + u.isAdmin() + "\n");
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

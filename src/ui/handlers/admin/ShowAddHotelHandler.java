package ui.handlers.admin;

import ui.handlers.DashboardActionHandler;
import dao.TourPlaceDAO;
import dao.HotelDAO;

import javax.swing.*;
import java.awt.*;

public class ShowAddHotelHandler implements DashboardActionHandler {
    private final TourPlaceDAO tourPlaceDAO;
    private final HotelDAO hotelDAO;

    public ShowAddHotelHandler(TourPlaceDAO tDao, HotelDAO hDao) {
        this.tourPlaceDAO = tDao;
        this.hotelDAO = hDao;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel form = new JPanel(new GridBagLayout());
        JComboBox<String> tourPlaceCombo = new JComboBox<>(tourPlaceDAO.getTourPlaces());
        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField contactField = new JTextField(20);
        JButton saveButton = new JButton("Save");

        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(5, 5, 5, 5);
        f.gridx = 0; f.gridy = 0; form.add(new JLabel("Tour Place:"), f);
        f.gridy++; form.add(new JLabel("Name:"), f);
        f.gridy++; form.add(new JLabel("Location:"), f);
        f.gridy++; form.add(new JLabel("Description:"), f);
        f.gridy++; form.add(new JLabel("Contact:"), f);

        f.gridx = 1; f.gridy = 0; form.add(tourPlaceCombo, f);
        f.gridy++; form.add(nameField, f);
        f.gridy++; form.add(locationField, f);
        f.gridy++; form.add(new JScrollPane(descriptionArea), f);
        f.gridy++; form.add(contactField, f);
        f.gridy++; form.add(saveButton, f);

        saveButton.addActionListener(e -> {
            String selectedPlace = (String) tourPlaceCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String description = descriptionArea.getText().trim();
            String contact = contactField.getText().trim();

            if (selectedPlace == null || selectedPlace.isEmpty() ||
                    name.isEmpty() || location.isEmpty() || description.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int placeId = tourPlaceDAO.getTourPlaceId(selectedPlace);
            if (placeId != -1 && hotelDAO.saveHotel(placeId, name, location, description, contact)) {
                JOptionPane.showMessageDialog(contentPanel, "Hotel added successfully!");
                // Optionally, clear fields after successful save
                nameField.setText("");
                locationField.setText("");
                descriptionArea.setText("");
                contactField.setText("");
            } else {
                JOptionPane.showMessageDialog(contentPanel, "Error adding hotel.");
            }
        });


        contentPanel.add(form, gbc);
    }
}

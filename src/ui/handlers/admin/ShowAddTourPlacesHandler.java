package ui.handlers.admin;

import ui.handlers.DashboardActionHandler;
import dao.TourPlaceDAO;

import javax.swing.*;
import java.awt.*;

public class ShowAddTourPlacesHandler implements DashboardActionHandler {
    private final TourPlaceDAO tourPlaceDAO;

    public ShowAddTourPlacesHandler(TourPlaceDAO dao) {
        this.tourPlaceDAO = dao;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel form = new JPanel(new GridBagLayout());
        JTextField nameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField addressField = new JTextField(20);
        JTextField latitudeField = new JTextField(20);
        JTextField longitudeField = new JTextField(20);
        JTextField photo1Field = new JTextField(20);
        JTextField photo2Field = new JTextField(20);
        JTextField photo3Field = new JTextField(20);
        JButton saveButton = new JButton("Save");

        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(5, 5, 5, 5);
        f.gridx = 0; f.gridy = 0; form.add(new JLabel("Name:"), f);
        f.gridy++; form.add(new JLabel("Description:"), f);
        f.gridy++; form.add(new JLabel("Address:"), f);
        f.gridy++; form.add(new JLabel("Latitude:"), f);
        f.gridy++; form.add(new JLabel("Longitude:"), f);
        f.gridy++; form.add(new JLabel("Photo 1 URL:"), f);
        f.gridy++; form.add(new JLabel("Photo 2 URL:"), f);
        f.gridy++; form.add(new JLabel("Photo 3 URL:"), f);

        f.gridx = 1; f.gridy = 0; form.add(nameField, f);
        f.gridy++; form.add(new JScrollPane(descriptionArea), f);
        f.gridy++; form.add(addressField, f);
        f.gridy++; form.add(latitudeField, f);
        f.gridy++; form.add(longitudeField, f);
        f.gridy++; form.add(photo1Field, f);
        f.gridy++; form.add(photo2Field, f);
        f.gridy++; form.add(photo3Field, f);
        f.gridy++; form.add(saveButton, f);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String address = addressField.getText().trim();
            String latitude = latitudeField.getText().trim();
            String longitude = longitudeField.getText().trim();
            String photo1 = photo1Field.getText().trim();
            String photo2 = photo2Field.getText().trim();
            String photo3 = photo3Field.getText().trim();


            if (name.isEmpty() || description.isEmpty() || address.isEmpty() ||
                    latitude.isEmpty() || longitude.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tourPlaceDAO.saveTourPlace(name, description, address, latitude, longitude)) {
                int tourPlaceId = tourPlaceDAO.getTourPlaceId(name);
                if (tourPlaceId != -1) {
                    String[] photos = {photo1, photo2, photo3};
                    tourPlaceDAO.savePhotos(tourPlaceId, photos);
                }
                JOptionPane.showMessageDialog(contentPanel, "Tour Place added successfully!");
                nameField.setText("");
                descriptionArea.setText("");
                addressField.setText("");
                latitudeField.setText("");
                longitudeField.setText("");
                photo1Field.setText("");
                photo2Field.setText("");
                photo3Field.setText("");
            } else {
                JOptionPane.showMessageDialog(contentPanel, "Error adding tour place.");
            }
        });


        contentPanel.add(form, gbc);
    }
}

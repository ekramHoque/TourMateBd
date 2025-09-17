package ui.handlers.admin;

import ui.handlers.DashboardActionHandler;
import dao.HotelDAO;
import dao.RoomDAO;

import javax.swing.*;
import java.awt.*;

public class ShowAddRoomHandler implements DashboardActionHandler {
    private final HotelDAO hotelDAO;
    private final RoomDAO roomDAO;

    public ShowAddRoomHandler(HotelDAO hDao, RoomDAO rDao) {
        this.hotelDAO = hDao;
        this.roomDAO = rDao;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel form = new JPanel(new GridBagLayout());
        JComboBox<String> hotelCombo = new JComboBox<>(hotelDAO.getHotels());
        JTextField roomTypeField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField capacityField = new JTextField(20);
        JButton saveButton = new JButton("Save");

        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(5, 5, 5, 5);
        f.gridx = 0; f.gridy = 0; form.add(new JLabel("Hotel:"), f);
        f.gridy++; form.add(new JLabel("Room Type:"), f);
        f.gridy++; form.add(new JLabel("Price:"), f);
        f.gridy++; form.add(new JLabel("Capacity:"), f);

        f.gridx = 1; f.gridy = 0; form.add(hotelCombo, f);
        f.gridy++; form.add(roomTypeField, f);
        f.gridy++; form.add(priceField, f);
        f.gridy++; form.add(capacityField, f);
        f.gridy++; form.add(saveButton, f);

        saveButton.addActionListener(e -> {
            String selectedHotel = (String) hotelCombo.getSelectedItem();
            String roomType = roomTypeField.getText().trim();
            String price = priceField.getText().trim();
            String capacity = capacityField.getText().trim();

            // Validation: check if any field is empty
            if (selectedHotel == null || selectedHotel.isEmpty() ||
                    roomType.isEmpty() || price.isEmpty() || capacity.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int hotelId = hotelDAO.getHotelId(selectedHotel);
            if (hotelId != -1 && roomDAO.saveRoom(hotelId, roomType, price, capacity)) {
                JOptionPane.showMessageDialog(contentPanel, "Room added successfully!");
                // Optionally clear fields after saving
                roomTypeField.setText("");
                priceField.setText("");
                capacityField.setText("");
            } else {
                JOptionPane.showMessageDialog(contentPanel, "Error adding room.");
            }
        });


        contentPanel.add(form, gbc);
    }
}

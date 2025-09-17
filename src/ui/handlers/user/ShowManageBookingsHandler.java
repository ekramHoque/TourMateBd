package ui.handlers.user;

import ui.handlers.DashboardActionHandler;
import dao.BookingDAO;
import dao.TourPlaceDAO;
import dao.HotelDAO;
import dao.RoomDAO;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowManageBookingsHandler implements DashboardActionHandler {
    private final BookingDAO bookingDAO;
    private final TourPlaceDAO tourPlaceDAO;
    private final HotelDAO hotelDAO;
    private final RoomDAO roomDAO;
    private final int currentUserId;

    public ShowManageBookingsHandler(BookingDAO bookingDAO, TourPlaceDAO tourPlaceDAO,
                                     HotelDAO hotelDAO, RoomDAO roomDAO, int userId) {
        this.bookingDAO = bookingDAO;
        this.tourPlaceDAO = tourPlaceDAO;
        this.hotelDAO = hotelDAO;
        this.roomDAO = roomDAO;
        this.currentUserId = userId;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JLabel placeLabel = new JLabel("Select Tour Place:");
        String[] placeNames = tourPlaceDAO.getTourPlaces();
        JComboBox<String> placeCombo = new JComboBox<>(placeNames.length > 0 ? placeNames : new String[]{"No places available"});

        JLabel hotelLabel = new JLabel("Select Hotel:");
        JComboBox<String> hotelCombo = new JComboBox<>();

        JLabel roomLabel = new JLabel("Select Room:");
        JComboBox<String> roomCombo = new JComboBox<>();

        JLabel dateLabel = new JLabel("Select Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField(10);

        JButton bookButton = new JButton("Book");
        bookButton.setBackground(Color.BLUE);
        bookButton.setForeground(Color.WHITE);

        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.insets = new Insets(5, 5, 5, 5);
        innerGbc.gridx = 0;

        wrapper.add(placeLabel, innerGbc);
        innerGbc.gridy = 1; wrapper.add(placeCombo, innerGbc);
        innerGbc.gridy = 2; wrapper.add(hotelLabel, innerGbc);
        innerGbc.gridy = 3; wrapper.add(hotelCombo, innerGbc);
        innerGbc.gridy = 4; wrapper.add(roomLabel, innerGbc);
        innerGbc.gridy = 5; wrapper.add(roomCombo, innerGbc);
        innerGbc.gridy = 6; wrapper.add(dateLabel, innerGbc);
        innerGbc.gridy = 7; wrapper.add(dateField, innerGbc);
        innerGbc.gridy = 8; wrapper.add(bookButton, innerGbc);

        // Listeners
        placeCombo.addActionListener(e -> {
            hotelCombo.removeAllItems();
            String selectedPlace = (String) placeCombo.getSelectedItem();
            if (selectedPlace != null && !selectedPlace.equals("No places available")) {
                int placeId = tourPlaceDAO.getTourPlaceId(selectedPlace);
                String[] hotels = hotelDAO.getHotelsByTourPlaceId(placeId);
                for (String h : hotels) hotelCombo.addItem(h);
            }
            if (hotelCombo.getItemCount() == 0) hotelCombo.addItem("No hotels available");
        });

        hotelCombo.addActionListener(e -> {
            roomCombo.removeAllItems();
            String selectedHotel = (String) hotelCombo.getSelectedItem();
            if (selectedHotel != null && !selectedHotel.equals("No hotels available")) {
                int hotelId = hotelDAO.getHotelId(selectedHotel);
                List<Object[]> rooms = roomDAO.getRoomsByHotelId(hotelId);
                for (Object[] r : rooms) {
                    roomCombo.addItem((String) r[1]); // room_type
                }
            }
            if (roomCombo.getItemCount() == 0) roomCombo.addItem("No rooms available");
        });

        bookButton.addActionListener(e -> {
            String place = (String) placeCombo.getSelectedItem();
            String hotel = (String) hotelCombo.getSelectedItem();
            String room = (String) roomCombo.getSelectedItem();
            String date = dateField.getText().trim();

            if (place == null || place.contains("No") ||
                    hotel == null || hotel.contains("No") ||
                    room == null || room.contains("No") || date.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel, "Please fill all fields with valid data", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tourPlaceId = tourPlaceDAO.getTourPlaceId(place);
            int hotelId = hotelDAO.getHotelId(hotel);
            int roomId = roomDAO.getRoomIdByFullName(room);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            try { sdf.parse(date); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(contentPanel, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = bookingDAO.createBooking(currentUserId, tourPlaceId, hotelId, roomId, date + " 00:00:00", "Pending");
            if (success) {
                JOptionPane.showMessageDialog(contentPanel, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(contentPanel, "Booking failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(wrapper, gbc);
    }
}

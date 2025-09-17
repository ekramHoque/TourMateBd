package ui.handlers.user;

import ui.handlers.DashboardActionHandler;
import dao.BookingDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ShowBookingsHandler implements DashboardActionHandler {
    private final BookingDAO bookingDAO;
    private final int userId;

    public ShowBookingsHandler(BookingDAO bookingDAO, int userId) {
        this.bookingDAO = bookingDAO;
        this.userId = userId;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JTextArea bookingsArea = new JTextArea(15, 50);
        bookingsArea.setEditable(false);

        // Use the new detailed method
        List<Object[]> bookings = bookingDAO.getBookingsByUserIdDetailed(userId);

        if (bookings.isEmpty()) {
            bookingsArea.append("No bookings found.\n");
        } else {
            for (Object[] b : bookings) {
                // b[0]=booking_id, b[1]=tour_place_name, b[2]=hotel_name, b[3]=room_type
                // b[4]=status, b[5]=booking_date
                bookingsArea.append(
                        "Booking ID: " + b[0] + "\n" +
                                "Tour Place: " + b[1] + "\n" +
                                "Hotel: " + b[2] + ", Room: " + b[3] + "\n" +
                                "Status: " + b[4] + ", Date: " + b[5] + "\n" +
                                "-------------------------------------------\n"
                );
            }
        }

        wrapper.add(new JScrollPane(bookingsArea), gbc);
        contentPanel.add(wrapper, gbc);
    }

}

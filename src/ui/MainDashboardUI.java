
package ui;

import ui.handlers.DashboardActionHandler;
import ui.handlers.user.*;
import java.awt.*;

public class MainDashboardUI extends BaseDashboardUI {
    private final int currentUserId;

    public MainDashboardUI(int userId) {
        super();
        this.currentUserId = userId;
    }

    public void displayDashboard() {
        setVisible(true);
    }

    @Override
    protected String getMenuTitle() {
        return "User Dashboard";
    }

    @Override
    protected String[] getMenuOptions() {
        return new String[]{
                "View Tour Places",
                "Manage Bookings",
                "View Bookings",
                "Back to Main Dashboard",
                "Back to HomePageUI"
        };
    }

    @Override
    protected void onMenuOptionSelected(String option) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        DashboardActionHandler handler = null;

        switch (option) {
            case "View Tour Places":
                handler = new ShowTourPlacesHandler(tourPlaceDAO);
                break;
            case "Manage Bookings":
                handler = new ShowManageBookingsHandler(bookingDAO, tourPlaceDAO, hotelDAO, roomDAO, currentUserId);
                break;
            case "View Bookings":
                handler = new ShowBookingsHandler(bookingDAO, currentUserId);
                break;
            case "Back to Main Dashboard":
                dispose();
                new MainDashboardUI(currentUserId).displayDashboard();
                return;
            case "Back to HomePageUI":
                dispose();
                new HomePageUI().setVisible(true);
                return;
        }

        if (handler != null) {
            contentPanel.removeAll();
            handler.execute(contentPanel, gbc);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
}
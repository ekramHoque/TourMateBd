// src/ui/AdminPanelUI.java
package ui;

import ui.handlers.DashboardActionHandler;
import ui.handlers.admin.*;
import java.awt.*;

public class AdminPanelUI extends BaseDashboardUI {

    public void displayAdminPanel() {
        setVisible(true);
    }

    @Override
    protected String getMenuTitle() {
        return "Admin Menu";
    }

    @Override
    protected String[] getMenuOptions() {
        return new String[]{
                "Add Tour Places",
                "Add Hotel",
                "Add Room",
                "View/Manage Users",
                "View/Manage Bookings",
                "Back to Admin Panel",
                "Back to HomePageUI"
        };
    }

    @Override
    protected void onMenuOptionSelected(String option) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        DashboardActionHandler handler = null;

        switch (option) {
            case "Add Tour Places":
                handler = new ShowAddTourPlacesHandler(tourPlaceDAO);
                break;
            case "Add Hotel":
                handler = new ShowAddHotelHandler(tourPlaceDAO, hotelDAO);
                break;
            case "Add Room":
                handler = new ShowAddRoomHandler(hotelDAO, roomDAO);
                break;
            case "View/Manage Users":
                handler = new ShowManageUsersHandler(userDAO);
                break;
            case "View/Manage Bookings":
                handler = new ShowManageBookingsHandler(bookingDAO);
                break;
            case "Back to Admin Panel":
                dispose();
                new AdminPanelUI().displayAdminPanel();
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
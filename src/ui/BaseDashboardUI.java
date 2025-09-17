package ui;

import javax.swing.*;
import java.awt.*;
import dao.*;

public abstract class BaseDashboardUI extends JFrame {
    protected JPanel contentPanel;
    protected JButton selectedButton;
    protected UserDAO userDAO;
    protected BookingDAO bookingDAO;
    protected TourPlaceDAO tourPlaceDAO;
    protected HotelDAO hotelDAO;
    protected RoomDAO roomDAO;

    public BaseDashboardUI() {
        this.userDAO = new UserDAO();
        this.bookingDAO = new BookingDAO();
        this.tourPlaceDAO = new TourPlaceDAO();
        this.hotelDAO = new HotelDAO();
        this.roomDAO = new RoomDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle(getMenuTitle());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(3, 252, 236));

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel titleLabel = new JLabel(getMenuTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(titleLabel, gbc);

        String[] options = getMenuOptions();
        JButton[] buttons = new JButton[options.length];
        for (int i = 0; i < options.length; i++) {
            buttons[i] = createMenuButton(options[i]);
            gbc.gridy = i + 1;
            int index = i;
            buttons[i].addActionListener(e -> handleOptionClick(buttons[index], options[index]));
            menuPanel.add(buttons[i], gbc);
        }

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(new JLabel("Select an option to begin"), gbc);

        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 0, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }

    private void handleOptionClick(JButton button, String option) {
        if (selectedButton != null) {
            selectedButton.setBackground(new Color(0, 0, 255));
        }
        button.setBackground(new Color(0, 51, 102));
        selectedButton = button;
        contentPanel.removeAll();
        onMenuOptionSelected(option);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Abstract methods to be implemented by subclasses
    protected abstract String getMenuTitle();
    protected abstract String[] getMenuOptions();
    protected abstract void onMenuOptionSelected(String option);
}

package ui;

import javax.swing.*;
import java.awt.*;
import controller.UserController;

public class HomePageUI extends JFrame {
    private UserController userController;

    public HomePageUI() {
        this.userController = new UserController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TourMateBD - Home");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel panel = new JPanel();
        panel.setBackground(new Color(3, 252, 236));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to TourMateBD", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        welcomeLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Span across all columns
        panel.add(welcomeLabel, gbc);

        // User Login button
        JButton userLoginButton = new JButton("User Login");
        userLoginButton.setBackground(Color.BLUE);
        userLoginButton.setForeground(Color.WHITE);
        userLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLoginButton.setFocusPainted(false);
        userLoginButton.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset to single column
        panel.add(userLoginButton, gbc);
        userLoginButton.addActionListener(e -> openUserLogin());

        // Admin Login button
        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setBackground(Color.BLUE);
        adminLoginButton.setForeground(Color.WHITE);
        adminLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        adminLoginButton.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(adminLoginButton, gbc);
        adminLoginButton.addActionListener(e -> openAdminLogin());

        // User Registration button
        JButton userRegistrationButton = new JButton("User Registration");
        userRegistrationButton.setBackground(Color.BLUE);
        userRegistrationButton.setForeground(Color.WHITE);
        userRegistrationButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userRegistrationButton.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(userRegistrationButton, gbc);
        userRegistrationButton.addActionListener(e -> openUserRegistration());

        add(panel);


    }

    private void openUserLogin() {
        dispose();
        LoginUI loginUI = new LoginUI(userController, false);
        loginUI.displayLogin();

    }

    private void openAdminLogin() {
        dispose();
        LoginUI loginUI = new LoginUI(userController, true);
        loginUI.displayLogin();
    }

    private void openUserRegistration() {
        dispose();
        RegistrationUI registrationUI = new RegistrationUI(userController);
        registrationUI.displayRegistration();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomePageUI homePage = new HomePageUI();
            homePage.setVisible(true);
        });

    }
}

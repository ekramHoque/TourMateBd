package ui;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.User;
import dao.UserDAO;

public class LoginUI extends JFrame {
    private UserController userController;
    private boolean isAdminLogin;
    private UserDAO userDAO;

    public LoginUI(UserController userController, boolean isAdminLogin) {
        this.userController = userController;
        this.isAdminLogin = isAdminLogin;
        this.userDAO = new UserDAO();
        initializeUI();
    }

    public void displayLogin() {
        setVisible(true);
    }

    private void initializeUI() {
        setTitle(isAdminLogin ? "Admin Login - TourMateBD" : "User Login - TourMateBD");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(3, 252, 236));
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);


        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton backToHomeButton = new JButton("Back to Home");


        Font font = new Font("Segoe UI", Font.BOLD, 16);
        emailLabel.setFont(font);
        emailField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        backToHomeButton.setFont(font);


        Color blue = Color.BLUE;
        loginButton.setBackground(blue);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);

        backToHomeButton.setBackground(blue);
        backToHomeButton.setForeground(Color.WHITE);
        backToHomeButton.setFocusPainted(false);
        backToHomeButton.setOpaque(true);
        backToHomeButton.setBorderPainted(false);


        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);
        gbc.gridy++;
        formPanel.add(emailField, gbc);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);
        gbc.gridy++;
        formPanel.add(loginButton, gbc);
        gbc.gridy++;
        formPanel.add(backToHomeButton, gbc);

        backgroundPanel.add(formPanel);


        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and password are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Login attempt: Email = " + email + ", Password = " + password + ", isAdminLogin = " + isAdminLogin);
            if (userController.handleLogin(email, password, isAdminLogin)) {
                dispose();
                int userId = userDAO.getUserId(email);
                if (userId != -1) {
                    if (isAdminLogin) {
                        new AdminPanelUI().displayAdminPanel();
                    } else {
                        new MainDashboardUI(userId).displayDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found after login", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login failed (Check console for details)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backToHomeButton.addActionListener(e -> {
            dispose();
            new HomePageUI().setVisible(true);
        });
    }
}
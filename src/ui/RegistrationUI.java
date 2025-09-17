package ui;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.User;

public class RegistrationUI extends JFrame {
    private UserController userController;

    public RegistrationUI(UserController userController) {
        this.userController = userController;
        initializeUI();
    }

    public void displayRegistration() {
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("User Registration - TourMateBD");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);



        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(3, 252, 236));
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton registerButton = new JButton("Register");
        JButton backToHomeButton = new JButton("Back to Home");

        // Font
        Font font = new Font("Segoe UI", Font.BOLD, 16);
        nameLabel.setFont(font);
        nameField.setFont(font);
        emailLabel.setFont(font);
        emailField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        registerButton.setFont(font);
        backToHomeButton.setFont(font);

        // Button styling
        Color blue = Color.BLUE; // Dodger Blue
        registerButton.setBackground(blue);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);

        backToHomeButton.setBackground(blue);
        backToHomeButton.setForeground(Color.WHITE);
        backToHomeButton.setFocusPainted(false);
        backToHomeButton.setOpaque(true);
        backToHomeButton.setBorderPainted(false);

        // Add components to form panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridy++;
        formPanel.add(nameField, gbc);
        gbc.gridy++;
        formPanel.add(emailLabel, gbc);
        gbc.gridy++;
        formPanel.add(emailField, gbc);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);
        gbc.gridy++;
        formPanel.add(registerButton, gbc);
        gbc.gridy++;
        formPanel.add(backToHomeButton, gbc);

        // Add form panel to center of background
        backgroundPanel.add(formPanel);

        // Action Listeners
        registerButton.addActionListener(e -> {
            if (!areFieldsFilled(nameField, emailField, passwordField)) {
                return;
            }
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(0, name, email, password); // id=0, isAdmin=false by default
            if (userController.handleRegistration(user)) {
                JOptionPane.showMessageDialog(this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new HomePageUI().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed (Duplicate name/email or error)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backToHomeButton.addActionListener(e -> {
            dispose();
            new HomePageUI().setVisible(true);
        });
    }

    /**
     * Checks if all required fields are filled.
     * @param nameField The name text field.
     * @param emailField The email text field.
     * @param passwordField The password field.
     * @return true if all fields are filled, false otherwise.
     */
    private boolean areFieldsFilled(JTextField nameField, JTextField emailField, JPasswordField passwordField) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
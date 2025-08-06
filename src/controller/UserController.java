package controller;

import dao.UserDAO;
import model.User;

public class UserController {
    private UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    public boolean handleRegistration(User user) {
        boolean success = userDAO.saveUser(user);
        if (!success) {
            System.err.println("Registration failed for user: " + user.getEmail());
        } else {
            System.out.println("Registration successful for: " + user.getEmail());
        }
        return success;
    }

    public boolean handleLogin(String email, String password, boolean isAdminLogin) {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            System.out.println("Retrieved user: Email = " + user.getEmail() + ", Password = " + user.getPassword() + ", isAdmin = " + user.isAdmin());
            if (password.equals(user.getPassword())) {
                if (isAdminLogin && user.isAdmin()) {
                    System.out.println("Admin login successful for: " + email);
                    return true;
                } else if (!isAdminLogin && !user.isAdmin()) {
                    System.out.println("User login successful for: " + email);
                    return true;
                } else {
                    System.err.println("Login type mismatch: Expected " + (isAdminLogin ? "Admin" : "User") + ", but user is " + (user.isAdmin() ? "Admin" : "User"));
                }
            } else {
                System.err.println("Password mismatch for: " + email + ". Entered: " + password + ", Stored: " + user.getPassword());
            }
        } else {
            System.err.println("User not found: " + email);
        }
        return false;
    }
}
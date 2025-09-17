package ui.handlers.user;

import ui.handlers.DashboardActionHandler;
import dao.TourPlaceDAO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ShowTourPlacesHandler implements DashboardActionHandler {
    private final TourPlaceDAO tourPlaceDAO;

    public ShowTourPlacesHandler(TourPlaceDAO dao) {
        this.tourPlaceDAO = dao;
    }

    @Override
    public void execute(JPanel contentPanel, GridBagConstraints gbc) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // Dropdown for places
        String[] placeNames = tourPlaceDAO.getTourPlaces();
        JComboBox<String> placeCombo = new JComboBox<>(placeNames.length > 0 ? placeNames : new String[]{"No places available"});
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.BLACK);

        GridBagConstraints inner = new GridBagConstraints();
        inner.insets = new Insets(5, 5, 5, 5);
        inner.gridx = 0;

        inner.gridy = 0;
        wrapper.add(new JLabel("Select Tour Place:"), inner);
        inner.gridy = 1;
        wrapper.add(placeCombo, inner);
        inner.gridy = 2;
        wrapper.add(detailsPanel, inner);

        // Default (latest place)
        List<Object[]> latestPlaces = tourPlaceDAO.getAllTourPlacesWithPhotos();
        if (!latestPlaces.isEmpty()) {
            displayPlaceDetails(detailsPanel, latestPlaces.get(0), new GridBagConstraints());
        } else {
            detailsPanel.add(new JLabel("No tour places available"), new GridBagConstraints());
        }

        // Update on selection
        placeCombo.addActionListener(e -> {
            String selected = (String) placeCombo.getSelectedItem();
            detailsPanel.removeAll();
            if (selected != null && !selected.equals("No places available")) {
                int placeId = tourPlaceDAO.getTourPlaceId(selected);
                if (placeId != -1) {
                    List<Object[]> allPlaces = tourPlaceDAO.getAllTourPlacesWithPhotos();
                    for (Object[] place : allPlaces) {
                        if ((int) place[0] == placeId) {
                            displayPlaceDetails(detailsPanel, place, new GridBagConstraints());
                            break;
                        }
                    }
                } else {
                    detailsPanel.add(new JLabel("Invalid tour place ID"), new GridBagConstraints());
                }
            } else {
                detailsPanel.add(new JLabel("No details available"), new GridBagConstraints());
            }
            detailsPanel.revalidate();
            detailsPanel.repaint();
        });

        contentPanel.add(wrapper, gbc);
    }

    private void displayPlaceDetails(JPanel panel, Object[] place, GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        int y = 0;

        String[] photos = (String[]) place[6];
        for (int i = 0; i < Math.min(3, photos.length); i++) {
            JLabel imageLabel = new JLabel();
            try {
                if (photos[i] != null && !photos[i].isEmpty()) {
                    String path = photos[i].trim();
                    if (path.startsWith("/")) path = path.substring(1);

                    java.net.URL imgURL = getClass().getClassLoader().getResource(path);

                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(400, 100, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(img));
                        System.out.println("Loaded from resources: " + path);
                    } else {
                        // fallback: load directly from file system
                        File f = new File("src/main/resources/" + path);
                        if (f.exists()) {
                            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                            Image img = icon.getImage().getScaledInstance(400, 100, Image.SCALE_SMOOTH);
                            imageLabel.setIcon(new ImageIcon(img));
                            System.out.println("Loaded from file system: " + f.getAbsolutePath());
                        } else {
                            imageLabel.setText("Image not found: " + path);
                            System.err.println("Image missing: " + path);
                        }
                    }
                } else {
                    imageLabel.setText("No Image " + (i + 1));
                }
            } catch (Exception e) {
                imageLabel.setText("Error: " + e.getMessage());
                System.err.println("Error loading " + photos[i] + ": " + e.getMessage());
            }
            gbc.gridy = y++;
            panel.add(imageLabel, gbc);
        }

        // Styled labels
        gbc.gridy = y++;
        JLabel nameLabel = new JLabel("Name: " + place[1]);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, gbc);

        gbc.gridy = y++;
        JLabel descLabel = new JLabel("Description: " + place[2]);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        descLabel.setForeground(Color.WHITE);
        panel.add(descLabel, gbc);

        gbc.gridy = y++;
        JLabel addrLabel = new JLabel("Address: " + place[3]);
        addrLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        addrLabel.setForeground(Color.WHITE);
        panel.add(addrLabel, gbc);

        gbc.gridy = y++;
        JLabel latLabel = new JLabel("Latitude: " + place[4]);
        latLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        latLabel.setForeground(Color.WHITE);
        panel.add(latLabel, gbc);

        gbc.gridy = y++;
        JLabel longLabel = new JLabel("Longitude: " + place[5]);
        longLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        longLabel.setForeground(Color.WHITE);
        panel.add(longLabel, gbc);
    }
}

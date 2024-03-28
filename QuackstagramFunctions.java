import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class QuackstagramFunctions extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
    
    
    protected abstract void initializeUI(NotificationsManager notificationManager);

    protected JPanel createHeaderPanel(String title) {
       
         // Header Panel (reuse from InstagramProfileUI or customize for home page)
          // Header with the Register label
          JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
          headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
          JLabel lblRegister = new JLabel(title);
          lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
          lblRegister.setForeground(Color.WHITE); // Set the text color to white
          headerPanel.add(lblRegister);
          headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
          return headerPanel;
    }

    protected JPanel createNavigationPanel(NotificationsManager notificationManager) {
        // Create and return the navigation panel
         // Navigation Bar
         JPanel navigationPanel = new JPanel();
         navigationPanel.setBackground(new Color(249, 249, 249));
         navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
         navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
 
         navigationPanel.add(createIconButton("img/icons/home.png", "home", notificationManager));
         navigationPanel.add(Box.createHorizontalGlue());
         navigationPanel.add(createIconButton("img/icons/search.png","explore", notificationManager));
         navigationPanel.add(Box.createHorizontalGlue());
         navigationPanel.add(createIconButton("img/icons/add.png","add", notificationManager));
         navigationPanel.add(Box.createHorizontalGlue());
         navigationPanel.add(createIconButton("img/icons/heart.png","notification", notificationManager));
         navigationPanel.add(Box.createHorizontalGlue());
         navigationPanel.add(createIconButton("img/icons/profile.png", "profile", notificationManager));
 
         return navigationPanel;
    }

    protected JButton createIconButton(String iconPath, String buttonType, NotificationsManager notificationManager) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
    
        // Define actions based on button type
        if ("home".equals(buttonType)) {
            button.addActionListener(e -> openHomeUI(notificationManager));
        } else if ("profile".equals(buttonType)) {
            button.addActionListener(e -> openProfileUI(notificationManager));
        } else if ("notification".equals(buttonType)) {
            button.addActionListener(e -> notificationsUI(notificationManager));
        } else if ("explore".equals(buttonType)) {
            button.addActionListener(e -> exploreUI(notificationManager));
        } else if ("add".equals(buttonType)) {
            button.addActionListener(e -> ImageUploadUI(notificationManager));
        }
        return button;
    
        
    }
 
    protected void ImageUploadUI(NotificationsManager notificationManager) {
        // Open InstagramProfileUI frame
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI(notificationManager);
        upload.setVisible(true);
    }

    protected void openProfileUI(NotificationsManager notificationManager) {
       // Open InstagramProfileUI frame
       this.dispose();
       String loggedInUsername = "";

        // Read the logged-in user's username from users.txt
    try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
        String line = reader.readLine();
        if (line != null) {
            loggedInUsername = line.split(":")[0].trim();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
     User user = new User(loggedInUsername);
       InstagramProfileUI profileUI = new InstagramProfileUI(user, notificationManager);
       profileUI.setVisible(true);
   }
 
    protected void notificationsUI(NotificationsManager notificationManager) {
        // Open InstagramProfileUI frame
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI(notificationManager);
        notificationsUI.setVisible(true);
    }
 
    protected void openHomeUI(NotificationsManager notificationManager) {
        // Open InstagramProfileUI frame
        this.dispose();
        QuakstagramHomeUI homeUI = new QuakstagramHomeUI(notificationManager);
        homeUI.setVisible(true);
    }

 
    protected void exploreUI(NotificationsManager notificationManager) {
        // Open InstagramProfileUI frame
        this.dispose();
        ExploreUI explore = new ExploreUI(notificationManager);
        explore.setVisible(true);
    }
}


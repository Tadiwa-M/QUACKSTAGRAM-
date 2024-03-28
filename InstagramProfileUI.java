import javax.swing.*;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.awt.*;
import java.nio.file.*;
import java.util.stream.Stream;



public class InstagramProfileUI extends QuackstagramFunctions {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel;   // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information

    public InstagramProfileUI(User user, NotificationsManager notificationManager) {
        this.currentUser = user;
        initializeCounts();
        initializeUI(notificationManager);
    }

    // Method to initialize counts
    protected void initializeCounts() {
        int imageCount = countUserImages(currentUser.getUsername());
        int[] followerFollowingCounts = calculateFollowerFollowingCounts(currentUser.getUsername());
        int followersCount = followerFollowingCounts[0];
        int followingCount = followerFollowingCounts[1];

        String bio = fetchUserBio(currentUser.getUsername());

        currentUser.setPostCount(imageCount);
        currentUser.setFollowersCount(followersCount);
        currentUser.setFollowingCount(followingCount);
        currentUser.setBio(bio);
    }

    // Method to count the number of images posted by the user
    private int countUserImages(String username) {
        int imageCount = 0;
        Path imageDetailsFilePath = Paths.get("img", "image_details.txt");
        try (BufferedReader imageDetailsReader = Files.newBufferedReader(imageDetailsFilePath)) {
            String line;
            while ((line = imageDetailsReader.readLine()) != null) {
                if (line.contains("Username: " + username)) {
                    imageCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageCount;
    }

    // Method to calculate followers and following counts
    private int[] calculateFollowerFollowingCounts(String username) {
        int followersCount = 0;
        int followingCount = 0;

        Path followingFilePath = Paths.get("data", "following.txt");
        try (BufferedReader followingReader = Files.newBufferedReader(followingFilePath)) {
            String line;
            while ((line = followingReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String user = parts[0].trim();
                    String[] followingUsers = parts[1].split(";");
                    if (user.equals(username)) {
                        followingCount = followingUsers.length;
                    } else {
                        for (String followingUser : followingUsers) {
                            if (followingUser.trim().equals(username)) {
                                followersCount++;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new int[]{followersCount, followingCount};
    }

    // Method to fetch user bio
    private String fetchUserBio(String username) {
        String bio = "";
        Path bioDetailsFilePath = Paths.get("data", "credentials.txt");
        try (BufferedReader bioDetailsReader = Files.newBufferedReader(bioDetailsFilePath)) {
            String line;
            while ((line = bioDetailsReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts.length >= 3) {
                    bio = parts[2];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bio;
    }


    // Method to initialize the UI
    public void initializeUI(NotificationsManager notificationManager) {
        setupFrame();
        setupPanels(notificationManager);
        initializeImageGrid(notificationManager);
    }

    // Method to set up the frame
    private void setupFrame() {
        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    // Method to set up header and navigation panels
    private void setupPanels(NotificationsManager notificationManager) {
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel("InstagramProfile");
        navigationPanel = createNavigationPanel(notificationManager);
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(navigationPanel, BorderLayout.SOUTH);
    }


    // Method to initialize the image grid
    private void initializeImageGrid(NotificationsManager notificationManager) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(0, 3, 5, 5));

        Path imageDir = Paths.get("img", "uploaded");
        try (Stream<Path> paths = Files.list(imageDir)) {
            paths.filter(path -> path.getFileName().toString().startsWith(currentUser.getUsername() + "_"))
                    .forEach(path -> addImageToGrid(path, notificationManager));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Method to add an image to the grid
    private void addImageToGrid(Path imagePath, NotificationsManager notificationManager) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(imagePath.toString()).getImage()
                .getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayImage(imageIcon, notificationManager);
            }
        });
        contentPanel.add(imageLabel);
    }

    // Method to display a clicked image
    private void displayImage(ImageIcon imageIcon, NotificationsManager notificationManager) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel fullSizeImageLabel = new JLabel(imageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            getContentPane().removeAll();
            initializeUI(notificationManager);
        });
        contentPanel.add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
}



   /* private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }




}*/

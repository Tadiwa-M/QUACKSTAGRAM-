import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageUploadUI extends QuackstagramFunctions {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int NAV_ICON_SIZE = 20; // Size for navigation icons
    private JLabel imagePreviewLabel;
    private JTextArea bioTextArea;
    private JButton uploadButton;
    private JButton saveButton;
    private boolean imageUploaded = false;

    public ImageUploadUI(NotificationsManager notificationManager) {
        setTitle("Upload Image");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeUI(notificationManager);
    }

   protected void initializeUI(NotificationsManager notificationManager) {
        JPanel headerPanel = createHeaderPanel();
        JPanel navigationPanel = createNavigationPanel(notificationManager);
        JPanel contentPanel = createContentPanel(); // Extracted method for creating the main content panel

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        imagePreviewLabel = createImagePreviewLabel();
        contentPanel.add(imagePreviewLabel);

        bioTextArea = createBioTextArea();
        contentPanel.add(bioTextArea);

        uploadButton = createUploadButton();
        contentPanel.add(uploadButton);

        saveButton = createSaveButton();
        contentPanel.add(saveButton);

        return contentPanel;
    }

    private JLabel createImagePreviewLabel() {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(WIDTH, HEIGHT / 3));
        label.setIcon(new ImageIcon()); // Set an initial empty icon
        return label;
    }

    private JTextArea createBioTextArea() {
        JTextArea textArea = new JTextArea("Enter a caption");
        textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));
        return textArea;
    }

    private JButton createUploadButton() {
        JButton button = new JButton("Upload Image");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this::uploadAction);
        return button;
    }

    private JButton createSaveButton() {
        JButton button = new JButton("Save Caption");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this::saveBioAction);
        return button;
    }

    private void uploadAction(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(filter);
    
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String username = readUsername(); // Read username from users.txt
                int imageId = getNextImageId(username);
                String fileExtension = getFileExtension(selectedFile);
                String newFileName = username + "_" + imageId + "." + fileExtension;
    
                Path destPath = Paths.get("img", "uploaded", newFileName);
                Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
    
                // Save the bio and image ID to a text file
                saveImageInfo(username + "_" + imageId, username, bioTextArea.getText());
    
                // Load the image from the saved path
                ImageIcon imageIcon = new ImageIcon(destPath.toString());
    
                // Check if imagePreviewLabel has a valid size
                if (imagePreviewLabel.getWidth() > 0 && imagePreviewLabel.getHeight() > 0) {
                    Image image = imageIcon.getImage();
    
                    // Calculate the dimensions for the image preview
                    int previewWidth = imagePreviewLabel.getWidth();
                    int previewHeight = imagePreviewLabel.getHeight();
                    int imageWidth = image.getWidth(null);
                    int imageHeight = image.getHeight(null);
                    double widthRatio = (double) previewWidth / imageWidth;
                    double heightRatio = (double) previewHeight / imageHeight;
                    double scale = Math.min(widthRatio, heightRatio);
                    int scaledWidth = (int) (scale * imageWidth);
                    int scaledHeight = (int) (scale * imageHeight);
    
                    // Set the image icon with the scaled image
                    imageIcon.setImage(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH));
                }
    
                imagePreviewLabel.setIcon(imageIcon);
    
                // Update the flag to indicate that an image has been uploaded
                imageUploaded = true;
    
                // Change the text of the upload button
                uploadButton.setText("Upload Another Image");
    
                JOptionPane.showMessageDialog(this, "Image uploaded and preview updated!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private int getNextImageId(String username) throws IOException {
        Path storageDir = Paths.get("img", "uploaded"); // Ensure this is the directory where images are saved
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    
        int maxId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDir, username + "_*")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(username.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore filenames that do not have a valid numeric ID
                    }
                }
            }
        }
        return maxId + 1; // Return the next available ID
    }
    
    private void saveImageInfo(String imageId, String username, String bio) throws IOException {
        Path infoFilePath = Paths.get("img", "image_details.txt");
        if (!Files.exists(infoFilePath)) {
            Files.createFile(infoFilePath);
        }
    
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
        try (BufferedWriter writer = Files.newBufferedWriter(infoFilePath, StandardOpenOption.APPEND)) {
            writer.write(String.format("ImageID: %s, Username: %s, Bio: %s, Timestamp: %s, Likes: 0", imageId, username, bio, timestamp));
            writer.newLine();
        }
    
}


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private void saveBioAction(ActionEvent event) {
        // Here you would handle saving the bio text
        String bioText = bioTextArea.getText();
        // For example, save the bio text to a file or database
        JOptionPane.showMessageDialog(this, "Caption saved: " + bioText);
    }
   
    private JPanel createHeaderPanel() {
       
        // Header Panel (reuse from InstagramProfileUI or customize for home page)
         // Header with the Register label
         JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
         JLabel lblRegister = new JLabel(" Upload Image 🐥");
         lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
         lblRegister.setForeground(Color.WHITE); // Set the text color to white
         headerPanel.add(lblRegister);
         headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
         return headerPanel;
   }

   private String readUsername() throws IOException {
    Path usersFilePath = Paths.get("data", "users.txt");
    try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
        String line = reader.readLine();
        if (line != null) {
            return line.split(":")[0]; // Extract the username from the first line
        }
    }
    return null; // Return null if no username is found
}


}

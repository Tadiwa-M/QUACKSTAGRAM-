import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRelationshipManager {
    private final String followersFilePath = "data/followers.txt";

    public void followUser(String follower, String followed) throws IOException {
        if (!isAlreadyFollowing(follower, followed)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(followersFilePath, true))) {
                writer.write(follower + ":" + followed);
                writer.newLine();
            }
        }
    }

    private boolean isAlreadyFollowing(String follower, String followed) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(followersFilePath))) {
            return reader.lines().anyMatch(line -> line.equals(follower + ":" + followed));
        }
    }

    public List<String> getFollowers(String username) throws IOException {
        return getUserRelationships(username, 1);
    }

    public List<String> getFollowing(String username) throws IOException {
        return getUserRelationships(username, 0);
    }

    private List<String> getUserRelationships(String username, int index) throws IOException {
        List<String> relationships = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(followersFilePath))) {
            reader.lines()
                  .map(line -> line.split(":"))
                  .filter(parts -> parts.length == 2)
                  .filter(parts -> parts[index].equals(username))
                  .map(parts -> parts[1 - index])
                  .forEach(relationships::add);
        }
        return relationships;
    }
}
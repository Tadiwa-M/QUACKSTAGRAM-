public class ImageDetails {
    private String username;
    private String bio;
    private String timestampString;
    private int likes;


    

    public ImageDetails(String username, String bio, String timestampString, int likes){
        this.username = username;
        this.bio = bio;
        this.timestampString = timestampString;
        this.likes = likes;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getTimestamp() {
        return timestampString;
    }

    public int getLikes() {
        return likes;
    }
    
}
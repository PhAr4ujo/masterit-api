package masterit.masterit.dtos.input;

public class UpdatePageRequest {
    private String title;
    private String content; // This will be the JSON string

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
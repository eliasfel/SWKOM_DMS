package swkom_dms.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class DocumentDTO {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Content must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String content;
    private LocalDateTime dateUploaded;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDateUploaded() { return dateUploaded; }
    public void setDateUploaded(LocalDateTime dateUploaded) { this.dateUploaded = dateUploaded; }
}

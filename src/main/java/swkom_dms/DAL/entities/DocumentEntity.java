package swkom_dms.DAL.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob // Use @Lob to store large objects like files
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "date_uploaded", nullable = false, updatable = false)
    private LocalDateTime dateUploaded;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDateUploaded() { return dateUploaded; }
    public void setDateUploaded(LocalDateTime dateUploaded) { this.dateUploaded = dateUploaded; }
}


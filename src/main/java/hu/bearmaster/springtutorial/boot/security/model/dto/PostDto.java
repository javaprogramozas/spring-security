package hu.bearmaster.springtutorial.boot.security.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "posts", schema = "blogs")
@SequenceGenerator(name = "postIdGenerator", sequenceName = "posts_seq", schema = "blogs", initialValue = 1, allocationSize = 1)
public class PostDto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postIdGenerator")
    private Long id;

    private String title;

    private String description;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    private int likes;

    private String slug;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserDto author;

    private String topic;

    private boolean published;
    
    public PostDto() {
    }

    public PostDto(boolean published) {
        this.published = published;
    }
    
    public PostDto(String title, String description, ZonedDateTime createdAt, int likes, String slug) {
        this(null, title, description, createdAt, likes, slug);
    }

    public PostDto(Long id, String title, String description, ZonedDateTime createdAt, int likes, String slug) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.likes = likes;
        this.slug = slug;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostDto post = (PostDto) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package hu.bearmaster.springtutorial.boot.security.model.vo;

import hu.bearmaster.springtutorial.boot.security.model.dto.PostDto;

import java.time.ZonedDateTime;

public class Post {

    private Long id;

    private String title;

    private String description;

    private ZonedDateTime createdAt;

    private int likes;

    private String slug;

    private User author;

    private String topic;

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + "]";
    }

    public static Post from(PostDto postDto) {
        Post post = new Post();
        post.id = postDto.getId();
        post.title = postDto.getTitle();
        post.description = postDto.getDescription();
        post.createdAt = postDto.getCreatedAt();
        post.likes = postDto.getLikes();
        post.slug = postDto.getSlug();
        post.author = User.from(postDto.getAuthor());
        post.topic = postDto.getTopic();
        return post;
    }
}

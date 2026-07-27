package com.shaadimetrics.website.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_image")
public class GalleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GalleryCategory category;

    /** Web path such as /images/gallery/entry/01.jpeg or /uploads/gallery/xyz.jpg */
    @Column(nullable = false)
    private String imagePath;

    private String caption;

    @Column(nullable = false)
    private int sortOrder = 0;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GalleryCategory getCategory() { return category; }
    public void setCategory(GalleryCategory category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

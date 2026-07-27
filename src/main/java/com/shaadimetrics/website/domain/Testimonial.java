package com.shaadimetrics.website.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "testimonial")
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String coupleNames;

    private String location;

    @Column(nullable = false, length = 1000)
    private String quote;

    private String photoPath;

    @Column(nullable = false)
    private boolean published = true;

    @Column(nullable = false)
    private int sortOrder = 0;

    /** Moderation state. Reviews added by a Content Manager start PENDING until a Super Admin approves them. */
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'APPROVED'")
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.APPROVED;

    /** Username of the team member who submitted this review, for accountability in the moderation queue. */
    private String submittedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoupleNames() { return coupleNames; }
    public void setCoupleNames(String coupleNames) { this.coupleNames = coupleNames; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
}

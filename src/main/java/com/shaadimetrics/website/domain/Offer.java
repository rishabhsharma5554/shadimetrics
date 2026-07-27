package com.shaadimetrics.website.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "offer")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, unique = true)
    private String couponCode;

    /** Short badge text, e.g. "Flat 15% Off" or "Free Photobooth" */
    @Column(nullable = false)
    private String discountText;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active = true;

    private String imagePath;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getDiscountText() { return discountText; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Transient
    public boolean isCurrentlyValid() {
        LocalDate today = LocalDate.now();
        boolean afterStart = validFrom == null || !today.isBefore(validFrom);
        boolean beforeEnd = validTo == null || !today.isAfter(validTo);
        return active && afterStart && beforeEnd;
    }
}

package com.shaadimetrics.website.domain;

public enum GalleryCategory {
    ENTRY("entry", "Entry Experiences"),
    HOME("home", "Home Celebrations"),
    PUJAN("pujan", "Divine Pujan Setups"),
    HALDI("haldi", "Haldi & Mehndi Moments"),
    SANGEET("sangeet", "Sangeet Nights"),
    BARAAT("baraat", "Baraat Celebrations"),
    MANDAP("mandap", "Mandap Ceremonies"),
    RECEPTION("reception", "Reception Evenings"),
    CATERING("catering", "Culinary Experiences (Catering)"),
    GUEST("guest", "Guest Management Services"),
    INVITATIONS("invitations", "Invitation & Stationery Designs"),
    PHOTOGRAPHY("photography", "Wedding Photography & Films");

    private final String slug;
    private final String label;

    GalleryCategory(String slug, String label) {
        this.slug = slug;
        this.label = label;
    }

    public String getSlug() { return slug; }
    public String getLabel() { return label; }
}

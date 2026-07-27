package com.shaadimetrics.website.web;

/** Form payload for the "Book Free Consultation" modal, bound directly from the request. */
public record ConsultationRequest(String name, String phone, String weddingDate, String message, String preferredCallTime) {

    public String summarize() {
        if (weddingDate() != null && !weddingDate().isBlank()) {
            return "%s (target date: %s)".formatted(name(), weddingDate());
        }
        return name();
    }
}

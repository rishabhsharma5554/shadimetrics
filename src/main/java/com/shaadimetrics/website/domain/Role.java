package com.shaadimetrics.website.domain;

/**
 * SUPER_ADMIN — full control, including managing other admin users.
 * CONTENT_MANAGER — manages gallery, testimonials, services, offers.
 * LEADS_MANAGER — views and actions consultation form submissions only.
 */
public enum Role {
    SUPER_ADMIN,
    CONTENT_MANAGER,
    LEADS_MANAGER
}

package com.shaadimetrics.website.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the current request path available to every template as ${currentUri},
 * since Thymeleaf 3.1 no longer exposes #request/#session directly to expressions.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

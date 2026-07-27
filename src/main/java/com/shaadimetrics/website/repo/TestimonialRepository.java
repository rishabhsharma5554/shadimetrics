package com.shaadimetrics.website.repo;

import com.shaadimetrics.website.domain.ReviewStatus;
import com.shaadimetrics.website.domain.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findAllByOrderBySortOrderAsc();
    List<Testimonial> findByPublishedTrueOrderBySortOrderAsc();
    List<Testimonial> findByStatusAndPublishedTrueOrderBySortOrderAsc(ReviewStatus status);
    long countByStatus(ReviewStatus status);
}

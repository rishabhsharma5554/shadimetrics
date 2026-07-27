package com.shaadimetrics.website.repo;

import com.shaadimetrics.website.domain.GalleryCategory;
import com.shaadimetrics.website.domain.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    List<GalleryImage> findAllByOrderByCategoryAscSortOrderAsc();
    List<GalleryImage> findByCategoryOrderBySortOrderAsc(GalleryCategory category);
    long countByCategory(GalleryCategory category);
}

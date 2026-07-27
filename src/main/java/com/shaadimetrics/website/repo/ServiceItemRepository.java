package com.shaadimetrics.website.repo;

import com.shaadimetrics.website.domain.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findAllByOrderBySortOrderAsc();
    List<ServiceItem> findByPublishedTrueOrderBySortOrderAsc();
}

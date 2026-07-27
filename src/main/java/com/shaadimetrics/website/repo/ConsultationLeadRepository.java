package com.shaadimetrics.website.repo;

import com.shaadimetrics.website.domain.ConsultationLead;
import com.shaadimetrics.website.domain.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsultationLeadRepository extends JpaRepository<ConsultationLead, Long> {
    List<ConsultationLead> findAllByOrderByCreatedAtDesc();

    long countByStatus(LeadStatus status);

    @Query("SELECT COUNT(DISTINCT cl.preferredCity) FROM ConsultationLead cl WHERE cl.preferredCity IS NOT NULL AND cl.preferredCity != ''")
    long countDistinctPreferredCities();
}

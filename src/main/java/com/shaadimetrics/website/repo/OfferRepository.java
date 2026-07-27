package com.shaadimetrics.website.repo;

import com.shaadimetrics.website.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findAllByOrderByIdDesc();
    List<Offer> findByActiveTrueOrderByIdDesc();
}

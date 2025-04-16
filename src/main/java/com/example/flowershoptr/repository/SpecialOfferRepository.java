package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.SpecialOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialOfferRepository extends JpaRepository<SpecialOffer, Long> {
    List<SpecialOffer> findByActiveTrue();

}

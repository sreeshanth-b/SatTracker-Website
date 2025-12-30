package com.sattracker.backend.repository;

import com.sattracker.backend.model.GroundStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroundStationRepository
        extends JpaRepository<GroundStation, Long> {

    Optional<GroundStation> findByNameIgnoreCase(String name);

    Optional<GroundStation> findFirstByCountryIgnoreCase(String country);
}




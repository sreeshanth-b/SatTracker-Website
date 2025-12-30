package com.sattracker.backend.repository;

import com.sattracker.backend.model.Tle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TleRepository extends JpaRepository<Tle, Long> {

    @Query(value = "SELECT * FROM tle LIMIT ?1", nativeQuery = true)
    List<Tle> findLimited(int limit);

    List<Tle> findByName(String name);

    List<Tle> findByNameContainingIgnoreCase(String fragment);

    List<Tle> findByNameIgnoreCase(String name);
}

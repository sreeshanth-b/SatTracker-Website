package com.sattracker.backend.service;

import com.sattracker.backend.model.Satellite;
import com.sattracker.backend.repository.SatelliteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SatelliteService {

    private final SatelliteRepository repository;

    public SatelliteService(SatelliteRepository repository) {
        this.repository = repository;
    }

    public List<Satellite> getAllSatellites() {
        return repository.findAll();
    }

    public Satellite addSatellite(Satellite sat) {
        return repository.save(sat);
    }
}

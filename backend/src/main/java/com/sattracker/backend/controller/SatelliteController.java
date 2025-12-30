package com.sattracker.backend.controller;

import com.sattracker.backend.model.Satellite;
import com.sattracker.backend.service.SatelliteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/satellites")
public class SatelliteController {

    private final SatelliteService service;

    public SatelliteController(SatelliteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Satellite> getSatellites() {
        return service.getAllSatellites();
    }

    @PostMapping
    public Satellite addSatellite(@RequestBody Satellite satellite) {
        return service.addSatellite(satellite);
    }
}

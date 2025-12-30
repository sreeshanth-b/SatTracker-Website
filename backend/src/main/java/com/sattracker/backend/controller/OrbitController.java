package com.sattracker.backend.controller;

import com.sattracker.backend.model.*;
import com.sattracker.backend.repository.*;
import com.sattracker.backend.service.OrbitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orbit")
@CrossOrigin
public class OrbitController {

    private final TleRepository tleRepository;
    private final GroundStationRepository groundStationRepository;
    private final OrbitService orbitService;

    public OrbitController(
            TleRepository tleRepository,
            GroundStationRepository groundStationRepository,
            OrbitService orbitService
    ) {
        this.tleRepository = tleRepository;
        this.groundStationRepository = groundStationRepository;
        this.orbitService = orbitService;
    }

    @GetMapping("/pos/{satellite}")
    public SatellitePositionResponse getPosition(
            @PathVariable String satellite,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double alt,
            @RequestParam(required = false) String utc
    ) {

        Tle tle = tleRepository.findByNameIgnoreCase(satellite)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("TLE not found"));

        GroundStation station = resolveStation(city, lat, lon, alt);

        return orbitService.computePosition(tle, station, utc);
    }


    @GetMapping("/pass/{satellite}")
    public SatellitePassResponse getNextPass(
            @PathVariable String satellite,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double alt
    ) {

        Tle tle = tleRepository.findByNameIgnoreCase(satellite)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("TLE not found"));

        GroundStation station = resolveStation(city, lat, lon, alt);

        return orbitService.computeNextPass(tle, station);
    }

    
    @GetMapping("/live/{satellite}")
    public RadarResponse getLiveRadar(
            @PathVariable String satellite,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double alt
    ) {

        Tle tle = tleRepository.findByNameIgnoreCase(satellite)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("TLE not found"));

        GroundStation station = resolveStation(city, lat, lon, alt);

        return orbitService.computeLiveRadar(tle, station);
    }

    
    @GetMapping("/track/{satellite}")
    public List<SatelliteTrackPoint> getOrbitTrack(
            @PathVariable String satellite
    ) {

        Tle tle = tleRepository.findByNameIgnoreCase(satellite)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("TLE not found"));

        return orbitService.computeOrbitTrack(tle);
    }

        @GetMapping("/visible")
public Map<String, Object> getVisibleSatellites(
        @RequestParam double lat,
        @RequestParam double lon,
        @RequestParam(required = false) Double alt
) {

    GroundStation gs = new GroundStation();
    gs.setName("Custom GS");
    gs.setLatitude(lat);
    gs.setLongitude(lon);
    gs.setAltitude(alt != null ? alt : 0);

    List<Tle> allTles = tleRepository.findAll();

    return orbitService.computeVisibleSatellites(allTles, gs);
}




    private GroundStation resolveStation(
            String city, Double lat, Double lon, Double alt
    ) {

        if (lat != null && lon != null) {
            GroundStation gs = new GroundStation();
            gs.setName("Custom Station");
            gs.setLatitude(lat);
            gs.setLongitude(lon);
            gs.setAltitude(alt != null ? alt : 0);
            return gs;
        }

        if (city != null) {
            return groundStationRepository.findByNameIgnoreCase(city)
                    .orElseThrow(() -> new RuntimeException("City not found"));
        }

        throw new RuntimeException("Provide city OR lat/lon");
    }
}

package com.sattracker.backend.model;

import lombok.Data;

@Data
public class SatelliteTrackPoint {
    private double latitude;
    private double longitude;
    private double altitude;
    private String timestamp;
}

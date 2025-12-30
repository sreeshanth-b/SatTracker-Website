package com.sattracker.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class VisibleSatellitesResult {
    private double latitude;
    private double longitude;
    private int count;
    private List<VisibleSatelliteResponse> satellites;
}

package com.sattracker.backend.model;

public class VisibleSatelliteResponse {

    private String name;
    private double azimuth;
    private double elevation;
    private double range;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAzimuth() { return azimuth; }
    public void setAzimuth(double azimuth) { this.azimuth = azimuth; }

    public double getElevation() { return elevation; }
    public void setElevation(double elevation) { this.elevation = elevation; }

    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
}

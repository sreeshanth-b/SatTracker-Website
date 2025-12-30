package com.sattracker.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GroundStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // City or country name
    private String name;
    private String country;

    // Ground station coordinates
    private double latitude;
    private double longitude;
    private double altitude;
}

package com.sattracker.backend.model;

import jakarta.persistence.*;

@Entity
public class Satellite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String tleLine1;
    private String tleLine2;

    public Satellite() {}

    public Satellite(String name, String tleLine1, String tleLine2) {
        this.name = name;
        this.tleLine1 = tleLine1;
        this.tleLine2 = tleLine2;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTleLine1() { return tleLine1; }
    public void setTleLine1(String tleLine1) { this.tleLine1 = tleLine1; }

    public String getTleLine2() { return tleLine2; }
    public void setTleLine2(String tleLine2) { this.tleLine2 = tleLine2; }
}

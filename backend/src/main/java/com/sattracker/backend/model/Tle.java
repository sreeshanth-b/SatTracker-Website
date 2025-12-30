package com.sattracker.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tle")
public class Tle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "line1", length = 1000)
    private String line1;

    @Column(name = "line2", length = 1000)
    private String line2;

    public Tle() {}

    public Tle(String name, String line1, String line2) {
        this.name = name;
        this.line1 = line1;
        this.line2 = line2;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }
}

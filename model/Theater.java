package com.movieticket.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Theaters")
@Data
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private int theaterId;                  // was theater_id

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "total_screens")
    private int totalScreens;               // was total_screens
}

package com.example.matthias_pc.challengeme.model;

import java.util.List;

public class Route {

    private List<Location> locations;
    private double length;


    public Route(List<Location> locations, double length) {
        this.locations = locations;
        this.length = length;
    }

    public Route() {
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}

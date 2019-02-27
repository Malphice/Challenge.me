package com.example.matthias_pc.challengeme.model;

import java.util.Objects;

public class Location {

    private String lat;
    private String lon;

    public Location() {
    }

    public Location(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(lat, location.lat) &&
                Objects.equals(lon, location.lon);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lat, lon);
    }
}

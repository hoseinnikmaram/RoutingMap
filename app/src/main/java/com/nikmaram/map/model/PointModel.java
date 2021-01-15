package com.nikmaram.map.model;

public class PointModel {

    private Long id;
    private Double lng;
    private Double lat;
    private String tag;

    public PointModel(Long id, Double lng, Double lat, String tag) {
        this.id = id;
        this.lng = lng;
        this.lat = lat;
        this.tag = tag;
    }


    public Long getId() {
        return id;
    }

    public Double getLng() {
        return lng;
    }

    public Double getLat() {
        return lat;
    }

    public String getTag() {
        return tag;
    }
}

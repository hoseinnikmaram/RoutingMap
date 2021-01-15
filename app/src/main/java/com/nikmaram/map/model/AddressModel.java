package com.nikmaram.map.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("route_name")
    @Expose
    private String routeName;
    @SerializedName("route_type")
    @Expose
    private String routeType;
    @SerializedName("neighbourhood")
    @Expose
    private String neighbourhood;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("place")
    @Expose
    private Object place;
    @SerializedName("municipality_zone")
    @Expose
    private String municipalityZone;
    @SerializedName("in_traffic_zone")
    @Expose
    private Boolean inTrafficZone;
    @SerializedName("in_odd_even_zone")
    @Expose
    private Boolean inOddEvenZone;
    @SerializedName("addresses")
    @Expose
    private List<Address> addresses = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getPlace() {
        return place;
    }

    public void setPlace(Object place) {
        this.place = place;
    }

    public String getMunicipalityZone() {
        return municipalityZone;
    }

    public void setMunicipalityZone(String municipalityZone) {
        this.municipalityZone = municipalityZone;
    }

    public Boolean getInTrafficZone() {
        return inTrafficZone;
    }

    public void setInTrafficZone(Boolean inTrafficZone) {
        this.inTrafficZone = inTrafficZone;
    }

    public Boolean getInOddEvenZone() {
        return inOddEvenZone;
    }

    public void setInOddEvenZone(Boolean inOddEvenZone) {
        this.inOddEvenZone = inOddEvenZone;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

}


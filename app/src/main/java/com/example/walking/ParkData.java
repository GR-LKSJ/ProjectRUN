package com.example.walking;

public class ParkData {

    private String location;
    private String parkName;
    private Double startLatitude;
    private Double startLongtitude;
    private Double endLatitude;
    private Double endLongtitude;

    public ParkData(String location, String parkName,
                    Double startLatitude, Double startLongtitude, Double endLatitude, Double endLongtitude){
        this.location = location;
        this.parkName = parkName;
        this.startLatitude = startLatitude;
        this.startLongtitude = startLongtitude;
        this.endLatitude = endLatitude;
        this.endLongtitude = endLongtitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongtitude() {
        return startLongtitude;
    }

    public void setStartLongtitude(Double startLongtitude) {
        this.startLongtitude = startLongtitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public Double getEndLongtitude() {
        return endLongtitude;
    }

    public void setEndLongtitude(Double endLongtitude) {
        this.endLongtitude = endLongtitude;
    }

}
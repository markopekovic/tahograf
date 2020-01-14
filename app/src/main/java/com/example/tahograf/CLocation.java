package com.example.tahograf;

import android.location.Location;

public class CLocation extends Location {

    private boolean metricUnits = false;
    // false = m/s; true = km/h;

    public CLocation(Location location) {
        this(location, true);
    }

    public CLocation(Location location, boolean metricUnits) {
        super(location);
        this.metricUnits = metricUnits;
    }

    public boolean isMetricUnits() {
        return metricUnits;
    }

    public CLocation setMetricUnits(boolean metricUnits) {
        this.metricUnits = metricUnits;
        return this;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance =  super.distanceTo(dest);

//        if (!this.isMetricUnits()) {
//            //convert meters o feet
//            nDistance = nDistance * 3.28083989501312f;
//        }
        return nDistance;
    }

    @Override
    public double getAltitude() {
        return super.getAltitude();
    }

    @Override
    public float getSpeed() {
        if (metricUnits) {
            return super.getSpeed()*3.6f; //km/h
        } else {
            return super.getSpeed();// m/s
        }
    }

    @Override
    public float getAccuracy() {
        return super.getAccuracy();
    }
}

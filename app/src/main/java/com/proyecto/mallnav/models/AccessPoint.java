package com.proyecto.mallnav.models;

public class AccessPoint {
    private String bssid;
    private String ssid;
    private double rssi;
    private int frecuencia;
    public AccessPoint(String bssid, String ssid, double rssi, int frecuencia) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.rssi = rssi;
        this.frecuencia = frecuencia;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }
}

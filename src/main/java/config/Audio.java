/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

/**
 *
 * @author 802996013
 */
public class Audio {
    private String locationName = "audio";
    private int volumeInitial = 100;
    private int volumeMinValue = 0;
    private int volumeMaxValue = 100;
    private int volumeSteps = 100;    

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getVolumeInitial() {
        return volumeInitial;
    }

    public void setVolumeInitial(int volumeInitial) {
        this.volumeInitial = volumeInitial;
    }

    public int getVolumeMinValue() {
        return volumeMinValue;
    }

    public void setVolumeMinValue(int volumeMinValue) {
        this.volumeMinValue = volumeMinValue;
    }

    public int getVolumeMaxValue() {
        return volumeMaxValue;
    }

    public void setVolumeMaxValue(int volumeMaxValue) {
        this.volumeMaxValue = volumeMaxValue;
    }

    public int getVolumeSteps() {
        return volumeSteps;
    }

    public void setVolumeSteps(int volumeSteps) {
        this.volumeSteps = volumeSteps;
    }
    
    
}

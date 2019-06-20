/*
 * Copyright (C) 2018 Stuiart Davies (stuartdd)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io;

public class AudioStatusIO {

    private String action;
    private String status;
    private String message;
    private double duration;
    private double position;
    private int volume;
    private int volumeStep;
    private int volumeMin;
    private int volumeMax;

    public AudioStatusIO() {
    }

    public AudioStatusIO(String action, String status, String message, double duration, double position, int volume, int volumeStep, int volumeMin, int volumeMax) {
        this.action = action;
        this.status = status;
        this.message = message;
        this.duration = duration;
        this.position = position;
        this.volume = volume;
        this.volumeStep = volumeStep;
        this.volumeMin = volumeMin;
        this.volumeMax = volumeMax;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolumeStep() {
        return volumeStep;
    }

    public void setVolumeStep(int volumeStep) {
        this.volumeStep = volumeStep;
    }

    public int getVolumeMin() {
        return volumeMin;
    }

    public void setVolumeMin(int volumeMin) {
        this.volumeMin = volumeMin;
    }

    public int getVolumeMax() {
        return volumeMax;
    }

    public void setVolumeMax(int volumeMax) {
        this.volumeMax = volumeMax;
    }

    @Override
    public String toString() {
        return "AudioStatusIO{" +
                "action='" + action + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", duration=" + duration +
                ", position=" + position +
                ", volume=" + volume +
                ", volumeStep=" + volumeStep +
                ", volumeMin=" + volumeMin +
                ", volumeMax=" + volumeMax +
                '}';
    }
}

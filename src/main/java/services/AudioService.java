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
package services;

import exceptions.AudioSetupException;
import io.AudioStatus;
import java.io.File;
import main.ConfigDataManager;
import tools.AudioPlayerThread;

/**
 *
 * @author stuart
 */
public class AudioService {

    private static AudioPlayerThread audioThread = null;
    private static String currentFileName = null;
    private static int currentVolume = 50;
    private static final int WAIT_TIME = 5000;

    public static AudioStatus queryStatus() {
        return buildAudioStatus("query");
    }

    public static int getCurrentVolume() {
        return currentVolume;
    }

    public static AudioStatus setCurrentVolume(String currentVolume) {
        return setCurrentVolume(readVolumeString(currentVolume));
    }

    public static AudioStatus setCurrentVolume(int currentVolume) {
        AudioService.currentVolume = currentVolume;
        return buildAudioStatus("setVol");
    }

    public static AudioStatus play(String file) {
        return play(file, String.valueOf(getCurrentVolume()));
    }

    public static AudioStatus play(String file, String vol) {
        if (audioThread != null) {
            audioThread.close();
            audioThread.waitForStatus(AudioPlayerThread.ThreadStatus.STOPPED, WAIT_TIME);
        }
        audioThread = new AudioPlayerThread(ConfigDataManager.getLocation("audio") + File.separator + file, readVolumeString(vol));
        audioThread.waitForStatus(AudioPlayerThread.ThreadStatus.STARTING, WAIT_TIME);
        audioThread.start();
        audioThread.waitForStatus(AudioPlayerThread.ThreadStatus.RUNNING, WAIT_TIME);
        audioThread.waitForRunning(1000);
        currentFileName = file;
        return buildAudioStatus("play");
    }

    public static AudioStatus pause() {
        if (audioThread != null) {
            return pause(!audioThread.isPaused());
        }
        return buildAudioStatus("pause");
    }

    public static AudioStatus pause(Boolean paused) {
        if (audioThread != null) {
            audioThread.setPaused(paused);
            if (paused) {
                audioThread.waitForStatus(AudioPlayerThread.ThreadStatus.PAUSED, WAIT_TIME);
            } else {
                audioThread.waitForStatusClear(AudioPlayerThread.ThreadStatus.PAUSED, WAIT_TIME);
            }
        }
        return buildAudioStatus("pause");
    }

    public static AudioStatus stop() {
        if (audioThread != null) {
            audioThread.close();
            audioThread.waitForStatus(AudioPlayerThread.ThreadStatus.STOPPED, WAIT_TIME);
        }
        return buildAudioStatus("stop");
    }

    private static AudioStatus buildAudioStatus(String action) {
        if ((audioThread == null || ((audioThread != null) && (!audioThread.isRunning())))) {
            return new AudioStatus(action, "STOPPED", "Nothing is playing", currentVolume);
        } else {
            String state = "PLAYING";
            if (audioThread.isPaused()) {
                state = "PAUSED";
            }
            return new AudioStatus(action, state, currentFileName, audioThread.getDurationSeconds(), audioThread.getEllapsedSeconds(), audioThread.getVolume());
        }
    }

    private static int readVolumeString(String vol) {
        if ((vol != null) && (vol.trim().length() != 0)) {
            try {
                int i = Integer.parseInt(vol);
                if ((i < 0) || (i > 99)) {
                    throw new AudioSetupException("Value for volume 'vol=" + vol + " must be from 0 to 99");
                }
                return i;
            } catch (NumberFormatException nfe) {
                throw new AudioSetupException("Value for volume 'vol=" + vol + " is not a number", nfe);
            }
        }
        return currentVolume;
    }

}

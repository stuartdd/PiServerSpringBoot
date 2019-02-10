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
package tools;

import exceptions.AudioSetupException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class AudioPlayerThread extends Thread {

    public enum ThreadStatus {
        STARTING, RUNNING, STOPPED, ERROR
    };

    private static final double MICROS_PER_SEC = 1000000f;
    private static final int BUFFER_SIZE = 4096;
    private final File file;
    private final AudioFormat outFormat;
    private final AudioInputStream audioInputStream;
    private final SourceDataLine line;

    private FloatControl control;
    private float minVolume;
    private float maxVolume;
    private float stepVolume;
    private boolean volumeIsSetup;
    private int currentVolumePercent;

    private boolean paused = false;
    private boolean close = false;
    private final double durationSeconds;
    private long positionWhenPaused;

    private ThreadStatus threadStatus;

    public AudioPlayerThread(String fileName, int currentVolumePercent) {
        this.threadStatus = ThreadStatus.STARTING;
        this.currentVolumePercent = currentVolumePercent;
        this.positionWhenPaused = -1;
        this.volumeIsSetup = false;
        this.file = new File((new File(fileName)).getAbsolutePath());
        if (!this.file.exists()) {
            this.threadStatus = ThreadStatus.ERROR;
            throw new AudioSetupException("Could not find audio file:" + this.file.getName() + ".");
        }
        this.durationSeconds = size(this.file);
        try {
            this.audioInputStream = AudioSystem.getAudioInputStream(this.file);
            this.outFormat = getOutFormat(this.audioInputStream.getFormat());
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, this.outFormat);
            this.line = (SourceDataLine) AudioSystem.getLine(info);
            if (this.line == null) {
                this.threadStatus = ThreadStatus.ERROR;
                closeAudioInputStream();
                throw new AudioSetupException("Audio Line is NULL for audio file:" + this.file.getName() + ".");
            }
        } catch (AudioSetupException ex) {
            throw ex;
        } catch (Exception ex) {
            this.threadStatus = ThreadStatus.ERROR;
            closeLine();
            closeAudioInputStream();
            throw new AudioSetupException("Failed to set up audio file:" + this.file.getName() + ".", ex);
        }
    }

    private double size(File file) {
        if (file == null) {
            return 0.0;
        }
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
            if (fileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
                return roundMicrosecondsTo2DP((Long) properties.get("duration"));
            } else {
                throw new AudioSetupException("Unsupported file type. Audio file:" + file.getName());
            }
        } catch (UnsupportedAudioFileException ex) {
            throw new AudioSetupException("Unsupported file type. Audio file:" + file.getName(), ex);
        } catch (IOException ex) {
            throw new AudioSetupException("Failed to read file. Audio file:" + file.getName(), ex);
        }
    }

    public void close() {
        this.close = true;
    }

    public boolean isRunning() {
        return (line == null ? false : line.isRunning());
    }

    public boolean isActive() {
        return (line == null ? false : line.isActive());
    }

    public boolean isOpen() {
        return (line == null ? false : line.isOpen());
    }

    public boolean isPaused() {
        return paused;
    }

    public ThreadStatus getThreadStatus() {
        return threadStatus;
    }

    public void setPaused(boolean paused) {
        if (isRunning()) {
            if ((paused && (positionWhenPaused <= 0))) {
                positionWhenPaused = line.getMicrosecondPosition();
            } else {
                positionWhenPaused = 0;
            }
        } else {
            positionWhenPaused = 0;
        }
        this.paused = paused;
    }

    public double getEllapsedSeconds() {
        if (isRunning()) {
            if (positionWhenPaused > 0) {
                return roundMicrosecondsTo2DP(positionWhenPaused);
            }
            return roundMicrosecondsTo2DP(line.getMicrosecondPosition());
        }
        return 0.0;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public int getVolume() {
        if (volumeIsSetup) {
            return currentVolumePercent;
        }
        return 0;
    }

    public final int setVolume(int volume) {
        if (volumeIsSetup) {
            currentVolumePercent = volume;
            float vol = minVolume + (stepVolume * (float) volume);
            if (vol > maxVolume) {
                vol = maxVolume;
                currentVolumePercent = 99;
            }
            if (vol < minVolume) {
                vol = minVolume;
                currentVolumePercent = 0;
            }
            control.setValue(vol);
        }
        return getVolume();
    }

    public void waitForStatus(ThreadStatus status, long timeOut) {
        long timeToQuit = System.currentTimeMillis() + timeOut;
        while (!threadStatus.equals(status)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(AudioPlayerThread.class.getName()).log(Level.INFO, null, ex);
            }
            if (timeToQuit < System.currentTimeMillis()) {
                throw new AudioSetupException("AudioPlayerThread: Wait for status " + status.name() + " timed out!");
            }
        }
    }

    public void waitForRunning(long timeOut) {
        long timeToQuit = System.currentTimeMillis() + timeOut;
        while (!this.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(AudioPlayerThread.class.getName()).log(Level.INFO, null, ex);
            }
            if (timeToQuit < System.currentTimeMillis()) {
                throw new AudioSetupException("AudioPlayerThread: Wait for player to start running. Timed out!");
            }
        }
    }

    @Override
    public synchronized void start() {
        if (line == null) {
            throw new AudioSetupException("Audio Line null. Cannot Start AudioPlayerThread. Audio file:" + (file == null ? "?" : file.getName()) + ".");
        }
        super.start();
    }

    @Override
    public void run() {
        waitOnPause();
        try {
            line.open(outFormat);
            control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            if (control == null) {
                throw new AudioSetupException("Audio Line MASTER_GAIN not available. Audio file:" + (file == null ? "?" : file.getName()) + ".");
            }
            minVolume = control.getMinimum();
            maxVolume = control.getMaximum();
            stepVolume = ((maxVolume - minVolume) / 100);
            volumeIsSetup = true;
            setVolume(currentVolumePercent);
            line.start();
            threadStatus = ThreadStatus.RUNNING;
            stream(AudioSystem.getAudioInputStream(outFormat, audioInputStream), line);
            closeLine();
            closeAudioInputStream();
            threadStatus = ThreadStatus.STOPPED;
        } catch (Exception ex) {
            threadStatus = ThreadStatus.ERROR;
            throw new AudioSetupException(ex.getClass().getSimpleName() + ": Failed to play audio. Audio file:" + (file == null ? "?" : file.getName()) + ".", ex);
        } finally {
            closeLine();
            closeAudioInputStream();
        }
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            if (close) {
                return;
            }
            waitOnPause();
            line.write(buffer, 0, n);
        }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void closeAudioInputStream() {
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(AudioPlayerThread.class.getName()).log(Level.INFO, null, ex);
            }
        }
    }

    private void closeLine() {
        if (line != null) {
            line.drain();
            line.stop();
            line.close();
        }
    }

    private void waitOnPause() {
        while (isPaused()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(AudioPlayerThread.class.getName()).log(Level.INFO, null, ex);
            }
        }
    }

    private double roundMicrosecondsTo2DP(long l) {
        return (Math.round((l / MICROS_PER_SEC) * 100) / 100.0);
    }
}

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

import config.LogProvider;
import javafx.scene.media.AudioClip;

/**
 *
 * @author stuart
 */
public class TestAudioTest {

    public static void main(String[] args) {
        TestAudioTest t = new TestAudioTest();
        try {
            t.getAudioStatus();
        } catch (InterruptedException ex) {
            LogProvider.logErr("", ex);
        }
    }

    public void getAudioStatus() throws InterruptedException {
        AudioPlayerThread apt = new AudioPlayerThread("Caledonia.mp3", 50);
        long st = System.currentTimeMillis();
        boolean pause = true;
        apt.start();
        for (int i = 0; i < 1000; i++) {
            if ((i % 4) == 0) {
                pause = !pause;
            }
            sleepX(i, st, apt, pause);
            if (!apt.isRunning()) {
                break;
            }
        }
        apt.close();
    }

    private void sleepX(int i, long startTime, AudioPlayerThread apt, boolean pause) {
        double dur = (Math.round(((System.currentTimeMillis() - startTime) / 1000l) * 100) / 100.0);
        apt.setPaused(pause);
        System.out.println(i + ": Running: " + apt.isRunning() + ": Paused: " + apt.isPaused() + " Dur:" + dur + " Total: " + apt.getDurationSeconds() + " Elapsed:" + apt.getEllapsedSeconds());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LogProvider.logErr("", ex);
        }
    }

    public void playAudioClip() throws InterruptedException {
        AudioClip player = new AudioClip("file:///Users/stuar/git/PiServerSpringBoot/Caledonia.mp3");
        player.play(50);
        int count = 0;
        while (player.isPlaying()) {
            System.out.print(".");
            Thread.sleep(1000);
            if (count > 10) {
                player.stop();
            }
            count++;
        }
        System.out.println("!");
    }
}

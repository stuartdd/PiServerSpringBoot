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

/**
 *
 * @author stuart
 */
public class AudioService {

    public static String audioStatus(String message) {
        return "{\"audio\":\"" + audioStatusString(message) + "\"}";
    }

    public static String audioStatusString(String message) {
        if (message != null) {
            return "STATUS:" + message;
        }
        return "STATUS:" + message;
//        if (audioBackgroundPlayer == null) {
//            return "STOPPED";
//        } else {
//            double dur = audioBackgroundPlayer.getDuration() / 1000;
//            double ply = audioBackgroundPlayer.getPlayed() / 1000;
//            double pc;
//            if (ply > 0) {
//                pc = 100.0 / (dur / ply);
//                if (pc > 100) {
//                    pc = 100;
//                }
//            } else {
//                pc = 0;
//            }
//            if (audioBackgroundPlayer.isPaused()) {
//                return "Paused:" + audioBackgroundPlayer.getFileName() + ',' + Math.round(dur) + ',' + Math.round(ply) + ',' + df.format(pc);
//            } else {
//                return "Playing:" + audioBackgroundPlayer.getFileName() + ',' + Math.round(dur) + ',' + Math.round(ply) + ',' + df.format(pc);
//            }
//        }
    }
}

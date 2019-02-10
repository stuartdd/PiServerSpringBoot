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

import services.AudioService;
import main.ConfigDataManager;
import org.joda.time.DateTime;

/**
 *
 * @author stuart
 */
public class ServerService {
   /**
     * { "time" : { "hour" : 14, "min" : 14, "sec" : 14, "nano" : 207723479,
     * "year" : 2017, "month" : "November", "mon" : 11, "dom" : 29, "timestamp"
     * : "2017-11-29T14:14:14.207723479Z" }}
     *
     * @return
     */
    public static String jsonTime() {
        DateTime dt = new DateTime();
        return "{\"time\":{"
                + "\"millis\":" + System.currentTimeMillis() + ", "
                + "\"hour\":" + dt.hourOfDay().get() + ", "
                + "\"min\":" + dt.minuteOfHour().get() + ", "
                + "\"sec\":" + dt.secondOfMinute().get() + ", "
                + "\"year\":" + dt.year().get() + ", "
                + "\"month\":" + dt.monthOfYear().get() + ", "
                + "\"dom\":" + dt.dayOfMonth().get() + ", "
                + "\"mon\":\"" + dt.monthOfYear().getAsText() + "\", "
                + "\"timestamp\":\"" + ConfigDataManager.formattedTimeSatmp() + "\"}";
    } 
    

}

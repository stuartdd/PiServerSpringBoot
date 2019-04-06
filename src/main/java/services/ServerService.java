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

import config.ConfigDataManager;
import org.joda.time.DateTime;

/**
 *
 * @author stuart
 */
public class ServerService {
   /**
     * {"time":{"millis":1554504586062, "time2":"23:49", "time3":"23:49:46", "monthDay":"April:05", "year":2019, "month":4, "dom":5, "mon":"April", "timestamp":"05-04-2019T23:49:46.0+0100"}}
     *
     * @return
     */
    public static String jsonTime() {
        DateTime dt = new DateTime();
        return "{\"time\":{"
                + "\"millis\":" + System.currentTimeMillis() + ", "
                + "\"time2\":" + "\""+time2(dt)+"\", "
                + "\"time3\":" +"\""+time3(dt)+"\", "
                + "\"monthDay\":" +"\""+monthDay(dt)+"\", "
                + "\"year\":" + dt.year().get() + ", "
                + "\"month\":" + dt.monthOfYear().get() + ", "
                + "\"dom\":" + dt.dayOfMonth().get() + ", "
                + "\"mon\":\"" + dt.monthOfYear().getAsText() + "\", "
                + "\"timestamp\":\"" + ConfigDataManager.formattedTimeStamp() + "\"}}";
    }

    public static String jsonUsers() {
        StringBuilder sb = new StringBuilder();
        int mark = 0;
        for (String s:ConfigDataManager.getUsers().keySet()) {
            sb.append("\""+s+"\"");
            mark = sb.length();
            sb.append(',');
        }
        sb.setLength(mark);
        return "{\"users\": ["+sb.toString()+"]}";
    }

    private static String monthDay(DateTime dt) {
        return dt.monthOfYear().getAsText()+":"+pad2(dt.dayOfMonth().get());
    }

    private static String time2(DateTime dt) {
        return pad2(dt.hourOfDay().get())+":"+pad2(dt.minuteOfHour().get());
    }

    private static String time3(DateTime dt) {
        return time2(dt) + ":" + pad2(dt.secondOfMinute().get());
    }

    private static String pad2(int i) {
        if (i < 10) {
            return "0"+i;
        }
        return ""+i;
    }


}

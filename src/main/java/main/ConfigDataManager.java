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
package main;

import config.Config;
import config.ConfigData;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;

/**
 *
 * @author stuart
 */
public class ConfigDataManager {

    private static ConfigData configData;
    private static Map<String, String> parameters;

    public static ConfigData getConfigData() {
        return configData;
    }

    public static void init(String[] args) {
        if (configData == null) {
            if (args.length > 0) {
                configData = (ConfigData) Config.configFromJsonFile(ConfigData.class, new File(args[0]));
            } else {
                configData = (ConfigData) Config.configFromJsonFile(ConfigData.class, new File("configWebApp.json"));
            }
        }
        parameters = new HashMap<>();
        for (Map.Entry<String, String> es : configData.getSystem().entrySet()) {
            System.setProperty(es.getKey(), es.getValue());
        }
        StringBuffer sb = new StringBuffer();
        int mark = 0;
        for (String user : getUsers().keySet()) {
            sb.append('"').append(user).append('"');
            mark = sb.length();
            sb.append(',');
        }
        sb.setLength(mark);
        parameters.put("userList", sb.toString());
        parameters.put("poleForTime", "" + configData.getFunctions().getPoleForTime());
        parameters.put("historyMaxLen", "" + configData.getResources().getHistoryMaxLen());
        parameters.put("user", "");
    }

    public static Map<String, String> getUsers() {
        return configData.getResources().getUsers();
    }

    public static Map<String, String> getProperties(Map<String, String> localParameters) {
        Map<String, String> map = new HashMap<>();
        map.putAll(parameters);
        map.putAll(localParameters);
        return map;
    }
    
    public static String formattedTimeSatmp() {
        DateTime now = DateTime.now();
        return now.toString(configData.getFormatTimeStamp());
    }

}

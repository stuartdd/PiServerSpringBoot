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

import exceptions.ConfigDataException;
import config.ConfigData;
import exceptions.ResourceNotFoundException;
import exceptions.ServerRestException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import tools.JsonUtils;

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
                configData = (ConfigData) JsonUtils.beanFromJson(ConfigData.class, new File(args[0]));
            } else {
                configData = (ConfigData) JsonUtils.beanFromJson(ConfigData.class, new File("configTestData.json"));
            }
        }

        if ((configData.getResources() == null) || (configData.getResources().getUsers() == null) || (configData.getResources().getUsers().size() == 0)) {
            throw new ConfigDataException("User data 'resources-->users' not found or is empty");
        }

        if (configData.isValidateLocations()) {
            for (Map.Entry<String, Map<String, String>> usr : configData.getResources().getUsers().entrySet()) {
                if (usr.getValue() != null) {
                    for (Map.Entry<String, String> loc : usr.getValue().entrySet()) {
                        File f = new File(loc.getValue());
                        f = new File(f.getAbsolutePath());
                        if (!f.exists()) {
                            throw new ConfigDataException("User '" + loc.getKey() + "'. Path '" + loc.getValue() + "' does not exist");
                        }
                    }
                }
            }
        }

        if ((configData.getResources().getLocations() == null) || (configData.getResources().getLocations().size() == 0)) {
            throw new ConfigDataException("Location data 'resources-->locations' not found or is empty");
        }

        if (configData.getResources().getLocations().get("scripts") == null) {
            throw new ConfigDataException("Location 'scripts' is undefined");
        }

        if (configData.getResources().getLocations().get("cache") == null) {
            throw new ConfigDataException("Location 'cache' is undefined");
        }

        if (configData.getResources().getLocations().get("userData") == null) {
            throw new ConfigDataException("Location 'userData' is undefined");
        }

        if (configData.isValidateLocations()) {
            for (Map.Entry<String, String> loc : configData.getResources().getLocations().entrySet()) {
                File f = new File(loc.getValue());
                f = new File(f.getAbsolutePath());
                if (!f.exists()) {
                    throw new ConfigDataException("Location '" + loc.getKey() + "=" + loc.getValue() + " does not exist");
                }
            }
        }

        if ((configData.getFunctions() == null) || (configData.getFunctions().getCommands() == null) || (configData.getFunctions().getCommands().size() == 0)) {
            throw new ConfigDataException("Config data 'functions-->commands' not found or is empty");
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

    public static Map<String, Map<String, String>> getUsers() {
        return configData.getResources().getUsers();
    }

    public static boolean userExists(String user) {
        return configData.getResources().getUsers().containsKey(user);
    }

    public static Map<String, String> getUser(String user) {
        Map<String, String> loc = getUsers().get(user);
        if (loc == null) {
            throw new ResourceNotFoundException(user);
        }
        return loc;
    }

    public static String getUserLocation(String user, String locationName) {
        Map<String, String> loc = getUser(user);
        String path = loc.get(locationName);
        if (path == null) {
            throw new ResourceNotFoundException(user + "." + locationName);
        }
        return path;
    }

    public static Map<String, String> getLocations() {
        return configData.getResources().getLocations();
    }

    public static String getLocation(String loc) {
        String path = configData.getResources().getLocations().get(loc);
        if (path == null) {
            throw new ResourceNotFoundException(loc);
        }
        return path;
       
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

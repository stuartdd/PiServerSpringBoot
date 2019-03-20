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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import tools.FileUtils;
import tools.JsonUtils;
import tools.StringUtils;

/**
 *
 * @author stuart
 */
public class ConfigDataManager {

    private static ConfigData configData;
    private static String configDataName;
    private static Map<String, String> parameters;

    public static ConfigData getConfigData() {
        return configData;
    }

    public static void init(String[] args) {
        if (configData == null) {
            if (args.length > 0) {
                configDataName = args[0];
            } else {
                configDataName = "configTestData.json";
            }
        }
        String configErrorPrefix = "Config data file:" + configDataName + ": ";
        configData = (ConfigData) JsonUtils.beanFromJson(ConfigData.class, new File(configDataName));

        if ((configData.getResources() == null) || (configData.getResources().getUsers() == null) || (configData.getResources().getUsers().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "User data 'resources-->users' not found or is empty");
        }

        if (configData.isValidateLocations()) {
            for (Map.Entry<String, Map<String, String>> usr : configData.getResources().getUsers().entrySet()) {
                if (usr.getValue() != null) {
                    for (Map.Entry<String, String> loc : usr.getValue().entrySet()) {
                        File f = new File(loc.getValue());
                        f = new File(f.getAbsolutePath());
                        if (!f.exists()) {
                            throw new ConfigDataException(configErrorPrefix + "User '" + loc.getKey() + "'. Path '" + loc.getValue() + "' does not exist");
                        }
                    }
                }
            }
        }

        if ((configData.getResources().getLocations() == null) || (configData.getResources().getLocations().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "Location data 'resources-->locations' not found or is empty");
        }

        if (configData.getResources().getLocations().get("scripts") == null) {
            throw new ConfigDataException(configErrorPrefix + "Location 'scripts' is undefined");
        }

        if (configData.getResources().getLocations().get("cache") == null) {
            throw new ConfigDataException(configErrorPrefix + "Location 'cache' is undefined");
        }

        if (configData.isValidateLocations()) {
            for (Map.Entry<String, String> loc : configData.getResources().getLocations().entrySet()) {
                File f = new File(loc.getValue());
                f = new File(f.getAbsolutePath());
                if (!f.exists()) {
                    throw new ConfigDataException(configErrorPrefix + "Location '" + loc.getKey() + "=" + loc.getValue() + " does not exist");
                }
            }
        }

        if ((configData.getFunctions() == null) || (configData.getFunctions().getCommands() == null) || (configData.getFunctions().getCommands().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "Config data 'functions-->commands' not found or is empty");
        }

        parameters = new HashMap<>();
        for (Map.Entry<String, String> es : configData.getSystem().entrySet()) {
            System.setProperty(es.getKey(), es.getValue());
        }

        StringBuilder sb = new StringBuilder();
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

    public static Map<String, String> getUser(String user) {
        Map<String, String> loc = getUsers().get(user);
        if (loc == null) {
            throw new ResourceNotFoundException(user);
        }
        return loc;
    }

    public static String getLocation(String locationName) {
        String path = configData.getResources().getLocations().get(locationName);
        if (path == null) {
            throw new ResourceNotFoundException(locationName);
        }
        return path;

    }

    public static File getLocation(String user, String locationName, String path) {
        return getLocation(user, locationName, path, null);
    }

    public static File getLocation(String user, String locationName, String path, String name) {
        String loc;
        if (user == null) {
            loc = getLocation(locationName);
        } else {
            Map<String, String> map = getUser(user);
            loc = map.get(locationName);
            if (loc == null) {
                throw new ResourceNotFoundException((user == null ? "" : user + ".") + locationName + (path == null ? "" : "." + path));
            }
        }
        File f;
        if (path == null) {
            if (name == null) {
                f = new File((new File(loc)).getAbsolutePath());
            } else {
                f = new File((new File(loc + File.separator + name)).getAbsolutePath());
            }

        } else {
            if (name == null) {
                f = new File((new File(loc + File.separator + path)).getAbsolutePath());
            } else {
                f = new File((new File(loc + File.separator + path + File.separator + name)).getAbsolutePath());
            }
        }
        if (f.exists()) {
            return f;
        }
        //shared.original.media/Photos/2004-10-22_HuwSchoolAssembly.imgp2446.jpg
        throw new ResourceNotFoundException((user == null ? "" : user + ".") + locationName + (path == null ? "" : "." + path) + (name == null ? "" : "." + name));
    }

    public static Map<String, String> getLocations() {
        return configData.getResources().getLocations();
    }

    public static Map<String, String> getProperties(Map<String, String> localParameters) {
        Map<String, String> map = new HashMap<>();
        map.putAll(parameters);
        map.putAll(localParameters);
        return map;
    }

    public static String formattedTimeStamp() {
        DateTime now = DateTime.now();
        return now.toString(configData.getFormatTimeStamp());
    }

    public static String readCacheStartToEnd(String fileName, String resourceName) {
        return StringUtils.readStartToEnd(readCache(fileName, resourceName));
    }

    public static String readCache(String fileName, String resourceName) {
        String path = ConfigDataManager.getLocation("cache");
        File f = new File(path + File.separator + fileName);
        if (!f.exists()) {
            throw new ResourceNotFoundException(resourceName);
        }
        try {
            return FileUtils.loadFile(f);
        } catch (IOException ex) {
            throw new ResourceNotFoundException(resourceName);
        }
    }
}

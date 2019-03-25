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
import config.Functions;
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

    private static final String FS = File.separator;
    private static ConfigData configData;
    private static String configDataName;
    private static String rootPath;
    private static Map<String, String> parameters;

    public static void init(String[] args) {
        String cache = null;
        String port = null;
        for (String arg : args) {
            if (arg != null) {
                String argLc = arg.toLowerCase();
                if (argLc.startsWith("-cache=")) {
                    cache = getArgValue(arg);
                } else if (argLc.startsWith("-root=")) {
                    rootPath = getArgValue(arg);
                } else if (argLc.startsWith("-port=")) {
                    port = getArgValue(arg);
                } else {
                    configDataName = arg;
                }
            }
        }

        if (configData == null) {
            if (configDataName == null) {
                configDataName = "configTestData.json";
            }
        }

        String configErrorPrefix = "Config data file:" + configDataName + ": ";
        configData = (ConfigData) JsonUtils.beanFromJson(ConfigData.class, new File(configDataName));

        if (cache != null) {
            getLocations().put("cache", cache);
        }

        /*
        Override root path is passed in as an arg
        */
        if (rootPath != null) {
            configData.getResources().setRoot(rootPath);
        }
        /*
        Validate root path. Null if locations are relative
        */
        rootPath = configData.getResources().getRoot();
        if (rootPath != null) {
            /*
            If empty then it should be null.
            */
            if (rootPath.trim().length() == 0) {
                rootPath = null;
            } else {
                /*
                Check it is a root path. If not make it one.
                */
                if (!rootPath.startsWith(FS)) {
                    rootPath = FS + rootPath;
                }
            }
        }

        if (port != null) {
            configData.getSystem().put("server.port", port);
        }

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

        if ((getLocations() == null) || (getLocations().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "Location data 'resources-->locations' not found or is empty");
        }

        if (getLocations().get("scripts") == null) {
            throw new ConfigDataException(configErrorPrefix + "Location 'scripts' is undefined");
        }

        if (getLocations().get("cache") == null) {
            throw new ConfigDataException(configErrorPrefix + "Location 'cache' is undefined");
        }

        if (configData.isValidateLocations()) {
            for (Map.Entry<String, String> loc : getLocations().entrySet()) {
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
        Map<String, String> map = getUsers().get(user);
        if (map == null) {
            throw new ResourceNotFoundException(user);
        }
        return map;
    }

    public static Map<String, String> getLocations() {
        return configData.getResources().getLocations();
    }

    public static String getLocation(String locationName) {
        String loc = getLocations().get(locationName);
        if (loc == null) {
            throw new ResourceNotFoundException(locationName);
        }
        return resolveLocation(loc);
    }

    public static File getUserLocation(String user, String locationName, String path) {
        return getUserLocation(user, locationName, path, null);
    }

    private static String resolveLocation(String loc) {
        if (loc.startsWith(FS)) {
            return loc;
        }
        return (rootPath == null ? loc : rootPath + FS + loc);
    }

    public static File getUserLocation(String user, String locationName, String path, String name) {
        String loc;
        if (user == null) {
            loc = getLocation(locationName);
        } else {
            Map<String, String> map = getUser(user);
            loc = map.get(locationName);
            if (loc == null) {
                throw new ResourceNotFoundException((user == null ? "" : user + ".") + locationName + (path == null ? "" : "." + path));
            }
            loc = resolveLocation(loc);
        }
        File f;
        if (path == null) {
            if (name == null) {
                f = new File((new File(loc)).getAbsolutePath());
            } else {
                f = new File((new File(loc + FS + name)).getAbsolutePath());
            }

        } else {
            if (name == null) {
                f = new File((new File(loc + FS + path)).getAbsolutePath());
            } else {
                f = new File((new File(loc + FS + path + FS + name)).getAbsolutePath());
            }
        }
        if (f.exists()) {
            return f;
        }
        //shared.original.media/Photos/2004-10-22_HuwSchoolAssembly.imgp2446.jpg
        throw new ResourceNotFoundException((user == null ? "" : user + ".") + locationName + (path == null ? "" : "." + path) + (name == null ? "" : "." + name));
    }

    public static Map<String, String> getProperties(Map<String, String> localParameters) {
        Map<String, String> map = new HashMap<>();
        map.putAll(parameters);
        map.putAll(localParameters);
        return map;
    }

    public static boolean isEchoScriptOutput() {
        return configData.getFunctions().isEchoScriptOutput();
    }

    public static boolean isAllowServerStopCtrl() {
        return configData.isAllowServerStopCtrl();
    }

    public static Functions getFunctions() {
        return configData.getFunctions();
    }

    public static String getLogName() {
        return configData.getLogName();
    }

    public static String getLogPath() {
        return configData.getLogPath();
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
        File f = new File(path + FS + fileName);
        if (!f.exists()) {
            throw new ResourceNotFoundException(resourceName);
        }
        try {
            return FileUtils.loadFile(f);
        } catch (IOException ex) {
            throw new ResourceNotFoundException(resourceName);
        }
    }

    private static String getArgValue(String arg) {
        int pos = arg.indexOf('=');
        if (pos < 1) {
            return null;
        }
        String val = arg.substring(pos + 1);
        val = val.trim();
        if (val.length() == 0) {
            return null;
        }
        return val;
    }
}

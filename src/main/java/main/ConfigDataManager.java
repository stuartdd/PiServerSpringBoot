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
import config.ConfigDataImpl;
import config.Functions;
import config.LogProvider;
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
    private static String configDataName;
    private static Map<String, String> parameters;
    /*
    Do not give direct access to configDataImpl. It should always remain wrapped
     */
    private static ConfigDataImpl configDataImpl;
    /*
    These values can be overriden with app args so once setup should NOT be read from configDataImpl.
     */
    private static String serverRoot;
    private static String cache;

    public static void init(String[] args) {

        for (String arg : args) {
            if (arg != null) {
                String argLc = arg.toLowerCase();
                if (argLc.startsWith("-cache=")) {
                    cache = getArgValue(arg);
                } else if (argLc.startsWith("-root=")) {
                    serverRoot = getArgValue(arg);
                } else {
                    configDataName = arg;
                }
            }
        }

        if (configDataName == null) {
            throw new ConfigDataException("Configuration data file name was not provided");
        }

        String configErrorPrefix = "Config data file:" + configDataName + ": ";
        configDataImpl = (ConfigDataImpl) JsonUtils.beanFromJson(ConfigDataImpl.class, new File(configDataName));

        if (configDataImpl.getResources() == null) {
            throw new ConfigDataException(configErrorPrefix + "Resources data not found or is empty");
        }

        if ((configDataImpl.getResources().getLocations() == null) || (configDataImpl.getResources().getLocations().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "Location data 'resources-->locations' not found or is empty");
        }

        if ((configDataImpl.getResources().getUsers() == null) || (configDataImpl.getResources().getUsers().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "User data 'resources-->users' not found or is empty");
        }

        if ((configDataImpl.getFunctions() == null) || (configDataImpl.getFunctions().getCommands() == null) || (configDataImpl.getFunctions().getCommands().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "Config data 'functions-->commands' not found or is empty");
        }

        if (getLocations().get("scripts") == null) {
            throw new ConfigDataException(configErrorPrefix + "Location 'scripts' is undefined");
        }

        if (cache != null) {
            LogProvider.debug("CACHE Path override provided:" + cache);
        } else {
            cache = getLocation("cache");
        }

        if (!FileUtils.exists(cache)) {
            throw new ConfigDataException(configErrorPrefix + "Cache does not exist:: " + cache);
        }

        if (serverRoot != null) {
            LogProvider.debug("SERVER ROOT override provided:" + serverRoot);
        } else {
            serverRoot = configDataImpl.getResources().getServerRoot();
        }

        /*
        Validate root path. Null if locations are relative
         */
        if (serverRoot != null) {
            /*
            If empty then it should be null.
             */
            if (serverRoot.trim().length() == 0) {
                serverRoot = null;
            } else {
                File sr = new File(serverRoot);
                sr = new File(sr.getAbsolutePath());
                if (!sr.exists()) {
                    throw new ConfigDataException(configErrorPrefix + "ServerRoot does not exist: " + sr.getAbsolutePath());
                }
                /*
                If it the current path
                 */
                if (serverRoot.equals(".")) {
                    serverRoot = null;
                } else {
                    /*
                    Check it is a root path. If not make it one.
                     */
                    if (!serverRoot.startsWith(FS)) {
                        serverRoot = FS + serverRoot;
                    }
                }
            }
        }

        if (!configDataImpl.isValidateLocations()) {
            try {
                for (Map.Entry<String, Map<String, String>> usr : getUsers().entrySet()) {
                    if (usr.getValue() != null) {
                        for (Map.Entry<String, String> loc : usr.getValue().entrySet()) {
                            getUserLocationFile(loc.getKey(), usr.getKey());
                        }
                    }
                }

                for (Map.Entry<String, String> loc : getLocations().entrySet()) {
                    getLocationFile(loc.getKey());
                }

                FileUtils.exists(getLogPath());
            } catch (Exception e) {
                throw new ConfigDataException(configErrorPrefix + "File syatem reasource not found: " + e.getMessage(), e);
            }
        }

        parameters = new HashMap<>();
        for (Map.Entry<String, String> es : configDataImpl.getSystem().entrySet()) {
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
        parameters.put("poleForTime", "" + configDataImpl.getFunctions().getPoleForTime());
        parameters.put("historyMaxLen", "" + configDataImpl.getResources().getHistoryMaxLen());
        parameters.put("user", "");

    }

    public static Map<String, Map<String, String>> getUsers() {
        return configDataImpl.getResources().getUsers();
    }

    public static Map<String, String> getUser(String user) {
        Map<String, String> map = getUsers().get(user);
        if (map == null) {
            throw new ResourceNotFoundException(user);
        }
        return map;
    }

    public static Map<String, String> getLocations() {
        return configDataImpl.getResources().getLocations();
    }

    public static String getLocation(String locationName) {
        String loc = getLocations().get(locationName);
        if (loc == null) {
            throw new ResourceNotFoundException(locationName);
        }
        return resolveLocation(loc);
    }

    public static File getLocationFile(String locationName) {
        File f = new File(getLocation(locationName));
        if (f.exists()) {
            return f;
        }
        throw new ResourceNotFoundException(locationName + (configDataImpl.isValidateLocations() ? " - ROOT: " + serverRoot + " PATH:" + f.getAbsolutePath() : ""));
    }

    public static File getUserLocationFile(String user, String locationName) {
        return getUserLocationFile(user, locationName, null);
    }

    public static File getUserLocationFile(String user, String locationName, String path) {
        return getUserLocationFile(user, locationName, path, null);
    }

    private static String resolveLocation(String loc) {
        if (loc.startsWith(FS)) {
            return loc;
        }
        return (serverRoot == null ? loc : serverRoot + FS + loc);
    }

    public static File getUserLocationFile(String user, String locationName, String path, String name) {
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
        throw new ResourceNotFoundException((user == null ? "" : user + ".") + locationName + (path == null ? "" : "." + path) + (name == null ? "" : "." + name) + (configDataImpl.isValidateLocations() ? " - ROOT: " + serverRoot + " PATH:" + f.getAbsolutePath() : ""));
    }

    public static Map<String, String> getProperties(Map<String, String> localParameters) {
        Map<String, String> map = new HashMap<>();
        map.putAll(parameters);
        map.putAll(localParameters);
        return map;
    }

    public static String getCache() {
        return cache;
    }

    public static boolean isEchoScriptOutput() {
        return configDataImpl.getFunctions().isEchoScriptOutput();
    }

    public static boolean isAllowServerStopCtrl() {
        return configDataImpl.isAllowServerStopCtrl();
    }

    public static Functions getFunctions() {
        return configDataImpl.getFunctions();
    }

    public static String getLogName() {
        return configDataImpl.getLogName();
    }

    public static String getLogPath() {
        return configDataImpl.getLogPath();
    }

    public static String formattedTimeStamp() {
        DateTime now = DateTime.now();
        return now.toString(configDataImpl.getFormatTimeStamp());
    }

    public static String readCacheStartToEnd(String fileName, String resourceName) {
        return StringUtils.readStartToEnd(readCache(fileName, resourceName));
    }

    public static String readCache(String fileName, String resourceName) {
        File f = new File(cache + FS + fileName);
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

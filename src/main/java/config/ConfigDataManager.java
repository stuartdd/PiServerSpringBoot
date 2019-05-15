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
package config;

import exceptions.ConfigDataException;
import exceptions.ResourceFileNotFoundException;
import exceptions.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import tools.FileUtils;
import tools.JsonUtils;
import tools.OsUtils;
import tools.StringTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author stuart
 */
public class ConfigDataManager {

    private static final String FS = System.getProperty("file.separator");
    private static String configDataName;
    private static Map<String, String> parameterMap;
    /*
    Do not give direct access to configDataImpl. It should always remain wrapped
     */
    private static ConfigDataImpl configDataImpl;
    /*
    These values can be overriden with app args so once setup should NOT be read from configDataImpl.
     */
    private static String serverRoot;
    private static String cacheOverride;

    public static void init(String[] args) {

        for (String arg : args) {
            if (arg != null) {
                String argLc = arg.toLowerCase();
                if (argLc.startsWith("-cache=")) {
                    cacheOverride = getArgValue(arg);
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

        if ((configDataImpl.getFunctions() == null) || (configDataImpl.getFunctions().getShellCommands() == null) || (configDataImpl.getFunctions().getShellCommands().size() == 0)) {
            throw new ConfigDataException(configErrorPrefix + "Config data 'functions-->getShellCommands' not found or is empty");
        }

        if (getLocations().get("scripts") == null) {
            throw new ConfigDataException(configErrorPrefix + "Location 'scripts' is undefined");
        }

        if (cacheOverride != null) {
            LogProvider.log("CACHE Path override provided:" + cacheOverride, 1);
            configDataImpl.getResources().getLocations().put("cache", cacheOverride);
        }

        if (serverRoot != null) {
            LogProvider.log("SERVER ROOT override provided:" + serverRoot, 0);
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
                if (!FileUtils.exists(serverRoot)) {
                    throw new ConfigDataException(configErrorPrefix + "ServerRoot does not exist: " + serverRoot);
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

        if (!FileUtils.exists(getLogPath())) {
            throw new ConfigDataException(configErrorPrefix + "LogPath does not exist: " + getLogPath());
        }

        try {
            for (Map.Entry<String, Map<String, String>> usr : getUsers().entrySet()) {
                if (usr.getValue() != null) {
                    for (Map.Entry<String, String> loc : usr.getValue().entrySet()) {
                        if (ConfigDataManager.shouldValidateLocation(loc.getKey())) {
                            getUserLocationFile(usr.getKey(), loc.getKey());
                        }
                    }
                }
            }

            for (Map.Entry<String, String> loc : getLocations().entrySet()) {
                if (ConfigDataManager.shouldValidateLocation(loc.getKey())) {
                    getLocationAsFile(loc.getKey());
                }
            }

        } catch (ConfigDataException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigDataException(configErrorPrefix + "System resource error " + e.getMessage(), e);
        }

        if (configDataImpl.getAudio() == null) {
            configDataImpl.setAudio(new Audio());
        }

        Audio audioData = configDataImpl.getAudio();
        if (configDataImpl.getResources().getLocations().get(audioData.getLocationName()) == null) {
            throw new ConfigDataException(configErrorPrefix + "Audio locationName '"+audioData.getLocationName()+"' cannot be found in resources.locations");
        }
        if (audioData.getVolumeMinValue() >= audioData.getVolumeMaxValue()) {
            throw new ConfigDataException(configErrorPrefix + "Audio volumeMinValue ["+audioData.getVolumeMinValue()+"] cannot above volumeMaxValue ["+audioData.getVolumeMaxValue()+"]");            
        }
        if (audioData.getVolumeInitial() > audioData.getVolumeMaxValue()) {
            throw new ConfigDataException(configErrorPrefix + "Audio volumeInitial ["+audioData.getVolumeInitial()+"] cannot above volumeMaxValue ["+audioData.getVolumeMaxValue()+"]");            
        }
        if (audioData.getVolumeInitial() < audioData.getVolumeMinValue()) {
            throw new ConfigDataException(configErrorPrefix + "Audio volumeInitial ["+audioData.getVolumeInitial()+"] cannot below volumeMinValue ["+audioData.getVolumeMinValue()+"]");            
        }
        int diff = audioData.getVolumeMaxValue() - audioData.getVolumeMinValue();
        if (audioData.getVolumeSteps() < 1) {
            throw new ConfigDataException(configErrorPrefix + "Audio volumeSteps ["+audioData.getVolumeSteps()+"] cannot below 1");            
        }
        if (diff < 2) {
            throw new ConfigDataException(configErrorPrefix + "Audio volumeMaxValue ["+audioData.getVolumeMaxValue()+"] must be at least volumeMinValue + 3 ["+(audioData.getVolumeMinValue()+3)+"]");                       
        }
        if (audioData.getVolumeSteps() < 1) {
            throw new ConfigDataException(configErrorPrefix + "Audio volumeSteps ["+audioData.getVolumeSteps()+"] cannot below 1");                       
        }
        
        /*
        Copy all properties in the System section of the configuration file to the System properties.
         */
        for (Map.Entry<String, String> es : configDataImpl.getSystem().entrySet()) {
            System.setProperty(es.getKey(), es.getValue());
        }

        StringBuilder sb = new StringBuilder();
        int mark = sb.length();
        for (String user : getUsers().keySet()) {
            sb.append(user);
            mark = sb.length();
            sb.append(',');
        }
        sb.setLength(mark);

        parameterMap = new HashMap<>();
        parameterMap.putAll(configDataImpl.getResources().getAlias());
        for (Map.Entry<Object, Object> s : System.getProperties().entrySet()) {
            if (s.getKey() != null) {
                String key = s.getKey().toString();
                if ((!key.startsWith("sun.")) && (!key.startsWith("java.")) && (!key.startsWith("user.")) && (!key.startsWith("os")) && (!key.startsWith("awt"))) {
                    parameterMap.put(s.getKey().toString(), s.getValue().toString());
                }
            }
        }
        parameterMap.put("userList", sb.toString());
        parameterMap.remove("user");
        String root = configDataImpl.getResources().getServerRoot();
        if (StringUtils.isNotBlank(root)) {
            parameterMap.put("serverRoot", root);
        }
        parameterMap.put("server.started", (new DateTime()).toString("yyyy/MM/dd HH:mm:ss"));
        parameterMap.put("server.config", configDataName);
        parameterMap.put("server.os", OsUtils.resolveOS().name());
    }

    public static Audio getAudio() {
        if (configDataImpl.getAudio() == null) {
            return new Audio();
        }
        return configDataImpl.getAudio();
    }

    public static String getSubstitutionData() {
        StringBuilder sb = new StringBuilder();
        Map<String, String> sorted = new TreeMap<>();
        sorted.putAll(parameterMap);
        sb.append("{\"status\":{");
        int mark = sb.length();
        for (Map.Entry<String, String> s : sorted.entrySet()) {
            sb.append('"').append(s.getKey()).append("\":\"").append(s.getValue()).append('"');
            mark = sb.length();
            sb.append(',');
        }
        sb.setLength(mark);
        sb.append("}}");
        return StringTools.cleanJsonString(sb.toString());
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

    /**
     * Check that a location exists and return the path.
     *
     * @param locationName The name of the location in the config data
     * @return The value of the location from the config file
     * @throws ResourceNotFoundException if the location is not in the config
     * data.
     */
    public static String getLocation(String locationName) {
        String loc = getLocations().get(locationName);
        if (loc == null) {
            throw new ResourceNotFoundException(locationName);
        }
        return resolveLocation(loc);
    }

    /**
     * Check that a location exists and return a file object for that location.
     *
     * @param locationName the name of the location in the config data
     * @return The file for that location.
     * @throws ResourceFileNotFoundException if the file does not exist.
     * ResourceNotFoundException if the location is not in the config data.
     */
    public static File getLocationAsFile(String locationName) {
        File f = new File(getLocation(locationName));
        if (f.exists()) {
            return f;
        }
        throw new ResourceFileNotFoundException("Location: " + locationName);
    }

    /**
     * <pre>
     * Return a file at a location.
     *    If location does not exist the throw ResourceNotFoundException.
     *    If location path does not exist the throw ResourceFileNotFoundException.
     *    DO NOT Check if the file exists. That is for the calling program to do!
     *    If you want to check it exists use getUserLocationFile(null, loc, null, fileName)
     * </pre>
     *
     * @param locationName the name of the location in the config data
     * @param fileName The name of the file at that location
     * @return A file.
     */
    public static File getFileAtLocation(String locationName, String fileName) {
        File locFile = getLocationAsFile(locationName);
        return new File(locFile.getAbsolutePath() + FS + fileName);
    }

    public static File getUserLocationFile(String user, String locationName) {
        return getUserLocationFile(user, locationName, null, null);
    }

    public static File getUserLocationFile(String user, String locationName, String path) {
        return getUserLocationFile(user, locationName, path, null);
    }

    /**
     * <pre>
     * Get a file at a location given an additional path and a file name.
     *   [serverRoot] [user.location] [path] [fileName]
     *   location can be a resource.user.location or a resource.location
     *
     *   Can return a path or a file. It MUST Exist.
     * </pre>
     *
     * @param user The user id from the config file (if null uses locations
     * instead)
     * @param locationName The location within that user
     * @param path an optional additional path
     * @param fileName an optional file nam e
     * @return A file if it exists.
     */
    public static File getUserLocationFile(String user, String locationName, String path, String fileName) {
        if (StringUtils.isBlank(locationName)) {
            throw new ResourceNotFoundException("location=isBlank");
        }
        String loc;
        if (StringUtils.isBlank(user)) {
            /*
            If user is null Location comes from resources.locations in config file
             */
            loc = getLocation(locationName);
        } else {
            /*
            Location comes from resources.users.[user].[locationName] in config file
             */
            Map<String, String> map = getUser(user);
            loc = map.get(locationName);
        }
        /*
        If no location was found we cannot continue
         */
        if (StringUtils.isBlank(loc)) {
            throw new ResourceNotFoundException((user == null ? "" : user + ".") + locationName);
        }
        /*
        Clean up location
         */
        loc = resolveLocation(loc);

        File f;
        if (StringUtils.isBlank(path)) {
            if (StringUtils.isBlank(fileName)) {
                f = new File((new File(loc)).getAbsolutePath());
            } else {
                f = new File((new File(loc + FS + fileName)).getAbsolutePath());
            }
        } else {
            if (StringUtils.isBlank(fileName)) {
                f = new File((new File(loc + FS + path)).getAbsolutePath());
            } else {
                f = new File((new File(loc + FS + path + FS + fileName)).getAbsolutePath());
            }
        }
        if (f.exists()) {
            return f;
        }
        throw new ResourceFileNotFoundException("Path to - USER:" + user + " LOCATION:" + locationName + " PATH:" + path + " NAME:" + fileName);
    }

    public static Map<String, String> getParameters(Map<String, String> localParameters) {
        Map<String, String> p = new HashMap<>();
        p.putAll(parameterMap);
        p.putAll(localParameters);
        return p;
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

//    public static String readCache(String fileName, String resourceName) {
//        File f = new File(getLocation("cache") + FS + fileName);
//        if (!f.exists()) {
//            throw new ResourceNotFoundException(resourceName);
//        }
//        try {
//            return FileUtils.loadFile(f);
//        } catch (IOException ex) {
//            throw new ResourceNotFoundException(resourceName);
//        }
//    }
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

    public static boolean shouldValidateLocation(String key) {
        for (String name : configDataImpl.getValidatePathsOnStartUp()) {
            if (name.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If a location is absolute then return it. Else prefix it with the server
     * root (if there is one!)
     *
     * @param location
     * @return
     */
    private static String resolveLocation(String location) {
        if (location.startsWith(FS)) {
            return location;
        }
        return (serverRoot == null ? location : serverRoot + FS + location);
    }

    public static String alias(String name) {
        return configDataImpl.getResources().getAlias().get(name);
    }

    public static int getLogLevelBar() {
        return configDataImpl.getLogLevelBar();
    }

}

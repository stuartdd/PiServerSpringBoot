/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author stuar
 */
public class Resources {

    @JsonIgnore
    private String fullScriptFiles;
    @JsonIgnore
    private String fullStaticFiles;

    private String serverRoot;
    private String staticFiles;
    private String scriptFiles;
    private int historyMaxLen = 5;

    private Map<String, Map<String, String>> users = new HashMap<>();
    private Map<String, String> locations = new HashMap<>();
    private Map<String, String> alias = new HashMap<>();

    @JsonIgnore
    public String getFullScriptFiles() {
        return fullScriptFiles;
    }

    @JsonIgnore
    public String getFullStaticFiles() {
        return fullStaticFiles;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    public void setServerRoot(String serverRoot) {
        this.serverRoot = serverRoot;
    }

    public String getStaticFiles() {
        return staticFiles;
    }

    public void setStaticFiles(String staticFiles) {
        this.staticFiles = staticFiles;
    }

    public String getScriptFiles() {
        return scriptFiles;
    }

    public void setScriptFiles(String scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    public int getHistoryMaxLen() {
        return historyMaxLen;
    }

    public void setHistoryMaxLen(int historyMaxLen) {
        this.historyMaxLen = historyMaxLen;
    }

    public Map<String, Map<String, String>> getUsers() {
        if (users == null) {
            return new HashMap<>();
        }
        return users;
    }

    public void setUsers(Map<String, Map<String, String>> users) {
        this.users = users;
    }

    public Map<String, String> getLocations() {
        if (locations == null) {
            return new HashMap<>();
        }
        return locations;
    }

    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }

    public Map<String, String> getAlias() {
        if (alias == null) {
            return new HashMap<>();
        }
        return alias;
    }

    public void setAlias(Map<String, String> alias) {
        this.alias = alias;
    }

}

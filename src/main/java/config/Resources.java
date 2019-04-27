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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;

public class Resources {

    @JsonIgnore
    private String fullScriptFiles;
    @JsonIgnore
    private String fullStaticFiles;

    private String serverRoot;
    private String staticFiles;
    private String scriptFiles;

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

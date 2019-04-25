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

import java.util.HashMap;
import java.util.Map;

public class Functions {

    private boolean echoScriptOutput;
    private long poleForTime = 2000;
    private Map<String,String> osTemplates;
    private Map<String, Map<String, String>> shellCommands = new HashMap<>();

    public Map<String, String> getCommandsForFunction(String funString) {
        Map<String, String> l = shellCommands.get(funString);
        return (l == null ? null : l);
    }

    public Map<String, Map<String, String>> getShellCommands() {
        return shellCommands;
    }

    public void setShellCommands(Map<String, Map<String, String>> shellCommands) {
        this.shellCommands = shellCommands;
    }
    
    public boolean isEchoScriptOutput() {
        return echoScriptOutput;
    }

    public void setEchoScriptOutput(boolean echoScriptOutput) {
        this.echoScriptOutput = echoScriptOutput;
    }

    public long getPoleForTime() {
        return poleForTime;
    }

    public void setPoleForTime(long poleForTime) {
        this.poleForTime = poleForTime;
    }

    public Map<String, String> getOsTemplates() {
        return osTemplates;
    }

    public void setOsTemplates(Map<String, String> osTemplates) {
        this.osTemplates = osTemplates;
    }

     
}

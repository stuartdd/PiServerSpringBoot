/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author stuar
 */
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

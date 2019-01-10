/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author stuar
 */
public class Functions {

    private boolean echoScriptOutput;
    private long poleForTime = 2000;
    private Map<String, Map<String, String>> commands = new HashMap<>();

    public Map<String, String> getCommandsForFunction(String funString) {
        Map<String, String> l = commands.get(funString);
        return (l == null ? null : Collections.unmodifiableMap(l));
    }

    public Map<String, Map<String, String>> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, Map<String, String>> commands) {
        this.commands = commands;
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

}

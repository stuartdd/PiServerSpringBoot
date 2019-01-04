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
    private Map<String, List<String>> commands = new HashMap<>();

    public boolean isEchoScriptOutput() {
        return echoScriptOutput;
    }

    public void setEchoScriptOutput(boolean echoScriptOutput) {
        this.echoScriptOutput = echoScriptOutput;
    }

    public Map<String, List<String>> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public List<String> getCommandsForFunction(String funString) {
        List<String> l = commands.get(funString);
        return (l == null ? null : Collections.unmodifiableList(l));
    }

    public void setCommands(Map<String, List<String>> commands) {
        this.commands = commands;
    }

    public long getPoleForTime() {
        return poleForTime;
    }

    public void setPoleForTime(long poleForTime) {
        this.poleForTime = poleForTime;
    }

}

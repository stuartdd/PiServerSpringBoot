/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.util.List;
import java.util.Map;

/**
 *
 * @author stuar
 */
public class ConfigData {

    private Resources resources;
    private Functions functions;
    private String formatTimeStamp;
    private Map<String, String> system;

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Functions getFunctions() {
        return functions;
    }

    public void setFunctions(Functions functions) {
        this.functions = functions;
    }

    public Map<String, String> getSystem() {
        return system;
    }

    public void setSystem(Map<String, String> system) {
        this.system = system;
    }

    public String getFormatTimeStamp() {
        return formatTimeStamp;
    }

    public void setFormatTimeStamp(String formatTimeStamp) {
        this.formatTimeStamp = formatTimeStamp;
    }

}

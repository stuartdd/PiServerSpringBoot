/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import exceptions.ServiceException;
import cmd.SystemCommand;
import cmd.SystemCommandQuietException;
import config.Functions;
import exceptions.ConfigDataException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import main.ConfigDataManager;
import tools.FileUtils;
import tools.OsUtils;
import tools.StringUtils;
import tools.Template;

/**
 *
 * @author stuart
 */
public class FunctionService {
    private static Functions functions;
    private static String osTemplate;
    
    public static void init(Functions configData) {
        functions = configData;
        osTemplate = configData.getOsTemplates().get(OsUtils.resolveOS().name().toLowerCase());
        if (osTemplate == null) {
            throw new ServiceException("Operating System could not be derived. Got: "+OsUtils.resolveOS());
        }
    }
    
    public static String func(String funcName, Map<String, String> queryParameters) {

        Map<String, String> functionData = functions.getCommandsForFunction(funcName);
        if ((functionData == null) || (functionData.size() < 1)) {
            throw new ConfigDataException("Function '" + funcName + "' is not defined");
        }
        String methodName = functionData.get("func");
        if (methodName == null) {
            throw new ConfigDataException("Function '" + funcName + "' does not define 'func'");
        }
        if (queryParameters != null) {
            functionData.putAll(queryParameters);
        }
        String desc = "Function '" + funcName + "'. Method '" + methodName + "'";
        Method m;
        try {
            m = FunctionService.class.getDeclaredMethod(methodName, new Class[]{Map.class, String.class});
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ConfigDataException(desc + " is not implemented");
        }
        FunctionService fs = new FunctionService();
        Object o;
        try {
            o = m.invoke(fs, new Object[]{functionData, "Function '" + funcName + "' "});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ConfigDataException(desc + " failed with a " + ex.getCause().getClass().getSimpleName(), ex.getCause());
        }
        if (o == null) {
            throw new ConfigDataException(desc + " returned null");
        }
        if (o.toString().trim().length() == 0) {
            throw new ConfigDataException(desc + " returned empty result");
        }
        return o.toString();
    }

    public String scriptAll(Map<String, String> map, String desc) {
        String minRc = map.get("minRC");
        int minRcValue = 0;
        if (minRc != null) {
            try {
                minRcValue = Integer.parseInt(minRc);
            } catch (NumberFormatException nfe) {
                throw new ConfigDataException(desc + " Invalid number in value 'minRC'");
            }
        }
        Object[] script = substituteAndSplit(map, desc);
        if (!FileUtils.exists((String)script[0])) {
            throw new ConfigDataException(desc + " Path to scripts does not exist'");
        }
        SystemCommand sc = new SystemCommand();
        sc.run((List<String>) script[1], (String) script[0]);
        int rc = sc.getExitValue();
        if (rc <= minRcValue) {
            return sc.getOutput();
        }
        throw new SystemCommandQuietException(desc + "returned code " + rc + ". Min value is " + minRcValue);
    }

    public String scriptBetween(Map<String, String> map, String desc) {
        return StringUtils.readStartToEnd(scriptAll(map, desc));
    }

    private Object[] substituteAndSplit(Map<String, String> map, String desc) {
        for (Map.Entry<String, String> key:map.entrySet()) {
            map.put(key.getKey(), Template.parse(key.getValue(), ConfigDataManager.getLocations(), true));
        }
        for (Map.Entry<String, String> key:map.entrySet()) {
            map.put(key.getKey(), Template.parse(key.getValue(), map, true));
        }
        for (Map.Entry<String, String> key:map.entrySet()) {
            map.put(key.getKey(), Template.parse(key.getValue(), System.getProperties(), true));
        }
        String t1 = Template.parse(osTemplate, map, true);
        t1 = Template.parse(t1, ConfigDataManager.getLocations(), true);
        String[] split = t1.split("\\|");
        if (split.length < 2) {
            throw new ConfigDataException(desc + " Invalid number arguments in osTemplates. Min is two. E.g. %{scripts}|%{script}");
        }
        List<String> l = new ArrayList<>();
        for (int i=1; i<split.length; i++) {
            l.add(split[i]);
        }
        Object[] res = new Object[2];
        res[0] = split[0];
        res[1] = l;
        return res;
    }
    

}

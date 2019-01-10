/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import exceptions.ConfigDataException;
import exceptions.ResourceNotFoundException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import main.ConfigDataManager;
import tools.FileUtils;

/**
 *
 * @author stuart
 */
public class FunctionService {

    public static String func(String funcName, Map<String, String> map) {
        Map<String, String> functionData = ConfigDataManager.getConfigData().getFunctions().getCommandsForFunction(funcName);
        if ((functionData == null) || (functionData.size() < 1)) {
            throw new ConfigDataException("Function '" + funcName + "' is not defined");
        }
        String methodName = functionData.get("func");
        if (methodName == null) {
            throw new ConfigDataException("Function '" + funcName + "' does not define 'func'");
        }
        String desc = "Function '" + funcName + "'. Method '" + methodName + "'";
        Method m;
        try {
            m = FunctionService.class.getDeclaredMethod(methodName, new Class[]{Map.class, String.class});
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ConfigDataException(desc +" is not implemented");
        }

        FunctionService fs = new FunctionService();
        Object o;
        try {
            o = m.invoke(fs, new Object[]{functionData, "Function '" + funcName + "' "});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ConfigDataException(desc +" failed with a "+ex.getCause().getClass().getSimpleName(), ex.getCause());
        }
        if (o == null) {
            throw new ConfigDataException(desc +" returned null");
        }
        if (o.toString().trim().length() == 0) {
            throw new ConfigDataException(desc +" returned empty result");
        }
        return o.toString();
    }

    public String userFileSizesJsonCached(Map<String, String> map1, String desc) {
        return null;
    }

    public String readFileFromLocation(Map<String, String> map, String desc) {
        try {
            return FileUtils.loadFile(getFileFromMap(map, desc));
        } catch (Exception ex) {
            throw new ResourceNotFoundException(desc + " File '" + map.get("file") + "' could not be read from location '" + map.get("location")+"'");
        }
    }

    private String getLocationFromMap(Map<String, String> map, String desc) {
        String loc = map.get("location");
        if (loc == null) {
            throw new ConfigDataException(desc + "does not define 'location'");
        }
        if (loc.trim().length() == 0) {
            throw new ConfigDataException(desc + "defines an empty 'location'");
        }
        String location = ConfigDataManager.getLocation(loc);
        if (loc == null) {
            throw new ConfigDataException(desc + " 'location' value was not found");
        }
        return location;
    }

    private String getFileNameFromMap(Map<String, String> map, String desc) {
        String fil = map.get("file");
        if (fil == null) {
            throw new ConfigDataException(desc + "does not define 'file'");
        }
        if (fil.trim().length() == 0) {
            throw new ConfigDataException(desc + "defines an empty 'file'");
        }
        return fil;
    }

    private File getFileFromMap(Map<String, String> map, String desc) {
        String loc = getLocationFromMap(map, desc);
        String fil = getFileNameFromMap(map, desc);
        File f = new File(loc + File.separator + fil);
        if (f.exists()) {
            return f;
        }
        throw new ConfigDataException(desc + ". File " + fil + " not found at location " + map.get("location"));
    }

}

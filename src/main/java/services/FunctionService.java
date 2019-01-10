/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import cmd.SystemCommand;
import cmd.SystemCommandQuietException;
import exceptions.ConfigDataException;
import exceptions.ResourceNotFoundException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import main.ConfigDataManager;
import tools.FileUtils;

/**
 *
 * @author stuart
 */
public class FunctionService {

    public static String func(String funcName, Map<String, String> queryParameters) {
        Map<String, String> functionData = ConfigDataManager.getConfigData().getFunctions().getCommandsForFunction(funcName);
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
        Object[] script = validateScript(map, desc);
        SystemCommand sc = new SystemCommand();
        sc.run((List<String>) script[1], (String) script[0]);
        int rc = sc.getExitValue();
        if (rc <= minRcValue) {
            return sc.getOutput();
        }
        throw new SystemCommandQuietException(desc + "returned code " + rc + ". Min value is " + minRcValue);
    }

    public String scriptBetween(Map<String, String> map, String desc) {
        String resp = scriptAll(map, desc);
        if (resp.length() > 13) {
            int st = resp.indexOf("{start}");
            if (st < 0) {
                st = 0;
            } else {
                st = st + 7;
            }
            int en = resp.indexOf("{end}");
            if (en < 0) {
                en = resp.length();
            }
            return resp.substring(st, en).trim();
        } else {
            return resp;
        }
    }

    public String readFileFromLocation(Map<String, String> map, String desc) {
        try {
            return FileUtils.loadFile(getFileFromMap(map, desc));
        } catch (Exception ex) {
            throw new ResourceNotFoundException(desc + " File '" + map.get("file") + "' could not be read from location '" + map.get("location") + "'");
        }
    }

    
    private String getLocationFromMap(Map<String, String> map, String desc) {
        String loc = validateNVP(map, "location", desc);
        String location = ConfigDataManager.getLocation(loc);
        if (loc == null) {
            throw new ConfigDataException(desc + " 'location' value was not found");
        }
        return location;
    }

    private String getFileNameFromMap(Map<String, String> map, String desc) {
        return validateNVP(map, "file", desc);
    }

    public Object[] validateScript(Map<String, String> map, String desc) {
        String scriptPath = ConfigDataManager.getLocation("scripts");
        String script = validateNVP(map, "file", desc);
        File f = new File(scriptPath + File.separator + script);
        f = new File(f.getAbsolutePath());
        if (f.exists()) {
            List<String> commands = new ArrayList<>();
            commands.add(f.getName());
            String user = map.get("user");
            if (user != null) {
                addIfNotNull(commands, user);
            }
            String userPath = map.get("userpath");
            if (userPath != null) {
                addIfNotNull(commands, ConfigDataManager.getUsers().get(userPath));
            }
            String loc = map.get("loc");
            if (loc != null) {
                addIfNotNull(commands, ConfigDataManager.getLocation(loc));
            }
            for (int i = 0; i < 10; i++) {
                String q = map.get("P" + i);
                if (q != null) {
                    addIfNotNull(commands, q);
                }
                String l = map.get("L" + i);
                if (l != null) {
                    addIfNotNull(commands, ConfigDataManager.getLocation(l));
                }
                String u = map.get("U" + i);
                if (u != null) {
                    addIfNotNull(commands, ConfigDataManager.getUsers().get(u));
                }
                String s = map.get("S" + i);
                if (s != null) {
                    addIfNotNull(commands, System.getProperty(s));
                }
            }
            File p = f.getParentFile();
            if (p == null) {
                return new Object[]{null, commands};
            }
            return new Object[]{p.getAbsolutePath(), commands};
        }
        throw new ConfigDataException(desc + ". File " + script + " not found at location 'scripts'");
    }

    private File getFileFromMap(Map<String, String> map, String desc) {
        String path = getLocationFromMap(map, desc);
        String fil = getFileNameFromMap(map, desc);
        File f = new File(path + File.separator + fil);
        if (f.exists()) {
            return f;
        }
        throw new ConfigDataException(desc + ". File " + fil + " not found at location '" + map.get("location") + "'");
    }

    private String validateNVP(Map<String, String> map, String name, String desc) {
        String val = map.get(name);
        if (val == null) {
            throw new ConfigDataException(desc + "does not define '" + name + "'");
        }
        val = val.trim();
        if (val.length() == 0) {
            throw new ConfigDataException(desc + "defines an empty '" + name + "'");
        }
        return val;
    }

    private void addIfNotNull(List<String> l, String s) {
        if (s == null) {
            return;
        }
        s = s.trim();
        if (s.length() == 0) {
            return;
        }
        l.add(s);
    }

}

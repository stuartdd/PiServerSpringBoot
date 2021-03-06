/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
import services.dto.FunctionResponseDto;
import shell.SystemCommand;
import shell.SystemCommandQuietException;
import config.Functions;
import exceptions.ConfigDataException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import config.ConfigDataManager;
import java.io.File;
import config.FileResource;
import config.LogProvider;
import tools.FileUtils;
import tools.OsUtils;
import tools.StringTools;
import tools.Template;

/**
 *
 * @author stuart
 */
public class FunctionService {
    private static Functions functions;
    private static String osTemplate;
    
    public static void init(Functions functionData) {
        functions = functionData;
        osTemplate = functionData.getOsTemplates().get(OsUtils.resolveOS().name().toLowerCase());
        if (osTemplate == null) {
            throw new ServiceException("Operating System could not be derived. Got: "+OsUtils.resolveOS());
        }
    }
    
    public static FunctionResponseDto func(String funcName, Map<String, String> queryParameters) {
        Map<String, String> functionData = functions.getCommandsForFunction(funcName);
        if ((functionData == null) || (functionData.size() < 1)) {
            throw new ConfigDataException("Function '" + funcName + "' is not defined");
        }
        String methodName = functionData.get("methodName");
        if (methodName == null) {
            throw new ConfigDataException("Function '" + funcName + "' does not define 'func'");
        }
        if (queryParameters != null) {
            functionData.putAll(queryParameters);
        }
        String desc = "Script ID'" + funcName + "'. Method '" + methodName + "'";
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
        return new FunctionResponseDto(o.toString(), functionData);
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
            LogProvider.log(String.format("[9] Path [%s] could not be found."), 8);
            throw new ConfigDataException(desc + " Path to scripts does not exist'");
        }
        SystemCommand sc = new SystemCommand();
        sc.run((List<String>) script[1], (String) script[0], ConfigDataManager.isEchoScriptOutput());
        int rc = sc.getExitValue();
        if (rc <= minRcValue) {
            return sc.getOutput();
        }
        throw new SystemCommandQuietException(desc + "returned code " + rc + ". Min value is " + minRcValue);
    }

    public String scriptBetween(Map<String, String> map, String desc) {
        return readStartToEnd(scriptAll(map, desc));
    }

    private Object[] substituteAndSplit(Map<String, String> mapIn, String desc) {
        Map<String,String> mapSub = ConfigDataManager.getParameters(ConfigDataManager.getResolvedLocations());
        for (Map.Entry<String, String> key:mapIn.entrySet()) {
            mapSub.put(key.getKey(), Template.parse(key.getValue(), mapSub, Template.URESOLVED_ACTION.IGNORE));
        }
        for (Map.Entry<String, String> key:mapSub.entrySet()) {
            mapSub.put(key.getKey(), Template.parse(key.getValue(), mapSub, Template.URESOLVED_ACTION.IGNORE));
        }
        String t1 = Template.parse(osTemplate, mapSub, Template.URESOLVED_ACTION.IGNORE);
        t1 = Template.parse(t1, mapSub, Template.URESOLVED_ACTION.BLANK);
        t1 = t1.trim();
        String[] split = StringTools.splice(t1, '|');
        if (split.length < 2) {
            throw new ConfigDataException(desc + " Invalid number arguments in osTemplates. Min is two. E.g. %{scripts}|%{script}");
        }
        List<String> l = new ArrayList<>();
        for (int i=1; i<split.length; i++) {
            if (StringUtils.isNotBlank(split[i])) {
                l.add(split[i]);
            }
        }
        Object[] res = new Object[2];
        res[0] = split[0];
        res[1] = l;
        return res;
    }

    private String readStartToEnd(String resp) {
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


}

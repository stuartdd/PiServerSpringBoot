package cmd;

import config.LogProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import config.ConfigDataManager;
import tools.StringUtils;

/**
 *
 * @author stuar
 */
public class SystemCommand {

    private static final String NL = System.getProperty("line.separator");
    private final StringBuilder errorStream = new StringBuilder();
    private final StringBuilder outputStream = new StringBuilder();
    private int exitValue;
    private List<String> commands;

    public int run(List<String> commandIn, String path) {
        boolean echoScriptOutput = ConfigDataManager.isEchoScriptOutput();
        if (commandIn == null) {
            throw new SystemCommandException("Failed commandIn is NULL");
        }
        if (commandIn.isEmpty()) {
            throw new SystemCommandException("Failed commandIn is EMPTY");
        }
        commands = commandIn;
        ProcessBuilder builder;
        builder = new ProcessBuilder(commands);
        if (path == null) {
            builder.directory(ConfigDataManager.getLocationFile("scripts"));
        } else {
            builder.directory(new File(path));
        }
        if (echoScriptOutput) {
            LogProvider.log("CMD:CURRENT_DIR: " + builder.directory().getAbsolutePath(), 1);
        }
        Process p;
        try {
            p = builder.start();
        } catch (IOException ex) {
            throw new SystemCommandException("Failed command :" + StringUtils.listToString(commands," "), ex);
        }
        try {
            BufferedReader bufferedReaderOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bufferedReaderOut.readLine()) != null) {
                if (echoScriptOutput) {
                    LogProvider.log("CMD:OUT:" + StringUtils.listToString(commands," ") + " --> " + line, 0);
                }
                outputStream.append(line).append(NL);
            }
            BufferedReader bufferedReaderErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bufferedReaderErr.readLine()) != null) {
                if (echoScriptOutput) {
                    LogProvider.logErr("CMD:ERR:"  + StringUtils.listToString(commands," ") + " --> " + line);
                }
                errorStream.append(line).append(NL);
            }
        } catch (IOException ex) {
            throw new SystemCommandException("Failed (IOException) command :"  + StringUtils.listToString(commands," "), ex);
        }

        try {
            p.waitFor();
        } catch (InterruptedException ex) {

        }
        exitValue = p.exitValue();
        if (echoScriptOutput) {
            LogProvider.log("CMD:EXIT: Code=" + exitValue, 0);
        }
        return exitValue;
    }

    public String getError() {
        return errorStream.toString();
    }

    public String getOutput() {
        return outputStream.toString();
    }

    public int getExitValue() {
        return exitValue;
    }

    public List<String> getCommands() {
        return commands;
    }

}

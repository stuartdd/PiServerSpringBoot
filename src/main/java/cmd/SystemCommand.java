package cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import main.ConfigDataManager;
import tools.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stuar
 */
public class SystemCommand {

    private static final Logger logger = LogManager.getLogger("SystemCommand");
    private static final String NL = System.getProperty("line.separator");
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private final StringBuilder errorStream = new StringBuilder();
    private final StringBuilder outputStream = new StringBuilder();
    private int exitValue;

    public int run(List<String> command, String path, boolean echoScriptOutput) {
        if (command == null) {
            throw new SystemCommandException("Failed command : (NULL)");
        }
        if (command.isEmpty()) {
            throw new SystemCommandException("Failed command : (EMPTY)");
        }

        ProcessBuilder builder;
        if (isWindows) {
            command.add(0, "/c");
            command.add(0, "cmd.exe");
            if (echoScriptOutput) {
                logger.info("CMD:WINDOWS:" + StringUtils.listToString(command, " "));
            }
            builder = new ProcessBuilder(command);
        } else {
            if (echoScriptOutput) {
                logger.info("CMD:UNIX: " + StringUtils.listToString(command, " "));
            }
            builder = new ProcessBuilder(command);
        }

//        Map<String, String> env = builder.environment();
//        env.put("VAR1", "myValue");
//        env.remove("OTHERVAR");
//        env.put("VAR2", env.get("VAR1") + "suffix");
        if (path == null) {
            builder.directory(new File(ConfigDataManager.getLocation("scripts")));
        } else {
            builder.directory(new File(path));
        }
        if (echoScriptOutput) {
            logger.info("CMD:CURRENT_DIR: " + builder.directory().getAbsolutePath());
        }

        Process p;
        try {
            p = builder.start();
        } catch (IOException ex) {
            throw new SystemCommandException("Failed (EX) command :" + command + " --> " + builder.directory().getAbsolutePath(), ex);
        }

        try {
            BufferedReader bufferedReaderOut = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = bufferedReaderOut.readLine()) != null) {
                if (echoScriptOutput) {
                    logger.info("CMD:OUT:" + command + " --> " + line);
                }
                outputStream.append(line).append(NL);
            }

            BufferedReader bufferedReaderErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = bufferedReaderErr.readLine()) != null) {
                if (echoScriptOutput) {
                    logger.info("CMD:ERR:" + command + " --> " + line);
                }
                errorStream.append(line).append(NL);
            }
        } catch (IOException ex) {
            throw new SystemCommandException("Failed (IO) command :" + command, ex);
        }

        try {
            p.waitFor();
        } catch (InterruptedException ex) {

        }
        exitValue = p.exitValue();
        if (echoScriptOutput) {
            logger.info("CMD:EXIT: Code=" + exitValue);
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

    public boolean isIsWindows() {
        return isWindows;
    }

}

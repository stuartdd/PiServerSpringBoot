package cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private static final boolean isWindows = resolveOS().equals("win");
    private final StringBuilder errorStream = new StringBuilder();
    private final StringBuilder outputStream = new StringBuilder();
    private int exitValue;
    private List<String> commands;

    public int run(List<String> commandIn, String path) {
        boolean echoScriptOutput = ConfigDataManager.getConfigData().getFunctions().isEchoScriptOutput();
        if (commandIn == null) {
            throw new SystemCommandException("Failed command : (NULL)");
        }
        if (commandIn.isEmpty()) {
            throw new SystemCommandException("Failed command : (EMPTY)");
        }
        
        commands = commandIn;
        ProcessBuilder builder;
        if (isWindows) {
            commands.add(0, "/c");
            commands.add(0, "cmd.exe");
            if (echoScriptOutput) {
                logger.info("CMD:WINDOWS:" + StringUtils.listToString(commands, " ")+" path:"+path);
            }
            builder = new ProcessBuilder(commands);
        } else {
            List<String> l = new ArrayList<>();
            for (String s : commands) {
                if (l.size() == 0) {
                    l.add(ConfigDataManager.getConfigData().getFunctions().getLinuxScriprPrefix() + s);
                } else {
                    l.add(s);
                }
            }
            commands = l;
            if (echoScriptOutput) {
                logger.info("CMD:UNIX: " + StringUtils.listToString(commands, " "));
            }
            builder = new ProcessBuilder(commands);
        }

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
            throw new SystemCommandException("Failed (EX) command :" + commands, ex);
        }

        try {
            BufferedReader bufferedReaderOut = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = bufferedReaderOut.readLine()) != null) {
                if (echoScriptOutput) {
                    logger.info("CMD:OUT:" + commands + " --> " + line);
                }
                outputStream.append(line).append(NL);
            }

            BufferedReader bufferedReaderErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = bufferedReaderErr.readLine()) != null) {
                if (echoScriptOutput) {
                    logger.info("CMD:ERR:" + commands + " --> " + line);
                }
                errorStream.append(line).append(NL);
            }
        } catch (IOException ex) {
            throw new SystemCommandException("Failed (IO) command :" + commands, ex);
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

    public List<String> getCommands() {
        return commands;
    }

    public static String resolveOS() {
        String OS = System.getProperty("os.name").toLowerCase();
        if ((OS.contains("win"))) {
            return "win";
        } else if (OS.contains("mac")) {
            return "mac";
        } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            return "linux";
        } else if (OS.contains("sunos")) {
            return "solaris";
        } else {
            return "unknown";
        }
    }
}

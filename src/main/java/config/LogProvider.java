/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author 802996013
 */
public class LogProvider {

    private static Logger logger;
    private static Logger loggerError;
    private static int logLevelBar;

    private static void init() {
        logLevelBar = ConfigDataManager.getLogLevelBar();
        if (logger == null) {
            logger = LogManager.getLogger("Server:");
        }
        if (loggerError == null) {
            loggerError = LogManager.getLogger("ServerError:");
        }
    }

    public static void log(String s, int level) {
        init();
        if (isUnderTheBar(level)) {
            logger.info(s);
        }
    }

    private static boolean isUnderTheBar(int level) {
        return (level < logLevelBar);
    }

    public static void logErr(String s) {
        init();
        logger.error(s);
        loggerError.error(s);
    }

    public static void logErr(String s, Exception e) {
        init();
        loggerError.error(s, e);
    }

    public static void shutDown() {
        LogManager.shutdown();
    }
}

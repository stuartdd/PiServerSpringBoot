/*
 * Copyright (C) 2018 Stuiart Davies (stuartdd)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogProvider {

    private static Logger logger;
    private static Logger loggerError;
    private static int logLevelBar;

    private static void init() {
        logLevelBar = ConfigDataManager.getLogLevelBar();
        if (logger == null) {
            if (logger == null) {
                logger = LogManager.getLogger("Server:");
            }
            if (loggerError == null) {
                loggerError = LogManager.getLogger("ServerError:");
            }
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

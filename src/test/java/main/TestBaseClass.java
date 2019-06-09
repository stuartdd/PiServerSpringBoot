/*
 * Copyright (C) 2019 stuartdd
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
package main;

import config.ConfigDataManager;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import services.FunctionService;
import tools.OsUtils;

/**
 *
 * @author stuar
 */
public class TestBaseClass {

    @BeforeClass
    public static void beforeClass() {
        ConfigDataManager.init(new String[]{"configTestData" + OsUtils.resolveOS().name().toUpperCase() + ".json"});
        FunctionService.init(ConfigDataManager.getFunctions());
        cleanLogs();
    }

    @AfterClass
    public static void afterClass() {
    }

    private static void cleanLogs() {
        String lName = ConfigDataManager.getLogName().toLowerCase();
        File f = new File(ConfigDataManager.getLogPath());
        File[] l = f.listFiles();
        for (File fil : l) {
            String n = fil.getName();
            if (!n.contains("Test")) {
                if ((n.toLowerCase().contains(lName)) && (n.toLowerCase().endsWith(".log"))) {
                    System.out.println("DELETED:" + fil.getAbsolutePath());
                    fil.delete();
                }
            }
        }
    }

}

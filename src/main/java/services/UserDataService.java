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
package services;

import exceptions.ServerRestException;
import java.io.File;
import java.io.IOException;
import main.ConfigDataManager;
import org.springframework.http.HttpStatus;
import tools.FileUtils;

/**
 *
 * @author stuart
 */
public class UserDataService {

    public static String getUserData(String user) {
        testUserExists(user);
        File f = new File(ConfigDataManager.getLocation("userData") + File.separator + user + ".json");
        if (f.exists()) {
            try {
                return FileUtils.loadFile(f);
            } catch (IOException ex) {
                throw new ServerRestException("Could not read userData file", HttpStatus.NOT_FOUND, "Not Found");
            }
        }
        return FileUtils.getResource("/userDataTemplate", UserDataService.class);
    }

    private static void testUserExists(String user) {
        if (!ConfigDataManager.userExists(user)) {
            throw new ServerRestException(user, HttpStatus.NOT_FOUND, "User Not Found");
        }
    }

}

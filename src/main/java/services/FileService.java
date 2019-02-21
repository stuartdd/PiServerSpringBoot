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

import exceptions.ResourceNotFoundException;
import io.PathsIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import main.ConfigDataManager;
import tools.FileExtFilter;
import tools.FileUtils;

/**
 *
 * @author stuart
 */
public class FileService {

    public static PathsIO paths(String user, String loc, Map<String, String> queryParameters) {
        String root = ConfigDataManager.getUserLocation(user, loc);
        File f = new File((new File(root)).getAbsolutePath());
        if (!f.exists()) {
            throw new ResourceNotFoundException(loc);
        }
        int prefix = f.getAbsolutePath().length();
        List<String> files = new ArrayList<>();
        FileUtils.tree(f, files, prefix, new FileExtFilter(new String[] {".java",".jpg"}));
        PathsIO resp = new PathsIO(user, loc);
        resp.setPaths(files);
        return resp;
    }

 }

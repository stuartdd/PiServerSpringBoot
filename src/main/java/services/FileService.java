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
import io.EncNameIo;
import io.FileListIo;
import io.FileSpecIo;
import io.PathsIO;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ConfigDataManager;
import tools.EncodeDecode;
import tools.FileExtFilter;
import tools.FileUtils;

/**
 *
 * @author stuart
 */
public class FileService {

    public static PathsIO paths(String user, String loc) {
        File root = ConfigDataManager.getLocation(user, loc, null);
        int prefix = root.getAbsolutePath().length();
        List<String> files = new ArrayList<>();
        FileUtils.tree(root, files, prefix, new FileExtFilter(new String[]{".java", ".jpg"}));
        PathsIO resp = new PathsIO(user, loc);
        for (String s : files) {
            resp.addPath(s);
        }
        return resp;
    }

    public static FileListIo userFiles(String user, String loc, String dir, FileFilter filter) {
        File root = ConfigDataManager.getLocation(user, loc, dir);
        FileListIo fileListOut = new FileListIo(user, loc, dir);
        File[] fileList = root.listFiles(filter);
        for (File f : fileList) {
            if (f.isFile()) {
                fileListOut.addFileSpec(new FileSpecIo(f.length(), new EncNameIo(f.getName(), EncodeDecode.encode(f.getName()))));
            }
        }
        return fileListOut;
    }
    
    public static String userFilesRead(String user, String loc, String dir, String file) {
        File root = ConfigDataManager.getLocation(user, loc, dir);
        try {
            return FileUtils.loadFile(new File(root.getAbsolutePath()+File.separator+EncodeDecode.decode(file)));
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Failed to read log", ex);
        }
    }

}

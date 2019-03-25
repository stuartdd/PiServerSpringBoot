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
import exceptions.ServerRestException;
import io.EncNameIo;
import io.FileListIo;
import io.FileSpecIo;
import io.PathsIO;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import main.ConfigDataManager;
import org.springframework.http.HttpStatus;
import tools.FileExtFilter;
import tools.FileUtils;

/**
 *
 * @author stuart
 */
public class FileService {
    private static final String FS = File.separator;
    
    public static PathsIO paths(String user, String loc) {
        File root = ConfigDataManager.getUserLocation(user, loc, null);
        int prefix = root.getAbsolutePath().length();
        List<String> files = new ArrayList<>();
        FileUtils.tree(root, files, prefix, new FileExtFilter(new String[]{".java", ".jpg"}));
        PathsIO resp = new PathsIO(user, loc);
        for (String s : files) {
            resp.addPath(s);
        }
        return resp;
    }

    public static void saveFile(String user, String loc, String path, String name, String body) {
        if ((name == null) || (name.isEmpty())) {
            throw new ServerRestException("Name must be provided", HttpStatus.BAD_REQUEST, "BAD REQUEST");
        }
        if ((body == null) || (body.isEmpty())) {
            throw new ServerRestException("Missing content. Nothing to save!", HttpStatus.BAD_REQUEST, "BAD REQUEST");
        }
        File root = ConfigDataManager.getUserLocation(user, loc, path);
        FileUtils.writeFileOverwrite(body, new File(root.getAbsolutePath()+FS+name));
    }

    public static byte[] userReadFiles(String user, String loc, String dir, String name) {
        File root = ConfigDataManager.getUserLocation(user, loc, dir, name);
        try {
            return FileUtils.loadBinaryFile(root);
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Failed to read file", ex);
        }
    }

    public static FileListIo userListFiles(String user, String loc, String dir) {
        return userListFiles(user, loc, dir, (FileFilter) null);
    }

    public static FileListIo userListFiles(String user, String loc, String dir, String[] filter) {
        if ((filter == null) || (filter.length == 0)) {
            return userListFiles(user, loc, dir, (FileFilter) null);
        }
        return userListFiles(user, loc, dir, new FileExtFilter(filter));
    }

    private static FileListIo userListFiles(String user, String loc, String dir, FileFilter filter) {
        File root = ConfigDataManager.getUserLocation(user, loc, dir);
        FileListIo fileListOut = new FileListIo(user, loc, dir);
        File[] fileList = null;
        if (filter == null) {
            fileList = root.listFiles();
        } else {
            fileList = root.listFiles(filter);
        }
        for (File f : fileList) {
            if (f.isFile()) {
                fileListOut.addFileSpec(new FileSpecIo(f.length(), new EncNameIo(f.getName())));
            }
        }
        return fileListOut;
    }
}

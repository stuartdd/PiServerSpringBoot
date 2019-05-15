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
import config.ConfigDataManager;
import java.util.Arrays;
import java.util.Comparator;
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
        File root = ConfigDataManager.getUserLocationFile(user, loc);
        int prefix = root.getAbsolutePath().length();
        List<String> fileNames = new ArrayList<>();
        FileUtils.tree(root, fileNames, prefix, new String[]{".java", ".jpg"});
        PathsIO resp = new PathsIO(user, loc);
        fileNames.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for (String s : fileNames) {
            if (s.startsWith(FS)) {
                s = s.substring(FS.length());
            }
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
        File root = ConfigDataManager.getUserLocationFile(user, loc, path);
        FileUtils.writeFileOverwrite(body, new File(root.getAbsolutePath() + FS + name));
    }

    public static byte[] userReadFiles(String user, String loc, String dir, String name) {
        File root = ConfigDataManager.getUserLocationFile(user, loc, dir, name);
        if (root.isFile()) {
            try {
                return FileUtils.loadBinaryFile(root);
            } catch (IOException ex) {
                throw new ResourceNotFoundException("Failed to read file", ex);
            }
        }
        throw new ResourceNotFoundException(user + "." + loc + "." + dir + "." + name + " is not a file!");
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
        File root = ConfigDataManager.getUserLocationFile(user, loc, dir);
        FileListIo fileListOut = new FileListIo(user, loc, dir);
        File[] fileList;
        if (filter == null) {
            fileList = root.listFiles();
        } else {
            fileList = root.listFiles(filter);
        }
        Arrays.sort(fileList);
        int p = 0;
        for (int i = fileList.length - 1; i  >= 0; i--) {
            if (fileList[i].isFile()) {
                fileListOut.addFileSpec(new FileSpecIo(fileList[i].length(), new EncNameIo(fileList[i].getName())));
            }
        }
        return fileListOut;
    }
    
    public static String conditionFileName(String s) {
        if (s== null) {
            return null;
        }
        String strim = s.trim();
        if (strim.startsWith("\\") || strim.startsWith("/")) {
            strim = strim.substring(1);
        }
        StringBuilder sb = new StringBuilder();
        for (char c:strim.toCharArray()) {
            if ((c == '\\') || (c == '/')) {
                sb.append(FS);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

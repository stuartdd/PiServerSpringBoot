/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import exceptions.ResourceFileNotFoundException;
import exceptions.ResourceNotFoundException;
import java.io.File;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import tools.FileUtils;

/**
 *
 * @author stuar
 */
public class FileResource {

    private static final String FS = System.getProperty("file.separator");

    private final String location;
    private String user;
    private String loc;
    private String path;
    private String name;

    private FileResource(String loc) {
        this.loc = loc;
        this.location = ConfigDataManager.getLocation(loc);
    }

    private FileResource(String user, String loc) {
        this.user = user;
        this.loc = loc;
        Map<String, String> map = ConfigDataManager.getUser(user);
        this.location = map.get(loc);
        if (this.location == null) {
            throw new ResourceNotFoundException("Location:" + loc + " for user:" + user);
        }
    }

    public static FileResource withLocation(String location) {
        return new FileResource(location);
    }

    public static FileResource withUserLocation(String user, String location) {
        if (StringUtils.isBlank(user)) {
            return new FileResource(location);
        }
        return new FileResource(user, location);
    }

    public FileResource withPath(String path) {
        if (StringUtils.isBlank(path)) {
            return this;
        }
        if (path.startsWith(FS)) {
            this.path = path.substring(FS.length());
        } else {
            this.path = path;
        }
        return this;
    }

    public FileResource andPath(String path) {
        return withPath(path);
    }

    public FileResource withName(String name) {
        if (StringUtils.isBlank(name)) {
            return this;
        }
        if (!FileUtils.isRootPath(name)) {
            this.name = name;
        } else {
            this.name = name.substring(FS.length());
        }
        return this;
    }

    public FileResource andName(String name) {
        return withName(name);
    }

    public File fileOrNull() {
        return findFirstFromRootPaths();
    }

    public File file() {
        File f = findFirstFromRootPaths();
        if (f == null) {
            throw new ResourceFileNotFoundException("(loc:"+loc +")"+ (StringUtils.isBlank(path) ? "" : ".(path:" + path + ")") + (StringUtils.isBlank(user) ? "" : ".(user:" + user + ")") + (StringUtils.isBlank(name) ? "" : ".(name:" + name + ")"));
        }
        return f;
    }

    private File findFirstFromRootPaths() {
        String file = location + (StringUtils.isBlank(path) ? "" : FS + path) + (StringUtils.isBlank(name) ? "" : FS + name);
        if (FileUtils.isRootPath(file)) {
            return fileIfExists(file);
        }
        if (ConfigDataManager.getServerRoots().isEmpty()) {
            return fileIfExists(file);
        }
        for (String root : ConfigDataManager.getServerRoots()) {
            File f = fileIfExists(root + FS + file);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    private File fileIfExists(String file) {
        File f = new File(file);
        if (f.exists()) {
            return f;
        }
        return null;
    }

}

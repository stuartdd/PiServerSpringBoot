/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stuart
 */
public class FileListIo {
    private String user;
    private String loc;
    private String path;
    private List<FileSpecIo> files = new ArrayList<>();

    public FileListIo() {
    }

    public FileListIo(String user, String loc, String path) {
        this.user = user;
        this.loc = loc;
        this.path = path;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FileSpecIo> getFiles() {
        return files;
    }

    public void setFiles(List<FileSpecIo> files) {
        this.files = files;
    }
    
    public void addFileSpec(FileSpecIo fileSpec) {
        files.add(fileSpec);
    }
    
}

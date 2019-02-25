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
    private String dir;
    private List<FileSpecIo> files = new ArrayList<>();

    public FileListIo() {
    }

    public FileListIo(String user, String loc, String dir) {
        this.user = user;
        this.loc = loc;
        this.dir = dir;
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

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
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

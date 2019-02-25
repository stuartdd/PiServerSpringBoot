/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import tools.EncodeDecode;

/**
 *
 * @author stuart
 */
public class FileSpecIo {
    private long size;
    private EncNameIo name;

    public FileSpecIo() {
    }

    public FileSpecIo(long size, EncNameIo name) {
        this.size = size;
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public EncNameIo getName() {
        return name;
    }

    public void setName(EncNameIo name) {
        this.name = name;
    }

    
}

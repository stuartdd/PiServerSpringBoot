package io;

import tools.EncodeDecode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stuart
 */
public class EncNameIo {

    private String name;
    private String encName;

    public EncNameIo() {
    }

    public EncNameIo(String name) {
        this.name = name;
        if (name == null) {
            this.encName = null;
        } else {
            this.encName = EncodeDecode.encode(name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncName() {
        return encName;
    }

    public void setEncName(String encName) {
        this.encName = encName;
    }

    @Override
    public String toString() {
        return name;
    }

}

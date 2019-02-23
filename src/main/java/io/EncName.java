package io;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stuart
 */
public class EncName {
    private String name;
    private String encName;

    public EncName() {
    }

    public EncName(String name, String encName) {
        this.name = name;
        this.encName = encName;
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
        return name ;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static org.junit.Assert.*;
import org.junit.Test;
import tools.FileUtils;

/**
 *
 * @author stuar
 */
public class TestIsRoot {
    private static final String FS = System.getProperty("file.separator");
    @Test
    public void test() {
        assertTrue(FileUtils.isRootPath("C:"+FS));
        assertTrue(FileUtils.isRootPath("C:"+FS+"abc"));
        assertTrue(FileUtils.isRootPath("C:"+FS+"abc"));
        assertTrue(FileUtils.isRootPath(FS+"abc"));
        assertTrue(FileUtils.isRootPath(FS));
        assertFalse(FileUtils.isRootPath("A"+FS));
        assertFalse(FileUtils.isRootPath("ABC/"));
        assertFalse(FileUtils.isRootPath(""));
        assertFalse(FileUtils.isRootPath(null));
    }
    
}

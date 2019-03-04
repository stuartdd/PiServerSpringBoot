/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.junit.Test;

/**
 *
 * @author 802996013
 */
public class TestPipe {
    @Test
    public void testPipe() throws IOException {
        OutputStream os = new PS();
        System.setOut(new PrintStream(os));
        Pipe2.main(new String[] {"-fdata/cache/diskStatusTestData.txt"});
    }
    
    private class PS extends OutputStream {
        private final StringBuilder sb = new StringBuilder();
        @Override
        public void write(int b) throws IOException {
            sb.append((char)b);
        }
        @Override
        public String toString() {
            return sb.toString();
        }

        
    }
 }

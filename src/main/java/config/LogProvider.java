/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

/**
 *
 * @author 802996013
 */
public class LogProvider {
    public static void log(String s) {
        System.out.println(s);
    }

    public static void logErr(String s) {
        System.out.println(s);
    }

    public static void logErr(String s, Exception e) {
        System.out.println(s);
        e.printStackTrace();
    }
}

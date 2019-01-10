/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stuart
 */
public class StringUtils {
    
  public static String listToString(List list, String delim) {
        StringBuilder sb = new StringBuilder();
        int mark = 0;
        for (Object s : list) {
            if (s != null) {
                sb.append(s.toString());
            } else {
                sb.append("null");
            }
            mark = sb.length();
            sb.append(delim);
        }
        sb.setLength(mark);
        return sb.toString();
    }

    public static String arrayToString(Object[] list, String delim) {
        return arrayToString(list, delim, 0);
    }

    public static String arrayToStringPath(Object[] list, int from) {
        return arrayToString(list, File.separator, from);
    }

    public static String arrayToString(Object[] list, String delim, int from) {
        StringBuilder sb = new StringBuilder();
        int mark = 0;
        for (int i = from; i < list.length; i++) {
            if (list[i] != null) {
                sb.append(list[i].toString());
            } else {
                sb.append("null");
            }
            mark = sb.length();
            sb.append(delim);
        }
        sb.setLength(mark);
        return sb.toString();
    }

    public static String[] splice(String s, char splice) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if ((c == splice) || ((splice < ' ') && (c <= ' '))) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[list.size()]);
    }
}

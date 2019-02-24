/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import exceptions.BadDataException;
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

    public static String readStartToEnd(String resp) {
        if (resp.length() > 13) {
            int st = resp.indexOf("{start}");
            if (st < 0) {
                st = 0;
            } else {
                st = st + 7;
            }
            int en = resp.indexOf("{end}");
            if (en < 0) {
                en = resp.length();
            }
            return resp.substring(st, en).trim();
        } else {
            return resp;
        }
    }

    public static String toJson(String in, String[] names, int[] positions, int count, String delimeters, String context) {
        if (names.length != positions.length) {
            throw new BadDataException("Invalid conversion to JSON:" + context);
        }
        StringBuilder sb = new StringBuilder();
        int tokenOnLine = 0;
        Scanner sc = new Scanner(in);
        sc.skipSpace();

        while (sc.hasNext()) {
            String tok = sc.nextToken(delimeters);
            if (tok.length() > 0) {
                tokenOnLine++;
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == tokenOnLine) {
                        sb.append(tok).append('|');
                    }
                }
                if (tokenOnLine == count) {
                    sb.append(tok).append('^');
                    tokenOnLine = 0;
                }
            }
        }
        return sb.toString();
    }

    public static String toJson(String in, String[] names, int[] positions, String dev, String context) {
        if (names.length != positions.length) {
            throw new BadDataException("Invalid conversion to JSON:" + context);
        }
        StringBuilder sb = new StringBuilder();
        int mark1 = 0;
        int mark2 = 0;
        sb.append('[');
        String[] s = in.split("\n");
        for (String l : s) {
            if (l.trim().length() > 0) {
                String[] p = l.split(dev);
                sb.append('{');
                for (int i = 0; i < names.length; i++) {
                    String name = names[i];
                    int pos = positions[i];
                    if ((pos >= 0) && (pos < p.length)) {
                        sb.append("\"").append(name.trim()).append("\":\"").append(p[pos].trim()).append("\"");
                        mark2 = sb.length();
                        sb.append(',');
                    }
                }
                sb.setLength(mark2);
                sb.append('}');
                mark1 = sb.length();
                sb.append(',');
            }
        }
        sb.setLength(mark1);
        sb.append(']');
        return sb.toString();
    }
    
    public static String replaceAll(String s, char c1, char c2) {
        StringBuilder sb = new StringBuilder();
        for (char c:s.toCharArray()) {
            if (c == c1) {
                sb.append(c2);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

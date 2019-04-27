/*
 * Copyright (C) 2018 Stuiart Davies (stuartdd)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stuart
 */
public class StringTools {

    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


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


    public static String replaceAll(String s, char c1, char c2) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == c1) {
                sb.append(c2);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String parseThumbnailFileName(String name) {

        int pos1 = name.lastIndexOf('.');
        if (pos1 < 0) {
            /*
            No '.' then not a Thumbnail name
             */
            return name;
        }
        name = name.substring(0, pos1);
        if (name.length() > 25) {
            return name.substring(20);
        }
        return name;
    }

    public static byte[] encodePlainText(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if (b == '<') {
                sb.append("&lt;");
            } else {
                if (b == '>') {
                    sb.append("&gt;");
                } else {
                    sb.append((char) b);
                }
            }
        }
        return sb.toString().getBytes();
    }

    public static String cleanJsonString(String jsonIn) {
        StringBuilder sb = new StringBuilder();
        for (char c : jsonIn.toCharArray()) {
            if (c == '\\') {
                sb.append('\\').append('\\');
            } else {
                if ((c < ' ') || (c > 127)) {
                    String hex = Integer.toHexString(c);
                    if (hex.length() == 1) {
                        hex = '0' + hex;
                    }
                    sb.append('#').append(hex.toUpperCase());
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}

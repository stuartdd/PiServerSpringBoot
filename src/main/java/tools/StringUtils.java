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

import config.ConfigDataManager;
import exceptions.BadDataException;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author stuart
 */
public class StringUtils {

    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

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
        Scanner sc = new Scanner(in);
        sc.skipSpace();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int mark01 = 0;
        int mark02 = 0;
        int tokenOnLine = 0;
        boolean firstToken = true;

        while (sc.hasNext()) {
            String tok = sc.nextToken(delimeters);
            if (tok.length() > 0) {
                if (firstToken) {
                    sb.append('{');
                    firstToken = false;
                }
                tokenOnLine++;

                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == tokenOnLine) {
                        sb.append("\"").append(names[i]).append("\":\"").append(tok).append("\"");
                        mark01 = sb.length();
                        sb.append(',');
                        break;
                    }
                }

                if (tokenOnLine == count) {
                    sb.setLength(mark01);
                    sb.append('}');
                    mark02 = sb.length();
                    sb.append(',');
                    tokenOnLine = 0;
                    firstToken = true;
                }
            }
        }
        sb.setLength(mark02);
        sb.append(']');
        return sb.toString();
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

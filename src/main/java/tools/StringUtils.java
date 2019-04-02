/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

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

    private final static Map<String, MediaTypeInf> map = new HashMap();
    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    static {
        map.put("jpg", new MediaTypeInf("image/jpeg", false));
        map.put("gif", new MediaTypeInf("image/gif", false));
        map.put("png", new MediaTypeInf("image/png", false));
        map.put("ico", new MediaTypeInf("image/x-icon", false));
        map.put("tiff", new MediaTypeInf("image/tiff", false));
        map.put("js", new MediaTypeInf("text/javascript", true));
        map.put("svg", new MediaTypeInf("image/svg+xml", true));
        map.put("json", new MediaTypeInf("application/json; charset=UTF-8", true));
        map.put("xml", new MediaTypeInf("application/xml; charset=UTF-8", true));
        map.put("css", new MediaTypeInf("text/css", true));
        map.put("html", new MediaTypeInf("text/html", true));
        map.put("log", new MediaTypeInf("text/plain", true));
        map.put("txt", new MediaTypeInf("text/plain", true));
    }

    public static MediaTypeInf getMediaTypeForFile(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos >= 0) {
            String ext = fileName.substring(pos + 1);
            MediaTypeInf mt = map.get(ext.toLowerCase());
            if (mt != null) {
                return mt;
            }
        }
        return null;
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
}

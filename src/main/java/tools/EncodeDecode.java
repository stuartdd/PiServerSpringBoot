/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 *
 * @author stuart
 */
public class EncodeDecode {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final char MARK = '#';
    private static final char[] ENC_CHARS = new char[]{' ', '/', '\\', '\'', '@', '%', '+', '-', '"'};
    private static final String[] ENC_HEX = new String[]{MARK + "20", MARK + "2F", MARK + "5C", MARK + "27", MARK + "40", MARK + "25", MARK + "2B", MARK + "2D", MARK + "22"};

    public static String encode(String s) {
        return new String(ENCODER.encode(s.getBytes(CHARSET)));
    }
    
    public static String decode(String s) {
        return new String(DECODER.decode(s), CHARSET);
    }
    
    public static String encodeOld(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            boolean notReplaced = true;
            if (c <= ' ') {
                sb.append(ENC_HEX[0]);
                notReplaced = false;
            } else {
                for (int i = 0; i < ENC_CHARS.length; i++) {
                    if (ENC_CHARS[i] == c) {
                        sb.append(ENC_HEX[i]);
                        notReplaced = false;
                        break;
                    }
                }
            }
            if (notReplaced) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int[] hexValues = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15};

    public static String decodeOld(String s) {
        StringBuilder sb = new StringBuilder();
        int state = 0;
        int val = 0;
        for (char c : s.toCharArray()) {
            if (c == MARK) {
                state = 1;
            }
            switch (state) {
                case 0:
                    sb.append(c);
                    break;
                case 1:
                    state = 2;
                    break;
                case 2:
                    val = hexValues[(c - '0')] * 16;
                    state = 3;
                    break;
                case 3:
                    val = val + hexValues[(c - '0')];
                    state = 0;
                    sb.append((char) val);
                    break;
            }
        }
        return sb.toString();
    }

}

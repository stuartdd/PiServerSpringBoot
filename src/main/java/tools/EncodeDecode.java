/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.Base64;

/**
 *
 * @author stuart
 */
public class EncodeDecode {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static String encode(String s) {
        return new String(ENCODER.encode(s.getBytes(StringUtils.DEFAULT_CHARSET)));
    }
    
    public static String decode(String s) {
        return new String(DECODER.decode(s), StringUtils.DEFAULT_CHARSET);
    } 
}

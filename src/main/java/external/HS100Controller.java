/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * https://machinekoder.com/controlling-tp-link-hs100110-smart-plugs-with-machinekit/
 * https://blog.georgovassilis.com/2016/05/07/controlling-the-tp-link-hs100-wi-fi-smart-plug/
 * 
 * @author stuart
 */
public class HS100Controller {

    private static final int KEY = 171;
    private static final int OFFSET = 4;
    private static final int BUFF_LEN = 1000;
    private static final String ON_STR = "{\"system\":{\"set_relay_state\":{\"state\":1}}}";
    private static final String OFF_STR = "{\"system\":{\"set_relay_state\":{\"state\":0}}}";
    private static final String QUERY_STR = "{\"system\":{\"get_sysinfo\":null}}";
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("192.168.1.13", 9999)) {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write(encrypt(ON_STR));
            System.out.println(decrypt(read(clientSocket.getInputStream())));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static byte[] read(InputStream is) throws IOException {
        final byte[] b = new byte[BUFF_LEN];
        int pos = 0;
        int c = is.read();
        while (c >= 0) {
            b[pos++] = (byte) c;
            c = is.read();
        }
        return Arrays.copyOf(b, pos);
    }

    private static String decrypt(final byte[] b) {
        final StringBuilder sb = new StringBuilder();
        int key = KEY;
        for (int j = OFFSET; j < b.length; j++) {
            int a = key ^ b[j];
            key = b[j];
            sb.append((char) (a & 0x7F));
        }
        return sb.toString();
    }

    private static byte[] encrypt(final String in) {
        final byte[] buf = new byte[BUFF_LEN];
        int pos = 0;
        for (int i = 0; i < OFFSET; i++) {
            buf[pos++] = 0;
        }
        int key = KEY;
        for (byte b : in.getBytes()) {
            int a = key ^ b;
            key = a;
            buf[pos++] = (byte) a;
        }
        return Arrays.copyOf(buf, pos);
    }
}

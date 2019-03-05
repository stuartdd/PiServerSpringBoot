/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author stuart
 */
public class Pipe {

    private static final String USAGE = "USAGE:\n"
            + "  -n<names-csv> - list of comma separated names\n"
            + "  -p<pos-csv> - list of comma separated 'word in line' positions\n"
            + "  -l<line-len-csv> - list of comma separated 'line length' values\n"
            + "     eg. -l5,6 - Line 1 is 5 words long, Line 2 is 6, line 3 is 5 line 4 is 6 etc\n"
            + "         -l8 - All lines are 8 words long";
    private static final String SEP = "\n----------------------------------------------------------------------------------------------------";
    private static String[] names = new String[]{};
    private static int[] linePos = new int[]{};
    private static int[] lineLen = new int[]{};
    private static File file;
    private static String outFileName;

    /*
    Instance parameters
     */
    private final char[] buffer;
    private final int max;
    private int pos;

    public Pipe(final String input) {
        this.buffer = input.toCharArray();
        this.pos = 0;
        this.max = this.buffer.length;
    }

    public char next() {
        if (pos < max) {
            return buffer[pos++];
        }
        return 0;
    }

    public boolean hasNext() {
        return (pos < max);
    }

    public int back() {
        if (pos > 0) {
            pos--;
        }
        return pos;
    }

    public void skipSpace() {
        while (hasNext()) {
            if (next() > ' ') {
                back();
                return;
            }
        }
    }

    public void skipToNext(final char c) {
        while (hasNext()) {
            if (next() == c) {
                back();
                return;
            }
        }
    }

    public String nextToken(String set) {
        StringBuilder sb = new StringBuilder();
        skipSpace();
        while (hasNext()) {
            char c = next();
            if (c < ' ') {
                c = ' ';
            }
            if (isIn(c, set)) {
                return sb.toString().trim();
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    public boolean isIn(char needle, String hayStack) {
        for (char c : hayStack.toCharArray()) {
            if (needle == c) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        for (String a : args) {
            if (a.startsWith("-in")) {
                file = readFileForIn(a.substring(3), "-in");
            }
            if (a.startsWith("-ou")) {
                outFileName = a.substring(3);
            }
            if (a.startsWith("-n")) {
                names = readStrings(a.substring(2), "-n");
            }
            if (a.startsWith("-p")) {
                linePos = readInts(a.substring(2), "-p");
            }
            if (a.startsWith("-l")) {
                lineLen = readInts(a.substring(2), "-l");
            }
        }
        if (names.length == 0) {
            exitCode("-n must be defined");
        }
        if (lineLen.length == 0) {
            exitCode("-l must be defined");
        }
        if (linePos.length != names.length) {
            exitCode("-n and -p must be defined and have the same number of arguments");
        }

        InputStream is;
        if (file == null) {
            is = System.in;
        } else {
            is = new FileInputStream(file);
        }

        StringBuilder sb = new StringBuilder();
        while (is.available() > 0) {
            int i = is.read();
            if (i > 0) {
                sb.append((char) i);
            }
        }
        if (outFileName != null) {
            writeFileForOut(toJson(sb.toString(), names, linePos, lineLen, " "), outFileName);
        } else {
            System.out.println(toJson(sb.toString(), names, linePos, lineLen, " "));
        }
    }

    private static String[] readStrings(String csv, String context) {
        if (csv.isEmpty()) {
            exitCode(context + " does not have any values. Use " + context + "value1,value2 etc.");
        }
        return csv.split("\\,");
    }

    private static int[] readInts(String csv, String context) {
        if (csv.isEmpty()) {
            exitCode(context + " does not have any values. Use " + context + "1,9 etc.");
        }
        String[] st = csv.split("\\,");
        int[] ns = new int[st.length];
        for (int i = 0; i < st.length; i++) {
            try {
                ns[i] = Integer.parseInt(st[i]);
            } catch (NumberFormatException nfe) {
                exitCode(context + " contins an invalid integer [" + st[i] + "]. Use " + context + "1,9 etc.");
            }
        }
        return ns;
    }

    public static void writeFileForOut(String s, String fileName) {
        File f = new File((new File(fileName)).getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(s.getBytes());
        } catch (IOException ex) {
            exitCode("Unable to write output file [" + f.getAbsolutePath() + "] :" + ex.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    exitCode("Unable to close output file [" + f.getAbsolutePath() + "]:" + ex.getMessage());
                }
            }
        }
    }

    private static File readFileForIn(String fileName, String context) {
        File f = new File((new File(fileName)).getAbsolutePath());
        if (f.exists()) {
            return f;
        }
        exitCode(context + " file does not exist");
        return null;
    }

    private static void exitCode(String m) {
        System.err.println(SEP + "\n" + m + SEP + "\n" + USAGE + SEP);
        System.exit(1);
    }

    private static String toJson(String in, String[] names, int[] positions, int[] lineLengths, String delimeters) {
        Pipe sc = new Pipe(in);
        sc.skipSpace();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int mark01 = 0;
        int mark02 = 0;
        int tokenOnLine = 0;
        int lineLengthPos = 0;

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

                if (tokenOnLine == lineLengths[lineLengthPos]) {
                    sb.setLength(mark01);
                    sb.append('}');
                    mark02 = sb.length();
                    sb.append(',');
                    tokenOnLine = 0;
                    firstToken = true;
                    lineLengthPos++;
                    if (lineLengthPos >= lineLengths.length) {
                        lineLengthPos = 0;
                    }
                }
            }
        }
        sb.setLength(mark02);
        sb.append(']');
        return sb.toString();
    }

}

package external;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PipeAction {
    private static boolean canRun = true;

    private static Properties config;

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                loadConfig("PipeAction.properties");
            } else {
                loadConfig(args[0]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        InputStream is = System.in;

        StringBuilder sb = new StringBuilder();
        try {
            while (canRun) {
                while ((is.available() > 0) && (canRun)) {
                    int i = is.read();
                    if (i > 0) {
                        sb.append((char) i);
                    }
                    if (i < 32) {
                        if (i == 0) {
                            canRun = false;
                        } else {
                            doAction(sb.toString());
                            sb.setLength(0);
                        }
                    }
                }
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void doAction(String message) {
        if ((message == null) || (message.trim().length()==0)) {
            return;
        }
        String key = message.trim();
        String action;
        String[] parts = splice(key.toLowerCase());
        for (String s:parts) {
            action = config.getProperty(s);
            if (action != null) {
                doAction(action, parts);
                return;
            }
        }
        action = config.getProperty("default");
        if (action != null) {
            doAction(action, parts);
        }
    }

    private static void doAction(String action, String[] parts) {
        for (int i = 0; i < 10; i++) {
            if (i < parts.length) {
                action = action.replaceAll("\\{" + i + "\\}", parts[i]);
            } else {
                action = action.replaceAll("\\{" + i + "\\}", "");
            }
        }
        doFinalAction(action);
    }

    private static void doFinalAction(String action) {
        String protocol = config.getProperty("sever.protocol","http://");
        String host = config.getProperty("server.host","localhost");
        String url = protocol+host+'/'+action;
        System.out.println(send(url));
    }

    private static String send(String action)  {
        try {
            System.out.println("Action:"+action);
            URLConnection connection = new URL(action).openConnection();
            connection.setRequestProperty("Accept-Charset", Charset.forName("UTF-8").name());
            InputStream response = connection.getInputStream();
            StringBuilder sb = new StringBuilder();
            while (response.available() > 0) {
                sb.append((char) response.read());
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to perform action ["+action+"]. Exception:"+e.getMessage();
        }
    }

    private static String[] splice(String s) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9'))) {
                sb.append(c);
            } else {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[list.size()]);
    }

    private static void loadConfig(String s) throws IOException {
        File f = new File((new File(s)).getAbsolutePath());
        if (f.exists()) {
            config = new Properties();
            config.load(new FileInputStream(f));
        } else {
            throw new FileNotFoundException("Config properties [" + f.getAbsolutePath() + "] was not found");
        }
    }

}

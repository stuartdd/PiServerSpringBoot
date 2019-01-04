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
package services;

import controllers.Server;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author stuart
 */
public class ShutDownService {
    
    public static String shutDownLater(ApplicationContext ac, int rc, long delay, String log) {
        ShutDown shutDown = new ShutDown(ac, rc, delay, log);
        shutDown.start();
        return shutDown.getMessage();
    }
    
    private static class ShutDown extends Thread {

        private final ApplicationContext appContext;
        private final int rc;
        private final long delay;
        private final String log;

        public ShutDown(ApplicationContext appContext, int rc, long delay, String log) {
            this.appContext = appContext;
            this.rc = rc;
            this.delay = delay;
            this.log = log;
        }

        public String getMessage() {
            return "{\"Status\":\"OK\", \"Msg\":\""+log+"\", \"Entity\":\"CODE:"+rc+" DELAY:"+delay+" Seconds\"}";
        }
        
        @Override
        public void run() {
            try {
                sleep(delay*1000);
            } catch (InterruptedException ex) {
                
            }
            Logger.getLogger(Server.class.getName()).log(Level.WARNING, getMessage());
            SpringApplication.exit(appContext, new ExitCodeGenerator() {
                @Override
                public int getExitCode() {
                    return rc;
                }
            });
        }

    }    
}

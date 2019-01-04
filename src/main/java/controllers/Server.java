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
package controllers;

import services.ServerService;
import services.ShutDownService;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import main.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author stuart
 */
@RestController("server")
public class Server extends ControllerBase {

    @Autowired
    private ApplicationContext appContext;

    @RequestMapping(value = "server/time", method = RequestMethod.GET)
    public String time(@RequestParam Map<String, String> queryParameters) {
        return ServerService.jsonTime();
    }

    @RequestMapping(value = "server/stop", method = RequestMethod.GET)
    public String stop(HttpServletRequest req, @RequestParam Map<String, String> queryParameters) {
        return ShutDownService.shutDownLater(appContext, 1, 2, "SHUTTING DOWN");
    }

    @RequestMapping(value = "server/restart", method = RequestMethod.GET)
    public String restart(HttpServletRequest req, @RequestParam Map<String, String> queryParameters) {
        return ShutDownService.shutDownLater(appContext, 2, 2, "RESTARTING");
    }

    

}

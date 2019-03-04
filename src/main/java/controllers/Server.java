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

import exceptions.ServerRestException;
import services.ServerService;
import services.ShutDownService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import main.ConfigDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.StringUtils;

/**
 *
 * @author stuart
 */
@RestController("server")
public class Server extends ControllerErrorHandlerBase {

    @Autowired
    private ApplicationContext appContext;

    @RequestMapping(value = "server/time", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String time(@RequestParam Map<String, String> queryParameters) {
        return ServerService.jsonTime();
    }

    @RequestMapping(value = "server/stop", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String stop(HttpServletRequest req, @RequestParam Map<String, String> queryParameters) {
        if (ConfigDataManager.getConfigData().isAllowServerStopCtrl()) {
            return ShutDownService.shutDownLater(appContext, 1, 2, "SHUTTING DOWN");
        } else {
            throw new ServerRestException("Stop command is disabled",HttpStatus.FORBIDDEN,"Forbidden");
        } 
    }

    @RequestMapping(value = "server/restart", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String restart(HttpServletRequest req, @RequestParam Map<String, String> queryParameters) {
        return ShutDownService.shutDownLater(appContext, 2, 2, "RESTARTING");
    }
    
    @RequestMapping(value = "server/ufs", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String ufs(@RequestParam Map<String, String> queryParameters) {
        String ufs = ConfigDataManager.readCacheStartToEnd("userFileSizes.out", "ufs");
        return StringUtils.toJson(ufs, new String[] {"Size","Name"}, new int[] {1,5}, 5, "/ ", "UFS");
    }
    
}

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
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author stuart
 */
public class ControllerBase {

    @ExceptionHandler(Exception.class)
    public void handleError(HttpServletResponse resp, Exception ex) {
        String message;
        String warning;
        if (ex instanceof ServerRestException) {
            ServerRestException sre = (ServerRestException) ex;
            resp.setStatus(sre.getStatus());
            warning = sre.getWarning();
            message = "{\"Status\":" + sre.getStatus() + ", \"Msg\":\"" + warning + "\", \"Entity\":\"" + sre.getMessage() + "\"}";
        } else {
            resp.setStatus(500);
            warning = "Unknown Server Error";
            message = "{\"Status\":500, \"Msg\":\"" + ex.getMessage() + "\", \"Entity\":\"Unknown\"}";
        }
        resp.setHeader("Content-Type", "application/json;charset=UTF-8");
        resp.setHeader("Warning", warning);
        resp.setContentLength(message.length());
        try {
            PrintWriter out = resp.getWriter();
            out.print(message);
            out.flush();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}

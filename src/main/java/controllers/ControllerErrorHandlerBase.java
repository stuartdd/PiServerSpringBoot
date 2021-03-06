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

import config.LogProvider;
import exceptions.ServerRestException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.MediaTypeInfAndName;
import tools.StringTools;

/**
 *
 * @author stuart
 */
public class ControllerErrorHandlerBase {
    /**
     * Required for when a request method throws an exception.
     *
     * @param resp The response. So we can return an error message.
     * @param root The exception that was thrown.
     */
    @ExceptionHandler(Exception.class)
    public void handleError(HttpServletResponse resp, Exception root) {
        String message;
        String warning;
        int status;
        boolean fullLog = true;
        Throwable cause = root.getCause();
        if (cause == null) {
            cause = root;
        }
        if (cause instanceof ServerRestException) {
            /*
            If it is a ServerRestException then it contains all the data we need.
             */
            ServerRestException sre = (ServerRestException) cause;
            warning = sre.getWarning();
            status = sre.getStatus();
            resp.setStatus(status);
            fullLog = sre.fullLog();
            message = "{\"Status\":" + status + ", \"Msg\":\"" + warning + "\", \"Entity\":\"" + sre.getMessage() + "\"}";
        } else {
            /*
            If it is NOT a ServerRestException then we do the best we can!.
             */
            status = 500;
            resp.setStatus(status);
            warning = "Unknown Server Error";
            message = "{\"Status\":" + status + ", \"Msg\":\"" + cause.getMessage() + "\", \"Entity\":\"Unknown\"}";
        }
        if (fullLog) {
            LogProvider.logErr(message + " EXCEPTION:", root);
        } else {
            LogProvider.logErr(message + " CAUSE: " + cause.getMessage());
        }
        /*
        Define the headers for the error.
        Write the response and return.
         */
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

    protected ResponseEntity<byte[]> byteResponseEntity(byte[] bytes, MediaTypeInfAndName mediaTypeInf) {
        if (mediaTypeInf.isPlainText()) {
            bytes = StringTools.encodePlainText(bytes);
        }
        return byteResponseEntity(bytes, mediaTypeInf.getMediaType());
    }

    protected ResponseEntity<byte[]> byteResponseEntity(byte[] bytes, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.add("Content-Type", contentType);
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        return responseEntity;
    }

}

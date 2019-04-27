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
import java.util.Map;
import config.ConfigDataManager;
import config.LogProvider;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.FileUtils;
import tools.MediaTypeInf;
import tools.StringUtils;
import tools.Template;

/**
 *
 * @author stuart
 */
@RestController("static")
public class Content extends ControllerErrorHandlerBase {
    private static final String FS = File.separator;


    @RequestMapping(value = "static/**", method = RequestMethod.GET)
    public ResponseEntity<byte[]> content(@RequestParam Map<String, String> queryParameters, HttpServletRequest request) {
        Map<String, String> fullMap = ConfigDataManager.getParameters(queryParameters);
        String finalName = request.getRequestURI();
        if (finalName.startsWith(FS)) {
            finalName = finalName.substring(1);
        }
        finalName = finalName.substring("static".length());
        MediaTypeInf mediaTypeInf = StringUtils.getMediaTypeForFile(finalName);
        if (mediaTypeInf == null) {
            mediaTypeInf = StringUtils.getMediaTypeForFile(".txt");
        }
        byte[] bytes;
        try {
            File f = ConfigDataManager.getFileAtLocation("templates", finalName);
            if (f.exists() && mediaTypeInf.isPlainText()) {
                if (mediaTypeInf.isPlainText()) {
                    bytes = Template.parse(FileUtils.loadFile(f), fullMap).getBytes(StringUtils.DEFAULT_CHARSET);
                } else {
                    throw new ServerRestException(finalName, HttpStatus.BAD_REQUEST, "Cannot template binary files types " + mediaTypeInf.getMediaType());
                }
            } else {
                f = ConfigDataManager.getFileAtLocation("static", finalName);
                if (f.exists()) {
                    bytes = FileUtils.loadBinaryFile(f);
                } else {
                    if (FileUtils.resourceExists("/templates/" + finalName, this.getClass())) {
                        if (mediaTypeInf.isPlainText()) {
                            bytes = Template.parse(FileUtils.getResource("/templates/" + finalName, this.getClass()), fullMap).getBytes(StringUtils.DEFAULT_CHARSET);
                        } else {
                            throw new ServerRestException(finalName, HttpStatus.BAD_REQUEST, "Cannot template binary resource types " + mediaTypeInf.getMediaType());
                        }
                    } else {
                        if (FileUtils.resourceExists("/static/" + finalName, this.getClass())) {
                            bytes = FileUtils.loadBinaryFileResource("/static/" + finalName, this.getClass());
                        } else {
                            throw new ServerRestException(finalName, HttpStatus.NOT_FOUND, "Static file Not Found");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServerRestException(finalName, HttpStatus.NOT_FOUND, "Static file read error", ex);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.add("Content-Type", mediaTypeInf.getMediaType());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}

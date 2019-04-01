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
import java.io.File;
import java.nio.charset.Charset;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "static/{name}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> content(@PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        MediaTypeInf mediaTypeInf = StringUtils.getMediaTypeForFile(name);
        if (mediaTypeInf == null) {
            mediaTypeInf = StringUtils.getMediaTypeForFile(".txt");
        }
        byte[] bytes;
        try {
            File f = ConfigDataManager.getFileAtLocation("templates", name);
            if (f.exists() && mediaTypeInf.isPlainText()) {
                bytes = Template.parse(FileUtils.loadFile(f), ConfigDataManager.getProperties(queryParameters)).getBytes(StringUtils.DEFAULT_CHARSET);
            } else {
                f = ConfigDataManager.getFileAtLocation("static", name);
                if (f.exists()) {
                    bytes = FileUtils.loadBinaryFile(f);
                } else {
                    if (FileUtils.resourceExists("/templates/" + name, this.getClass())) {
                        bytes = Template.parse(FileUtils.getResource("/templates/" + name, this.getClass()), ConfigDataManager.getProperties(queryParameters)).getBytes(StringUtils.DEFAULT_CHARSET);
                    } else {
                        if (FileUtils.resourceExists("/static/" + name, this.getClass())) {
                            bytes = FileUtils.loadBinaryFileResource("/static/" + name, this.getClass());
                        } else {
                            throw new ServerRestException(name, HttpStatus.NOT_FOUND, "Static file Not Found");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServerRestException(name, HttpStatus.NOT_FOUND, "Static file read error", ex);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.add("Content-Type", mediaTypeInf.getMediaType());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}

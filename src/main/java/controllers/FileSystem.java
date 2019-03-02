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
import io.FileListIo;
import io.PathsIO;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.FileService;
import tools.FileExtFilter;
import tools.StringUtils;

@RestController("paths")
public class FileSystem extends ControllerErrorHandlerBase {

    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.POST)
    public ResponseEntity write(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestBody String body) {
        FileService.saveFiles(user, loc, path, name, body);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.GET)
    public HttpServletResponse file(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, HttpServletResponse response) {
        byte[] bytes = FileService.userFiles(user, loc, path, name);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            bos.write(bytes, 0, bytes.length);
            bos.flush();
        } catch (IOException io) {
            throw new ServerRestException("Failed to Stream response for file [" + name + "]", HttpStatus.FAILED_DEPENDENCY, name);
        }
        response.setContentType(StringUtils.getMediaTypeFroFile(name));
        return response;
    }

    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public FileListIo files(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @RequestParam Map<String, String> queryParameters) {
        String filter = queryParameters.getOrDefault("ext", null);
        if ((filter == null) || (filter.isEmpty())) {
            return FileService.userFiles(user, loc, path);

        }
        return FileService.userFiles(user, loc, path, filter.split("\\,"));
    }

    @RequestMapping(value = "/paths/user/{user}/loc/{loc}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public PathsIO paths(@PathVariable String user, @PathVariable String loc, @RequestParam Map<String, String> queryParameters) {
        return FileService.paths(user, loc);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public FileListIo logs(@RequestParam Map<String, String> queryParameters) {
        return FileService.userFiles(null, "logs", null, new FileExtFilter(new String[]{"log"}));
    }

    @RequestMapping(value = "/logs/file/{fileName}", method = RequestMethod.GET, produces = "text/plain")
    public String paths(@PathVariable String fileName, @RequestParam Map<String, String> queryParameters) {
        return FileService.userFilesRead(null, "logs", null, fileName);
    }
}

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

import io.FileListIo;
import io.PathsIO;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    /**
     * Save some JSON text to the server
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param path a directory added to the location
     * @param name the name of the file
     * @param body the JSON text to save!
     * @return HttpStatus.CREATED
     */
    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.POST)
    public ResponseEntity write(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestBody String body) {
        FileService.saveFiles(user, loc, path, name, body);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Read ANY content from the server. If the thumbnail flag is true the
     * the name field is a thumbnail file name which needs to have the date
     * prefix removed and .jpg removed if there are two.
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param path a directory added to the location
     * @param name the name of the file of a thumbnail
     * @param queryParameters the query parameters.
     * @return HttpStatus.CREATED
     */
    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> file(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        String subStringExpression = queryParameters.get("thumbnail");
        if ((subStringExpression != null) && (subStringExpression.equalsIgnoreCase("true"))) {
            name = StringUtils.parseThumbnailFileName(name);
        }
        byte[] bytes = FileService.userFiles(user, loc, path, name);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        return responseEntity;
    }

    /**
     * List files for a user location and path
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param path a directory added to the location
     * @param queryParameters the query parameters.
     * @return list of files with encoded and un-encoded names
     */
    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public FileListIo files(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @RequestParam Map<String, String> queryParameters) {
        String filter = queryParameters.getOrDefault("ext", null);
        if ((filter == null) || (filter.isEmpty())) {
            return FileService.userFiles(user, loc, path);

        }
        return FileService.userFiles(user, loc, path, filter.split("\\,"));
    }

    /**
     * List directories (recursive paths) for a user at a location Only returns
     * paths that contain files.
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param queryParameters
     * @return list of paths with encoded and un-encoded names
     */
    @RequestMapping(value = "/paths/user/{user}/loc/{loc}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public PathsIO paths(@PathVariable String user, @PathVariable String loc, @RequestParam Map<String, String> queryParameters) {
        return FileService.paths(user, loc);
    }

    /**
     * List a list of log files from resources/logs
     *
     * @return list of files with encoded and un-encoded names
     */
    @RequestMapping(value = "/logs", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public FileListIo logs() {
        return FileService.userFiles(null, "logs", null, new FileExtFilter(new String[]{"log"}));
    }

    /**
     * List a log files from resources/logs
     *
     * @param fileName the name of the log file
     * @return the content of the log file.
     */
    @RequestMapping(value = "/logs/file/{fileName}", method = RequestMethod.GET, produces = "text/plain")
    public String paths(@PathVariable String fileName) {
        return FileService.userFilesRead(null, "logs", null, fileName);
    }
}

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
import tools.MediaTypeInf;
import tools.StringUtils;

@RestController("files")
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
    public ResponseEntity writeFile(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestBody String body) {
        FileService.saveFile(user, loc, path, name, body);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }
    
    /**
     * Save some JSON text to the server
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param name the name of the file
     * @param body the JSON text to save!
     * @return HttpStatus.CREATED
     */
    @RequestMapping(value = "/files/user/{user}/loc/{loc}/name/{name}", method = RequestMethod.POST)
    public ResponseEntity writeFile(@PathVariable String user, @PathVariable String loc, @PathVariable String name, @RequestBody String body) {
        return writeFile(user, loc, null, name, body);
    }

    /**
     * Read ANY content from the server. If the thumbnail flag is true the the
     * name field is a thumbnail file name which needs to have the date prefix
     * removed and .jpg removed if there are two.
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param path a sub directory within parent resources.users.{location}
     * @param name the name of the file of a thumbnail
     * @param queryParameters the query parameters.
     * @return HttpStatus.CREATED
     */
    @RequestMapping(value = "/files/user/{user}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> fileReadUserLocationBase(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        String subStringExpression = queryParameters.get("thumbnail");
        if ((subStringExpression != null) && (subStringExpression.equalsIgnoreCase("true"))) {
            name = StringUtils.parseThumbnailFileName(name);
        }
        byte[] bytes = FileService.userReadFiles(user, loc, path, name);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        MediaTypeInf mediaTypeInf = StringUtils.getMediaTypeFroFile(name);
        if (mediaTypeInf.isPlainText()) {
            bytes = StringUtils.encodePlainText(bytes);
        }
        headers.add("Content-Type", mediaTypeInf.getMediaType());
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        return responseEntity;
    }
    
    /**
     * Read ANY content from the server. If the thumbnail flag is true the the
     * name field is a thumbnail file name which needs to have the date prefix
     * removed and .jpg removed if there are two.
     *
     * @param user User name from resources.users
     * @param loc location for resources.users.{location}
     * @param path a sub directory within parent resources.users.{location}
     * @param name the name of the file of a thumbnail
     * @param queryParameters the query parameters.
     * @return HttpStatus.CREATED
     */
    @RequestMapping(value = "/files/user/{user}/loc/{loc}/name/{name}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> fileReadUserLocation(@PathVariable String user, @PathVariable String loc, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        return fileReadUserLocationBase(user, loc, (String)null, name, queryParameters);
    }

    /**
     * Read ANY content from the server. If the thumbnail flag is true the the
     * name field is a thumbnail file name which needs to have the date prefix
     * removed and .jpg removed if there are two.
     *
     * @param loc The name from resources.locations
     * @param path a sub directory within parent resources.locations
     * @param fileName the name of the file
     * @return the content of the log file.
     */
    @RequestMapping(value = "/files/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.GET, produces = "text/plain")
    public ResponseEntity<byte[]> fileReadLocation(@PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        return fileReadUserLocationBase((String)null, loc, path, name, queryParameters);
    }
    /**
     * Read ANY content from the server. If the thumbnail flag is true the the
     * name field is a thumbnail file name which needs to have the date prefix
     * removed and .jpg removed if there are two.
     *
     * @param loc The name from resources.locations
     * @param fileName the name of the file
     * @return the content of the log file.
     */
    @RequestMapping(value = "/files/loc/{loc}/name/{name}", method = RequestMethod.GET, produces = "text/plain")
    public ResponseEntity<byte[]> fileReadLocation(@PathVariable String loc, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        return fileReadUserLocationBase((String)null, loc, (String)null, name, queryParameters);
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
    public FileListIo listFilesBase(@PathVariable String user, @PathVariable String loc, @PathVariable String path, @RequestParam Map<String, String> queryParameters) {
        String filter = queryParameters.getOrDefault("ext", null);
        if ((filter == null) || (filter.isEmpty())) {
            return FileService.userListFiles(user, loc, path);
        }
        return FileService.userListFiles(user, loc, path, filter.split("\\,"));
    }
    
    /**
     * List of files at a resources.{location}
     *
     * @return list of files with encoded and un-encoded names
     */
    @RequestMapping(value = "/files/loc/{loc}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public FileListIo listFiles(@PathVariable String loc, @RequestParam Map<String, String> queryParameters) {
        return listFilesBase((String)null, loc, (String)null, queryParameters);
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
    public PathsIO listPaths(@PathVariable String user, @PathVariable String loc, @RequestParam Map<String, String> queryParameters) {
        return FileService.paths(user, loc);
    }

}

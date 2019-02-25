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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.FileService;
import tools.FileExtFilter;

@RestController("paths")
public class FileSystem extends ControllerErrorHandlerBase {

    @RequestMapping(value = "/paths/user/{user}/loc/{loc}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public PathsIO paths(@PathVariable String user, @PathVariable String loc, @RequestParam Map<String, String> queryParameters) {
        return FileService.paths(user, loc);
    }
    
    @RequestMapping(value = "/logs", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public FileListIo logs(@RequestParam Map<String, String> queryParameters) {
        return FileService.userFiles(null, "logs", null, new FileExtFilter(new String[] {"log"}));
    }
    
    @RequestMapping(value = "/logs/file/{file}", method = RequestMethod.GET, produces = "text/plain; charset=UTF-8")
    public FileListIo paths(@PathVariable String file, @RequestParam Map<String, String> queryParameters) {
        return FileService.userFiles(null, "logs", null, new FileExtFilter(new String[] {"log"}));
    }
}

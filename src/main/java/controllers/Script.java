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

import config.ConfigDataManager;
import config.LogProvider;
import exceptions.ResourceFileNotFoundException;
import java.io.File;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.FileService;
import services.FunctionService;
import services.dto.FileResource;
import services.dto.FunctionResponseDto;
import tools.EncodeDecode;
import tools.MediaTypeInfAndName;
import tools.StringTools;

/**
 *
 * @author stuart
 */
@RestController("scripts")
public class Script extends ControllerErrorHandlerBase {

    @RequestMapping(value = "/script/{function}/user/{user}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> scriptWithFile(@PathVariable String function, @PathVariable String user, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        MediaTypeInfAndName mediaTypeInf = MediaTypeInfAndName.getMediaTypeForFile(name);
        String finalName = mediaTypeInf.getFileName();
        String finalPath = null;
        if (path != null) {
            finalPath = FileService.conditionFileName(EncodeDecode.decode(path));
        }

        LogProvider.log("scriptUserLocationBase: function:[" + function + "] user:[" + user + "] loc:[" + loc + "] path:[" + finalPath + "] encPath:[" + path + "] name:[" + finalName + "] encName:[" + name + "]", 1);
        String subStringExpression = queryParameters.get("thumbnail");
        if ((subStringExpression != null) && (subStringExpression.equalsIgnoreCase("true"))) {
            finalName = StringTools.parseThumbnailFileName(mediaTypeInf.getFileName());
            LogProvider.log("scriptUserLocationBase: function:[" + function + "] finalName:[" + finalName + "]", 2);
        }

        try {
            File file = FileResource.withUserLocation(user, loc).andPath(finalPath).andName(finalName).file();
            queryParameters.put("fullName", file.getAbsolutePath());
            if (file.getParent() != null) {
                queryParameters.put("parentPath", file.getParent());
            }
        } catch (ResourceFileNotFoundException ex) {

        }
        queryParameters.put("filePath", finalPath);
        queryParameters.put("fileName", finalName);
        if (user != null) {
            queryParameters.put("user", user);
            queryParameters.putAll(ConfigDataManager.getUser(user));
        } else {
            queryParameters.putAll(ConfigDataManager.getLocations());
        }
        FunctionResponseDto functionResponseDto = FunctionService.func(function, queryParameters);
        byte[] bytes = functionResponseDto.getResponse().getBytes(StringTools.DEFAULT_CHARSET);

        return byteResponseEntity(bytes, mediaTypeInf);

    }


    @RequestMapping(value = "/script/{function}/user/{user}/loc/{loc}/name/{name}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> scriptUserLocation(@PathVariable String function, @PathVariable String user, @PathVariable String loc, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        return scriptWithFile(function, user, loc, (String) null, name, queryParameters);
    }

    @RequestMapping(value = "/script/{function}/loc/{loc}/path/{path}/name/{name}", method = RequestMethod.GET, produces = "text/plain")
    public ResponseEntity<byte[]> fileReadLocation(@PathVariable String function, @PathVariable String loc, @PathVariable String path, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        return scriptWithFile(function, (String) null, loc, path, name, queryParameters);
    }

    @RequestMapping(value = "/script/{function}/loc/{loc}/name/{name}", method = RequestMethod.GET, produces = "text/plain")
    public ResponseEntity<byte[]> fileReadLocation(@PathVariable String function, @PathVariable String loc, @PathVariable String name, @RequestParam Map<String, String> queryParameters) {
        return scriptWithFile(function, (String) null, loc, (String) null, name, queryParameters);
    }

    @RequestMapping(value = "script/{function}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> script(@PathVariable String function, @RequestParam Map<String, String> queryParameters) {
        FunctionResponseDto functionResponseDto = FunctionService.func(function, queryParameters);
        return byteResponseEntity(functionResponseDto.getBytes(), functionResponseDto.getContentType());
    }
}

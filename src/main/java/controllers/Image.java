/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import io.FileListIo;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.FileService;
import tools.FileExtFilter;

/**
 *
 * @author stuart
 */
@RestController("image")
public class Image extends ControllerErrorHandlerBase {
    
    @RequestMapping(value = "thumbnails/user/{user}/path/{path}", method = RequestMethod.GET)
    public FileListIo thumbnails(@PathVariable String user, @PathVariable String path, @RequestParam Map<String, String> queryParameters) {
        return FileService.userFiles(user, "thumbnails", path, new FileExtFilter(new String[] {".jpg"}));
    }
    
}

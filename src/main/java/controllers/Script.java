/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.FunctionService;

/**
 *
 * @author stuart
 */
@RestController("scripts")
public class Script extends ControllerErrorHandlerBase {
    @RequestMapping(value = "func/{id}", method = RequestMethod.GET)
    public String script(@PathVariable String id, @RequestParam Map<String, String> queryParameters) {
        return FunctionService.func(id, queryParameters);
    }
    
}

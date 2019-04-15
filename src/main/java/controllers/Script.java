/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.Map;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.FunctionService;
import services.dto.FunctionResponseDto;
import tools.StringUtils;

/**
 *
 * @author stuart
 */
@RestController("scripts")
public class Script extends ControllerErrorHandlerBase {
    @RequestMapping(value = "func/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> script(@PathVariable String id, @RequestParam Map<String, String> queryParameters) {
        FunctionResponseDto functionResponseDto = FunctionService.func(id, queryParameters);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.add("Content-Type", functionResponseDto.getMap().get("Content-Type"));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(functionResponseDto.getResponse(), headers, HttpStatus.OK);
        return responseEntity;
    }
}

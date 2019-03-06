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
import main.ConfigDataManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.FileUtils;
import tools.Template;

/**
 *
 * @author stuart
 */
@RestController("static")
public class Templates extends ControllerErrorHandlerBase {

    @RequestMapping(value = "static/{template}", method = RequestMethod.GET)
    public String template(@PathVariable String template, @RequestParam Map<String, String> queryParameters) {
        try {
            return Template.parse(FileUtils.getResource("/templates/" + template, this.getClass()), ConfigDataManager.getProperties(queryParameters));
        } catch (Exception ex) {
            throw new ServerRestException(template, HttpStatus.NOT_FOUND, "Template Not Found");
        }
    }

}

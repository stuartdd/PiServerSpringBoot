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
import java.util.ArrayList;
import java.util.List;
import main.ConfigDataManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author stuart
 */
@RestController("users")
public class Users extends ControllerBase {

    @RequestMapping(value = "/users/{user}", method = RequestMethod.GET)
    public String user(@PathVariable String user) {
        String location = ConfigDataManager.getUsers().get(user);
        if (location == null) {
            throw new ServerRestException(user, HttpStatus.NOT_FOUND, "User Not Found");
        }
        return "{\"location\":\""+ConfigDataManager.getUsers().get(user)+"\"}";
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public @ResponseBody List<String> getUsers() {
        List<String> users = new ArrayList<>();
        for (String keys:ConfigDataManager.getUsers().keySet()) {
            users.add(keys);
        }
        return users;
    }

}

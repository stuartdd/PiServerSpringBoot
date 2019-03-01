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

import io.AudioStatusIO;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import services.AudioService;

/**
 *
 * @author stuart
 */
@RestController("audio")
public class Audio extends ControllerErrorHandlerBase {

    @RequestMapping(value = "audio/status", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody
    AudioStatusIO status(@RequestParam Map<String, String> queryParameters) {
        return AudioService.queryStatus();
    }

    @RequestMapping(value = "audio/stop", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody
    AudioStatusIO stop(@RequestParam Map<String, String> queryParameters) {
        return AudioService.stop();
    }

    @RequestMapping(value = "audio/play/{file}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody
    AudioStatusIO play(@PathVariable String file, @RequestParam Map<String, String> queryParameters) {
        return AudioService.play(file, queryParameters.get("vol"));
    }

    @RequestMapping(value = "audio/pause", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody
    AudioStatusIO pause(@RequestParam Map<String, String> queryParameters) {
        return AudioService.pause();
    }

    @RequestMapping(value = "audio/volume/{volume}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody
    AudioStatusIO pause(@PathVariable String volume) {
        return AudioService.setCurrentVolume(volume);
    }

}

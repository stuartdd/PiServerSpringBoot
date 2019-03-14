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
package main;

import config.LogProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import services.FunctionService;
import services.ShutDownService;

@SpringBootApplication
@ComponentScan(basePackages="controllers") 
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConfigDataManager.init(args);
        FunctionService.init(ConfigDataManager.getConfigData().getFunctions());
        
        ShutDownService.setExitFlag(0);
        ThreadPool.init();
        SpringApplication.run(Main.class, args);
    }

 }

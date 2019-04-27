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
package tools;

/**
 * Utility class used by FileUtils for files in a Jar or Zip
 * 
 * @author - Stuart Davies
 */
public class ContainerFile {

    private final String container;
    private final String name;
/**
 * Create with the path (container) and name 
 * 
 * @param container
 * @param name 
 */
    public ContainerFile(String container, String name) {
        this.container = container;
        this.name = name;
    }

    public String getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "JAR=" + container + " --> " + name;
    }

}

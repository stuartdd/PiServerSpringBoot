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
package io;

import java.util.ArrayList;
import java.util.List;
import tools.EncodeDecode;

/**
 *
 * @author stuart
 */
public class PathsIO {

    private String loc;
    private String user;
    private List<EncName> paths;

    public PathsIO() {
    }

    public PathsIO(String user, String loc) {
        this.loc = loc;
        this.user = user;
        this.paths = new ArrayList<>();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public List<EncName> getPaths() {
        return paths;
    }

    public void setPaths(List<EncName> paths) {
        this.paths = paths;
    }

     
    public void addPath(String name) {
        paths.add(new EncName(name, EncodeDecode.encode(name)));
    }

    @Override
    public String toString() {
        return "PathsIO{" + "loc=" + loc + ", paths=" + paths + '}';
    }

}

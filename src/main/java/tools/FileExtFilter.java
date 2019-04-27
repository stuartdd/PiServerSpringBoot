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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Used by FileUtils to filter the files returned from recursive directory scans
 *
 * @author - Stuart Davies
 */
public class FileExtFilter implements FileFilter {

    private final List<String> ext;

    public FileExtFilter(List<String> in) {
        ext = new ArrayList<>();
        for (String e : in) {
            ext.add(e.toLowerCase());
        }
    }

    public FileExtFilter(String[] in) {
        ext = new ArrayList<>();
        for (String e : in) {
            ext.add(e.toLowerCase());
        }
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String s = file.getName().toLowerCase();
        for (String e : ext) {
            if (s.endsWith(e)) {
                return true;
            }
        }
        return false;
    }

}

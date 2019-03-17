/*
 * Copyright (C) 2019 stuartdd
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
 *
 * @author stuar
 */
public class MediaTypeInf {
    private final String mediaType;
    private final boolean plainText;

    public MediaTypeInf(String mediaType, boolean plainText) {
        this.mediaType = mediaType;
        this.plainText = plainText;
    }

    public MediaTypeInf(String mediaType) {
        this.mediaType = mediaType;
        this.plainText = false;
    }

    public String getMediaType() {
        return mediaType;
    }

    public boolean isPlainText() {
        return plainText;
    }
    
    
}

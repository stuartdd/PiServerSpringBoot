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

import config.ConfigDataManager;
import java.util.HashMap;
import java.util.Map;

public class MediaTypeInfAndName {

    private final static Map<String, MediaTypeInf> map = new HashMap();
    static {
        map.put("jpg", new MediaTypeInf("image/jpeg", false));
        map.put("gif", new MediaTypeInf("image/gif", false));
        map.put("png", new MediaTypeInf("image/png", false));
        map.put("ico", new MediaTypeInf("image/x-icon", false));
        map.put("tiff", new MediaTypeInf("image/tiff", false));
        map.put("js", new MediaTypeInf("text/javascript", true));
        map.put("svg", new MediaTypeInf("image/svg+xml", true));
        map.put("json", new MediaTypeInf("application/json; charset=UTF-8", true));
        map.put("xml", new MediaTypeInf("application/xml; charset=UTF-8", true));
        map.put("css", new MediaTypeInf("text/css", true));
        map.put("html", new MediaTypeInf("text/html", true));
        map.put("log", new MediaTypeInf("text/plain", true));
        map.put("txt", new MediaTypeInf("text/plain", true));
    }

    private final MediaTypeInf mediaTypeInf;
    private final String fileName;

    public MediaTypeInfAndName(MediaTypeInf mediaTypeInf, String fileName) {
        this.mediaTypeInf = mediaTypeInf;
        this.fileName = fileName;
    }

    public String getMediaType() {
        return mediaTypeInf.getMediaType();
    }

    public boolean isPlainText() {
        return mediaTypeInf.isPlainText();
    }

    public String getFileName() {
        return fileName;
    }

        public static MediaTypeInfAndName getMediaTypeForFile(String fileName) {
        MediaTypeInf mediaTypeInf = null;
        String finalName = null;
        if (fileName != null) {
            mediaTypeInf = getMediaTypeForFileImpl(fileName);
            if (mediaTypeInf == null) {
                finalName = EncodeDecode.decode(fileName);
                mediaTypeInf = getMediaTypeForFileImpl(finalName);
            }
            if (mediaTypeInf == null) {
                finalName = ConfigDataManager.alias(fileName);
                mediaTypeInf = getMediaTypeForFileImpl(finalName);
            }
        }
        if (finalName == null) {
            finalName = fileName;
        }
        if (mediaTypeInf == null) {
            mediaTypeInf = getMediaTypeForFileImpl(".txt");
        }
        return new MediaTypeInfAndName(mediaTypeInf, finalName);
    }

    private static MediaTypeInf getMediaTypeForFileImpl(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos >= 0) {
            String ext = fileName.substring(pos + 1);
            MediaTypeInf mt = map.get(ext.toLowerCase());
            if (mt != null) {
                return mt;
            }
        }
        return null;
    }

}

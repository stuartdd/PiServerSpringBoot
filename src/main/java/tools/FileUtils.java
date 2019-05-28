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

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility to manage the file system
 *
 * @author - Stuart Davies
 */
public class FileUtils {

    private static final String FS = System.getProperty("file.separator");

    public static boolean isRootPath(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        int pos = path.indexOf(':');
        if ((pos > 0) && (pos < 3)) {
            path = path.substring(pos+1);
        }
        return (path.startsWith(FS));
    }

    public static void tree(File f, List<String> l, int pathLen, final String[] extensions) {
        File[] fList = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (File fil : fList) {
            if (fil.isDirectory()) {
                String[] count = fil.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        for (String ext : extensions) {
                            if (name.toLowerCase().endsWith(ext)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
                if (count.length > 0) {
                    l.add(fil.getAbsolutePath().substring(pathLen));
                }
                tree(fil, l, pathLen, extensions);
            }
        }
    }

    /**
     * Recursive directory crawler
     *
     * @param f Start in this directory
     * @param infileTypes Filter the files returned (eg .java)
     * @param recursive To recurse or not to recurse?
     * @return List of files
     */
    public static List<File> getFileList(File f, List<String> infileTypes, boolean recursive) {
        List<File> files = new ArrayList<>();
        if ((infileTypes == null) || (infileTypes.isEmpty())) {
            listFilesRecursive(f, files, null, recursive);
        } else {
            listFilesRecursive(f, files, new FileExtFilter(infileTypes), recursive);
        }
        return files;
    }

    private static void listFilesRecursive(File f, List<File> list, FileExtFilter filter, boolean recursive) {
        File[] files;
        if (filter == null) {
            files = f.listFiles();
        } else {
            files = f.listFiles(filter);
        }
        for (File fil : files) {
            if (fil.isDirectory()) {
                if (recursive) {
                    listFilesRecursive(fil, list, filter, recursive);
                }
            } else {
                if (fil.isFile()) {
                    list.add(fil);
                }
            }
        }
    }

    /**
     * Return the MD5 checksum of the contents of a file
     *
     * @param file The file
     * @return The MD5 checksum
     */
    public static String StringMD5(File file) {
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new FileUtilsException("MD5 in file [" + file.getAbsolutePath() + "] failed", ex);
        }
        return checksum;
    }

    /**
     * Unpack the contents of a ZIP or JAR file
     *
     * @param zFil The ZIP or JAR file
     * @param list The list to be updated (added to)
     */
    public static void getZipFileList(File zFil, List<ContainerFile> list) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(zFil);
            Enumeration<? extends ZipEntry> e = zip.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = e.nextElement();
                if (!ze.isDirectory()) {
                    list.add(new ContainerFile(zFil.getAbsolutePath(), ze.getName()));
                }
            }
        } catch (IOException ex) {
            throw new FileUtilsException("Zip file [" + zFil.getAbsolutePath() + "] List failed", ex);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException ex) {
                    throw new FileUtilsException("Zip file [" + zFil.getAbsolutePath() + "] List failed to close", ex);
                }
            }
        }
    }

    /**
     * Read a file from the resource directory relative to a specific class.
     *
     * @param fileName The name of the file. Use '/' to indicate the root of the
     * package
     * @param clazz The package this class is in will be the package searched.
     * @return The contents of the file
     */
    public static String getResource(String fileName, Class clazz) {
        StringBuilder result = new StringBuilder("");
        try {
            InputStream is = clazz.getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundUtilsException("Failed to find resource file[" + fileName + "]");
            }
            while (is.available() > 0) {
                result.append((char) is.read());
            }
        } catch (IOException e) {
            throw new FileUtilsException("Failed to read resource file[" + fileName + "]", e);
        }
        return result.toString();
    }

    public static boolean resourceExists(String fileName, Class clazz) {
        return (clazz.getResourceAsStream(fileName) != null);
    }

    public static byte[] loadBinaryFile(File fil) throws IOException {
        return Files.readAllBytes(fil.toPath());
    }

    public static byte[] loadBinaryFileResource(String fileName, Class clazz) {
        InputStream is = clazz.getResourceAsStream(fileName);
        if (is == null) {
            throw new FileNotFoundUtilsException("Failed to find resource file[" + fileName + "]");
        }
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new FileUtilsException("Failed to read resource file[" + fileName + "]", e);
        }
    }

    /**
     * Optimal read the contents of a file
     *
     * @param fil The file to be read
     * @return The contents
     * @throws IOException If the file cannot be read
     */
    public static String loadFile(File fil) throws IOException {
        return new String(Files.readAllBytes(fil.toPath()));
    }

    /**
     * Optimal read the contents of a file as a list
     *
     * @param fil The file to be read
     * @return The contents of the file
     * @throws IOException If the file cannot be read
     */
    public static List<String> loadFileAsLines(File fil) throws IOException {
        return Files.readAllLines(fil.toPath(), StringTools.DEFAULT_CHARSET);
    }

    /**
     * Optimal read the contents of a file through a basic filter
     *
     * @param fil The file to be read
     * @return The contents filtered
     * @throws IOException If the file cannot be read
     */
    public static List<LineWithNumber> loadFileAsLines(File fil, List<String> filter) throws IOException {
        return filter(loadFileAsLines(fil), filter);
    }

    /**
     * Write a file and overwrite the existing file
     *
     * @param s New contents of the file
     * @param fil The file
     */
    public static void writeFileOverwrite(String s, File fil) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fil);
            fos.write(s.getBytes());
        } catch (IOException ex) {
            throw new FileUtilsException("Unable to write file [" + fil.getAbsolutePath() + "]", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    throw new FileUtilsException("Unable to close file [" + fil.getAbsolutePath() + "]", ex);
                }
            }
        }
    }

    /**
     * Write a file and overwrite the existing file
     *
     * @param s New contents of the file
     * @param fil The file
     */
    public static void writeFileAppend(String s, File fil) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fil, true);
            fos.write(s.getBytes());
        } catch (IOException ex) {
            throw new FileUtilsException("Unable to append file [" + fil.getAbsolutePath() + "]", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    throw new FileUtilsException("Unable to close file [" + fil.getAbsolutePath() + "]", ex);
                }
            }
        }
    }

    /**
     * Write a file but back it up first
     *
     * @param s New contents of the file
     * @param fil The file
     * @throws IOException If the file cannot be backed up
     */
    public static void backupAndWriteFile(String s, File fil) throws IOException {
        File temp = new File(fil.getAbsolutePath() + ".temp");
        if (temp.exists()) {
            if (!temp.delete()) {
                throw new IOException("Unable to delete previous temporary file [" + temp.getAbsolutePath() + "]");
            }
        }
        writeFileOverwrite(s, temp);
        File back = new File(fil.getAbsolutePath() + ".back");
        if (back.exists()) {
            if (!back.delete()) {
                throw new IOException("Unable to delete previous backup file [" + back.getAbsolutePath() + "]");
            }
        }
        fil.renameTo(back);
        temp.renameTo(fil);
    }

    /**
     * Optimal copy the contents of a file
     *
     * @param fil The file to be copied
     * @param file The file to copy too.
     * @throws IOException If the file cannot be copied
     */
    public static void copyFile(File fil, File file) throws IOException {
        Files.copy(fil.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Does the file with this name exist!
     *
     * @param fileName The name of the file as a string
     * @return To exist or not to exist!
     */
    public static boolean exists(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        return newFile(fileName).exists();
    }

    /**
     * Create a file. If you create a file with a file name such as 'a.txt' then
     * the file name returned is 'a.txt'. Doing this will always return the full
     * path of the file
     *
     * @param fileName The file name as a string
     * @return
     */
    public static File newFile(String fileName) {
        File f = new File(fileName);
        return new File(f.getAbsolutePath());
    }

    private static List<LineWithNumber> filter(List<String> lines, List<String> filter) {
        List<LineWithNumber> linesOut = new ArrayList<>();
        int lineNum = 0;
        for (String line : lines) {
            lineNum++;
            String l = line.trim().toLowerCase();
            if (l.length() > 1) {
                int count = 0;
                for (String searchString : filter) {
                    String searchFor = searchString.trim();
                    if (searchFor.trim().equals("")) {
                        count++;
                    } else {
                        if (searchFor.startsWith("*")) {
                            if (l.endsWith(searchFor.substring(1))) {
                                count++;
                            }
                        } else {
                            if (searchFor.endsWith("*")) {
                                if (l.startsWith(searchFor.substring(0, searchFor.length() - 1))) {
                                    count++;
                                }
                            } else {
                                if (l.contains(searchFor)) {
                                    count++;
                                }
                            }
                        }
                    }
                }
                if (count == 3) {
                    linesOut.add(new LineWithNumber(lineNum, line));
                }
            }
        }
        return linesOut;
    }

}

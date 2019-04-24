/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author stuart
 */
public class CopyFile {

    public static void main(String[] args) throws IOException {
        boolean replace = false;
        StringBuilder sb = new StringBuilder();
        for (String s:args) {
            sb.append('\'').append(s).append('\'').append(' ');
            if (s.equalsIgnoreCase("replace")) {
                replace = true;
            }
        }
        if (args.length < 2) {
            System.out.println("CopyFile: Args["+sb+"] Not enough args. Requires from and too files.");
            System.exit(1);
        }
        File from = new File((new File(args[0])).getAbsolutePath());
        if (!from.exists()) {
            System.out.println("CopyFile: Args["+sb+"] From File was not found.");
            System.exit(1);
        }
        File dest = new File((new File(args[1])).getAbsolutePath());
        if (!dest.exists()) {
            File destPath = dest.getParentFile();
            if (destPath != null) {
                if (!destPath.exists()) {
                    if (!destPath.mkdirs()) {
                        System.out.println("CopyFile: Args["+sb+"] Failed to create parent directories.");
                        System.exit(1);
                    } else {
                        System.out.println("CopyFile: Parent directories created.");
                    }
                }
            }
            copyAndCheck(from, dest, false, sb.toString());
        } else {
            if (replace) {
                copyAndCheck(from, dest, true, sb.toString());
            } else {
                System.out.println("CopyFile: File already exists.");
            }
        }
      }

    private static void copyAndCheck(File fromFile, File toFile, boolean replace, String args) throws IOException {
        if (replace) {
            Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(fromFile.toPath(), toFile.toPath());
        }

        if (!toFile.exists()) {
            System.out.println("CopyFile: Args["+args+"] Destination File was not created.");
            System.exit(1);
        }
        System.out.println("CopyFile: Destination file created.");
    }
}

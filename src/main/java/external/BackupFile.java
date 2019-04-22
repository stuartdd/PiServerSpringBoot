/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author stuart
 */
public class BackupFile {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("BackupFile: Not enough args. Requires from and too files.");
            System.exit(1);
        }
        File from = new File((new File(args[0])).getAbsolutePath());
        if (!from.exists()) {
            System.out.println("BackupFile: From File was not found.");
            System.exit(1);
        }
        File dest = new File((new File(args[1])).getAbsolutePath());
        if (!dest.exists()) {
            File destPath = dest.getParentFile();
            if (destPath != null) {
                if (!destPath.exists()) {
                    destPath.mkdirs();
                    System.out.println("BackupFile: Parent created.");
                }
            }
            Files.copy(from.toPath(), dest.toPath());
            System.out.println("BackupFile: Complete.");
        } else {
            System.out.println("BackupFile: File already exists.");
        }
        if (!dest.exists()) {
            System.out.println("BackupFile: Backed up File was not found.");
            System.exit(1);
        }
    }
}

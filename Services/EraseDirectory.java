package com.Server.Services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import com.Server.Utilities.BlockAccess;
import com.Server.Utilities.SizeofDirectory;


public class EraseDirectory {

    public static void run(File folder, String IP) throws IOException {
        if (!folder.exists()) {
            throw new FileNotFoundException("Folder not found: " + folder);
        }
    
        if (!Files.isWritable(folder.toPath())) {
            System.err.println("Access denied.");
            return;
        }

		if(!BlockAccess.makeFolderUnreadableForOthers(folder)){
            System.err.println("Failed to set permissions of the folder"); 
            return; }
		 
    
        if (!folder.isDirectory()) {
            EraseFile.run(folder, IP);
            return;
        }

        // Set total size using the utility function
        long totalSize = SizeofDirectory.calc(folder);
    
        // Recursively delete files and folders
        deleteRecursively(folder, IP);
    }
    
    private static void deleteRecursively(File folder, String IP) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteRecursively(file, IP);
                } else {
                    EraseFile.run(file, IP);
                }
            }
        }
    
        // Delete the folder
        if (folder.delete()) {
            // Optionally, you could track directory deletion here as well.
        }
    }
}
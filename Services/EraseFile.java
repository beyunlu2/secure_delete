package com.Server.Services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

import com.Server.Utilities.UndeletedFiles;


public class EraseFile {
    private static final int BufferSize = 1 * 1024 * 1024;
    private static byte[] Buffer = new byte[BufferSize];

    public static void run(File file, String IP) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found or not a file.");
        }
        long length = file.length();

        
        try (RandomAccessFile raf = new RandomAccessFile(file, "rws")) {

            if (!Files.isWritable(file.toPath())){
                System.err.println("Access denied.");
                UndeletedFiles.record(file, IP);
                return;
            }

            long bytesRemaining = length;
            while (bytesRemaining > 0) {
                // Determine how many bytes to write in this iteration
                int bytesToWrite = (int) Math.min(Buffer.length, bytesRemaining);

                // Write the buffer to the file
                raf.write(Buffer, 0, bytesToWrite);

                // Update bytes remaining to write
                bytesRemaining -= bytesToWrite;
                
            }
            
            // Close the file access
            raf.close();
            
            // Delete the file
            if (!file.delete()) {
                throw new IOException("Failed to delete the file");
            }

        }
        catch(Exception e){
            
        }
    }
    
}

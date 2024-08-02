package com.Server.Utilities;

import java.io.File;

public class SizeofDirectory {
    
    public static long calc(File file) {
        long fileSize = 0L;
        if(file.isDirectory()) {
           File[] children = file.listFiles();
           for(File child : children) {
             fileSize += calc(child);
           }
        }
        else {
          fileSize = file.length();
        }
        return fileSize;
      }
}

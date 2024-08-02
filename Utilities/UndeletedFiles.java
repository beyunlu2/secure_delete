package com.Server.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class UndeletedFiles {
    private static HashMap<String, ArrayList<String>> undeletedFiles =  new HashMap<String, ArrayList<String>>();

    public static HashMap<String, ArrayList<String>> getUndeletedFiles() {
        return undeletedFiles;
    }

    public static void record(File file, String IP){
        String path = file.getPath();
        var paths = new ArrayList<String>();
        paths.add(path);
        if (undeletedFiles.putIfAbsent(IP, paths) != null){
            undeletedFiles.get(IP).add(path);
        }
        
    }
    
}

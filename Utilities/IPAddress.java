package com.Server.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Scope
public class IPAddress {
    private static HashMap<String, ArrayList<String>> Requests = new HashMap<>();
    private static boolean first = true;
    private static String ipAddress;

    public HashMap<String, ArrayList<String>> getRequests() {
        return Requests;
    }

    public static void setFirst(boolean first) {
		IPAddress.first = first;
	}
  
	public static void setPath(String path) {
		IPAddress.addPathtoRequests(path);
	}

	public static String get(HttpServletRequest request){
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        IPAddress.ipAddress = ipAddress;
        return ipAddress;
    }
	
	public static void addPathtoRequests(String path) {
		if(first) {
	        var paths = new ArrayList<String>();
	        paths.add(path);
	        Requests.put(ipAddress, paths);
	        System.out.println(ipAddress);
        }
        else {
        	ArrayList<String> newPaths = Requests.get(ipAddress);
        	newPaths.add(path);
        	Requests.put(ipAddress, newPaths);
        }
	}
    
}


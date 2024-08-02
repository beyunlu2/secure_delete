package com.Server.Controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Server.Services.EraseDirectory;
import com.Server.Utilities.IPAddress;
import com.Server.Utilities.HandleStates;
import com.Server.Utilities.HandleStates.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@ComponentScan(basePackages = "com.example.Utilities")
public class SecureErase {
	//Format variable "second" to two decimal points.
	final static DecimalFormat decimal = new DecimalFormat("0.00");

    @SuppressWarnings("unused")
	@PostMapping(value = "/secure_erase")
    public ResponseEntity<String> safeDelete(@RequestBody(required = false) String data, HttpServletRequest request) {
    	
    	//Create a new session or edit existing one.
        HttpSession session = request.getSession();
        State currentState = (State) session.getAttribute("currentState");
        String path = (String) session.getAttribute("path");
        String confirmationWord = (String) session.getAttribute("confirmationWord");
        Boolean first = (Boolean) session.getAttribute("first");
        
        if (first == null) {
            session.setAttribute("first", true);
        }
        if (currentState == null) {
            currentState = State.READY;
            session.setAttribute("currentState", currentState);
        }
        if (!(Boolean) session.getAttribute("first")){
            path = data;
            return Begin(path, request);
        }

        switch (currentState) {
            case READY:
                return HandleStates.Ready(data, session);

            case PATH_RECEIVED:
                return HandleStates.PathReceived(data, session);

            case CONFIRMED:
                return HandleStates.Confirmed(data, session, path, request);

            default:
                session.invalidate();
                return ResponseEntity.badRequest().body("Unexpected state.");
        }
        
    }

    public static ResponseEntity<String> Begin(String path, HttpServletRequest request){
    	
        // Get IP Address and register it.
        String IP = IPAddress.get(request);
        IPAddress.setPath(path);
        IPAddress.setFirst(false);

        //Begin erasure process.
        try {
        	File directory = new File(path);
            double startTime = System.nanoTime();
            EraseDirectory.run(directory, IP);
            double endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1000000000;
            
            return ResponseEntity.ok("File(s) securely erased in " + decimal.format(executionTime) +"s");
             
        } catch (FileNotFoundException nf){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                  .body("File Not Found: " + nf.getMessage());
        } catch (IOException e) {
            // could not delete error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("Internal Server Error: " + e.getMessage());
        }
        

    }
        
    
}

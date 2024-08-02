package com.Server.Utilities;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.Server.Controllers.SecureErase;

public class HandleStates {
	
	public static enum State {
        READY,       // Initial state: waiting for string
        PATH_RECEIVED,   // String received, waiting for confirmation text
        CONFIRMED          // Confirmation text received, ready to generate confirmation word
    }
	
	public static ResponseEntity<String> Ready(String data, HttpSession session) {
        if (data == null) {
            return ResponseEntity.badRequest().body("String is required in the request body.");
        }
        session.setAttribute("path", data);
        session.setAttribute("currentState", State.PATH_RECEIVED);
        return ResponseEntity.ok().body("Path received. Are you sure you want to delete this file(s)?");
    }

	public static ResponseEntity<String> PathReceived(String data, HttpSession session) {
        if (data == null) {
            return ResponseEntity.badRequest().body("Confirmation is required in the request body.");
        }
        if (data.equalsIgnoreCase("yes")) {
            String confirmationWord = Generator.ConfirmationWord();
            session.setAttribute("confirmationWord", confirmationWord);
            session.setAttribute("currentState", State.CONFIRMED);
            return ResponseEntity.ok().body("Confirmation received. Repeat the confirmation word: " + confirmationWord);
        }
        session.setAttribute("currentState", State.READY);
        return ResponseEntity.ok().body("Confirmation failed. Please send the path again.");
    }

	public static ResponseEntity<String> Confirmed(String data, HttpSession session, String path, HttpServletRequest request) {
        String confirmationWord = (String) session.getAttribute("confirmationWord");
        if (data == null) {
            return ResponseEntity.badRequest().body("Confirmation word is required in the request body.");
        }
        if (!data.equals(confirmationWord)) {
            return ResponseEntity.badRequest().body("Confirmation word does not match. Please try again: " + confirmationWord);
        }
        session.setAttribute("currentState", State.READY);
        session.setAttribute("first", false);
        
        return SecureErase.Begin(path, request);
    }

}

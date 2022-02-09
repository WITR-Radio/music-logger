package edu.rit.witr.musiclogger.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class EndpointUtility {

    public static ResponseEntity<?> badRequest(String message) {
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<?> ok(String message) {
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }

    public static ResponseEntity<?> ok(Map<String, String> json) {
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

}

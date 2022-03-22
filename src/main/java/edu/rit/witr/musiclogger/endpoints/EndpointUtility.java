package edu.rit.witr.musiclogger.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Basic utility methods to be used in endpoint controller classes.
 */
public class EndpointUtility {

    /**
     * Returns a {@link ResponseEntity} with a status of {@link HttpStatus#BAD_REQUEST} and a JSON body such that:
     * <pre>{"message": "xxx"}</pre>
     * Where {@code xxx} is the {@code message} parameter.
     *
     * @param message The message of the error
     * @return The {@link ResponseEntity} to be immediately returned
     */
    public static ResponseEntity<?> badRequest(String message) {
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.BAD_REQUEST);
    }

    /**
     * Returns a {@link ResponseEntity} with a status of {@link HttpStatus#OK} and a JSON body such that:
     * <pre>{"message": "xxx"}</pre>
     * Where {@code xxx} is the {@code message} parameter.
     *
     * @param message The message of the error
     * @return The {@link ResponseEntity} to be immediately returned
     */
    public static ResponseEntity<?> ok(String message) {
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
    }

    /**
     * Returns a {@link ResponseEntity} with a status of {@link HttpStatus#OK} and a JSON serialization of the
     * {@code json} parameter as the body.
     *
     * @return The {@link ResponseEntity} to be immediately returned
     */
    public static ResponseEntity<?> ok(Map<String, String> json) {
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

}

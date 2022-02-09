package edu.rit.witr.musiclogger.endpoints.tracks;

public interface Validatable {

    /**
     * Performs a validation to ensure all data in this object is usable.
     * @return true if the object is valid, false if otherwise
     */
    boolean validate();

}

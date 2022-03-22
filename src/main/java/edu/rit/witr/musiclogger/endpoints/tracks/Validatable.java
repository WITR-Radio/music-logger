package edu.rit.witr.musiclogger.endpoints.tracks;

/**
 * An interface for a piece of requested data that can be valid or not. For example, a track may implement this for a
 * controller class to validate if it contains correct data.
 */
public interface Validatable {

    /**
     * Performs a validation to ensure all data in this object is usable.
     *
     * @return true if the object is valid, false if otherwise
     */
    boolean validate();

}

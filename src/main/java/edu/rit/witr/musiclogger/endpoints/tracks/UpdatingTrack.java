package edu.rit.witr.musiclogger.endpoints.tracks;

/**
 * Used as a POST body for requests, this class represents a normal {@link AddedTrack} with an additional {@link #id}
 * field to update an existing track in the system with the given id.
 */
public class UpdatingTrack extends AddedTrack {

    private final Long id;

    UpdatingTrack(Long id, String title, String artist, String group, Long createdAt) {
        super(title, artist, group, createdAt);
        this.id = id;
    }

    @Override
    public boolean validate() {
        return super.validate() && id != null;
    }

    /**
     * Gets the ID of the track. This should be an existing ID already in the system.
     *
     * @return The ID
     */
    public Long getId() {
        return id;
    }
}

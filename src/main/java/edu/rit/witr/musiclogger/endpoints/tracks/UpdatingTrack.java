package edu.rit.witr.musiclogger.endpoints.tracks;

public class UpdatingTrack extends AddedTrack {

    private final Long id;

    UpdatingTrack(Long id, String title, String artist, String group, Long createdAt) {
        super(title, artist, group, createdAt);
        this.id = id;
    }

    @Override
    public boolean validate() {
        return id != null;
    }

    public Long getId() {
        return id;
    }
}

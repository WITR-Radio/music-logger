package edu.rit.witr.musiclogger.endpoints.tracks;

import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;

public class BroadcastTrack implements Validatable {
    private final String title;
    private final String artist;
    private final String group;

    public BroadcastTrack(String title, String artist, String group) {
        this.title = title;
        this.artist = artist;
        this.group = group;
    }

    @Override
    public boolean validate() {
        return title != null && artist != null && group != null;
    }

    /**
     * Converts this into a {@link Track} to add to the database. This assumes all data in this object is present
     * and valid.
     *
     * @param group The group to pair the {@link Track} with
     * @return The {@link Track}
     */
    public Track toTrack(@Nullable Group group) {
        return new Track(artist, title, new Timestamp(System.currentTimeMillis()), group);
    }

    /**
     * Gets the title of the track.
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the artist of the track.
     *
     * @return The artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the group name of the track.
     *
     * @return The group name
     */
    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "BroadcastTrack{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
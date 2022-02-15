package edu.rit.witr.musiclogger.endpoints.tracks;

import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.lang.Nullable;

import java.sql.Date;
import java.sql.Timestamp;

public class AddedTrack implements Validatable {
    private final String title;
    private final String artist;
    private final String group;
    private final Long time;

    AddedTrack(String title, String artist, String group, Long time) {
        this.title = title;
        this.artist = artist;
        this.group = group;
        this.time = time;
    }

    /**
     * Validates the currently added track to check for missing values.
     * @return true if all parameters are existent
     */
    @Override
    public boolean validate() {
        return title != null && artist != null && group != null && time != null;
    }

    /**
     * Converts this into a {@link Track} to add to the database. This assumes all data in this object is present
     * and valid.
     * @param group The group to pair the {@link Track} with
     * @return The {@link Track}
     */
    public Track toTrack(@Nullable Group group) {
        var createdDate = new Date(time);
        return new Track(artist, title, new Timestamp(time), false, group, createdDate, createdDate);
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGroup() {
        return group;
    }

    public Long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "AddedTrack{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", group='" + group + '\'' +
                ", time=" + time +
                '}';
    }
}

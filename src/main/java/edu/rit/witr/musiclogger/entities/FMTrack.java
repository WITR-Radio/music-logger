package edu.rit.witr.musiclogger.entities;

import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity(name = "tracks")
public class FMTrack extends Track {

    public FMTrack() {
        super();
    }

    public FMTrack(String artist, String title, @NonNull Timestamp time, Group group) {
        super(artist, title, time, group);
    }
}

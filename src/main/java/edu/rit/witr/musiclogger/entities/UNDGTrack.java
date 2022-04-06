package edu.rit.witr.musiclogger.entities;

import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity(name = "tracks_undg")
public class UNDGTrack extends Track {

    public UNDGTrack() {
        super();
    }

    public UNDGTrack(String artist, String title, @NonNull Timestamp time, Group group) {
        super(artist, title, time, group);
    }
}

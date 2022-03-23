package edu.rit.witr.musiclogger.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.rit.witr.musiclogger.entities.serializer.TrackSerializer;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

// TODO: Add more analyzers for improved searching?

/**
 * An individual track object, stored in the {@code tracks} table.
 */
@Entity
@Indexed
@Table(name = "tracks")
@JsonSerialize(using = TrackSerializer.class)
public class Track {

    @Id
    @GenericField(sortable = Sortable.YES)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @FullTextField(analyzer = "standard")
    private String artist;

    @FullTextField(analyzer = "standard")
    private String title;

    // TODO: In the current database this is nullable... why?
    @NonNull
    @GenericField(sortable = Sortable.YES)
    private Timestamp time;

    // TODO: For ManyToOne, I don't need a list of Tracks in Group, right?
    @ManyToOne(targetEntity = Group.class)
    @JoinColumn(name = "group_id")
    private Group group;

    // TODO: Original database had `queue_job_id` INT(16) but isn't used anywhere

    public Track() {}

    public Track(String artist, String title, @NonNull Timestamp time, Group group) {
        this.artist = artist;
        this.title = title;
        this.time = time;
        this.group = group;
    }

    public long getId() {
        return id;
    }

    public @Nullable String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public @Nullable String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public @NonNull Timestamp getTime() {
        return time;
    }

    public void setTime(@NonNull Timestamp time) {
        this.time = time;
    }

    public @Nullable Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", time=" + time +
                ", group=" + group +
                '}';
    }
}

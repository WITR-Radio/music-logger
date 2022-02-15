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

    private Boolean rivendell;

    // TODO: For ManyToOne, I don't need a list of Tracks in Group, right?
    @ManyToOne(targetEntity = Group.class)
    @JoinColumn(name = "group_id")
    private Group group;

    @NonNull
    @Column(name = "created_at", nullable = false)
    private java.sql.Date created;

    @NonNull
    @Column(name = "updated_at", nullable = false)
    private java.sql.Date updated;

    // TODO: Original database had `queue_job_id` INT(16) but isn't used anywhere

    public Track() {}

    public Track(String artist, String title, Timestamp time, boolean rivendell, Group group, @NonNull Date created, @NonNull Date updated) {
        this.artist = artist;
        this.title = title;
        this.time = time;
        this.rivendell = rivendell;
        this.group = group;
        this.created = created;
        this.updated = updated;
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

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Boolean isRivendell() {
        return rivendell;
    }

    public void setRivendell(Boolean rivendell) {
        this.rivendell = rivendell;
    }

    public @Nullable Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public @NonNull Date getCreated() {
        return created;
    }

    public void setCreated(@NonNull Date created) {
        this.created = created;
    }

    public @NonNull Date getUpdated() {
        return updated;
    }

    public void setUpdated(@NonNull Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", time=" + time +
                ", rivendell=" + rivendell +
                ", group=" + group +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}

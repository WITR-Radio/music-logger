package edu.rit.witr.musiclogger.entities;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.SortableField;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.sql.Timestamp;

// TODO: Add more analyzers for improved searching?

@Entity
@Indexed
@Analyzer(definition = "standard")
public class Track {

    @Id
    @SortableField
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Field
    private String artist;

    @Field
    private String title;

    @Field
    @SortableField
    private Timestamp time;

    private Boolean rivendell;

    // TODO: For ManyToOne, I don't need a list of Tracks in Group, right?
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @NonNull
    @Column(name = "created_at", nullable = false)
    private java.sql.Date created;

    @NonNull
    @Column(name = "updated_at", nullable = false)
    private java.sql.Date updated;

    private Boolean request;

    private String requester;

    // TODO: Original database had `queue_job_id` INT(16) but isn't used anywhere

    protected Track() {}

    public Track(String artist, String title, Timestamp time, boolean rivendell, Group group, @NonNull Date created, @NonNull Date updated, boolean request, String requester) {
        this.artist = artist;
        this.title = title;
        this.time = time;
        this.rivendell = rivendell;
        this.group = group;
        this.created = created;
        this.updated = updated;
        this.request = request;
        this.requester = requester;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
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

    public Group getGroup() {
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

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }
}

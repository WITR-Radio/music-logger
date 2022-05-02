package edu.rit.witr.musiclogger.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.rit.witr.musiclogger.entities.serializer.TrackSerializer;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// TODO: Add more analyzers for improved searching?

/**
 * An individual track object, stored in the {@code tracks} table.
 */
@Indexed
@MappedSuperclass
@JsonSerialize(using = TrackSerializer.class)
public abstract class Track {

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

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "streaming_links", nullable = true)
    private List<StreamingLink> streamingLinks;

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

    public Optional<List<StreamingLink>> getStreamingLinks() {
        return Optional.ofNullable(streamingLinks);
    }

    public void setStreamingLinks(List<StreamingLink> streamingLinks) {
        this.streamingLinks = streamingLinks;
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

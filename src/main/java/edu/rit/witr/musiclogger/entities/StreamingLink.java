package edu.rit.witr.musiclogger.entities;

import edu.rit.witr.musiclogger.streaming.Services;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "streaming_link")
@Table(name = "streaming", uniqueConstraints={
        @UniqueConstraint(columnNames = {"artist", "title", "service"})
})
public class StreamingLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String artist;

    private String title;

    @Enumerated(EnumType.STRING)
    private Services service;

    private String link;

    private String albumArt;

    public StreamingLink() {}

    public StreamingLink(String artist, String title, Services service, String link, String albumArt) {
        this.artist = artist;
        this.title = title;
        this.service = service;
        this.link = link;
        this.albumArt = albumArt;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Services getService() {
        return service;
    }

    public String getLink() {
        return link;
    }

    public String getAlbumArt() {
        return albumArt;
    }
}

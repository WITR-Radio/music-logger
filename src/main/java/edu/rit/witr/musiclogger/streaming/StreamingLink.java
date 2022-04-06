package edu.rit.witr.musiclogger.streaming;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
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

}

enum Services {
    SPOTIFY
}

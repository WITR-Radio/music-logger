package edu.rit.witr.musiclogger.tcp;

import edu.rit.witr.musiclogger.entities.FMTrack;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import edu.rit.witr.musiclogger.entities.UNDGTrack;
import org.springframework.lang.Nullable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.Timestamp;

@XmlType
@XmlRootElement(name = "nowplaying")
@XmlAccessorType(XmlAccessType.FIELD)
public class NowPlaying {

    @XmlElement(name = "sched_time")
    private long scheduledTime;

    @XmlElement(name = "air_time")
    private long airTime;

    // stack_pos?

    private String title;

    private String artist;

    private String trivia;

    private String category;

    private int cart;

    private int intro;

    // end?

    private String station;
    private int duration;

    @XmlElement(name = "media_type")
    private String mediaType;

    @XmlElement(name = "milliseconds_left")
    private long millisecondsLeft;

    public Track toTrack(@Nullable Group group) {
        if (!isUnderground()) {
            return new FMTrack(artist, title, new Timestamp(System.currentTimeMillis()), group);
        } else {
            return new UNDGTrack(artist, title, new Timestamp(System.currentTimeMillis()), group);
        }
    }

    public boolean isUnderground() {
        return station.equals("WITR-UDG");
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public long getAirTime() {
        return airTime;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getTrivia() {
        return trivia;
    }

    public String getCategory() {
        return category;
    }

    public int getCart() {
        return cart;
    }

    public int getIntro() {
        return intro;
    }

    public String getStation() {
        return station;
    }

    public int getDuration() {
        return duration;
    }

    public String getMediaType() {
        return mediaType;
    }

    public long getMillisecondsLeft() {
        return millisecondsLeft;
    }

    @Override
    public String toString() {
        return "NowPlaying{" +
                "scheduledTime=" + scheduledTime +
                ", airTime=" + airTime +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", trivia='" + trivia + '\'' +
                ", category='" + category + '\'' +
                ", cart=" + cart +
                ", intro=" + intro +
                ", station='" + station + '\'' +
                ", duration=" + duration +
                ", mediaType='" + mediaType + '\'' +
                ", millisecondsLeft=" + millisecondsLeft +
                '}';
    }
}

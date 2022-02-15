package edu.rit.witr.musiclogger.entities.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.rit.witr.musiclogger.entities.Track;

import java.io.IOException;

public class TrackSerializer extends StdSerializer<Track> {

    public TrackSerializer() {
        this(null);
    }

    protected TrackSerializer(Class<Track> t) {
        super(t);
    }

    @Override
    public void serialize(Track track, JsonGenerator json, SerializerProvider provider) throws IOException {
        json.writeStartObject();
        json.writeNumberField("id", track.getId());
        json.writeStringField("artist", track.getArtist());
        json.writeStringField("title", track.getTitle());

        var time = track.getTime();
        json.writeStringField("time", time != null ? time.toString() : null); // TODO: What date format?

        var group = track.getGroup();
        json.writeStringField("group", group != null ? group.getName() : null);

        json.writeEndObject();
    }
}

package edu.rit.witr.musiclogger.entities.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.rit.witr.musiclogger.entities.Track;

import java.io.IOException;
import java.util.Collections;

/**
 * The custom JSON serializer for {@link Track}. This has been implemented to allow for advanced customizability, such
 * as optional values or exclusion of fields.
 */
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
        json.writeStringField("time", track.getTime().toString());

        var group = track.getGroup();
        json.writeStringField("group", group != null ? group.getName() : null);

        json.writeArrayFieldStart("streaming");

        for (var link : track.getStreamingLinks().orElse(Collections.emptyList())) {
            json.writeStartObject();
            json.writeStringField("link", link.getLink());
            json.writeStringField("artwork", link.getAlbumArt());
            json.writeStringField("service", link.getService().name().toLowerCase());
            json.writeEndObject();
        }

        json.writeEndArray();

        json.writeEndObject();
    }
}

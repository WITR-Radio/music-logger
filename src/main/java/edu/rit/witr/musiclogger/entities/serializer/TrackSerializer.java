package edu.rit.witr.musiclogger.entities.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.database.repositories.RepositoryService;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Collections;

/**
 * The custom JSON serializer for {@link Track}. This has been implemented to allow for advanced customizability, such
 * as optional values or exclusion of fields.
 */
public class TrackSerializer extends StdSerializer<Track> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackSerializer.class);

    protected TrackSerializer() {
        this(null);
    }

    public TrackSerializer(Class<Track> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(Track track, JsonGenerator json, SerializerProvider provider) throws IOException {
        json.writeStartObject();
        json.writeNumberField("id", track.getId());
        json.writeStringField("artist", track.getArtist());
        json.writeStringField("title", track.getTitle());
        json.writeStringField("time", track.getTime().toString());

        var group = track.getGroup();
        json.writeStringField("group", convertToValidName(group));

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

    /**
     * If the given {@link Group} is not null and is valid, return its name. Otherwise, return "Music".
     *
     * @param group The group to check
     * @return The valid group name
     */
    private String convertToValidName(@Nullable Group group) {
        if (group != null && GroupRepository.isValidGroup(group)) {
            return group.getName();
        }

        return "Music";
    }
}

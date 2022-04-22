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

import java.io.IOException;
import java.util.Collections;

/**
 * The custom JSON serializer for {@link Track}. This has been implemented to allow for advanced customizability, such
 * as optional values or exclusion of fields.
 */
public class TrackSerializer extends StdSerializer<Track> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackSerializer.class);

    private Group defaultGroup = null;

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
        json.writeStringField("group", group != null ? convertToValid(group).getName() : null);

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

    private Group convertToValid(Group group) {
        if (!GroupRepository.isValidGroup(group)) {
            if (defaultGroup == null) {
                var found = RepositoryService.getRepo(GroupRepository.class).findByName("Music");;
                defaultGroup = found;
                return defaultGroup;
            }

            return defaultGroup;
        }

        return group;
    }
}

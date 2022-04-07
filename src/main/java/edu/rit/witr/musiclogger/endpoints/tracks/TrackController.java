package edu.rit.witr.musiclogger.endpoints.tracks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.rit.witr.musiclogger.broadcast.BroadcastService;
import edu.rit.witr.musiclogger.database.SearchingService;
import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.database.repositories.TrackRepository;
import edu.rit.witr.musiclogger.database.repositories.TrackUpdater;
import edu.rit.witr.musiclogger.endpoints.EndpointUtility;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import edu.rit.witr.musiclogger.streaming.StreamingManager;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The controller for all /track/ endpoints.
 * This relates to everything dealing with tracks themselves, e.g. listing, adding, updating, etc.
 */
@RestController
public class TrackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private final SearchingService searchingService;
    private final TrackRepository trackRepository;
    private final GroupRepository groupRepository;
    private final TrackUpdater trackUpdater;
    private final ObjectMapper mapper;
    private final BroadcastService broadcastService;
    private final StreamingManager streamingManager;

    public TrackController(@Autowired SearchingService searchingService,
                           @Autowired TrackRepository trackRepository,
                           @Autowired GroupRepository groupRepository,
                           @Autowired TrackUpdater trackUpdater,
                           ObjectMapper mapper,
                           @Autowired BroadcastService broadcastService,
                           @Autowired StreamingManager streamingManager) {
        this.searchingService = searchingService;
        this.trackRepository = trackRepository;
        this.groupRepository = groupRepository;
        this.trackUpdater = trackUpdater;
        this.mapper = mapper;
        this.broadcastService = broadcastService;
        this.streamingManager = streamingManager;
    }

    @GetMapping("/api/tracks/list")
    public ResponseEntity<ObjectNode> listTracks(HttpServletRequest request,
                                        @RequestParam(required = false) String song,
                                        @RequestParam(required = false) String artist,
                                        @RequestParam(required = false) Long start,
                                        @RequestParam(required = false) Long end,
                                        @RequestParam(defaultValue = "20") int count,
                                        @RequestParam(defaultValue = "0") int offset,
                                        @RequestParam(defaultValue = "false") boolean underground)
            throws InterruptedException {
        var tracks = searchingService.findAllBy(song, artist, start, end, offset, count, underground);
        streamingManager.applyLinks(tracks).join(); // TODO: PROPER ASYNC!!
        var node = constructTrackObject(request, tracks, count);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @PostMapping("/api/tracks/add")
    public ResponseEntity<?> addTrack(@RequestBody AddedTrack adding, @RequestParam(defaultValue = "false") boolean underground) {
        LOGGER.info("Adding: {}", adding);

        if (!adding.validate()) {
            return EndpointUtility.badRequest("title, artist, group, and time parameters must all be non-null");
        }

        if (adding.getTime() <= 0) {
            return EndpointUtility.badRequest("created_at must be a positive integer");
        }

        var groupOptional = findGroup(adding.getGroup());
        if (groupOptional.isEmpty()) {
            return EndpointUtility.badRequest("Invalid group");
        }

        var track = adding.toTrack(groupOptional.get(), underground);
        trackRepository.save(track, underground);

        return new ResponseEntity<>(track, HttpStatus.OK);
    }

    @DeleteMapping("/api/tracks/delete")
    public ResponseEntity<?> removeTrack(@RequestParam long id, @RequestParam(defaultValue = "false") boolean underground) {
        LOGGER.info("Deleting track {}", id);
        trackRepository.deleteById(id, underground);
        return EndpointUtility.ok("Deleted track");
    }

    @PatchMapping("/api/tracks/update")
    public ResponseEntity<?> updateTrack(@RequestBody UpdatingTrack updating, @RequestParam(defaultValue = "false") boolean underground) {
        LOGGER.info("Updating: {}", updating);

        if (!updating.validate()) {
            return EndpointUtility.badRequest("id must be non-null");
        }

        if (updating.getTime() != null && updating.getTime() <= 0) {
            return EndpointUtility.badRequest("time must be a positive integer");
        }

        Group group = null;
        if (updating.getGroup() != null) {
            var groupOptional = findGroup(updating.getGroup());
            if (groupOptional.isEmpty()) {
                return EndpointUtility.badRequest("Invalid group");
            }

            group = groupOptional.get();
        }

        Timestamp timestamp = updating.getTime() == null ? null : new Timestamp(updating.getTime());

        trackUpdater.updateTrack(updating.getId(), updating.getTitle(), updating.getArtist(), group, timestamp);
        return new ResponseEntity<>(trackRepository.findById(updating.getId(), underground), HttpStatus.OK);
    }

    @PostMapping("/api/tracks/broadcast")
    @Async
    public CompletableFuture<ResponseEntity<?>> broadcastTrack(@RequestBody BroadcastTrack broadcasting, @RequestParam(defaultValue = "false") boolean underground) {
        LOGGER.info("Broadcasting: {}", broadcasting);

        if (!broadcasting.validate()) {
            return CompletableFuture.completedFuture(EndpointUtility.badRequest("title, artist and group parameters must all be non-null"));
        }

        var groupOptional = findGroup(broadcasting.getGroup());
        if (groupOptional.isEmpty()) {
            return CompletableFuture.completedFuture(EndpointUtility.badRequest("Invalid group"));
        }

        var track = broadcasting.toTrack(groupOptional.get(), underground);
        trackRepository.save(track, underground);

        // TODO: Properly handle async?
        return broadcastService.broadcastTrack(broadcasting, underground)
                .thenApply($ -> EndpointUtility.ok(Map.of("message", "ok")));
    }

    /**
     * Looks through the database and finds the {@link Group} that's name matches with the given string (case
     * -insensitive).
     *
     * @param groupName The group name to look up
     * @return The {@link Group}, if found
     */
    private Optional<Group> findGroup(String groupName) {
        return groupRepository.findAll()
                .stream()
                .filter(group -> group.getName().equalsIgnoreCase(groupName))
                .findFirst();
    }

    /**
     * Constructs the JSON object to send back to the client containing a list of tracks and an additional
     * <code>_links</code> object. An example of this is:
     * <pre>
     * {
     *     "tracks": [
     *         {
     *             "id": 626,
     *             "artist": "Artist 99",
     *             "title": "Your Girlfriend",
     *             "time": "2022-04-10 01:00:00.0",
     *             "group": "Feature"
     *         },
     *         // ...
     *     ],
     *     "_links": {
     *         "next": "http://localhost:8080/tracks/list?offset=20"
     *     }
     * }
     * </pre>
     *
     * @param request The original {@link HttpServletRequest}
     * @param tracks The {@link Track}s to list
     * @param count The amount of tracks requested originally
     * @return The JSON {@link ObjectNode} to be serialized and sent
     */
    private ObjectNode constructTrackObject(HttpServletRequest request, List<Track> tracks, int count) {
        var node = mapper.createObjectNode();
        var trackArray = node.putArray("tracks");
        tracks.forEach(trackArray::addPOJO);

        var query = new HashMap<>(URLEncodedUtils.parse(request.getQueryString(), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue)));

        if (!tracks.isEmpty()) {
            var currAfter = Integer.parseInt(query.getOrDefault("offset", "0"));
            query.put("offset", String.valueOf(currAfter + count));
        }

        var queryString = query.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        if (!queryString.isEmpty()) {
            queryString = "?" + queryString;
        }

        node.putObject("_links")
                .put("next", System.getenv("INDEX_URL") + "/api/tracks/list" + queryString);
        return node;
    }
}

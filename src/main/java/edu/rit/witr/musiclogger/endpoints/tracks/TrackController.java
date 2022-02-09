package edu.rit.witr.musiclogger.endpoints.tracks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.rit.witr.musiclogger.database.SearchingService;
import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.database.repositories.TrackRepository;
import edu.rit.witr.musiclogger.database.repositories.TrackUpdater;
import edu.rit.witr.musiclogger.endpoints.EndpointUtility;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
public class TrackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private final SearchingService searchingService;
    private final TrackRepository trackRepository;
    private final GroupRepository groupRepository;
    private final TrackUpdater trackUpdater;
    private final ObjectMapper mapper;

    public TrackController(@Autowired SearchingService searchingService, @Autowired TrackRepository trackRepository, @Autowired GroupRepository groupRepository, @Autowired TrackUpdater trackUpdater, ObjectMapper mapper) {
        this.searchingService = searchingService;
        this.trackRepository = trackRepository;
        this.groupRepository = groupRepository;
        this.trackUpdater = trackUpdater;
        this.mapper = mapper;
    }

    @GetMapping("/tracks/list")
    public ResponseEntity<ObjectNode> listTracks(HttpServletRequest request,
                                        @RequestParam(required = false) String song,
                                        @RequestParam(required = false) String artist,
                                        @RequestParam(required = false) Long start,
                                        @RequestParam(required = false) Long end,
                                        @RequestParam(defaultValue = "20") int count,
                                        @RequestParam(required = false) Long after,
                                        @RequestParam(defaultValue = "false") boolean underground)
            throws InterruptedException {
        var tracks = searchingService.findAllBy(song, artist, start, end, after, count, underground);
        var node = constructTrackObject(request, tracks, count);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @PostMapping("/tracks/add")
    public ResponseEntity<?> addTrack(@RequestBody AddedTrack adding, @RequestParam boolean underground) {
        LOGGER.info("Adding: {}", adding);

        if (!adding.validate()) {
            return EndpointUtility.badRequest("title, artist, group, and time parameters must all be non-null");
        }

        if (adding.getTime() <= 0) {
            return EndpointUtility.badRequest("created_at must be a positive integer");
        }

        var groupOptional = findGroup(adding);
        if (groupOptional.isEmpty()) {
            return EndpointUtility.badRequest("Invalid group");
        }

        var track = adding.toTrack(groupOptional.get());
        trackRepository.save(track);

        return new ResponseEntity<>(track, HttpStatus.OK);
    }

    @DeleteMapping("/tracks/delete")
    public ResponseEntity<?> removeTrack(@RequestParam long id, @RequestParam(defaultValue = "false") boolean underground) {
        LOGGER.info("Deleting track {}", id);
        trackRepository.deleteById(id);
        return EndpointUtility.ok("Deleted track");
    }

    @PatchMapping("/tracks/update")
    public ResponseEntity<?> updateTrack(@RequestBody UpdatingTrack updating) {
        LOGGER.info("Updating: {}", updating);

        if (!updating.validate()) {
            return EndpointUtility.badRequest("id must be non-null");
        }

        if (updating.getTime() != null && updating.getTime() <= 0) {
            return EndpointUtility.badRequest("time must be a positive integer");
        }

        Group group = null;
        if (updating.getGroup() != null) {
            var groupOptional = findGroup(updating);
            if (groupOptional.isEmpty()) {
                return EndpointUtility.badRequest("Invalid group");
            }

            group = groupOptional.get();
        }

        Timestamp timestamp = updating.getTime() == null ? null : new Timestamp(updating.getTime());

        trackUpdater.updateTrack(updating.getId(), updating.getTitle(), updating.getArtist(), group, timestamp);
        return new ResponseEntity<>(trackRepository.findById(updating.getId()), HttpStatus.OK);
    }

    private Optional<Group> findGroup(AddedTrack track) {
        return groupRepository.findAll()
                .stream()
                .filter(group -> group.getName().equalsIgnoreCase(track.getGroup()))
                .findFirst();
    }

    private ObjectNode constructTrackObject(HttpServletRequest request, List<Track> tracks, int count) {
        var node = mapper.createObjectNode();
        var trackArray = node.putArray("tracks");
        tracks.forEach(trackArray::addPOJO);
        var next = request.getRequestURL()
                .append("?count=")
                .append(count);

        if (!tracks.isEmpty()) {
            next.append("&after=").append(tracks.get(tracks.size() - 1).getTime().getTime());
        }

        node.putObject("_links")
                .put("next", next.toString());
        return node;
    }
}

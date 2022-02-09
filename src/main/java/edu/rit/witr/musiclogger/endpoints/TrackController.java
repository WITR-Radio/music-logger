package edu.rit.witr.musiclogger.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.rit.witr.musiclogger.database.SearchingService;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class TrackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private final SearchingService searchingService;
    private final ObjectMapper mapper;

    public TrackController(@Autowired SearchingService searchingService, ObjectMapper mapper) {
        this.searchingService = searchingService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/tracks/list")
    public ResponseEntity<?> listGroups(HttpServletRequest request,
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

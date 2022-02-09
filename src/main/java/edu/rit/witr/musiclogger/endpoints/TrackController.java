package edu.rit.witr.musiclogger.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rit.witr.musiclogger.database.SearchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TrackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private final SearchingService searchingService;
    private final ObjectMapper mapper;

    public TrackController(@Autowired SearchingService searchingService, ObjectMapper mapper) {
        this.searchingService = searchingService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/tracks/search")
    public ResponseEntity<?> listGroups(HttpServletRequest request,
                                        @RequestParam(defaultValue = "20") int count,
                                        @RequestParam(required = false) Long after,
                                        @RequestParam(defaultValue = "false", required = false) boolean underground)
            throws InterruptedException {
        var tracks = searchingService.findAllBy(null, null, null, null, after, count, underground);
        var node = mapper.createObjectNode();
        var trackArray = node.putArray("tracks");
        tracks.forEach(trackArray::addPOJO);
        node.putObject("_links")
                .put("next", request.getRequestURL()
                        .append("?count=")
                        .append(count)
                        .append("&after=")
                        .append(tracks.get(tracks.size() - 1).getTime().getTime())
                        .toString());
        return new ResponseEntity<>(node, HttpStatus.OK);
    }
}

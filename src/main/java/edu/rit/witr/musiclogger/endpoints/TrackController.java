package edu.rit.witr.musiclogger.endpoints;

import edu.rit.witr.musiclogger.database.SearchingService;
import edu.rit.witr.musiclogger.database.TrackRepository;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class TrackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private final SearchingService searchingService;
    private final TrackRepository repository;

    public TrackController(@Autowired SearchingService searchingService, TrackRepository repository) {
        this.searchingService = searchingService;
        this.repository = repository;
    }

    @GetMapping("/tracks/list")
    List<Track> listGroups(@RequestParam int count) throws InterruptedException {
        LOGGER.info("listing {} results", count);
//        return searchingService.findAllBy(null, null, null, Timestamp.valueOf("2022-2-12 18:00:00"), null, count, false);
        return searchingService.findAllBy(null, null, Timestamp.valueOf("2022-02-22 18:00:00"), null, null, count, false);
    }
}

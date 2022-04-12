package edu.rit.witr.musiclogger.streaming;

import edu.rit.witr.musiclogger.entities.StreamingLink;
import edu.rit.witr.musiclogger.entities.Track;
import edu.rit.witr.musiclogger.streaming.spotify.SpotifyService;
import edu.rit.witr.musiclogger.streaming.spotify.StreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BasicStreamingManager implements StreamingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicStreamingManager.class);

    private final List<StreamingService> streamingServices;

    public BasicStreamingManager(@Autowired SpotifyService spotifyService) {
        this.streamingServices = List.of(spotifyService);

        LOGGER.info("Streaming services loaded: {}", streamingServices.stream().map(StreamingService::getName).collect(Collectors.joining(", ")));
    }

    @Override
    public CompletableFuture<Void> applyLinks(List<Track> tracks) {
        LOGGER.info("Applying {}", tracks);
        // Check if every track has a non-null streaming link list. If not, populate it
        return CompletableFuture.allOf(tracks.stream()
                .collect(Collectors.groupingBy(track -> new TrackArtistPair(track.getTitle(), track.getArtist())))
                .entrySet().stream()
                .filter(entry -> {
                    var links = entry.getValue().get(0).getStreamingLinks();
                    LOGGER.info("({}) links = {}", links.isEmpty(), links);
                    return links.map(List::isEmpty).orElse(true);
                })
                .map(entry -> {
                    var pair = entry.getKey();
                    var title = pair.track();
                    var artist = pair.artist();

                    LOGGER.info("Applying for {}", title);
                    var futures = (CompletableFuture<Optional<StreamingLink>>[]) streamingServices.stream()
                            .map(serv -> serv.getStreamingLink(title, artist))
                            .<CompletableFuture<Optional<StreamingLink>>>toArray(CompletableFuture[]::new);

                    return CompletableFuture.allOf(futures)
                            .thenApply($ -> Arrays.stream(futures)
                                    .map(CompletableFuture::join)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .toList())
                            .thenAccept(links -> entry.getValue()
                                    .forEach(track -> track.setStreamingLinks(links))); // Is thread safety a concern here, given the context?
                }).toArray(CompletableFuture[]::new));
    }

    record TrackArtistPair(String track, String artist) {}
}

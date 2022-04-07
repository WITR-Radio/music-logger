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
        // Check if every track has a non-null streaming link list. If not, populate it
        return CompletableFuture.allOf(tracks.stream()
                .filter(track -> track.getStreamingLinks().isEmpty())
                .map(track -> {
                    var futures = streamingServices.stream()
                            .map(serv -> serv.getStreamingLink(track.getTitle(), track.getArtist()))
                            .<CompletableFuture<Optional<StreamingLink>>>toArray(CompletableFuture[]::new);

                    return CompletableFuture.allOf(futures)
                            .thenApply($ -> Arrays.stream(futures)
                                    .map(CompletableFuture::join)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .toList())
                            .thenAccept(track::setStreamingLinks); // Is thread safety a concern here, given the context?
                }).toArray(CompletableFuture[]::new));
    }
}

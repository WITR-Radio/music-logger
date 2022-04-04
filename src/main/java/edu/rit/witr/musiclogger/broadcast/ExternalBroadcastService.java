package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Broadcasts tracks to the following services:
 * <ul>
 *     <li>RDS Encoder</li>
 *     <li>Icecast</li>
 *     <li>TuneIn</li>
 * </ul>
 */
@Service
public class ExternalBroadcastService implements BroadcastService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalBroadcastService.class);

    private final List<Broadcaster> broadcasters;

    /**
     * Creates an {@link ExternalBroadcastService} and creates the internal {@link Broadcaster}s. This is an intensive
     * constructor, that should be replaced with a factory. This should only be invoked by Spring though, so it is not
     * a priority.
     */
    public ExternalBroadcastService() {
        this.broadcasters = Stream.of(RDSBroadcaster.create(), IcecastBroadcaster.create(), TuneInBroadcaster.create())
                .filter(Optional::isPresent)
                .<Broadcaster>map(Optional::get)
                .toList();

        LOGGER.info("Broadcasters loaded: {}", broadcasters.stream().map(Broadcaster::getName).collect(Collectors.joining(", ")));
    }

    @Override
    public CompletableFuture<Void> broadcastTrack(BroadcastTrack track, boolean underground) {
        return CompletableFuture.allOf(broadcasters.stream()
                        .filter(broadcaster -> {
                            if (broadcaster.restrictToFM()) {
                                return !underground;
                            }

                            return true;
                        })
                .map(broadcaster -> broadcaster.broadcast(track, underground))
                .toArray(CompletableFuture[]::new));
    }
}

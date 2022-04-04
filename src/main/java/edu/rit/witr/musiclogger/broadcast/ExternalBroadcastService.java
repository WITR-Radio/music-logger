package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    private final List<Broadcaster> broadcasters;

    public ExternalBroadcastService() {
        this.broadcasters = List.of(new RDSBroadcaster(), new IcecastBroadcaster(), new TuneInBroadcaster());
    }

    @Override
    public CompletableFuture<Void> broadcastTrack(BroadcastTrack track) {
        return CompletableFuture.allOf(broadcasters.stream()
                .map(broadcaster -> broadcaster.broadcast(track))
                .toArray(CompletableFuture[]::new));
    }
}

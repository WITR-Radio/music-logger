package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RDSBroadcaster implements Broadcaster {

    public static Optional<RDSBroadcaster> create() {
        return Optional.empty();
    }

    @Override
    public CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track, boolean underground) {
        return null;
    }

    @Override
    public String getName() {
        return "RDS";
    }
}

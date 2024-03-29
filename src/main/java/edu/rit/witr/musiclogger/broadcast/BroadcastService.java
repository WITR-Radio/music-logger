package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;

import java.util.concurrent.CompletableFuture;

/**
 * A service to handle broadcasting of tracks to multiple external services.
 */
public interface BroadcastService {

    /**
     * Handles the broadcasting of a given track. If one of multiple broadcasts are unsuccessful, do not halt
     * broadcasting. If multiple broadcasters are present, order should not matter. If an internal {@link Broadcaster}
     * is restricted to FM and <code>underground</code> is <code>true</code>, the broadcaster will be skipped.
     *
     * @param track The track to broadcast
     * @param underground If the broadcasted track is on underground
     * @return The {@link CompletableFuture} for all combined broadcasters
     */
    CompletableFuture<Void> broadcastTrack(BroadcastTrack track, boolean underground);

}

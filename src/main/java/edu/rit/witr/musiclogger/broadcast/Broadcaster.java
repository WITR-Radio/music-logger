package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import org.springframework.lang.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * An interface for broadcasting a now-playing track to a specific third-party service.
 */
public interface Broadcaster {

    /**
     * Broadcasts the given track to the implementation-specific service.
     *
     * @param track The track to broadcast
     * @return The status of the broadcast
     */
    CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track);

    /**
     * Gets the name of the third-party service this is being broadcasted to.
     *
     * @return The name
     */
    String getName();

    /*
    Icecast updating
    RDS
    tunein
     */

    /**
     * The response of a broadcast request.
     *
     * @param success If the broadcast is successful
     * @param error Non-null if `success` is true, this contains any information that may be useful for
     *              debugging/showing why the error occurred.
     */
    record BroadcastStatus(boolean success, @Nullable String error) {}

}

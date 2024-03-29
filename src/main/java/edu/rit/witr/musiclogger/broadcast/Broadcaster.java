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
     * @param underground
     * @return The status of the broadcast
     */
    CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track, boolean underground);

    /**
     * Gets the name of the third-party service this is being broadcasted to.
     *
     * @return The name
     */
    String getName();

    /**
     * Returns if this broadcaster should be restricted to the FM station (true), or if it may be used by Underground
     * as well (false).
     *
     * @return If this broadcaster is restricted to the FM stream
     */
    boolean restrictToFM();

    /**
     * The response of a broadcast request.
     *
     * @param success If the broadcast is successful
     * @param error Non-null if `success` is true, this contains a short message of why the error occurred. Generally,
     *              more information should be available in the error console (e.g. stack traces, params, etc.)
     */
    record BroadcastStatus(boolean success, @Nullable String error) {

        /**
         * An erroneous status with a given {@link #error}.
         *
         * @param error The error message
         */
        BroadcastStatus(String error) {
            this(false, error);
        }

        /**
         * A successful status with a null {@link #error}.
         */
        BroadcastStatus() {
            this(true, null);
        }
    }

}

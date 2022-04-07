package edu.rit.witr.musiclogger.streaming;

import edu.rit.witr.musiclogger.entities.StreamingLink;
import edu.rit.witr.musiclogger.entities.Track;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A service to manage all the individual {@link edu.rit.witr.musiclogger.streaming.spotify.StreamingService}s.
 */
public interface StreamingManager {

    /**
     * Fetches all {@link StreamingLink}s for each individual {@link Track} and invokes
     * {@link Track#setStreamingLinks(List)} on them to override any previous value.
     *
     * @param tracks The tracks to set the links to
     * @return The {@link CompletableFuture} for the task
     */
    CompletableFuture<Void> applyLinks(List<Track> tracks);

}

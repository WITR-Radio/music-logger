package edu.rit.witr.musiclogger.spotify;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A streaming service that should provide a playing link for a given song.
 */
public interface StreamingService {

    /**
     * Attempts to fetch the streaming link for a given track and artist. This is cached in the database.
     *
     * @param track The track name
     * @param artist The artist name
     * @return The link to stream, if present
     */
    CompletableFuture<Optional<String>> getStreamingLink(String track, String artist);

}

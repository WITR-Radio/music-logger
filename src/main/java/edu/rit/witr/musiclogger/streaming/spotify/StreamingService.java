package edu.rit.witr.musiclogger.streaming.spotify;

import edu.rit.witr.musiclogger.entities.StreamingLink;
import edu.rit.witr.musiclogger.streaming.Services;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A streaming service that should provide a playing link for a given song.
 *
 * When a track is added to the system, {@link #getStreamingLink(String, String)} is immediately invoked, and its
 * result is saved to a database. When the track is requested in a list,
 */
public interface StreamingService {

    /**
     * Attempts to fetch the streaming link for a given track and artist. This is cached in the database.
     *
     * @param track The track name
     * @param artist The artist name
     * @return The link to stream, if present
     */
    CompletableFuture<Optional<StreamingLink>> getStreamingLink(String track, String artist);

    /**
     * Gets the name of the service (e.g. Spotify, Apple Music). By default, this invokes {@link #getService()} and
     * returns {@link Services#getName()}.
     *
     * @return The name of the service
     */
    default String getName() {
        return getService().getName();
    }

    /**
     * Gets the {@link Services} that this class represents.
     *
     * @return The {@link Services}
     */
    Services getService();

}

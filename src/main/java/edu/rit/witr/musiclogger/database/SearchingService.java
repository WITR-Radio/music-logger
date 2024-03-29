package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * A service to search the database.
 * TODO: Should this be explicitly async?
 */
public interface SearchingService {

    /**
     * Searches tracks by optional parameters. This has the same options as the previous logger.
     *
     * @param song        The song
     * @param artist      The artist
     * @param afterTime   Show results after this date/time
     * @param beforeTime  Restrict results to before this date/time
     * @param offset      The number of results to skip, for paging
     * @param count       The amount of results to return
     * @param underground If this should search underground data
     * @return The found tracks
     * @throws InterruptedException
     */
    List<Track> findAllBy(@Nullable String song,
                          @Nullable String artist,
                          @Nullable Long afterTime,
                          @Nullable Long beforeTime,
                          int offset,
                          int count,
                          boolean underground) throws InterruptedException;
}

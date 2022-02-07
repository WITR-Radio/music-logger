package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

public interface SearchingService {

    /**
     * Searches tracks by optional parameters. This has the same options as the previous logger.
     *
     * @param song        The song
     * @param artist      The artist
     * @param start       Show results after this date
     * @param end         Restrict results to before this date
     * @param after       The timestamp to list after, used for paginating
     * @param count       The amount of results to return
     * @param underground If this should search underground data
     * @return The found tracks
     * @throws InterruptedException
     */
    List<Track> findAllBy(@Nullable String song,
                          @Nullable String artist,
                          @Nullable Timestamp start,
                          @Nullable Timestamp end,
                          @Nullable Long after,
                          int count,
                          boolean underground) throws InterruptedException;
}

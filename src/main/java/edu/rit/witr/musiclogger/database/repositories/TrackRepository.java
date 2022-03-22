package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.List;

/**
 * The {@link CrudRepository} for the {@code tracks} table.
 */
public interface TrackRepository extends CrudRepository<Track, Long> {

    /**
     * Fetches all {@link Track}s by invoking {@link #findAllByOrderByIdDesc()}.
     *
     * @return All {@link Track}s
     */
    @NonNull
    default List<Track> findAll() {
        return findAllByOrderByIdDesc();
    }

    /**
     * Fetches all {@link Track}s by invoking {@link #findAllByOrderByIdDesc(Pageable)}, with {@link Pageable} to
     * fine-tune results.
     *
     * @param pageable The {@link Pageable} for the results
     * @return Fetched {@link Track}s
     */
    @NonNull
    default List<Track> findAll(Pageable pageable) {
        return findAllByOrderByIdDesc(pageable);
    }

    /**
     * Fetches all {@link Track}s ordered by their descending ID.
     *
     * @return Fetched {@link Track}s
     */
    List<Track> findAllByOrderByIdDesc();

    /**
     * Fetches all {@link Track}s ordered by their descending ID, with {@link Pageable} for fine-tuning results.
     *
     * @param pageable The {@link Pageable} for the results
     * @return Fetched {@link Track}s
     */
    List<Track> findAllByOrderByIdDesc(Pageable pageable);

    /**
     * Fetches all {@link Track}s between the given {@link Timestamp}s.
     *
     * @param start The starting {@link Timestamp}
     * @param end The ending {@link Timestamp}
     * @return Fetched {@link Track}s
     */
    List<Track> findAllByTimeBetween(Timestamp start, Timestamp end);

    /**
     * Fetches all {@link Track}s between the given {@link Timestamp}s, with {@link Pageable} for fine-tuning results.
     *
     * @param start The starting {@link Timestamp}
     * @param end The ending {@link Timestamp}
     * @param pageable The {@link Pageable} for the results
     * @return Fetched {@link Track}s
     */
    List<Track> findAllByTimeBetween(Timestamp start, Timestamp end, Pageable pageable);

}

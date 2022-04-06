package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * The {@link CrudRepository} for the {@code tracks} and {@code tracks_undg} tables. This is separated because in order
 * to have two separate tables, separate entities must be made. This is the best attempt at unifying the repositories.
 */
interface VariantTrackRepository<T extends Track> {

    /**
     * Fetches all {@link Track}s by invoking {@link #findAllByOrderByIdDesc()}.
     *
     * @return All {@link Track}s
     */
    @NonNull
    default List<T> findAll() {
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
    default List<T> findAll(Pageable pageable) {
        return findAllByOrderByIdDesc(pageable);
    }

    /**
     * Fetches all {@link Track}s ordered by their descending ID.
     *
     * @return Fetched {@link Track}s
     */
    List<T> findAllByOrderByIdDesc();

    /**
     * Fetches all {@link Track}s ordered by their descending ID, with {@link Pageable} for fine-tuning results.
     *
     * @param pageable The {@link Pageable} for the results
     * @return Fetched {@link Track}s
     */
    List<T> findAllByOrderByIdDesc(Pageable pageable);

    /**
     * Fetches all {@link Track}s between the given {@link Timestamp}s.
     *
     * @param start The starting {@link Timestamp}
     * @param end The ending {@link Timestamp}
     * @return Fetched {@link Track}s
     */
    List<T> findAllByTimeBetween(Timestamp start, Timestamp end);

    /**
     * Fetches all {@link Track}s between the given {@link Timestamp}s, with {@link Pageable} for fine-tuning results.
     *
     * @param start The starting {@link Timestamp}
     * @param end The ending {@link Timestamp}
     * @param pageable The {@link Pageable} for the results
     * @return Fetched {@link Track}s
     */
    List<T> findAllByTimeBetween(Timestamp start, Timestamp end, Pageable pageable);

    /**
     * Deletes a track by its ID.
     *
     * @param id The ID of the track to delete
     */
    void deleteById(Long id);

    /**
     * Finds a track, if any, by the given ID.
     *
     * @param id The ID to search for
     * @return The track, if any, found
     */
    Optional<T> findById(Long id);

}

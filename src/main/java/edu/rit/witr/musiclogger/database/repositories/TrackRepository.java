package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.List;

public interface TrackRepository extends CrudRepository<Track, Long> {

    @NonNull
    default List<Track> findAll() {
        return findAllByOrderByIdDesc();
    }

    @NonNull
    default List<Track> findAll(Pageable pageable) {
        return findAllByOrderByIdDesc(pageable);
    }

    List<Track> findAllByOrderByIdDesc();

    List<Track> findAllByOrderByIdDesc(Pageable pageable);

    List<Track> findAllByTimeBetween(Timestamp start, Timestamp end);

    List<Track> findAllByTimeBetween(Timestamp start, Timestamp end, Pageable pageable);

}

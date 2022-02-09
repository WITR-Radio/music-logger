package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TrackRepository extends CrudRepository<Track, Long> {

    @NonNull
    default List<Track> findAll() {
        return findAllByOrderByIdDesc();
    }

    List<Track> findAllByOrderByIdDesc();

}

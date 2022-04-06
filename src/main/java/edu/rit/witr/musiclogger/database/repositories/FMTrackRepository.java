package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.FMTrack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FMTrackRepository extends VariantTrackRepository<FMTrack>, CrudRepository<FMTrack, Long> {

    @NonNull
    @Override
    default List<FMTrack> findAll() {
        return VariantTrackRepository.super.findAll();
    }

    @NonNull
    @Override
    default List<FMTrack> findAll(Pageable pageable) {
        return VariantTrackRepository.super.findAll(pageable);
    }
}

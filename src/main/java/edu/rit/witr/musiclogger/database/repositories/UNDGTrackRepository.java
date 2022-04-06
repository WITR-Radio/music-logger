package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.UNDGTrack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UNDGTrackRepository extends VariantTrackRepository<UNDGTrack>, CrudRepository<UNDGTrack, Long> {

    @NonNull
    @Override
    default List<UNDGTrack> findAll() {
        return VariantTrackRepository.super.findAll();
    }

    @NonNull
    @Override
    default List<UNDGTrack> findAll(Pageable pageable) {
        return VariantTrackRepository.super.findAll(pageable);
    }
}

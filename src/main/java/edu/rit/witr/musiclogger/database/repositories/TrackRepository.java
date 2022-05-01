package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.endpoints.SocketHandler;
import edu.rit.witr.musiclogger.entities.FMTrack;
import edu.rit.witr.musiclogger.entities.Track;
import edu.rit.witr.musiclogger.entities.UNDGTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * A wrapper/delegator for {@link VariantTrackRepository} so the FM/Underground repositories can be automatically
 * selected instead of dealing with two repository classes at once.
 *
 * This class should have all the methods {@link VariantTrackRepository} has.
 */
@Service
public class TrackRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepository.class);

    private final FMTrackRepository fmTrackRepository;
    private final UNDGTrackRepository undgTrackRepository;

    public TrackRepository(@Autowired FMTrackRepository fmTrackRepository, @Autowired UNDGTrackRepository undgTrackRepository) {
        this.fmTrackRepository = fmTrackRepository;
        this.undgTrackRepository = undgTrackRepository;
    }

    private VariantTrackRepository<?> getRepo(boolean underground) {
        return underground ? undgTrackRepository : fmTrackRepository;
    }

    public void save(Track track, boolean underground) {
        if (!underground && track instanceof FMTrack) {
            fmTrackRepository.save((FMTrack) track);
        } else if (underground && track instanceof UNDGTrack) {
            undgTrackRepository.save((UNDGTrack) track);
        } else {
            LOGGER.error("underground option and track type mismatch during save(), this is FATAL as a track cannot be added.");
        }
    }

    public Optional<Track> getLastTrack(boolean underground) {
        var found = (underground ? undgTrackRepository : fmTrackRepository).findAll(PageRequest.of(0, 1));
        if (found.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(found.get(0));
    }

    public void deleteById(Long id, boolean underground) {
        getRepo(underground).deleteById(id);
    }

    @NonNull
    public List<Track> findAll(boolean underground) {
        return findAllByOrderByIdDesc(underground);
    }

    @NonNull
    public List<Track> findAll(Pageable pageable, boolean underground) {
        return findAllByOrderByIdDesc(pageable, underground);
    }

    public List<Track> findAllByOrderByIdDesc(boolean underground) {
        return (List<Track>) getRepo(underground).findAllByOrderByIdDesc();
    }

    public List<Track> findAllByOrderByIdDesc(Pageable pageable, boolean underground) {
        return (List<Track>) getRepo(underground).findAllByOrderByIdDesc(pageable);
    }

    public List<Track> findAllByTimeBetween(Timestamp start, Timestamp end, boolean underground) {
        return (List<Track>) getRepo(underground).findAllByTimeBetween(start, end);
    }

    public List<Track> findAllByTimeBetween(Timestamp start, Timestamp end, Pageable pageable, boolean underground) {
        return (List<Track>) getRepo(underground).findAllByTimeBetween(start, end, pageable);
    }

    public Optional<Track> findById(Long id, boolean underground) {
        return getRepo(underground).findById(id).map(Track.class::cast);
    }
}

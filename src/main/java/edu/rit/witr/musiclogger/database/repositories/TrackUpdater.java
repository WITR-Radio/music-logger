package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A service to update tracks by their ID
 */
@Service
public class TrackUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackUpdater.class);

    private final EntityManager em;

    public TrackUpdater(@Autowired EntityManager em) {
        this.em = em;
    }

    /**
     * Updates a track in the database by the given ID. This assumes at least one non-id parameter is not null.
     * If a parameter is null, it is ignored in the update. This means it is impossible to remove/nullify a value,
     * which is intended behavior.
     *
     * @param id The ID of the track to update. This remains unmodified
     * @param title The new title of the track
     * @param artist The new artist of the track
     * @param group The new group of the track
     * @param time The new time of the track
     */
    @Transactional
    public void updateTrack(long id,
                            @Nullable String title,
                            @Nullable String artist,
                            @Nullable Group group,
                            @Nullable Timestamp time) {
        var updatedTime = new Date(System.currentTimeMillis());
        var params = new HashMap<>(Map.<String, Pair<String, Consumer<Query>>>of(
                "title", Pair.of("t.title = :title", query -> setParam(query, "title", String.class, title)),
                "artist", Pair.of("t.artist = :artist", query -> setParam(query, "artist", String.class, artist)),
                "group", Pair.of("t.group = :group", query -> setParam(query, "group", Group.class, group)),
                "time", Pair.of("t.time = :time", query -> setParam(query, "time", Timestamp.class, time)),
                "updated", Pair.of("t.updated = :updated", query -> setParam(query, "updated", java.sql.Date.class, updatedTime))));

        if (title == null) {
            params.remove("title");
        }

        if (artist == null) {
            params.remove("artist");
        }

        if (group == null) {
            params.remove("group");
        }

        if (time == null) {
            params.remove("time");
        }

        var queryString = "update Track t set " +
                params.values()
                        .stream()
                        .map(Pair::getFirst)
                        .collect(Collectors.joining(", ")) +
                " where t.id = :id";

        var query = em.createQuery(queryString);
        params.values()
                .stream()
                .map(Pair::getSecond)
                .forEach(consumer -> consumer.accept(query));

        setParam(query, "id", Long.class, id);

        query.executeUpdate();
    }

    private <T> void setParam(Query query, String name, Class<T> type, @Nullable T value) {
        var param = em.getCriteriaBuilder().parameter(type, name);
        query.setParameter(param, value);
    }
}

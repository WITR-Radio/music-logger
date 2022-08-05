package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.FMTrack;
import edu.rit.witr.musiclogger.entities.Track;
import edu.rit.witr.musiclogger.entities.UNDGTrack;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.common.EntityReference;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

/**
 * The default implementation of {@link SearchingService} that searches via Elasticsearch.
 */
@Service
public class DefaultSearchingService implements SearchingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSearchingService.class);

    private final EntityManager em;

    protected DefaultSearchingService(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public List<Track> findAllBy(@Nullable String song,
                                 @Nullable String artist,
                                 @Nullable Long afterTime,
                                 @Nullable Long beforeTime,
                                 int offset,
                                 int count,
                                 boolean underground) {
        SearchSession session = Search.session(em);

        SearchQuerySelectStep<?, EntityReference, ? extends Track, SearchLoadingOptionsStep, ?, ?> searching;

        if (underground) {
            searching = session.search(UNDGTrack.class);
        } else {
            searching = session.search(FMTrack.class);
        }

        return (List<Track>) searching
                .where(f -> f.bool(b -> {
                    b.must(f.matchAll());

                    if (afterTime != null) {
                        if (beforeTime != null) {
                            b.filter(f.range().field("time").between(new Timestamp(beforeTime), new Timestamp(afterTime)));
                        } else {
                            b.filter(f.range().field("time").atMost(new Timestamp(afterTime)));
                        }
                    }

                    if (song != null) {
                        // Exact matching should be higher
                        b.must(f.match().field("title").matching(song));
                    }

                    if (artist != null) {
                        b.must(f.match().field("artist").matching(artist));
                    }
                }))
                .sort(sort -> sort.field("time").desc())
                .fetchHits(offset, count);
    }

}

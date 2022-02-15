package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Track;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

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
        LOGGER.info("Getting entity manager stuff...");
        SearchSession session = Search.session(em);

        return session.search(Track.class)
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

                        // TODO: Inspect fuzzy searching
                        b.should(f.match().field("title").matching(song).fuzzy(2));
                    }

                    if (artist != null) {
                        b.must(f.match().field("artist").matching(artist));
                    }
                }))
                .sort(sort -> {
                    if (song != null || artist != null) {
                        return sort.score();
                    }

                    return sort.field("id").desc();
                })
                .fetchHits(offset, count);
    }

}

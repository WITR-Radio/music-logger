package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Track;
//import org.hibernate.search.jpa.FullTextEntityManager;
//import org.hibernate.search.jpa.Search;
//import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.engine.search.query.dsl.SearchQueryFinalStep;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.awt.print.Book;
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
                                 @Nullable Timestamp start,
                                 @Nullable Timestamp end,
                                 @Nullable Long after,
                                 int count,
                                 boolean underground) {
        LOGGER.info("Getting entity manager stuff...");
        SearchSession session = Search.session(em);

        return session.search(Track.class)
                .where(f -> f.bool(b -> {
                    b.must(f.matchAll());
                    if (song != null) {
                        b.must(f.match().field("title").matching(song));
                    }

                    if (artist != null) {
                        b.must(f.match().field("artist").matching(artist));
                    }

                    if (start != null) {
                        b.must(f.range().field("time").atLeast(start));
                    }

                    if (end != null) {
                        b.must(f.range().field("time").atMost(end));
                    }

                    if (after != null) {
                        b.must(f.range().field("time").atLeast(after));
                    }
                }))
                .fetchHits(count);

//        SearchQuerySelectStep step = session.search(Track.class);

//        QueryBuilder qb = fullTextEntityManager
//                .getSearchFactory()
//                .buildQueryBuilder()
//                .forEntity(Track.class)
//                .get();

//        var finalQuery = qb.bool();


//        var fullTextQuery = fullTextEntityManager.createFullTextQuery(finalQuery.createQuery(), Track.class);
//        fullTextQuery.setSort(qb.sort().byScore().createSort());
//        fullTextQuery.setMaxResults(count);
//        return (List<Track>) fullTextQuery.getResultList();
    }

}

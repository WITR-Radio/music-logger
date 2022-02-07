package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Track;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
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
                                 @Nullable Timestamp start,
                                 @Nullable Timestamp end,
                                 @Nullable Long after,
                                 int count,
                                 boolean underground) {
        LOGGER.info("Getting entity manager stuff...");
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

        QueryBuilder qb = fullTextEntityManager
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Track.class)
                .get();

        var finalQuery = qb.bool();

        if (song != null) {
            finalQuery.must(qb.keyword()
                    .onField("title")
                    .matching(song)
                    .createQuery());
        }

        if (artist != null) {
            finalQuery.must(qb.keyword()
                    .onField("artist")
                    .matching(artist)
                    .createQuery());
        }

        if (start != null) {
            finalQuery.must(qb.range()
                    .onField("time")
                    .above(start)
                    .createQuery());
        }

        if (end != null) {
            finalQuery.must(qb.range()
                    .onField("time")
                    .below(end)
                    .createQuery());
        }

        if (after != null) {
            finalQuery.must(qb.range()
                    .onField("time")
                    .above(after)
                    .createQuery());
        }

        var fullTextQuery = fullTextEntityManager.createFullTextQuery(finalQuery.createQuery(), Track.class);
        fullTextQuery.setSort(qb.sort().byScore().createSort());
        fullTextQuery.setMaxResults(count);
        return (List<Track>) fullTextQuery.getResultList();
    }

}

package edu.rit.witr.musiclogger.database;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * A service to index the database via Elasticsearch.
 */
@Service
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    private final EntityManager em = HibernateOptions.getEntityManager();

    /**
     * Indexes the database.
     *
     * @throws InterruptedException
     */
    @Transactional
    public void initiateIndexing() throws InterruptedException {
        LOGGER.info("Initiating indexing...");
        SearchSession session = Search.session(em);
        session.massIndexer().startAndWait();
        LOGGER.info("All entities indexed");
    }

}

package edu.rit.witr.musiclogger.database;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    private final EntityManager em;

    public IndexingService(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void initiateIndexing() throws InterruptedException {
        LOGGER.info("Initiating indexing...");
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        fullTextEntityManager.createIndexer().startAndWait();
        LOGGER.info("All entities indexed");
    }

}

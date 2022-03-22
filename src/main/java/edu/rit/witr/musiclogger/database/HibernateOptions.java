package edu.rit.witr.musiclogger.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * The options for Hibernate.
 */
public class HibernateOptions {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("edu.rit.witr.musiclogger.persist.Track");;

    /**
     * The {@link EntityManager} provided by the {@link EntityManagerFactory}.
     *
     * @return The created {@link EntityManager}
     */
    public static EntityManager getEntityManager() {
         return emf.createEntityManager();
    }
}

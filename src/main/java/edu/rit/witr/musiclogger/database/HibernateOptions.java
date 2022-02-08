package edu.rit.witr.musiclogger.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateOptions {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("edu.rit.witr.musiclogger.persist.Track");;

    public static EntityManager getEntityManager() {
         return emf.createEntityManager();
    }
}

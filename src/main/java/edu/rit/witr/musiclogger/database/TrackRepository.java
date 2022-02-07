package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import java.awt.print.Book;
import java.sql.Timestamp;
import java.util.List;

public interface TrackRepository extends CrudRepository<Track, Long> {

}

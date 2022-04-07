package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.StreamingLink;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StreamingLinkRepository extends CrudRepository<StreamingLink, Long> {

    List<StreamingLink> findAllByArtistEqualsAndTitleEquals(String artist, String title);

}

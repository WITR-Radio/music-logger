package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group, Long> {

    @NonNull
    List<Group> findAll();

}

package edu.rit.witr.musiclogger.database;

import edu.rit.witr.musiclogger.entities.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group, Long> {

}

package edu.rit.witr.musiclogger.database.repositories;

import edu.rit.witr.musiclogger.entities.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * The {@link CrudRepository} for the {@code groups} table.
 */
public interface GroupRepository extends CrudRepository<Group, Long> {

    /**
     * Returns all {@link Group}s in the database.
     *
     * @return All {@link Group}s
     */
    @NonNull
    List<Group> findAll();

}

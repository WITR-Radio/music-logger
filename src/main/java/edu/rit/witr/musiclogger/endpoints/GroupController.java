package edu.rit.witr.musiclogger.endpoints;

import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.entities.Group;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The controller for all /groups/ endpoints.
 */
@RestController
public class GroupController {

    private final GroupRepository repository;

    public GroupController(GroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/groups/list")
    List<String> listGroups() {
        return repository.findAll()
                .stream()
                .map(Group::getName)
                .collect(Collectors.toList());
    }
}

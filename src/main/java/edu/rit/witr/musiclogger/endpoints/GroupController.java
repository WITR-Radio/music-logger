package edu.rit.witr.musiclogger.endpoints;

import edu.rit.witr.musiclogger.database.GroupRepository;
import edu.rit.witr.musiclogger.entities.Group;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupController {

    private final GroupRepository repository;

    public GroupController(GroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/groups/list")
    List<Group> listGroups() {
        return repository.findAll();
    }
}

package edu.rit.witr.musiclogger.database.repositories;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * A workaround to get an instance of {@link GroupRepository} (with the ability for future expansion) without having
 * access to Autowiring.
 */
@Service
public class RepositoryService {

    @Resource
    private GroupRepository groupRepository;

    private static final Map<Class<?>, Repository<?, ?>> repos = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        repos.put(GroupRepository.class, groupRepository);
    }

    /**
     * Gets a repository registered via {@link #postConstruct()} with the given class.
     *
     * @param repoClass The class of the repository
     * @return The repository instance
     * @param <T> The repository type
     */
    public static <T> T getRepo(Class<T> repoClass) {
        return (T) repos.get(repoClass);
    }
}

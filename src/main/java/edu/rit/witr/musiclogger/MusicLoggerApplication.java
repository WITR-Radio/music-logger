package edu.rit.witr.musiclogger;

import edu.rit.witr.musiclogger.database.GroupRepository;
import edu.rit.witr.musiclogger.entities.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class MusicLoggerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicLoggerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MusicLoggerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(GroupRepository repository) {
        return args -> {
            // init(repository);

            LOGGER.info("All groups:");
            for (var group : repository.findAll()) {
                LOGGER.info(group.toString());
            }
        };
    }

    private void init(GroupRepository repository) {
        repository.deleteAll();

        Stream.of("Feature", "NewBin", "Library", "Recurrent")
                .forEach(group -> repository.save(new Group(group, Date.valueOf("2022-2-7"), Date.valueOf("2022-2-7"))));

    }
}

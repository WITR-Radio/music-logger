package edu.rit.witr.musiclogger;

import edu.rit.witr.musiclogger.database.GroupRepository;
import edu.rit.witr.musiclogger.database.TrackRepository;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.stream.Stream;

@SpringBootApplication
@EntityScan({"edu.rit.witr.musiclogger.entities"})
public class MusicLoggerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicLoggerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MusicLoggerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(GroupRepository groupRepository, TrackRepository trackRepository) {
        return args -> {
//            initGroups(groupRepository);
//            initTracks(trackRepository);

            LOGGER.info("All groups:");
            for (var group : groupRepository.findAll()) {
                LOGGER.info(group.toString());
            }

            LOGGER.info("All tracks:");
            for (var track : trackRepository.findAll()) {
                LOGGER.info(track.toString());
            }
        };
    }

    private void initGroups(GroupRepository repository) {
        repository.deleteAll();

        Stream.of("Feature", "NewBin", "Library", "Recurrent")
                .forEach(group -> repository.save(new Group(group, Date.valueOf("2022-2-7"), Date.valueOf("2022-2-7"))));

    }

    private void initTracks(TrackRepository repository) {
        repository.deleteAll();

        for (int i = 1; i <= 25; i++) {
            var time = Timestamp.valueOf("2022-2-" + i + " 18:00:00");
            repository.save(new Track("artist" + i, "title" + i, time, false, null, new Date(time.getTime()), new Date(time.getTime()), false, null));
        }
    }
}

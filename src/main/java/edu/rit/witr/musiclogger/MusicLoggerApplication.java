package edu.rit.witr.musiclogger;

import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.database.repositories.TrackRepository;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
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

        Stream.of("Feature", "NewBin", "Library", "Recurrent", "Event")
                .forEach(group -> repository.save(new Group(group, Date.valueOf("2022-2-7"), Date.valueOf("2022-2-7"))));

    }

    private void initTracks(TrackRepository repository) {
        repository.deleteAll();

        var list =  List.of("Zen (with K.Flay & grandson)", "Level of Concern", "OK", "how will i rest in peace if i'm buried by a highway?//", "Is Everybody Going Crazy?", "Real Long Time", "Dead Horse", "Brooklyn Bridge To Chorus", "Cathedral Bell", "All Your Love", "OK OK", "Dream World", "friends*", "Alaska", "The Garden", "Beautiful Anyway", "rock bottom", "Disaster Party", "Kangaroo", "CFS", "Out Of Style", "Heat Seeker", "Not OK!", "Let Me Down", "That's It", "Sayonara", "Caution - Radio Edit", "Love's Not Enough", "Weird!", "Mayday!!! Fiesta Fever", "Give A Little Bit More (Disaster)", "Troublemaker", "If You’re Too Shy (Let Me Know) - Edit", "Starz", "Deleter", "Hollywood", "Hero", "Lie Out Loud", "If That's Alright", "Bad Vacation", "Hallucinogenics", "Upside Down", "Come On Out", "Pretty Lady", "Summer of Love (feat. The Griswolds)", "The Steps", "Anything Could Happen", "Used To Like", "Stand", "On Our Own", "Lonely", "loneliness for love", "If I Want To", "Someone Else", "Aries (feat. Peter Hook and Georgia)", "Black Madonna", "I'm Not Having Any Fun", "slowdown", "", "Light at the End of the Tunnel", "Like It Like This", "Invincible", "Who’s Gonna Love Me Now", "everyone blooms", "Big Shot", "Multiply", "Van Horn", "Weirdo", "Pedestal", "August", "Half Your Age", "Strange Clouds", "Cradles", "Beautiful Faces", "Strangers", "Cyanide", "Forever", "Dead Weight", "Careless", "Karma", "The Apartment", "Good Old Days", "Be Your Drug", "Basement", "Dance Of The Clairvoyants", "Time Moves On", "Bang!", "Everyone Knows", "Caught In The Middle", "February", "Death Rattle", "I Want More", "Want What You Got", "Hometown Heroes", "I Just Wanna Shine", "Valentine", "More", "15 Years", "melancholyism.", "Your Girlfriend");


        var date = new java.util.Date(122, Calendar.JANUARY, 1, 2, 42);
        var mills = date.getTime();
        System.out.println("mills = " + mills);
        for (int i = 0; i < list.size(); i++) {
            repository.save(new Track("Artist " + i, list.get(i), new Timestamp(mills), null));
            mills += 86400000; // 1 day
        }
    }
}

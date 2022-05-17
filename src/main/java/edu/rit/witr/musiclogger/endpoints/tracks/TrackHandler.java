package edu.rit.witr.musiclogger.endpoints.tracks;

import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.database.repositories.TrackRepository;
import edu.rit.witr.musiclogger.endpoints.SocketHandler;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.entities.Track;
import org.springframework.stereotype.Service;

/**
 * A handler to do common tasks with tracks. In the future, this should do more.
 */
@Service
public class TrackHandler {

    private final TrackRepository trackRepository;
    private final GroupRepository groupRepository;
    private final SocketHandler socketHandler;

    private Group musicGroup;

    public TrackHandler(TrackRepository trackRepository, GroupRepository groupRepository, SocketHandler socketHandler) {
        this.trackRepository = trackRepository;
        this.groupRepository = groupRepository;
        this.socketHandler = socketHandler;
    }

    public void saveTrack(Track track, boolean underground) {
        trackRepository.save(track, underground);
        socketHandler.broadcastTrack(track, underground);
    }

    /**
     * Gets the cached group with the name of "Music".
     *
     * @return The Music group
     */
    // TODO: This should probably be in another class
    public Group getMusicGroup() {
        if (musicGroup == null) {
            musicGroup = groupRepository.findByName("Music");
        }

        return musicGroup;
    }

}

package edu.rit.witr.musiclogger.tcp;

import edu.rit.witr.musiclogger.broadcast.BroadcastService;
import edu.rit.witr.musiclogger.database.repositories.GroupRepository;
import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import edu.rit.witr.musiclogger.endpoints.tracks.TrackHandler;
import edu.rit.witr.musiclogger.entities.Group;
import edu.rit.witr.musiclogger.streaming.StreamingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.util.List;

@MessageEndpoint
public class WOReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(WOReceiver.class);

    private final JAXBContext jaxbContext;
    private final TrackHandler trackHandler;
    private final StreamingManager streamingManager;
    private final BroadcastService broadcastService;

    public WOReceiver(@Autowired @Qualifier("nowPlayingContext") JAXBContext jaxbContext,
                      @Autowired TrackHandler trackHandler,
                      @Autowired StreamingManager streamingManager,
                      @Autowired BroadcastService broadcastService) {
        this.jaxbContext = jaxbContext;
        this.trackHandler = trackHandler;
        this.streamingManager = streamingManager;
        this.broadcastService = broadcastService;
    }

    /**
     * Invoked when a TCP message from WideOrbit is received. This assumes the data is in the form of a
     * {@link NowPlaying} XML document, assumed with no terminating character.
     *
     * @param bytes The incoming bytes
     */
    @ServiceActivator(inputChannel = "wideorbit")
    public void consume(byte[] bytes) {
        try {
            var input = new String(bytes);

            var jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            var nowPlaying = (NowPlaying) jaxbUnmarshaller.unmarshal(new StringReader(input));

            if (!nowPlaying.getCategory().equals("MUS") && !nowPlaying.getCategory().equals("XMS")) {
                return;
            }

            var underground = nowPlaying.isUnderground();

            var track = nowPlaying.toTrack(trackHandler.getMusicGroup());
            var broadcasting = BroadcastTrack.fromTrack(track);

            LOGGER.debug("Broadcasting from WideOrbit: {}", broadcasting);

            streamingManager.applyLinks(List.of(track)).join(); // TODO: PROPER ASYNC?
            trackHandler.saveTrack(track, underground);

            // handle async better here too?
            broadcastService.broadcastTrack(broadcasting, underground);
        } catch (JAXBException e) {
            LOGGER.error("An error occurred while decoding WideOrbit TCP data", e);
        }
    }

}

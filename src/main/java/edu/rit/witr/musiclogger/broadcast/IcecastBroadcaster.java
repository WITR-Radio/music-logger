package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.broadcast.icecast.IceStats;
import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class IcecastBroadcaster implements Broadcaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(IcecastBroadcaster.class);

    private final static String ADMIN_URL = "https://streaming.witr.rit.edu/admin";
    private final static String LISTMOUNTS_URL = ADMIN_URL + "/listmounts";
    private final static String META_URL = ADMIN_URL + "/metadata";

    private final HttpClient client;
    private final JAXBContext jaxbContext;

    private IcecastBroadcaster(HttpClient client, JAXBContext jaxbContext) {
        this.client = client;
        this.jaxbContext = jaxbContext;
    }

    /**
     * Creates an {@link IcecastBroadcaster}. If a fatal error occurs, an empty {@link Optional} is returned and the
     * broadcaster should be ignored.
     *
     * @return The created {@link IcecastBroadcaster}
     */
    public static Optional<IcecastBroadcaster> create() {
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .authenticator(new IcecastAuthenticator())
                .build();

        try {
            var jaxbContext = JAXBContext.newInstance(IceStats.class);

            return Optional.of(new IcecastBroadcaster(client, jaxbContext));
        } catch (JAXBException e) {
            LOGGER.error("A fatal error occurred while creating the IceStats JAXBContext", e);
            return Optional.empty();
        }
    }

    @Override
    public CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track, boolean underground) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(LISTMOUNTS_URL))
                .timeout(Duration.ofMinutes(2))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        var jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                        var iceStats = (IceStats) jaxbUnmarshaller.unmarshal(new StringReader(body));

                        var tasks = new ArrayList<CompletableFuture<?>>();
                        for (var source : iceStats.getSources()) {
                            var mount = source.getMount();
                            var undergroundMount = mount.contains("udg");
                            if (underground == undergroundMount) {
                                tasks.add(updateIcecast(mount, track.getTitle(), track.getArtist(), underground));
                            }
                        }

                        CompletableFuture.allOf(tasks.toArray(CompletableFuture<?>[]::new)).join();

                        return new BroadcastStatus();
                    } catch (JAXBException e) {
                        LOGGER.error("An error occurred while decoding Icecast XML response", e);
                        return new BroadcastStatus("An error occurred while decoding Icecast XML response: " + e.getMessage());
                    }
                });
    }

    /**
     * Sends an update to Icecast with a specific mount, found by listing {@link #LISTMOUNTS_URL}.
     *
     * @param mount       The mount point (e.g. "witr-hockey-mp3", "witr-mobile", etc.)
     * @param song        The song name to send
     * @param artist      The artist name to send
     * @param underground If this request is being performed on underground (used only for logging purposes)
     * @return The {@link CompletableFuture} of the request
     */
    private CompletableFuture<Void> updateIcecast(String mount, String song, String artist, boolean underground) {
        try {
            var songLine = artist + " - " + song;
            var uri = new URIBuilder(META_URL)
                    .addParameter("mount", mount)
                    .addParameter("mode", "updinfo")
                    .addParameter("song", songLine)
                    .build();

            var request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(res -> {
                        if (res.statusCode() != 200) {
                            LOGGER.error("Erroneous status code ({}) on icecast update (mount = {}, song = {}): {}", res.statusCode(), mount, songLine, res.body());
                        } else {
                            LOGGER.debug("Updated {}icecast: {}", underground ? "underground " : "", songLine);
                        }
                    });
        } catch (URISyntaxException e) {
            LOGGER.error("An error occurred while creating the Icecast URI (mount = {}, song = {})", mount, song, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public String getName() {
        return "Icecast";
    }

    @Override
    public boolean restrictToFM() {
        return false;
    }

    static class IcecastAuthenticator extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(System.getenv("ICECAST_USER"), System.getenv("ICECAST_PASS").toCharArray());
        }
    }
}

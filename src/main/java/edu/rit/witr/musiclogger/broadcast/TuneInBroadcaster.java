package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TuneInBroadcaster implements Broadcaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(TuneInBroadcaster.class);

    private static final String TUNEIN_PLAYING_URL = "http://air.radiotime.com/Playing.ashx";

    private final HttpClient client;

    private TuneInBroadcaster(HttpClient client) {
        this.client = client;
    }

    public static Optional<TuneInBroadcaster> create() {
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        return Optional.of(new TuneInBroadcaster(client));
    }

    @Override
    public CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track, boolean underground) {
        try {
            var uri = new URIBuilder(TUNEIN_PLAYING_URL)
                    .addParameter("partnerId", System.getenv("TUNEIN_PARTNER_ID"))
                    .addParameter("partnerKey", System.getenv("TUNEIN_PARTNER_KEY"))
                    .addParameter("id", System.getenv("TUNEIN_STATION_ID"))
                    .addParameter("title", track.getTitle())
                    .addParameter("artist", track.getArtist())
                    .build();

            var request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(res -> {
                        if (res.statusCode() != 200) {
                            LOGGER.error("Erroneous status code ({}) while updating TuneIn (song = {}, artist = {}): {}", res.statusCode(), track.getTitle(), track.getArtist(), res.body());
                            return new BroadcastStatus("Status code " + res.statusCode() + " while updating TuneIn");
                        }

                        return new BroadcastStatus();
                    });
        } catch (URISyntaxException e) {
            LOGGER.error("An error occurred while creating the TuneIn URI (song = {}, artist = {})", track.getTitle(), track.getArtist(), e);
            return CompletableFuture.completedFuture(new BroadcastStatus("An error occurred while creating the TuneIN URI"));
        }
    }

    @Override
    public String getName() {
        return "TuneIn";
    }

    @Override
    public boolean restrictToFM() {
        return true;
    }
}

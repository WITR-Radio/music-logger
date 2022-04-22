package edu.rit.witr.musiclogger.streaming.spotify;

import edu.rit.witr.musiclogger.database.repositories.StreamingLinkRepository;
import edu.rit.witr.musiclogger.entities.StreamingLink;
import edu.rit.witr.musiclogger.streaming.Services;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class SpotifyService implements StreamingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyService.class);

    private final SpotifyApi spotifyApi;
    private final StreamingLinkRepository streamingLinkRepository;

    private SpotifyService(@Autowired StreamingLinkRepository streamingLinkRepository) {
        this.streamingLinkRepository = streamingLinkRepository;
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(System.getenv("SPOTIFY_CLIENT_ID"))
                .setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET"))
                .build();

        var clientCredentialsRequest = spotifyApi.clientCredentials().build();

        // TODO: DONT DO THIS!!! remove the get()
        try {
            refreshAuth(clientCredentialsRequest, 0).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<?> refreshAuth(ClientCredentialsRequest clientCredentialsRequest, int seconds) {
        return CompletableFuture.runAsync(() -> {
            try {
                var clientCredentials = clientCredentialsRequest.execute();
                spotifyApi.setAccessToken(clientCredentials.getAccessToken());
                refreshAuth(clientCredentialsRequest, clientCredentials.getExpiresIn());
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                e.printStackTrace();
                refreshAuth(clientCredentialsRequest, 10);
            }
        }, CompletableFuture.delayedExecutor(seconds, TimeUnit.SECONDS));
    }

    @Override
    public CompletableFuture<Optional<StreamingLink>> getStreamingLink(String track, String artist) {
        if (track.isBlank()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                var all = streamingLinkRepository.findAllByArtistEqualsAndTitleEquals(artist, track);

                // There should only ever be 0-1 found
                if (!all.isEmpty()) {
                    return Optional.of(all.get(0));
                }

                var paging = spotifyApi.searchItem("track:" + track + " artist:" + artist, "track")
                        .build()
                        .execute()
                        .getTracks();

                if (paging.getTotal() == 0) {
                    return Optional.empty();
                }

                var spotifyTrack = paging.getItems()[0];
                var streamingLink = new StreamingLink(artist, track, Services.SPOTIFY, "https://open.spotify.com/track/" + translateURI(spotifyTrack.getUri()), getAlbumArtLink(spotifyTrack.getAlbum()));
                streamingLinkRepository.save(streamingLink);

                return Optional.of(streamingLink);
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                LOGGER.error("There was an error fetching the Spotify URL for track \"{}\" by \"{}\"", track, artist, e);
            }
            return Optional.empty();
        });
    }

    @Override
    public Services getService() {
        return Services.SPOTIFY;
    }

    /**
     * Translates a URI such as {@code spotify:track:xxx} to just xxx
     *
     * @param spotifyURI The full URI (does not need to be track
     * @return The translated/stripped URI
     */
    private String translateURI(String spotifyURI) {
        var colon = spotifyURI.lastIndexOf(":") + 1;
        return spotifyURI.substring(colon);
    }

    private String getAlbumArtLink(AlbumSimplified album) {
        return album.getImages()[album.getImages().length - 1].getUrl();
    }
}

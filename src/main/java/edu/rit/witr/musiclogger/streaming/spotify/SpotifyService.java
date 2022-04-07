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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class SpotifyService implements StreamingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyService.class);

    private final SpotifyApi spotifyApi;
    private final StreamingLinkRepository streamingLinkRepository;

    private SpotifyService(@Autowired StreamingLinkRepository streamingLinkRepository) {
        this.streamingLinkRepository = streamingLinkRepository;
        this.spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(System.getenv("SPOTIFY_ACCESS_TOKEN"))
                .build();
    }

    @Override
    public CompletableFuture<Optional<StreamingLink>> getStreamingLink(String track, String artist) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var all = streamingLinkRepository.findAllByArtistEqualsAndTitleEquals(artist, track);

                // There should only ever be 0-1 found
                if (!all.isEmpty()) {
                    return Optional.of(all.get(0));
                }

                var paging = spotifyApi.searchTracks(track).build().execute();

                if (paging.getTotal() == 0) {
                    return Optional.empty();
                }

                var spotifyTrack = paging.getItems()[0];
                var streamingLink = new StreamingLink(artist, track, Services.SPOTIFY, "https://open.spotify.com/track/" + spotifyTrack.getUri());
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
}

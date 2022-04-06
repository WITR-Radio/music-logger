package edu.rit.witr.musiclogger.spotify;

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class SpotifyService implements StreamingService {

    private final SpotifyApi spotifyApi;

    public SpotifyService() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(System.getenv("SPOTIFY_ACCESS_TOKEN"))
                .build();
    }

    @Override
    public CompletableFuture<Optional<String>> getStreamingLink(String track, String artist) {
        return null;
    }
}

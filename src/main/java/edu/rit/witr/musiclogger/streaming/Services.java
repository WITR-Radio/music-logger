package edu.rit.witr.musiclogger.streaming;

/**
 * The different available {@link edu.rit.witr.musiclogger.streaming.spotify.StreamingService}s.
 */
public enum Services {
    SPOTIFY("Spotify");

    private final String name;

    Services(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the service (e.g. Spotify, Apple Music).
     *
     * @return The name of the service
     */
    public String getName() {
        return name;
    }
}


package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;

import java.util.concurrent.CompletableFuture;

public class TuneInBroadcaster implements Broadcaster {

    @Override
    public CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track) {
        return null;
    }

    @Override
    public String getName() {
        return "TuneIn";
    }
}

package edu.rit.witr.musiclogger.endpoints.tracks;

import edu.rit.witr.musiclogger.entities.Track;

/**
 * A DTO to store metadata along with a track to be sent over a websocket.
 *
 * @param track     The track being sent over.
 * @param requested If the track was manually requested. {@code false} means this is a normal broadcasted track that is currently
 *                  being played.
 */
public record TrackBroadcastDTO(Track track, boolean requested) {
}

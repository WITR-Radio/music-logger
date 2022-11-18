package edu.rit.witr.musiclogger.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rit.witr.musiclogger.database.repositories.TrackRepository;
import edu.rit.witr.musiclogger.endpoints.tracks.TrackBroadcastDTO;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketHandler.class);

    private final Executor broadcastExecutor = Executors.newCachedThreadPool();

    private final List<WebSocketSession> undergroundSessions = new CopyOnWriteArrayList<>();
    private final List<WebSocketSession> fmSessions = new CopyOnWriteArrayList<>();

    private final TrackRepository trackRepository;
    private final ObjectMapper objectMapper;

    public SocketHandler(TrackRepository trackRepository, ObjectMapper objectMapper) {
        this.trackRepository = trackRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        var underground = isUnderground(session);
        getSessions(underground).add(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        var json = objectMapper.readValue(message.getPayload(), HashMap.class);
        var underground = isUnderground(session);

        if (json.containsKey("request")) {
            if ("current".equals(json.get("request"))) {
                trackRepository.getLastTrack(underground).ifPresent(track -> {
                    try {
                        var trackJson = objectMapper.writeValueAsString(new TrackBroadcastDTO(track, true));
                        broadcastTrack(trackJson, session);
                    } catch (IOException e) {
                        LOGGER.error("An error occurred while sending requested track", e);
                    }
                });
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        getSessions(isUnderground(session)).remove(session);
    }

    /**
     * Broadcasts a given track to all connected sockets ({@code underground} having its own collection of sockets) to
     * be added to the list.
     *
     * @param track The track to broadcast
     * @param underground If the track is on the underground database
     */
    public void broadcastTrack(Track track, boolean underground) {
        CompletableFuture.runAsync(() -> {
            try {
                var json = objectMapper.writeValueAsString(new TrackBroadcastDTO(track, false));
                getSessions(underground).forEach(session -> {
                    try {
                        broadcastTrack(json, session);
                    } catch (IOException | IllegalStateException e) {
                        try {
                            session.close();
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }

                        getSessions(underground).remove(session);
                    }
                });
            } catch (IOException e) {
                LOGGER.error("There was an error broadcasting to websockets", e);
            }
        }, broadcastExecutor);
    }

    /**
     * Broadcasts a given track JSON string to the given session.
     *
     * @param json      The JSON of the track being broadcasted
     * @param session   The session to send this to
     * @throws IOException If an exception occurs
     */
    private void broadcastTrack(String json, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(json));
    }

    /**
     * Checks if the given {@link WebSocketSession} is using the underground playlist.
     *
     * @param session The client {@code session}
     * @return If this is from underground
     */
    private boolean isUnderground(WebSocketSession session) {
        var decoded = decodeQuery(session.getUri());
        return decoded.getOrDefault("underground", List.of("")).contains("true");
    }

    /**
     * Decodes a URI's query string into a key, value map.
     *
     * @param uri The URI to decode
     * @return The query parameters
     */
    private MultiValueMap<String, String> decodeQuery(URI uri) {
        return UriComponentsBuilder.fromUri(uri).build().getQueryParams();
    }

    /**
     * Gets the list of {@link WebSocketSession}s for either underground or FM.
     *
     * @param underground If the underground collection should be returned
     * @return The collection of sessions
     */
    private List<WebSocketSession> getSessions(boolean underground) {
        return underground ? undergroundSessions : fmSessions;
    }
}

package edu.rit.witr.musiclogger.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rit.witr.musiclogger.entities.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
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

    private final ObjectMapper objectMapper;

    public SocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        getSessions(isUnderground(session)).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
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
                var json = objectMapper.writeValueAsString(track);
                getSessions(underground).forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(json));
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
     * Checks if the given {@link WebSocketSession} is using the underground playlist.
     *
     * @param session The client {@code session}
     * @return If this is from underground
     */
    private boolean isUnderground(WebSocketSession session) {
        var decoded = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();
        return decoded.getOrDefault("underground", List.of("")).contains("true");
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

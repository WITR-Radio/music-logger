package edu.rit.witr.musiclogger.broadcast;

import edu.rit.witr.musiclogger.endpoints.tracks.BroadcastTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RDSBroadcaster implements Broadcaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDSBroadcaster.class);

    private static final String ARTIST = "ARTISTNAME=";
    private static final String SONG = "SONGTITLE=";
    private static final String DURATION = "DURATION=";
    private static final int DEFAULT_DURATION = 500;

    private final InetAddress address;
    private final int port;

    private RDSBroadcaster(InetAddress rdsAddress, int port) {
        this.address = rdsAddress;
        this.port = port;
    }

    public static Optional<RDSBroadcaster> create() {
        var rdsIp = System.getenv("RDS_IP");

        try {
            var address = InetAddress.getByName(rdsIp);
            var port = Integer.parseInt(System.getenv("RDS_PORT"));
            return Optional.of(new RDSBroadcaster(address, port));
        } catch (UnknownHostException e) {
            LOGGER.error("An error occurred while getting the RDS host ({})", rdsIp, e);
            return Optional.empty();
        }
    }

    @Override
    public CompletableFuture<BroadcastStatus> broadcast(BroadcastTrack track, boolean underground) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                sendRds(ARTIST + track.getArtist());
                sendRds(SONG + track.getTitle());
                sendRds(DURATION + DEFAULT_DURATION);
            } catch (IOException e) {
                LOGGER.error("An error occurred while sending RDS messages", e);
                return new BroadcastStatus("An error occurred while sending RDS messages: " + e.getMessage());
            }

            return new BroadcastStatus();
        });
    }

    /**
     * Synchronously sends the given {@code message} to the RDS encoder via UDP.
     *
     * @param message The message to send
     */
    // TODO: I'm unsure how to detect error handling, the old python script checked if the response was
    //       a "+" but I can't find the documentation on that anywhere.
    private void sendRds(String message) throws IOException {
        var buffer = message.getBytes(StandardCharsets.UTF_8);
        var packet = new DatagramPacket(buffer, buffer.length, address, port);
        var datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
    }

    @Override
    public String getName() {
        return "RDS";
    }
}

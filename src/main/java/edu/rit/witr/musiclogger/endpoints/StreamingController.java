package edu.rit.witr.musiclogger.endpoints;

import edu.rit.witr.musiclogger.streaming.spotify.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StreamingController {

    private final SpotifyService spotifyService;

    public StreamingController(@Autowired SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/api/streaming/lookup")
    public ResponseEntity<?> lookupTrackMeta(HttpServletRequest request, @RequestParam String track, @RequestParam String artist) {
        if (!System.getenv("STREAMING_API_TOKEN").equals(request.getHeader("Authentication"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        var foundLinkOptional = spotifyService.getStreamingLink(track, artist).join();
        if (foundLinkOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        var found = foundLinkOptional.get();

        return new ResponseEntity<>(new ServiceDataDTO(found.getLink(), found.getAlbumArt(), found.getService().getName()), HttpStatus.OK);
    }

    class ServiceDataDTO {
        private final String link;
        private final String artwork;
        private final String service;

        ServiceDataDTO(String link, String artwork, String service) {
            this.link = link;
            this.artwork = artwork;
            this.service = service;
        }

        public String getLink() {
            return link;
        }

        public String getArtwork() {
            return artwork;
        }

        public String getService() {
            return service;
        }
    }
}

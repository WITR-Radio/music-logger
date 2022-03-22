package edu.rit.witr.musiclogger.endpoints;

import edu.rit.witr.musiclogger.database.repositories.TrackRepository;
import edu.rit.witr.musiclogger.entities.Track;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

@RestController
public class ExportController {

    private final TrackRepository repository;

    public ExportController(TrackRepository repository) {
        this.repository = repository;
    }

    private List<Track> listTracks(@Nullable Long start, @Nullable Long end, @Nullable Integer limit) {
        if (limit != null) {
            var paging = PageRequest.of(0, limit);
            if (start == null || end == null) {
                return repository.findAll(paging);
            } else {
                return repository.findAllByTimeBetween(new Timestamp(start), new Timestamp(end), paging);
            }
        } else {
            if (start == null || end == null) {
                return repository.findAll();
            } else {
                return repository.findAllByTimeBetween(new Timestamp(start), new Timestamp(end));
            }
        }
    }

    @GetMapping("/export")
    ResponseEntity<StreamingResponseBody> export(@RequestParam(required = false) Long start,
                                                 @RequestParam(required = false) Long end,
                                                 @RequestParam(required = false) Integer limit, // TODO: Limit
                                                 @RequestParam(defaultValue = "export.csv") String fileName) {

        var tracks = listTracks(start, end, limit);

        var responseBody = new StreamingResponseBody() {
            @Override
            public void writeTo(@NonNull OutputStream out) throws IOException {
                for (var track : tracks) {
                    write(out, track.getTitle(), ",");
                    write(out, track.getArtist(), ",");

                    var group = track.getGroup();
                    write(out, group == null ? "" : group.getName(), ",");

                    write(out, track.getTime().toString(), "\n");
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName.replaceAll("[^\\w,\\s\\-\\.]+", "_"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    private void write(OutputStream out, String value, String... additional) throws IOException {
        out.write((StringEscapeUtils.escapeCsv(value) + String.join("", additional)).getBytes(StandardCharsets.UTF_8));
    }
}

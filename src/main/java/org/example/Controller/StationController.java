package org.example.Controller;

import org.example.DTO.StationRequest;
import org.example.DTO.StationResponse;
import org.example.Model.Stations;
import org.example.Services.TicketingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private TicketingSystem ts;

    @GetMapping
    public List<StationResponse> getAll() {
        return ts.getAllStations().stream()
                .map(s -> new StationResponse(s.getId(), s.getName()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody StationRequest req) {
        if (req.name() == null || req.name().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Station name is required."));
        Stations s = ts.addAndReturnStation(req.name().trim());
        return ResponseEntity.ok(new StationResponse(s.getId(), s.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable int id) {
        if (!ts.removeStation(id))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }
}

package org.example.Controller;

import org.example.DTO.*;
import org.example.Model.Route;
import org.example.Model.Train;
import org.example.Model.TrainPaths.ChangeOverPath;
import org.example.Model.TrainPaths.DirectPath;
import org.example.Model.Stations;
import org.example.Services.MailService;
import org.example.Services.TicketingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    @Autowired private TicketingSystem ts;
    @Autowired private MailService     mail;

    // ── GET all ──────────────────────────────────────────────────────────────
    @GetMapping
    public List<TrainResponse> getAll() {
        return ts.getAllTrains().stream().map(this::toResponse).toList();
    }

    // ── GET one ──────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable int id) {
        Train t = ts.findTrainById(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(t));
    }

    // ── POST create ──────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TrainRequest req) {
        if (req.name() == null || req.name().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Train name required."));
        if (req.capacity() < 1)
            return ResponseEntity.badRequest().body(Map.of("error", "Capacity must be > 0."));

        Route route = req.routeId() != null ? ts.findRouteById(req.routeId()) : null;
        Train t = ts.addAndReturnTrain(req.name().trim(), route, req.capacity());
        return ResponseEntity.ok(toResponse(t));
    }

    // ── PUT update ───────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody TrainRequest req) {
        Train t = ts.findTrainById(id);
        if (t == null) return ResponseEntity.notFound().build();

        if (req.name() != null && !req.name().isBlank())  t.setTrainName(req.name().trim());
        if (req.capacity() > 0)                           t.setCapacity(req.capacity());
        if (req.routeId() != null) {
            Route r = ts.findRouteById(req.routeId());
            if (r == null) return ResponseEntity.badRequest().body(Map.of("error", "Route not found."));
            t.setRouteOfTrain(r);
        }
        return ResponseEntity.ok(toResponse(t));
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (!ts.removeTrain(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    // ── POST set delay ───────────────────────────────────────────────────────
    @PostMapping("/{id}/delay")
    public ResponseEntity<?> setDelay(@PathVariable int id, @RequestBody DelayRequest req) {
        Train t = ts.findTrainById(id);
        if (t == null) return ResponseEntity.notFound().build();
        if (req.minutes() < 1)
            return ResponseEntity.badRequest().body(Map.of("error", "Delay must be at least 1 minute."));

        ts.setTrainDelay(t, req.minutes());

        // Notify every passenger on this train
        t.getBookings().forEach(b -> {
            try { mail.sendDelayNotification(b, req.minutes()); }
            catch (Exception e) { /* log but don't abort */ }
        });

        return ResponseEntity.ok(toResponse(t));
    }

    // ── DELETE clear delay ───────────────────────────────────────────────────
    @DeleteMapping("/{id}/delay")
    public ResponseEntity<?> clearDelay(@PathVariable int id) {
        Train t = ts.findTrainById(id);
        if (t == null) return ResponseEntity.notFound().build();
        ts.clearTrainDelay(t);
        return ResponseEntity.ok(toResponse(t));
    }

    // ── GET travel options ────────────────────────────────────────────────────
    @GetMapping("/travel-options")
    public ResponseEntity<?> travelOptions(@RequestParam int fromId, @RequestParam int toId) {
        Stations from = ts.findStationById(fromId);
        Stations to   = ts.findStationById(toId);

        if (from == null || to == null) return ResponseEntity.badRequest().build();

        List<TravelOptionResponse> responses = new ArrayList<>();

        // Map Direct Paths
        ts.findDirectPaths(from, to).forEach(dp ->
                responses.add(toDirectResponse(dp)));

        // Map Changeover Paths
        ts.findChangeoverPaths(from, to).forEach(co ->
                responses.add(toChangeoverResponse(co)));

        return ResponseEntity.ok(responses);
    }
    // ── Mapping helpers ───────────────────────────────────────────────────────
    private TrainResponse toResponse(Train t) {
        Route r = t.getRouteOfTrain();
        int pax = t.getBookings().stream().mapToInt(b -> b.getNumberOfTickets()).sum();
        return new TrainResponse(
                t.getId(), t.getTrainName(),
                r != null ? r.getId() : null,
                r != null ? r.getRouteName() : null,
                t.getCapacity(), t.isDelayed(), t.getDelayMinutes(), pax);
    }

    private TravelOptionResponse toDirectResponse(DirectPath dp) {
        Route r = dp.getRoute();
        return new TravelOptionResponse(
                "DIRECT",
                dp.getTrain().getId(), dp.getTrain().getTrainName(),
                dp.getFrom().getId(), dp.getFrom().getName(),
                fmt(r.getArrivalAt(dp.getFrom())),
                null, null, null, null,
                null, null,
                dp.getTo().getId(), dp.getTo().getName(),
                fmt(r.getArrivalAt(dp.getTo())),
                dp.getTrain().getAvailableSeats(dp.getFrom(), dp.getTo()),
                null);
    }

    private TravelOptionResponse toChangeoverResponse(ChangeOverPath co) {
        return new TravelOptionResponse(
                "CHANGEOVER",
                co.getTrainA().getId(), co.getTrainA().getTrainName(),
                co.getFrom().getId(), co.getFrom().getName(),
                fmt(co.getRouteA().getArrivalAt(co.getFrom())),
                co.getMid().getId(), co.getMid().getName(),
                fmt(co.getRouteA().getArrivalAt(co.getMid())),
                fmt(co.getRouteB().getArrivalAt(co.getMid())),
                co.getTrainB().getId(), co.getTrainB().getTrainName(),
                co.getTo().getId(), co.getTo().getName(),
                fmt(co.getRouteB().getArrivalAt(co.getTo())),
                co.getTrainA().getAvailableSeats(co.getFrom(), co.getMid()),
                co.getTrainB().getAvailableSeats(co.getMid(), co.getTo()));
    }

    private String fmt(java.util.Date d) {
        return d != null ? String.format("%tH:%<tM", d) : "N/A";
    }
}

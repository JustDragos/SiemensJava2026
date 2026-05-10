package org.example.Controller;

import org.example.DTO.RouteRequest;
import org.example.DTO.RouteResponse;
import org.example.DTO.RouteStopRequest;
import org.example.Model.Route;
import org.example.Model.Stations;
import org.example.Services.TicketingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private TicketingSystem ts;

    // ── GET all ──────────────────────────────────────────────────────────────
    @GetMapping
    public List<RouteResponse> getAll() {
        return ts.getAllRoutes().stream().map(this::toResponse).toList();
    }

    // ── GET one ──────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable int id) {
        Route r = ts.findRouteById(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(r));
    }

    // ── POST create ──────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RouteRequest req) {
        if (req.name() == null || req.name().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Route name is required."));
        if (req.stops() == null || req.stops().size() < 2)
            return ResponseEntity.badRequest().body(Map.of("error", "At least 2 stops required."));

        ArrayList<Stations> stList  = new ArrayList<>();
        ArrayList<Date>     timeList = new ArrayList<>();

        for (RouteStopRequest stop : req.stops()) {
            Stations s = ts.findStationById(stop.stationId());
            if (s == null)
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Station not found: " + stop.stationId()));
            stList.add(s);
            timeList.add(parseTime(stop.time()));
        }

        Route r = ts.addAndReturnRoute(req.name().trim(), stList, timeList);
        return ResponseEntity.ok(toResponse(r));
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (!ts.removeRoute(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    // ── POST add a stop to an existing route ─────────────────────────────────
    @PostMapping("/{id}/stops")
    public ResponseEntity<?> addStop(@PathVariable int id, @RequestBody RouteStopRequest req) {
        Route r = ts.findRouteById(id);
        if (r == null) return ResponseEntity.notFound().build();
        Stations s = ts.findStationById(req.stationId());
        if (s == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Station not found."));
        r.addStationToRoute(s, parseTime(req.time()));
        return ResponseEntity.ok(toResponse(r));
    }

    // ── DELETE remove a stop from a route ─────────────────────────────────────
    @DeleteMapping("/{id}/stops/{stationId}")
    public ResponseEntity<?> removeStop(@PathVariable int id, @PathVariable int stationId) {
        Route r = ts.findRouteById(id);
        if (r == null) return ResponseEntity.notFound().build();
        Stations s = ts.findStationById(stationId);
        if (s == null) return ResponseEntity.notFound().build();
        if (!r.removeStationFromRoute(s))
            return ResponseEntity.badRequest().body(Map.of("error", "Station not on this route."));
        return ResponseEntity.ok(toResponse(r));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private RouteResponse toResponse(Route r) {
        List<RouteResponse.RouteStopResponse> stops = new ArrayList<>();
        ArrayList<Stations> ss = r.getStationOnTheRoute();
        ArrayList<Date>     ts = r.getArrivalTimes();
        for (int i = 0; i < ss.size(); i++) {
            String t = (i < ts.size() && ts.get(i) != null)
                    ? String.format("%tH:%<tM", ts.get(i)) : "";
            stops.add(new RouteResponse.RouteStopResponse(ss.get(i).getId(), ss.get(i).getName(), t));
        }
        return new RouteResponse(r.getId(), r.getRouteName(), stops);
    }

    private Date parseTime(String t) {
        if (t == null || !t.contains(":")) return new Date();
        String[] p = t.split(":");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(p[0]));
        c.set(Calendar.MINUTE,      Integer.parseInt(p[1]));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}

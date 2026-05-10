package org.example.Controller;

import org.example.DTO.BookingRequest;
import org.example.DTO.BookingResponse;
import org.example.Model.Booking;
import org.example.Model.Stations;
import org.example.Model.Train;
import org.example.Services.MailService;
import org.example.Services.TicketingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired private TicketingSystem ts;
    @Autowired private MailService     mail;

    // ── GET all bookings (optional ?trainId= filter) ──────────────────────────
    @GetMapping
    public List<BookingResponse> getAll(@RequestParam(required = false) Integer trainId) {
        Stream<Train> trainStream = trainId != null
                ? ts.getAllTrains().stream().filter(t -> t.getId() == trainId)
                : ts.getAllTrains().stream();

        return trainStream
                .flatMap(t -> t.getBookings().stream())
                .map(this::toResponse)
                .toList();
    }

    // ── POST create booking ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookingRequest req) {
        Train    train = ts.findTrainById(req.trainId());
        Stations from  = ts.findStationById(req.fromStationId());
        Stations to    = ts.findStationById(req.toStationId());

        if (train == null) return ResponseEntity.badRequest().body(Map.of("error", "Train not found."));
        if (from  == null) return ResponseEntity.badRequest().body(Map.of("error", "Departure station not found."));
        if (to    == null) return ResponseEntity.badRequest().body(Map.of("error", "Arrival station not found."));

        if (req.passengerName() == null || req.passengerName().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Passenger name is required."));
        if (req.email() == null || req.email().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required."));
        if (req.tickets() < 1)
            return ResponseEntity.badRequest().body(Map.of("error", "Tickets must be >= 1."));

        Booking booking;
        try {
            booking = ts.bookTickets(
                    req.passengerName().trim(), req.email().trim(),
                    train, from, to, req.tickets());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        // Send confirmation email (non-blocking — failure doesn't abort the booking)
        try { mail.sendBookingConfirmation(booking); }
        catch (Exception e) { /* log */ }

        return ResponseEntity.ok(toResponse(booking));
    }

    // ── Mapping helper ────────────────────────────────────────────────────────
    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getTrain().getId(),
                b.getTrain().getTrainName(),
                b.getStationToEnter().getId(),
                b.getStationToEnter().getName(),
                b.getStationToLeave().getId(),
                b.getStationToLeave().getName(),
                b.getPassengerName(),
                b.getPassengerEmail(),
                b.getNumberOfTickets(),
                String.format("%tF", b.getBookingDate()));
    }
}

package org.example.DTO;

public record TravelOptionResponse(
        String type,            // "DIRECT" or "CHANGEOVER"

        // ── Leg A (also the only leg for DIRECT) ─────────────────────────
        int    trainAId,
        String trainAName,
        int    fromId,
        String fromName,
        String depTime,         // "HH:MM"

        // ── Changeover point (null for DIRECT) ───────────────────────────
        Integer midId,
        String  midName,
        String  midArrTime,     // arrival on train A at mid
        String  midDepTime,     // departure on train B from mid

        // ── Leg B (null for DIRECT) ───────────────────────────────────────
        Integer trainBId,
        String  trainBName,

        // ── Destination ───────────────────────────────────────────────────
        int    toId,
        String toName,
        String arrTime,         // "HH:MM"

        // ── Seat availability ─────────────────────────────────────────────
        int availSeatsA,        // for DIRECT: total available; for CHANGEOVER: leg A
        Integer availSeatsB     // null for DIRECT, leg B for CHANGEOVER
) {}

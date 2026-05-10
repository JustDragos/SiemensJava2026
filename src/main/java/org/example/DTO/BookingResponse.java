package org.example.DTO;

public record BookingResponse(
        int id,
        int trainId,
        String trainName,
        int fromStationId,
        String fromStationName,
        int toStationId,
        String toStationName,
        String passengerName,
        String email,
        int tickets,
        String bookingDate
) {}

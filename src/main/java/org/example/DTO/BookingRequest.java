package org.example.DTO;
public record BookingRequest(int trainId, int fromStationId, int toStationId,
                             String passengerName, String email, int tickets) {}

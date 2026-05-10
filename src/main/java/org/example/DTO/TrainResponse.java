package org.example.DTO;

public record TrainResponse(
        int id,
        String name,
        Integer routeId,
        String routeName,
        int capacity,
        boolean delayed,
        int delayMinutes,
        int totalPassengers
) {}

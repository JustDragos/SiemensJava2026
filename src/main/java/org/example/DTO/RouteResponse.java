package org.example.DTO;

import java.util.List;

public record RouteResponse(
        int id,
        String name,
        List<RouteStopResponse> stops
) {
    public record RouteStopResponse(int stationId, String stationName, String time) {}
}

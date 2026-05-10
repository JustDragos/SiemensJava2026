package org.example.DTO;

import java.util.List;

public record RouteRequest(String name, List<RouteStopRequest> stops) {}

package org.example.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
    private static int idCounter = 1;
    private int id;
    private String routeName;
    private ArrayList<Stations> stationOnTheRoute;
    private ArrayList<Date> arrivalTimes; // one per station

    public Route(String routeName, ArrayList<Stations> stations, ArrayList<Date> arrivalTimes) {
        this.id = idCounter++;
        this.routeName = routeName;
        this.stationOnTheRoute = stations != null ? stations : new ArrayList<>();
        this.arrivalTimes = arrivalTimes != null ? arrivalTimes : new ArrayList<>();
    }

    private void ensureNotNull() {
        if (stationOnTheRoute == null) stationOnTheRoute = new ArrayList<>();
        if (arrivalTimes == null) arrivalTimes = new ArrayList<>();
    }

    public void addStationToRoute(Stations station, Date arrivalTime) {
        ensureNotNull();
        stationOnTheRoute.add(station);
        arrivalTimes.add(arrivalTime);
    }

    public boolean removeStationFromRoute(Stations station) {
        ensureNotNull();
        int idx = stationOnTheRoute.indexOf(station);
        if (idx >= 0) {
            stationOnTheRoute.remove(idx);
            if (idx < arrivalTimes.size()) arrivalTimes.remove(idx);
            return true;
        }
        return false;
    }

    public boolean stationIsInRoute(Stations station) {
        ensureNotNull();
        return stationOnTheRoute.contains(station);
    }

    public int indexOfStation(Stations station) {
        ensureNotNull();
        return stationOnTheRoute.indexOf(station);
    }

    public Date getArrivalAt(Stations station) {
        int idx = indexOfStation(station);
        if (idx >= 0 && idx < arrivalTimes.size()) return arrivalTimes.get(idx);
        return null;
    }

    public void updateArrivalTime(Stations station, Date newTime) {
        int idx = indexOfStation(station);
        if (idx >= 0 && idx < arrivalTimes.size()) arrivalTimes.set(idx, newTime);
    }

    /** Returns list of route segments [fromIdx, toIdx) — used for capacity tracking */
    public List<int[]> getSegmentsBetween(Stations from, Stations to) {
        int fromIdx = indexOfStation(from);
        int toIdx = indexOfStation(to);
        List<int[]> segments = new ArrayList<>();
        if (fromIdx < 0 || toIdx < 0 || fromIdx >= toIdx) return segments;
        for (int i = fromIdx; i < toIdx; i++) {
            segments.add(new int[]{i, i + 1});
        }
        return segments;
    }

    public int getId() { return id; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public ArrayList<Stations> getStationOnTheRoute() { ensureNotNull(); return stationOnTheRoute; }
    public ArrayList<Date> getArrivalTimes() { ensureNotNull(); return arrivalTimes; }

    @Override
    public String toString() {
        ensureNotNull();
        StringBuilder sb = new StringBuilder();
        sb.append("Route [").append(id).append("] ").append(routeName).append(": ");
        for (int i = 0; i < stationOnTheRoute.size(); i++) {
            sb.append(stationOnTheRoute.get(i).getName());
            if (i < arrivalTimes.size() && arrivalTimes.get(i) != null) {
                sb.append("(").append(String.format("%tH:%<tM", arrivalTimes.get(i))).append(")");
            }
            if (i < stationOnTheRoute.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }
}
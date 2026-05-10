package org.example.Services;

import jakarta.annotation.PostConstruct;
import org.example.Model.Booking;
import org.example.Model.Route;
import org.example.Model.Stations;
import org.example.Model.Train;
import org.example.Model.TrainPaths.ChangeOverPath;
import org.example.Model.TrainPaths.DirectPath;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicketingSystem {

    private final List<Train>    trains   = new ArrayList<>();
    private final List<Stations> stations = new ArrayList<>();
    private final List<Route>    routes   = new ArrayList<>();

    // ── Seed data ────────────────────────────────────────────────────────────
    @PostConstruct
    public void seedData() {

        // ── Stations ─────────────────────────────────────────────────────────
        Stations bucharest = addAndReturnStation("Bucharest Nord");
        Stations ploiesti  = addAndReturnStation("Ploiesti");
        Stations sinaia    = addAndReturnStation("Sinaia");
        Stations brasov    = addAndReturnStation("Brasov");
        Stations sibiu     = addAndReturnStation("Sibiu");
        Stations cluj      = addAndReturnStation("Cluj-Napoca");
        Stations oradea    = addAndReturnStation("Oradea");
        Stations timisoara = addAndReturnStation("Timisoara Nord");
        Stations arad      = addAndReturnStation("Arad");
        Stations iasi      = addAndReturnStation("Iasi");
        Stations bacau     = addAndReturnStation("Bacau");
        Stations suceava   = addAndReturnStation("Suceava");
        Stations constanta = addAndReturnStation("Constanta");
        Stations medgidia  = addAndReturnStation("Medgidia");
        Stations craiova   = addAndReturnStation("Craiova");
        Stations pitesti   = addAndReturnStation("Pitesti");

        // ── Routes ───────────────────────────────────────────────────────────

        // 1. Bucharest -> Sinaia -> Brasov  (mountain corridor)
        Route rCarpathian = addAndReturnRoute("Carpathian Express",
                new ArrayList<>(List.of(bucharest, ploiesti, sinaia, brasov)),
                new ArrayList<>(List.of(makeTime(6,0), makeTime(7,10), makeTime(8,5), makeTime(9,0))));

        // 2. Brasov -> Sibiu -> Cluj-Napoca  (Transylvania)
        Route rTransylvania = addAndReturnRoute("Transylvania Arrow",
                new ArrayList<>(List.of(brasov, sibiu, cluj)),
                new ArrayList<>(List.of(makeTime(9,30), makeTime(11,15), makeTime(13,20))));

        // 3. Cluj-Napoca -> Oradea
        Route rWestern = addAndReturnRoute("Western Link",
                new ArrayList<>(List.of(cluj, oradea)),
                new ArrayList<>(List.of(makeTime(7,0), makeTime(9,0))));

        // 4. Bucharest -> Pitesti -> Craiova  (Oltenia)
        Route rOltenia = addAndReturnRoute("Oltenia Intercity",
                new ArrayList<>(List.of(bucharest, pitesti, craiova)),
                new ArrayList<>(List.of(makeTime(7,30), makeTime(8,55), makeTime(10,30))));

        // 5. Bucharest -> Medgidia -> Constanta  (Black Sea)
        Route rBlackSea = addAndReturnRoute("Black Sea Express",
                new ArrayList<>(List.of(bucharest, medgidia, constanta)),
                new ArrayList<>(List.of(makeTime(8,0), makeTime(9,50), makeTime(10,30))));

        // 6. Bucharest -> Bacau -> Suceava -> Iasi  (Moldova corridor)
        Route rMoldova = addAndReturnRoute("Moldova Express",
                new ArrayList<>(List.of(bucharest, bacau, suceava, iasi)),
                new ArrayList<>(List.of(makeTime(10,0), makeTime(13,0), makeTime(14,30), makeTime(15,45))));

        // 7. Timisoara -> Arad -> Cluj  (Banat-Transylvania)
        Route rBanat = addAndReturnRoute("Banat Corridor",
                new ArrayList<>(List.of(timisoara, arad, cluj)),
                new ArrayList<>(List.of(makeTime(6,30), makeTime(7,20), makeTime(10,10))));

        // 8. Brasov -> Sinaia -> Ploiesti -> Bucharest  (return Carpathian)
        Route rCarpathianReturn = addAndReturnRoute("Carpathian Return",
                new ArrayList<>(List.of(brasov, sinaia, ploiesti, bucharest)),
                new ArrayList<>(List.of(makeTime(16,0), makeTime(16,55), makeTime(17,50), makeTime(19,0))));

        // ── Trains ───────────────────────────────────────────────────────────
        Train dacia       = addAndReturnTrain("IC 501 Dacia",        rCarpathian,       160);
        Train daciaReturn = addAndReturnTrain("IC 502 Dacia",        rCarpathianReturn, 160);
        Train adyEndre    = addAndReturnTrain("EC 65 Ady Endre",     rTransylvania,     180);
        Train regional1   = addAndReturnTrain("R 301",               rWestern,          100);
        Train oltul       = addAndReturnTrain("IC 701 Oltul",        rOltenia,          140);
        Train blackSea    = addAndReturnTrain("IC 811 Marea Neagra", rBlackSea,         200);
        Train moldovaEx   = addAndReturnTrain("IC 401 Moldova",      rMoldova,          160);
        Train regional2   = addAndReturnTrain("R 551",               rBanat,            120);

        // ── Sample bookings ───────────────────────────────────────────────────
        bookTickets("Ana Ionescu",     "ana@example.com",    dacia,    bucharest, brasov,    2);
        bookTickets("Mihai Popescu",   "mihai@example.com",  dacia,    ploiesti,  brasov,    1);
        bookTickets("Elena Dumitrescu","elena@example.com",  blackSea, bucharest, constanta, 3);
        bookTickets("Ion Stanciu",     "ion@example.com",    moldovaEx,bucharest, iasi,      1);
        bookTickets("Maria Constantin","maria@example.com",  adyEndre, brasov,    cluj,      2);
        bookTickets("Andrei Gheorghe", "andrei@example.com", oltul,    bucharest, craiova,   1);
    }

    private Date makeTime(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    // ── Station management ───────────────────────────────────────────────────
    public Stations addAndReturnStation(String name) {
        Stations s = new Stations(name);
        stations.add(s);
        return s;
    }

    public boolean removeStation(int id) {
        return stations.removeIf(s -> s.getId() == id);
    }

    public Stations findStationById(int id) {
        return stations.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public Stations findStationByName(String name) {
        return stations.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public List<Stations> getAllStations() { return stations; }

    // ── Route management ─────────────────────────────────────────────────────
    public Route addAndReturnRoute(String name, ArrayList<Stations> stationsList, ArrayList<Date> times) {
        Route r = new Route(name, stationsList, times);
        routes.add(r);
        return r;
    }

    public boolean removeRoute(int id) {
        return routes.removeIf(r -> r.getId() == id);
    }

    public Route findRouteById(int id) {
        return routes.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    public List<Route> getAllRoutes() { return routes; }

    // ── Train management ─────────────────────────────────────────────────────
    public Train addAndReturnTrain(String name, Route route, int capacity) {
        Train t = new Train(name, route, capacity);
        trains.add(t);
        return t;
    }

    public boolean removeTrain(int id) {
        return trains.removeIf(t -> t.getId() == id);
    }

    public Train findTrainById(int id) {
        return trains.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    public List<Train> getAllTrains() { return trains; }

    // ── Booking ──────────────────────────────────────────────────────────────
    /**
     * Creates and registers a booking. Email notification is the caller's responsibility.
     * Returns null (with reason) if the booking cannot be made.
     */
    public Booking bookTickets(String passengerName, String email,
                               Train train, Stations from, Stations to,
                               int numberOfTickets) {
        Route route = train.getRouteOfTrain();
        if (route == null)           throw new IllegalArgumentException("Train has no route assigned.");

        int fromIdx = route.indexOfStation(from);
        int toIdx   = route.indexOfStation(to);
        if (fromIdx < 0)             throw new IllegalArgumentException("Departure station not on this train's route.");
        if (toIdx   < 0)             throw new IllegalArgumentException("Arrival station not on this train's route.");
        if (fromIdx >= toIdx)        throw new IllegalArgumentException("Departure must come before arrival on the route.");

        if (!train.canBook(from, to, numberOfTickets)) {
            int available = train.getAvailableSeats(from, to);
            throw new IllegalStateException("Not enough seats. Available on this segment: " + available);
        }

        Booking booking = new Booking(passengerName, email, from, to, train, numberOfTickets);
        train.addBooking(booking);
        return booking;
    }

    // ── Travel options ───────────────────────────────────────────────────────

    /** Direct trains from → to */
    public List<DirectPath> findDirectPaths(Stations from, Stations to) {
        List<DirectPath> results = new ArrayList<>();
        for (Train train : trains) {
            Route route = train.getRouteOfTrain();
            if (route == null) continue;
            int fi = route.indexOfStation(from);
            int ti = route.indexOfStation(to);
            if (fi >= 0 && ti >= 0 && fi < ti) {
                results.add(new DirectPath(from, to, route, train));
            }
        }
        return results;
    }

    /** One-changeover paths from → mid → to */
    public List<ChangeOverPath> findChangeoverPaths(Stations from, Stations to) {
        List<ChangeOverPath> results = new ArrayList<>();
        for (Train tA : trains) {
            Route rA = tA.getRouteOfTrain();
            if (rA == null) continue;
            int fiA = rA.indexOfStation(from);
            if (fiA < 0) continue;

            for (Train tB : trains) {
                if (tA == tB) continue;
                Route rB = tB.getRouteOfTrain();
                if (rB == null) continue;
                int tiB = rB.indexOfStation(to);
                if (tiB < 0) continue;

                for (Stations mid : rA.getStationOnTheRoute()) {
                    int midA = rA.indexOfStation(mid);
                    int midB = rB.indexOfStation(mid);
                    if (midA <= fiA || midB < 0 || midB >= tiB) continue;

                    Date arrMidA  = rA.getArrivalAt(mid);
                    Date depMidB  = rB.getArrivalAt(mid);
                    boolean ok = (arrMidA == null || depMidB == null || arrMidA.before(depMidB));
                    if (!ok) continue;

                    results.add(new ChangeOverPath(from, to, rA, rB, tA, tB, mid));
                }
            }
        }
        return results;
    }

    // ── Delay ────────────────────────────────────────────────────────────────
    /** Marks the train delayed. Callers are responsible for sending notifications. */
    public void setTrainDelay(Train train, int delayMinutes) {
        train.setDelayed(true, delayMinutes);
    }

    public void clearTrainDelay(Train train) {
        train.setDelayed(false, 0);
    }

    // ── Kept for console app (Main.java) ─────────────────────────────────────
    public String createDirectPath(DirectPath dp) {
        Date dep = dp.getRoute().getArrivalAt(dp.getFrom());
        Date arr = dp.getRoute().getArrivalAt(dp.getTo());
        int avail = dp.getTrain().getAvailableSeats(dp.getFrom(), dp.getTo());
        return String.format("[DIRECT] Train: %s | %s %s -> %s %s | Available seats: %d",
                dp.getTrain().getTrainName(),
                dp.getFrom().getName(), dep != null ? String.format("%tH:%<tM", dep) : "N/A",
                dp.getTo().getName(),   arr != null ? String.format("%tH:%<tM", arr) : "N/A",
                avail);
    }

    public String createChangeOverPath(ChangeOverPath co) {
        Date dep  = co.getRouteA().getArrivalAt(co.getFrom());
        Date arr  = co.getRouteB().getArrivalAt(co.getTo());
        int availA = co.getTrainA().getAvailableSeats(co.getFrom(), co.getMid());
        int availB = co.getTrainB().getAvailableSeats(co.getMid(), co.getTo());
        return String.format(
                "[CHANGEOVER] Train %s (%s->%s, dep %s) + Train %s (%s->%s, arr %s) | Seats: %d / %d",
                co.getTrainA().getTrainName(), co.getFrom().getName(), co.getMid().getName(),
                dep != null ? String.format("%tH:%<tM", dep) : "N/A",
                co.getTrainB().getTrainName(), co.getMid().getName(), co.getTo().getName(),
                arr != null ? String.format("%tH:%<tM", arr) : "N/A",
                availA, availB);
    }

    public List<String> findTravelOptions(Stations from, Stations to) {
        List<String> options = new ArrayList<>();
        findDirectPaths(from, to).forEach(dp -> options.add(createDirectPath(dp)));
        findChangeoverPaths(from, to).forEach(co -> options.add(createChangeOverPath(co)));
        return options;
    }
}
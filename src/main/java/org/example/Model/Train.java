package org.example.Model;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private static int idCounter = 1;
    private int id;
    private String trainName;
    private Route routeOfTrain;
    private List<Booking> bookings;
    private int capacity;
    private boolean delayed;
    private int delayMinutes;

    public Train(String trainName, Route routeOfTrain, int capacity) {
        this.id = idCounter++;
        this.trainName = trainName;
        this.routeOfTrain = routeOfTrain;
        this.capacity = capacity;
        this.bookings = new ArrayList<>();
        this.delayed = false;
        this.delayMinutes = 0;
    }

    /**
     * Returns the maximum number of passengers occupying the train
     * at any single segment between fromStation and toStation.
     * This is used to check overbooking.
     */
    public int getOccupancyOnSegment(Stations from, Stations to) {
        if (routeOfTrain == null) return 0;
        List<int[]> requestedSegments = routeOfTrain.getSegmentsBetween(from, to);
        if (requestedSegments.isEmpty()) return 0;

        // For each segment, count tickets from all bookings that overlap it
        int maxOccupancy = 0;
        for (int[] seg : requestedSegments) {
            int segOccupancy = 0;
            for (Booking b : bookings) {
                List<int[]> bookingSegments = routeOfTrain.getSegmentsBetween(
                        b.getStationToEnter(), b.getStationToLeave());
                for (int[] bSeg : bookingSegments) {
                    if (bSeg[0] == seg[0] && bSeg[1] == seg[1]) {
                        segOccupancy += b.getNumberOfTickets();
                        break;
                    }
                }
            }
            if (segOccupancy > maxOccupancy) maxOccupancy = segOccupancy;
        }
        return maxOccupancy;
    }

    public int getAvailableSeats(Stations from, Stations to) {
        return capacity - getOccupancyOnSegment(from, to);
    }

    public boolean canBook(Stations from, Stations to, int tickets) {
        return getAvailableSeats(from, to) >= tickets;
    }

    public void addBooking(Booking b) { bookings.add(b); }

    public boolean removeBooking(Booking b) { return bookings.remove(b); }

    public void setDelayed(boolean delayed, int minutes) {
        this.delayed = delayed;
        this.delayMinutes = minutes;
    }

    public int getId() { return id; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public Route getRouteOfTrain() { return routeOfTrain; }
    public void setRouteOfTrain(Route routeOfTrain) { this.routeOfTrain = routeOfTrain; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public List<Booking> getBookings() { return bookings; }
    public boolean isDelayed() { return delayed; }
    public int getDelayMinutes() { return delayMinutes; }

    @Override
    public String toString() {
        String status = delayed ? " [DELAYED " + delayMinutes + " min]" : "";
        return String.format("Train [%d] %s | Capacity: %d | Route: %s%s",
                id, trainName, capacity,
                routeOfTrain != null ? routeOfTrain.getRouteName() : "None",
                status);
    }
}
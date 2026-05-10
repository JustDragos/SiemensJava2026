package org.example.Model;

import java.util.Date;

public class Booking {
    private static int idCounter = 1;
    private int id;
    private String passengerName;
    private String passengerEmail;
    private Stations stationToEnter;
    private Stations stationToLeave;
    private Train train;
    private Date bookingDate;
    private int numberOfTickets;

    public Booking(String passengerName, String passengerEmail,
                   Stations stationToEnter, Stations stationToLeave,
                   Train train, int numberOfTickets) {
        this.id = idCounter++;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.stationToEnter = stationToEnter;
        this.stationToLeave = stationToLeave;
        this.train = train;
        this.numberOfTickets = numberOfTickets;
        this.bookingDate = new Date();
    }

    public int getId() { return id; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public Stations getStationToEnter() { return stationToEnter; }
    public Stations getStationToLeave() { return stationToLeave; }
    public Train getTrain() { return train; }
    public Date getBookingDate() { return bookingDate; }
    public int getNumberOfTickets() { return numberOfTickets; }

    @Override
    public String toString() {
        return String.format(
                "Booking #%d | Passenger: %s | Email: %s | Train: %s | From: %s -> To: %s | Tickets: %d | Booked on: %tF",
                id, passengerName, passengerEmail,
                train.getTrainName(),
                stationToEnter.getName(), stationToLeave.getName(),
                numberOfTickets, bookingDate
        );
    }
}
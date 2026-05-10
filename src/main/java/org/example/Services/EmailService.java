package org.example.Services;

import org.example.Model.Booking;

public class EmailService {

    public static void sendBookingConfirmation(Booking booking) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              [EMAIL SENT - Booking Confirmation]     ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║  To:      %-42s ║%n", booking.getPassengerEmail());
        System.out.printf("║  Subject: %-42s ║%n", "Booking Confirmed - Train " + booking.getTrain().getTrainName());
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║  Dear %-47s ║%n", booking.getPassengerName() + ",");
        System.out.printf("║  Your booking #%-37d ║%n", booking.getId());
        System.out.printf("║  From: %-45s ║%n", booking.getStationToEnter().getName());
        System.out.printf("║  To:   %-45s ║%n", booking.getStationToLeave().getName());
        System.out.printf("║  Tickets: %-42d ║%n", booking.getNumberOfTickets());
        System.out.println("║  Have a pleasant journey!                            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    public static void sendDelayNotification(Booking booking, int delayMinutes) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              [EMAIL SENT - Delay Notification]       ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║  To:      %-42s ║%n", booking.getPassengerEmail());
        System.out.printf("║  Subject: %-42s ║%n", "Train Delay - " + booking.getTrain().getTrainName());
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║  Dear %-47s ║%n", booking.getPassengerName() + ",");
        System.out.printf("║  Train %-46s ║%n", booking.getTrain().getTrainName() + " is delayed.");
        System.out.printf("║  Delay: %-44s ║%n", delayMinutes + " minutes");
        System.out.println("║  We apologize for the inconvenience.                 ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
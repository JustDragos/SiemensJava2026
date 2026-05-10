package org.example.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.Model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendBookingConfirmation(Booking b) throws MessagingException {
        send(b.getPassengerEmail(),
                "Booking Confirmed – Train " + b.getTrain().getTrainName(),
                confirmationHtml(b));
    }
    public void sendDelayNotification(Booking b, int delayMinutes) throws MessagingException {
        send(b.getPassengerEmail(),
                "Train Delay – " + b.getTrain().getTrainName(),
                delayHtml(b, delayMinutes));
    }
    private String delayHtml(Booking b, int minutes) {
        return """
            <div style="font-family:sans-serif;max-width:520px;margin:auto;border:1px solid #e5e5e5;border-radius:10px;overflow:hidden">
              <div style="background:#a32d2d;padding:24px 28px">
                <h2 style="color:#fff;margin:0;font-size:18px">&#x26A0; Train Delay Notice</h2>
              </div>
              <div style="padding:24px 28px">
                <p>Dear <strong>%s</strong>,</p>
                <p>We regret to inform you that your train is delayed.</p>
                <table style="width:100%%;border-collapse:collapse;font-size:14px">
                  <tr style="background:#f5f5f4"><td style="padding:10px 12px;font-weight:500">Train</td><td style="padding:10px 12px">%s</td></tr>
                  <tr><td style="padding:10px 12px;font-weight:500">Delay</td><td style="padding:10px 12px">%d minutes</td></tr>
                  <tr style="background:#f5f5f4"><td style="padding:10px 12px;font-weight:500">Your journey</td><td style="padding:10px 12px">%s → %s</td></tr>
                </table>
                <p style="color:#888;font-size:13px;margin-top:20px">We apologise for the inconvenience.</p>
              </div>
            </div>
            """.formatted(b.getPassengerName(), b.getTrain().getTrainName(), minutes,
                b.getStationToEnter().getName(), b.getStationToLeave().getName());
    }
    private void send(String to, String subject, String html) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(msg);
    }

    private String confirmationHtml(Booking b) {
        return """
            <div style="font-family:sans-serif;max-width:520px;margin:auto;border:1px solid #e5e5e5;border-radius:10px;overflow:hidden">
              <div style="background:#1a1a18;padding:24px 28px">
                <h2 style="color:#f5f0e8;margin:0;font-size:18px">&#x1F686; Booking Confirmed</h2>
              </div>
              <div style="padding:24px 28px">
                <p>Dear <strong>%s</strong>,</p>
                <p>Your ticket has been booked successfully.</p>
                <table style="width:100%%;border-collapse:collapse;font-size:14px">
                  <tr style="background:#f5f5f4"><td style="padding:10px 12px;font-weight:500">Booking ID</td><td style="padding:10px 12px">#%d</td></tr>
                  <tr><td style="padding:10px 12px;font-weight:500">Train</td><td style="padding:10px 12px">%s</td></tr>
                  <tr style="background:#f5f5f4"><td style="padding:10px 12px;font-weight:500">From</td><td style="padding:10px 12px">%s</td></tr>
                  <tr><td style="padding:10px 12px;font-weight:500">To</td><td style="padding:10px 12px">%s</td></tr>
                  <tr style="background:#f5f5f4"><td style="padding:10px 12px;font-weight:500">Tickets</td><td style="padding:10px 12px">%d</td></tr>
                </table>
              </div>
            </div>
            """.formatted(b.getPassengerName(), b.getId(), b.getTrain().getTrainName(),
                b.getStationToEnter().getName(), b.getStationToLeave().getName(),
                b.getNumberOfTickets());
    }
}
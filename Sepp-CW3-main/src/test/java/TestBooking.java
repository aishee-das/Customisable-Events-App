import command.BookEventCommand;
import command.CancelBookingCommand;
import command.LogoutCommand;
import command.UpdateConsumerProfileCommand;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestBooking extends ConsoleTest{

    Booking booking;
    Event event;
    Consumer consumer;
    LocalDateTime time = LocalDateTime.now();

    @BeforeEach
    void setup(){
         consumer = new Consumer(
                "james",
                "james@gmail.com",
                "69",
                "55.948171 -3.180945",
                "joe"
        );

         event = new Event(1,
                "A Christmas Carol",
                EventType.Theatre,
                25,
                20,
                "55.94373085057988, -3.1887541458174073",
                "Pay",
                LocalDateTime.now().plusHours(23),
                LocalDateTime.now().plusHours(24),
                new EventTagCollection()
        );

        booking = new Booking(
                1,
                consumer,
                event,
                1,
                time
        );
    }

    @AfterEach
    void tearDown() {booking = null;}

    @Test
    void booking_get_booking_number() throws IllegalAccessException, NoSuchFieldException {
        final Field bookingNumber = booking.getClass().getDeclaredField("bookingNumber");
        bookingNumber.setAccessible(true);
        long result = booking.getBookingNumber();
        assertEquals(result, bookingNumber.get(booking));
    }

    @Test
    void booking_get_booking_status() throws IllegalAccessException, NoSuchFieldException {
        final Field bookingStatus = booking.getClass().getDeclaredField("status");
        bookingStatus.setAccessible(true);
        BookingStatus result = booking.getStatus();
        assertEquals(result, bookingStatus.get(booking));
    }

    @Test
    void booking_get_event() throws IllegalAccessException, NoSuchFieldException {
        final Field bookingEvent = booking.getClass().getDeclaredField("event");
        bookingEvent.setAccessible(true);
        Event result = booking.getEvent();
        assertEquals(result, bookingEvent.get(booking));
    }

    @Test
    void booking_get_num_tickets() throws IllegalAccessException, NoSuchFieldException {
        final Field bookingTickets = booking.getClass().getDeclaredField("numTickets");
        bookingTickets.setAccessible(true);
        int result = booking.getNumTickets();
        assertEquals(result, bookingTickets.get(booking));
    }

    @Test
    void booking_get_booking_date_time() throws IllegalAccessException, NoSuchFieldException {
        final Field bookingDateTime = booking.getClass().getDeclaredField("bookingDateTime");
        bookingDateTime.setAccessible(true);
        LocalDateTime result = booking.getBookingDateTime();
        assertEquals(result, bookingDateTime.get(booking));
    }

    @Test
    void booking_get_booker() throws IllegalAccessException, NoSuchFieldException {
        final Field booker = booking.getClass().getDeclaredField("booker");
        booker.setAccessible(true);
        Consumer result = booking.getBooker();
        assertEquals(result, booker.get(booking));
    }

    @Test
    void booking_to_string_test() {
        String expected = "Booking{" +
                "status=" + BookingStatus.Active +
                ", bookingNumber=" + 1 +
                ", booker=" + consumer.getName() +
                ", event=" + event +
                ", numTickets=" + 1 +
                ", bookingDateTime=" + time +
                '}';
        assertEquals(expected, booking.toString());
    }

    @Test
    void booking_set_status() throws IllegalAccessException, NoSuchFieldException {
        BookingStatus status = BookingStatus.CancelledByConsumer;
        booking.setStatus(status);
        final Field statusField = booking.getClass().getDeclaredField("status");
        statusField.setAccessible(true);
        assertEquals(statusField.get(booking), status);
    }

    @Test
    void booking_set_booking_time() throws IllegalAccessException, NoSuchFieldException {
        LocalDateTime time = LocalDateTime.now().plusHours(2);
        booking.setBookingDateTime(time);
        final Field field = booking.getClass().getDeclaredField("bookingDateTime");
        field.setAccessible(true);
        assertEquals(field.get(booking), time);
    }

    @Test
    void booking_cancel_by_consumer() throws IllegalAccessException, NoSuchFieldException {
        BookingStatus expectedStatus = BookingStatus.CancelledByConsumer;
        booking.cancelByConsumer();
        assertEquals(expectedStatus, booking.getStatus());
    }

    @Test
    void booking_cancel_by_provider() throws IllegalAccessException, NoSuchFieldException {
        BookingStatus expectedStatus = BookingStatus.CancelledByProvider;
        booking.cancelByProvider();
        assertEquals(expectedStatus, booking.getStatus());
    }

}

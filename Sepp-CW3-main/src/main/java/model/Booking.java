package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * {@link Booking} represents a booking made by a {@link Consumer} for an {@link Event}.
 */

//Added Serializable for SaveAppStateCommand
public class Booking implements Serializable {
    private final long bookingNumber;
    private final Consumer booker;
    private final Event event;
    private final int numTickets;
    private LocalDateTime bookingDateTime;
    private BookingStatus status;

    /**
     * @param bookingNumber   unique identifier for this booking
     * @param booker          the {@link Consumer} who made this booking
     * @param event           the {@link Event} this booking is for
     * @param numTickets      the number of booked tickets
     * @param bookingDateTime the date and time when this booking was made
     */
    public Booking(long bookingNumber,
                   Consumer booker,
                   Event event,
                   int numTickets,
                   LocalDateTime bookingDateTime) {
        this.status = BookingStatus.Active;
        this.booker = booker;
        this.event = event;
        this.bookingNumber = bookingNumber;
        this.numTickets = numTickets;
        this.bookingDateTime = bookingDateTime;
    }

    public long getBookingNumber() {
        return bookingNumber;
    }
    public LocalDateTime getBookingDateTime() {return bookingDateTime;}

    public BookingStatus getStatus() {
        return status;
    }

    public Consumer getBooker() {
        return booker;
    }

    public Event getEvent() {
        return event;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public void setStatus(BookingStatus status) {this.status = status;}

    public void setBookingDateTime(LocalDateTime time){this.bookingDateTime = time;}

    /**
     * Sets the {@link #status} to {@link BookingStatus#CancelledByConsumer}.
     */
    public void cancelByConsumer() {
        this.status = BookingStatus.CancelledByConsumer;
    }

    /**
     * Sets the {@link #status} to {@link BookingStatus#CancelledByProvider}.
     */
    public void cancelByProvider() {
        this.status = BookingStatus.CancelledByProvider;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "status=" + status +
                ", bookingNumber=" + bookingNumber +
                ", booker=" + booker.getName() +
                ", event=" + event +
                ", numTickets=" + numTickets +
                ", bookingDateTime=" + bookingDateTime +
                '}';
    }
}

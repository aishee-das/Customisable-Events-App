package state;

import model.Booking;
import model.Consumer;
import model.Event;

import java.util.List;

/**
 * {@link IBookingState} is an interface representing the portion of application state that contains all the
 * {@link Booking} information.
 */
public interface IBookingState {
    /**
     * Get a {@link Booking} with the specified booking number
     *
     * @param bookingNumber unique booking identifier to look up in the booking state
     * @return {@link Booking} corresponding to the bookingNumber if there is one, and null otherwise
     */
    Booking findBookingByNumber(long bookingNumber);

    /**
     * @return List of all registered {@link Booking}s in the application
     */
    List<Booking> getAllBookings();

    /**
     * Get a list of all the {@link Booking}s for a {@link Event} with the given event number
     *
     * @param eventNumber unique event identifier to find bookings for
     * @return List of {@link Booking}s for the {@link Event} corresponding to the provided event number
     */
    List<Booking> findBookingsByEventNumber(long eventNumber);

    /**
     * Create a new {@link Booking} (includes generating a new unique booking number) and add it to the booking state
     *
     * @param booker     {@link Consumer} who made the booking
     * @param event      {@link Event} that the booking is for
     * @param numTickets number of tickets booked
     * @return The newly created {@link Booking} instance
     */
    Booking createBooking(Consumer booker, Event event, int numTickets);
}

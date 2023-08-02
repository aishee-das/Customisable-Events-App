package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * {@link BookEventCommand} allows {@link model.Consumer Consumers} to book tickets for an
 * {@link Event}. The command applies for the currently logged-in user.
 */
public class BookEventCommand implements ICommand<Booking> {
    private final long eventNumber;
    private final int numTicketsRequested;
    private Booking bookingResult;

    /**
     * @param eventNumber         identifier of the {@link Event} to book
     * @param numTicketsRequested number of tickets to book
     */
    public BookEventCommand(long eventNumber, int numTicketsRequested) {
        this.eventNumber = eventNumber;
        this.numTicketsRequested = numTicketsRequested;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that event number corresponds to an existing event
     * @verifies.that the event is active
     * @verifies.that number of requested tickets is not less than 1
     * @verifies.that the selected event has not ended yet
     * @verifies.that the requested number of tickets are still available
     * @verifies.that if the ticket price is greater than 0, the payment is successful before creating the booking
     */
    @Override
    public void execute(Context context, IView view) {

        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Consumer)) {
            view.displayFailure("BookEventCommand",
                    LogStatus.BOOK_EVENT_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            bookingResult = null;
            return;
        }

        Consumer consumer = (Consumer) currentUser;

        Event event = context.getEventState().findEventByNumber(eventNumber);
        if (event == null) {
            view.displayFailure(
                    "BookEventCommand",
                    LogStatus.BOOK_EVENT_EVENT_NOT_FOUND,
                    Map.of("eventNumber", eventNumber)
            );
            bookingResult = null;
            return;
        }

        if (event.getStatus() != EventStatus.ACTIVE) {
            view.displayFailure(
                    "BookEventCommand",
                    LogStatus.BOOK_EVENT_EVENT_NOT_ACTIVE,
                    Map.of("eventNumber", eventNumber)
            );
            bookingResult = null;
            return;
        }

        if (numTicketsRequested < 1) {
            view.displayFailure(
                    "BookEventCommand",
                    LogStatus.BOOK_EVENT_INVALID_NUM_TICKETS,
                    Map.of("numTicketsRequested", numTicketsRequested)
            );
            bookingResult = null;
            return;
        }

        if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
            view.displayFailure(
                    "BookEventCommand",
                    LogStatus.BOOK_EVENT_ALREADY_OVER,
                    Map.of("eventNumber", eventNumber,
                            "DateTime.now", LocalDateTime.now(),
                            "performance.endDateTime", event.getEndDateTime())
            );
            bookingResult = null;
            return;
        }

        int numTicketsLeft = event.getNumTicketsLeft();
        if (numTicketsLeft < numTicketsRequested) {
            view.displayFailure(
                    "BookEventCommand",
                    LogStatus.BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT,
                    Map.of("eventNumber", eventNumber,
                            "numTicketsRequested", numTicketsRequested,
                            "numTicketsLeft", numTicketsLeft)
            );
            bookingResult = null;
            return;
        }

        int ticketPrice = event.getTicketPriceInPence();
        if (ticketPrice > 0) {
            int amountToPay = numTicketsRequested * ticketPrice;
            boolean paymentSucceeded = context.getPaymentSystem().processPayment(
                    consumer.getEmail(),
                    context.getOrgEmail(),
                    amountToPay
            );

            if (!paymentSucceeded) {
                view.displayFailure(
                        "BookEventCommand",
                        LogStatus.BOOK_EVENT_PAYMENT_FAILED,
                        Map.of("eventNumber", eventNumber,
                                "numTicketsRequested", numTicketsRequested,
                                "ticketPrice", ticketPrice)
                );
                bookingResult = null;
                return;
            }
        }

        Booking booking = context.getBookingState().createBooking(consumer, event, numTicketsRequested);
        consumer.addBooking(booking);
        event.setNumTicketsLeft(numTicketsLeft - numTicketsRequested);
        view.displaySuccess(
                "BookEventCommand",
                LogStatus.BOOK_EVENT_SUCCESS,
                Map.of("eventNumber", eventNumber,
                        "numTicketsRequested", numTicketsRequested)
        );
        bookingResult = booking;

    }

    /**
     * @return A unique booking number corresponding to a {@link Booking} if successful and null otherwise
     */
    @Override
    public Booking getResult() {
        return bookingResult;
    }

    private enum LogStatus {
        BOOK_EVENT_SUCCESS,
        BOOK_EVENT_USER_NOT_CONSUMER,
        BOOK_EVENT_EVENT_NOT_FOUND,
        BOOK_EVENT_EVENT_NOT_ACTIVE,
        BOOK_EVENT_ALREADY_OVER,
        BOOK_EVENT_INVALID_NUM_TICKETS,
        BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT,
        BOOK_EVENT_PAYMENT_FAILED,
    }
}

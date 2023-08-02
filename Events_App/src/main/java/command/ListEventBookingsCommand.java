package command;

import controller.Context;
import model.Booking;
import model.Staff;
import model.Event;
import model.User;
import view.IView;

import java.util.List;
import java.util.Map;

/**
 * {@link ListEventBookingsCommand} allows an event organiser ({@link Staff})
 * to get a list of all {@link Booking}s for a chosen {@link Event}.
 */
public class ListEventBookingsCommand implements ICommand<List<Booking>> {
    private final long eventNumber;
    private List<Booking> bookingListResult;

    /**
     * @param eventNumber identifier of the {@link Event} to look up {@link Booking}s for
     */
    public ListEventBookingsCommand(long eventNumber) {
        this.eventNumber = eventNumber;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current user is logged in
     * @verifies.that the event identifier corresponds to an existing event
     * @verifies.that the event is a ticketed event
     * @verifies.that currently logged-in user is either a government representative or the organiser of the event
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "ListEventBookingsCommand",
                    LogStatus.LIST_EVENT_BOOKINGS_USER_NOT_STAFF,
                    Map.of("eventNumber", eventNumber,
                            "currentUser", currentUser != null ? currentUser : "none")
            );
            bookingListResult = null;
            return;
        }

        Event event = context.getEventState().findEventByNumber(eventNumber);
        if (event == null) {
            view.displayFailure(
                    "ListEventBookingsCommand",
                    LogStatus.LIST_EVENT_BOOKINGS_EVENT_NOT_FOUND,
                    Map.of("eventNumber", eventNumber)
            );
            bookingListResult = null;
            return;
        }

        view.displaySuccess(
                "ListEventBookingsCommand",
                LogStatus.LIST_EVENT_BOOKINGS_SUCCESS,
                Map.of("eventNumber", eventNumber)
        );
        bookingListResult = context.getBookingState().findBookingsByEventNumber(eventNumber);
    }

    /**
     * @return List of {@link Booking}s if successful and null otherwise
     */
    @Override
    public List<Booking> getResult() {
        return bookingListResult;
    }

    private enum LogStatus {
        LIST_EVENT_BOOKINGS_USER_NOT_STAFF,
        LIST_EVENT_BOOKINGS_EVENT_NOT_FOUND,
        LIST_EVENT_BOOKINGS_SUCCESS,
    }
}

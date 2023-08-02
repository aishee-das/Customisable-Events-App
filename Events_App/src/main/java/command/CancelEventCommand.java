package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * {@link CancelEventCommand} allows {@link Staff} members to cancel a previously added
 * {@link Event}. This cancels and refunds all bookings linked to the event.
 */
public class CancelEventCommand implements ICommand<Boolean> {
    private final long eventNumber;
    private final String organiserMessage;
    private Boolean successResult;

    /**
     * @param eventNumber      identifier of the {@link Event} to cancel
     * @param organiserMessage message from the organiser to the {@link Consumer}s who had
     *                         {@link Booking}s for the event
     */
    public CancelEventCommand(long eventNumber, String organiserMessage) {
        this.eventNumber = eventNumber;
        this.organiserMessage = organiserMessage;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Staff member
     * @verifies.that provided event number corresponds to an existing event
     * @verifies.that the event is active
     * @verifies.that the event has not already started
     * @verifies.that the organiser message is not blank
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "CancelEventCommand",
                    LogStatus.CANCEL_EVENT_USER_NOT_STAFF,
                    Map.of("eventNumber", eventNumber,
                            "currentUser", currentUser != null ? currentUser : "none")
            );
            successResult = false;
            return;
        }

        Event event = context.getEventState().findEventByNumber(eventNumber);

        if (event == null) {
            view.displayFailure(
                    "CancelEventCommand",
                    LogStatus.CANCEL_EVENT_EVENT_NOT_FOUND,
                    Map.of("eventNumber", eventNumber)
            );
            successResult = false;
            return;
        }

        if (event.getStatus() != EventStatus.ACTIVE) {
            view.displayFailure(
                    "CancelEventCommand",
                    LogStatus.CANCEL_EVENT_NOT_ACTIVE,
                    Map.of("eventNumber", eventNumber)
            );
            successResult = false;
            return;
        }

        if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
            view.displayFailure(
                    "CancelEventCommand",
                    LogStatus.CANCEL_EVENT_ALREADY_STARTED,
                    Map.of("eventNumber", eventNumber)
            );
            successResult = false;
            return;
        }

        if (organiserMessage == null || organiserMessage.isBlank()) {
            view.displayFailure(
                    "CancelEventCommand",
                    LogStatus.CANCEL_EVENT_MESSAGE_MUST_NOT_BE_BLANK,
                    Map.of("organiserMessage", String.valueOf(organiserMessage))
            );
            successResult = false;
            return;
        }

        event.cancel();
        List<Booking> eventBookings = context.getBookingState().findBookingsByEventNumber(eventNumber);

        for (Booking booking : eventBookings) {
            if (booking.getStatus() == BookingStatus.Active) {
                booking.cancelByProvider();
                booking.getBooker().notify(organiserMessage);

                if (event.getTicketPriceInPence() > 0) {
                    context.getPaymentSystem().processRefund(
                            booking.getBooker().getEmail(),
                            context.getOrgEmail(),
                            booking.getNumTickets() * event.getTicketPriceInPence()
                    );
                }
            }
        }

        view.displaySuccess(
                "CancelEventCommand",
                LogStatus.CANCEL_EVENT_SUCCESS,
                Map.of("eventNumber", eventNumber)
        );
        successResult = true;
    }

    /**
     * @return True if successful and false otherwise
     */
    @Override
    public Boolean getResult() {
        return successResult;
    }

    private enum LogStatus {
        CANCEL_EVENT_SUCCESS,
        CANCEL_EVENT_MESSAGE_MUST_NOT_BE_BLANK,
        CANCEL_EVENT_USER_NOT_STAFF,
        CANCEL_EVENT_EVENT_NOT_FOUND,
        CANCEL_EVENT_NOT_ACTIVE,
        CANCEL_EVENT_ALREADY_STARTED,
    }
}

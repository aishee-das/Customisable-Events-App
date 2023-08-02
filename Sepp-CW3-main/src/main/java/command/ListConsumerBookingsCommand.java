package command;

import controller.Context;
import model.Booking;
import model.Consumer;
import model.User;
import view.IView;

import java.util.List;
import java.util.Map;

/**
 * {@link ListConsumerBookingsCommand} allows a logged-in {@link Consumer} to get a list of all their own
 * {@link Booking}s.
 */
public class ListConsumerBookingsCommand implements ICommand<List<Booking>> {
    private List<Booking> bookingListResult;

    public ListConsumerBookingsCommand() {

    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that the current user is logged in
     * @verifies.that the logged-in user is a Consumer
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Consumer)) {
            view.displayFailure(
                    "ListConsumerBookingsCommand",
                    LogStatus.LIST_CONSUMER_BOOKINGS_USER_NOT_CONSUMER,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            bookingListResult = null;
            return;
        }

        view.displaySuccess(
                "ListConsumerBookingsCommand",
                LogStatus.LIST_CONSUMER_BOOKINGS_SUCCESS
        );
        bookingListResult = ((Consumer) currentUser).getBookings();
        System.out.println(bookingListResult);
    }

    /**
     * @return A list of the {@link Consumer}'s {@link Booking}s if successful and null otherwise
     */
    @Override
    public List<Booking> getResult() {
        return bookingListResult;
    }

    private enum LogStatus {
        LIST_CONSUMER_BOOKINGS_USER_NOT_CONSUMER,
        LIST_CONSUMER_BOOKINGS_SUCCESS
    }
}

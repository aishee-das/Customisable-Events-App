package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReviewEventCommand implements ICommand<Review> {

    private Review reviewResult;
    private long eventNumber;
    private String content;

    /**
     * @param eventNumber   used to identify what event the review is left for
     * @param content       contents of the review inputted by a {@link Consumer}
     * */


    public ReviewEventCommand(long eventNumber, String content) {
        this.eventNumber = eventNumber;
        this.content = content;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that {@Link Event} exists
     * @verifies.that {@Link User} isn't {@Link Staff}
     * @verifies.that {@Link Event} has happened
     * @verifies.that {@Link User} has a  {@Link Booking}
     * @verifies.that {@Link Booking} is valid
     */

    @Override
    public void execute(Context context, IView view) {

        //check if event number is correct
        if(context.getEventState().findEventByNumber(eventNumber) == null) {
            view.displayFailure(
                    "ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_INVALID_EVENT_NUM,
                    Map.of("eventNumber", eventNumber)
            );
            reviewResult = null;
            return;
        }
        // Check that current user is a Consumer

        User currentUser = context.getUserState().getCurrentUser();

        if(!(currentUser instanceof Consumer)) {
            view.displayFailure(
                    "SaveAppStateCommand",
                    LogStatus.REVIEW_EVENT_AUTHOR_NOT_CONSUMER,
                    Map.of("user", !(currentUser instanceof Consumer))
            );
            reviewResult = null;
            return;
        }


        // Check that the consumer had at least 1 valid booking at the event

        List<Booking> bookingsFromEvent = context.getBookingState().findBookingsByEventNumber(eventNumber);
        boolean authorHasBooking = false;
        Booking bookingForReview = null;

        for(int i = 0; i < bookingsFromEvent.size(); i++) {
            if(bookingsFromEvent.get(i).getBooker() == currentUser) {
                authorHasBooking = true;
                bookingForReview = bookingsFromEvent.get(i);
            }
        }

        if(!authorHasBooking) {
            view.displayFailure(
                    "ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_AUTHOR_NO_BOOKING,
                    Map.of("currentUser", "No Booking")
            );
            reviewResult = null;
            return;
        }

        //checks that the booking is active and wasn't cancelled
        if(!(bookingForReview.getStatus()==BookingStatus.Active)) {
            view.displayFailure(
                    "ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_BOOKING_INVALID,
                    Map.of("bookingStatus", !(bookingForReview.getStatus()==BookingStatus.Active))
            );
            reviewResult = null;
            return;
        }

        //Checking date time

        Event event = context.getEventState().findEventByNumber(eventNumber);
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime eventDateTime = event.getEndDateTime();

        int dateTimeDifference = currentDateTime.compareTo(eventDateTime);

        // dateTimeDifference < 0 indicates that currentDateTime is earlier than eventDateTime
        if(dateTimeDifference < 0) {
            view.displayFailure(
                    "ReviewEventCommand",
                    LogStatus.REVIEW_EVENT_NOT_HAPPENED_YET,
                    Map.of("currentDateTime", currentDateTime, "eventDateTime", eventDateTime)
            );
            reviewResult = null;
            return;
        }



        Review review = new Review((Consumer) currentUser, event, currentDateTime, content);

        view.displaySuccess(
                "ReviewEventCommand",
                LogStatus.REVIEW_EVENT_SUCCESS,
                Map.of("review", review)
        );
        reviewResult = review;

        List<Review> allReviews = event.getReviews();
        allReviews.add(review);
        event.setReviews(allReviews);

    }

    @Override
    public Review getResult() {
        return reviewResult;
    }
    private enum LogStatus {
        REVIEW_EVENT_INVALID_EVENT_NUM,
        REVIEW_EVENT_NOT_HAPPENED_YET,
        REVIEW_EVENT_AUTHOR_NOT_CONSUMER,
        REVIEW_EVENT_AUTHOR_NO_BOOKING,
        REVIEW_EVENT_BOOKING_INVALID,
        REVIEW_EVENT_SUCCESS
    }


}

package command;

import controller.Context;
import model.Event;
import model.Review;
import view.IView;
import java.util.*;

public class ListEventReviewsCommand implements ICommand<Review>{

    private List<Review> reviewsResult;
    private String eventTitle;

    /**
     * @param eventTitle Title of event which we are finding reviews for
     * */

    public ListEventReviewsCommand(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that checks if {@Link Event}that review is intended for exists
     */
    @Override
    public void execute(Context context, IView view) {

    List<Event> allEvents = context.getEventState().getAllEvents();
    long eventNumber = 0;
    for(int i = 0; i < allEvents.size(); i++) {
        if(allEvents.get(i).getTitle().equals(eventTitle)) {
            eventNumber = allEvents.get(i).getEventNumber();
        }
        break;
    }

    if(eventNumber == 0) {
        view.displayFailure(
                "ListEventReviewsCommand",
                ListEventReviewsCommand.LogStatus.LIST_EVENT_REVIEWS_COMMAND_NO_EVENT_FOUND,
                Map.of("eventNumber", eventNumber)
        );
        reviewsResult = null;
        return;
    }
    Event event = context.getEventState().findEventByNumber(eventNumber);
        view.displaySuccess(
                "ListEventReviewsCommand",
                ListEventReviewsCommand.LogStatus.LIST_EVENT_REVIEWS_SUCCESS,
                Map.of("reviews", event.getReviews())
        );

    reviewsResult = event.getReviews();

    }

    @Override
    public Review getResult() {
        return null;
    }

    private enum LogStatus{
        LIST_EVENT_REVIEWS_COMMAND_NO_EVENT_FOUND,
        LIST_EVENT_REVIEWS_SUCCESS
    }
}

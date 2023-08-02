package command;

import controller.Context;
import model.*;
import view.IView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link ListEventsCommand} allows anyone to get a list of {@link Event}s available on the system.
 * Optionally, users can specify a particular {@link LocalDate} to look up events for.
 */
public class ListEventsCommand implements ICommand<List<Event>> {
    final boolean userEventsOnly;
    final boolean activeEventsOnly;
    final LocalDate searchDate;
    List<Event> eventListResult;

    /**
     * @param userEventsOnly   if true, the returned events will be filtered depending on the logged-in user:
     *                         for {@link Staff}s only the {@link Event}s they have created,
     *                         and for {@link Consumer}s only the {@link Event}s that match their {@link ConsumerPreferences}
     * @param activeEventsOnly if true, returned {@link Event}s will be filtered to contain only {@link Event}s with
     *                         {@link EventStatus#ACTIVE}
     * @param searchDate       chosen date to look for events. Can be null. If not null, only {@link Event}s that are
     *                         happening on {@link #searchDate} (i.e., starting, ending, or in between) will be included
     */
    public ListEventsCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDate searchDate) {
        this.userEventsOnly = userEventsOnly;
        this.activeEventsOnly = activeEventsOnly;
        this.searchDate = searchDate;
    }

    /**
     * @param map                          contains a Map from the tag name (of type String) to an {@link EventTag}which
     *                                     contains the tag name, and a set of values possible (asw as a default value)
     *
     * @param consumerPreferences          is a custom collection of {@link EventTag}s in a Map but that map type is map<String, String> where,
     *                                     the first String is the tag's name and the second tag is the value assigned to it. This is
     *                                     the users preferences which will be crossreferenced against 'map'.
     *
     * @param event                         is the event we are looking at to see if it staisfies the preferences
     *
     * */

    //NOTE: unclear why we need map? If consumerPreferences can only be updated with values on EventTags that exist, why is there a need to
    //      cross reference it against the full possibleTags map?
    //      Maybe it's to check for other values within an EventTag and not the one specifified? Is this in specification?
    private static boolean eventSatisfiesPreferences(Map<String, EventTag> map, EventTagCollection consumerPreferences, Event event) {

        EventTagCollection eventsAssociatedEventTags = event.getEventTagCollection();
        for(Map.Entry<String, String> entry : consumerPreferences.getTags().entrySet()) {
            String tagName = entry.getKey();
            String tagValue = entry.getValue();

            //if the tag name from consumer preferences doesn't exist in the tag names for the event
            if(!(eventsAssociatedEventTags.getTags().containsKey(tagName))) {
                return false;
            }
            //if the tag name from consumer preferences doesn't exist in the tag names for the event
            if(!(eventsAssociatedEventTags.getTags().containsValue(tagValue))){
                return false;
            }
        }
        return true;
    }

    private static List<Event> filterEvents(List<Event> events, boolean activeEventsOnly, LocalDate searchDate) {
        Stream<Event> filteredEvents = events.stream();

        if (activeEventsOnly) {
            filteredEvents = filteredEvents.filter(event -> event.getStatus() == EventStatus.ACTIVE);
        }
        if (searchDate != null) {
            filteredEvents = filteredEvents.filter(event ->
                    event.getStartDateTime().toLocalDate().equals(searchDate)
                            || event.getEndDateTime().toLocalDate().equals(searchDate)
                            || (searchDate.isAfter(event.getStartDateTime().toLocalDate())
                            && searchDate.isBefore(event.getEndDateTime().toLocalDate())));
        }
        return filteredEvents.collect(Collectors.toList());
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that if userEventsOnly is set, the current user must be logged in
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        //Checks if the events are user events only
        if (userEventsOnly) {
            //Checks if there is user that's logged in
            if(currentUser == null){
                view.displayFailure(
                        "ListEventsCommand",
                        LogStatus.LIST_EVENTS_NOT_LOGGED_IN,
                        Map.of("userEventsOnly", userEventsOnly,
                                "currentUser", "null")
                );
                eventListResult = null;
                return;
                //will only run if userEventsOnly is True and currentUser is logged in
            }
        }

        if (currentUser instanceof Staff) {
            eventListResult = filterEvents(context.getEventState().getAllEvents(), activeEventsOnly, searchDate);
            view.displaySuccess(
                    "ListEventsCommand",
                    LogStatus.LIST_EVENTS_SUCCESS,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", true,
                            "searchDate", String.valueOf(searchDate),
                            "eventList", eventListResult)
            );
            return;
        }

        if (currentUser instanceof Consumer) {
            Consumer consumer = (Consumer) currentUser;
            EventTagCollection preferences = consumer.getPreferences();
            Map<String, EventTag> allTags = context.getEventState().getPossibleTags();
            List<Event> eventsFittingPreferences = context.getEventState().getAllEvents().stream()
                    .filter(event -> eventSatisfiesPreferences(allTags, preferences, event))
                    .collect(Collectors.toList());

            eventListResult = filterEvents(eventsFittingPreferences, activeEventsOnly, searchDate);
            view.displaySuccess(
                    "ListEventsCommand",
                    LogStatus.LIST_EVENTS_SUCCESS,
                    Map.of("activeEventsOnly", activeEventsOnly,
                            "userEventsOnly", true,
                            "searchDate", String.valueOf(searchDate),
                            "eventList", eventListResult)
            );
            return;
        }

        eventListResult = null;
    }

    /**
     * @return List of {@link Event}s if successful and null otherwise
     */
    @Override
    public List<Event> getResult() {
        return eventListResult;
    }

    private enum LogStatus {
        LIST_EVENTS_SUCCESS,
        LIST_EVENTS_NOT_LOGGED_IN,
    }
}

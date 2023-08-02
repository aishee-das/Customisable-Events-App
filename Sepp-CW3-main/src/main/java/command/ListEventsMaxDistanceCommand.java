package command;

import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.Consumer;
import model.Event;
import model.TransportMode;
import model.User;
import view.IView;
import java.util.*;


import java.time.LocalDate;
import java.util.Map;

public class ListEventsMaxDistanceCommand extends ListEventsCommand {

    private TransportMode transportMode;
    private double maxDistance;

    /**
     * @param userEventsOnly   if true, the returned events will be filtered depending on the logged-in user:
     *                         for {@link model.Staff}s only the {@link Event}s they have created,
     *                         and for {@link Consumer}s only the {@link Event}s that match their {@link model.EventTagCollection}
     * @param activeEventsOnly if true, returned {@link Event}s will be filtered to contain only {@link Event}s with
     *                         {@link model.EventStatus}
     * @param searchDate       chosen date to look for events. Can be null. If not null, only {@link Event}s that are
     *                         happening on {@link #searchDate} (i.e., starting, ending, or in between) will be included
     *
     * @param transportMode    mode of transporation selected, necessary for using {@link MapSystem}
     *
     * @param maxDistance      max distance which user wants to search events for
     */


    public ListEventsMaxDistanceCommand(boolean userEventsOnly, boolean activeEventsOnly,
                                        LocalDate searchDate, double maxDistance, TransportMode transportMode) {
        super(userEventsOnly, activeEventsOnly, searchDate);
        this.maxDistance = maxDistance;
        this.transportMode = transportMode;
    }
    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current user isn't null
     * @verifies.that current user isn't {@Link Staff}
     * @verifies.that current user address isn't blank/null
     * @verifies.that current user is logged in
     * @verifies.that distance isn't negative
     */
    @Override
    public void execute(Context context, IView view) {

        User currentUser = context.getUserState().getCurrentUser();
        //Checks if user is logged in
        if(currentUser == null) {
            view.displayFailure(
                    "ListEventsMaxDistanceCommand",
                    ListEventsMaxDistanceCommand.LogStatus.LIST_EVENTS_MAX_DISTANCE_USER_NOT_LOGGED_IN,
                    Map.of("currentUser", "null"));
            //Maybe change this to an empty list
            eventListResult = null;
            return;
        }

        if (!(currentUser instanceof Consumer)){
            view.displayFailure("ListEventsMaxDistanceCommand",
                    ListEventsMaxDistanceCommand.LogStatus.LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER,
                    Map.of("currentUser",currentUser instanceof Consumer)
            );
            eventListResult = null;
            return;
        }

        if(maxDistance < 0) {
            view.displayFailure(
                    "ListEventsMaxDistanceCommand",
                    ListEventsMaxDistanceCommand.LogStatus.LIST_EVENTS_MAX_DISTANCE_INVALID_DISTANCE,
                    Map.of("maxDistance", maxDistance));
            //Maybe change this to an empty list
            eventListResult = null;
            return;
        }


        Consumer consumer = (Consumer) currentUser;
        String consumerAddr = consumer.getAddress();

        //checks if current user has an address set up in their profile
        //NOTE: maybe should add check that it's a valid address?
        //NOTE: you can only add an address if its valid so isn't necessary

        if(consumerAddr == null || consumerAddr.equals("")) {
            view.displayFailure(
                    "ListEventsMaxDistanceCommand",
                    ListEventsMaxDistanceCommand.LogStatus.LIST_EVENTS_MAX_DISTANCE_NO_CONSUMER_ADDR,
                    Map.of("consumerAddr", "null"));
            //Maybe change this to an empty list
            eventListResult = null;
            return;
        }


        MapSystem map = context.getMapSystem();


        List<Event> allEvents = context.getEventState().getAllEvents();
        List<Event> eventsWithinDistance = new ArrayList<>();

        GHPoint ghPointConsumerAddr = map.convertToCoordinates(consumerAddr);

        //all of this assumes venueAddress and consumerAddress are within bounds of map
        for(int i = 0; i<allEvents.size(); i++) {
            Event currentEvent = allEvents.get(i);
            String venueAddr = currentEvent.getVenueAddress();
            GHPoint ghPointVenueAddr = map.convertToCoordinates(venueAddr);

            ResponsePath responsePath = map.routeBetweenPoints(transportMode, ghPointConsumerAddr, ghPointVenueAddr);

            double distanceToEvent = responsePath.getDistance();
            if(distanceToEvent <= maxDistance) {
                eventsWithinDistance.add(currentEvent);
            }

        }
        eventListResult =  eventsWithinDistance;

        view.displaySuccess(
                "ListEventsMaxDistanceCommand",
                ListEventsMaxDistanceCommand.LogStatus.LIST_EVENTS_MAX_DISTANCE_SUCCESS,
                Map.of("eventListResult", eventListResult));
    }
    private enum LogStatus {
        LIST_EVENTS_MAX_DISTANCE_USER_NOT_LOGGED_IN,
        LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER,
        LIST_EVENTS_MAX_DISTANCE_NO_CONSUMER_ADDR,
        LIST_EVENTS_MAX_DISTANCE_SUCCESS,
        LIST_EVENTS_MAX_DISTANCE_INVALID_DISTANCE
    }
}

package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.*;
import view.IView;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * {@link CreateEventCommand} allows {@link Staff}s to create new {@link Event}s.
 * The command applies for the currently logged-in user.
 */
public class CreateEventCommand implements ICommand<Event> {
    private final String title;
    private final EventType type;
    private final int numTickets;
    private final int ticketPriceInPence;
    private final String venueAddress;
    private final String description;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final EventTagCollection tagCollection;
    private Event eventResult;

    /**
     * @param title               title of the event
     * @param type                type of the event
     * @param numTickets          number of initially available tickets for the event. This can be 0 if the event does
     *                            not need booking.
     * @param ticketPriceInPence  price in GBP pence per event ticket. This can be 0 if the event is free.
     * @param venueAddress        indicates where this performance will take place, would be displayed to users in app
     * @param description         additional details about the event
     * @param startDateTime       indicates the date and time when this performance is due to start
     * @param endDateTime         indicates the date and time when this performance is due to end
     * @param tagCollection       contains a custom collection of tags
     */
    public CreateEventCommand(String title,
                              EventType type,
                              int numTickets,
                              int ticketPriceInPence,
                              String venueAddress,
                              String description,
                              LocalDateTime startDateTime,
                              LocalDateTime endDateTime,
                              String tagCollection) {
        this.title = title;
        this.type = type;
        this.numTickets = numTickets;
        this.ticketPriceInPence = ticketPriceInPence;
        this.venueAddress = venueAddress;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.tagCollection = new EventTagCollection(tagCollection);
    }

    //Test venueAddr: 55.931117 -3.177361
    //Test startTime: 2023-05-01T00:00:00
    //Test endTime: 2023-05-01T20:00:00

    /**
     * @return event number corresponding to the created event if successful and null otherwise
     */
    @Override
    public Event getResult() {
        return eventResult;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that currently logged-in user is a Staff member
     * @verifies.that event startDateTime is not after endDateTime
     * @verifies.that event startDateTime is in the future
     * @verifies.that no other event with the same title has the same startDateTime and endDateTime
     * @verifies.that the event ticket price is non-negative
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "CreateEventCommand",
                    CreateEventCommand.LogStatus.CREATE_EVENT_USER_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            eventResult = null;
            return;
        }

        if (startDateTime.isAfter(endDateTime)) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_START_AFTER_END,
                    Map.of("startDateTime", startDateTime,
                            "endDateTime", endDateTime)
            );
            eventResult = null;
            return;
        }

        if (startDateTime.isBefore(LocalDateTime.now())) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_IN_THE_PAST,
                    Map.of("startDateTime", startDateTime)
            );
            eventResult = null;
            return;
        }

        // Use case 3.6:
        // If all the fields were correctly provided and an event with the
        // same name did not already exist for some or all of the same dates and times
        boolean isEventTitleAndTimeClash = context.getEventState().getAllEvents().stream()
                .anyMatch(otherEvent -> otherEvent.getTitle().equals(title)
                        && otherEvent.getStartDateTime().equals(startDateTime)
                        && otherEvent.getEndDateTime().equals(endDateTime)
                );
        if (isEventTitleAndTimeClash) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_TITLE_AND_TIME_CLASH,
                    Map.of("title", title,
                            "startDateTime", startDateTime,
                            "endDateTime", endDateTime)
            );
            eventResult = null;
            return;
        }

        if (ticketPriceInPence < 0) {
            view.displayFailure(
                    "CreateEventCommand",
                    LogStatus.CREATE_EVENT_NEGATIVE_TICKET_PRICE,
                    Map.of("ticketPriceInPence", ticketPriceInPence)
            );
            eventResult = null;
            return;
        }

        //Checks if venueAddress is within bounds
        try {
            String[] firstSplit = venueAddress.split(" ");
            Double lat = Double.parseDouble(firstSplit[0]);
            Double longi = Double.parseDouble(firstSplit[1]);

            //Checks if latitude value is a real possible latitude value
            if(lat > 90 || lat < -90) {
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_COMMAND_ADDRESS_LAT_OUTSIDE_RANGE,
                        Map.of("lat", lat));
                eventResult = null;
                return;
            }
            //Checks if longitudinal value is a real possible longitudinal value
            if(longi > 180 || longi < -180) {
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_COMMAND_ADDRESS_LONG_OUTSIDE_RANGE,
                        Map.of("lat", lat));
                eventResult = null;
                return;
            }

            GHPoint newAddress = new GHPoint(lat, longi);
            MapSystem map = context.getMapSystem();
            boolean withinBounds = map.isPointWithingMapBounds(newAddress);

            //checks coords are actually within bounds of program
            if(!withinBounds) {
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_COMMAND_ADDRESS_OUTSIDE_BOUNDS,
                        Map.of("venueAddress", venueAddress));
                eventResult = null;
                return;
            }
            /**
             * Catch exception for failure to split the String into two
             * separate latitudinal values and longitudinal ones
             * */

        } catch (Exception ex) {
            view.displayFailure(
                    "CreateEventCommand",
                    CreateEventCommand.LogStatus.CREATE_EVENT_COMMAND_ADDRESS_INVALID_FORMAT,
                    Map.of("venueAddress", venueAddress));
            //ex.printStackTrace();
            eventResult = null;
            return;
        }
        Map<String, String> tags = tagCollection.getTags();
        if(tags.containsKey("incorrect")) {
            view.displayFailure(
                    "CreateEventCommand",
                    CreateEventCommand.LogStatus.CREATE_EVENT_INCORRECT_EVENT_TAG_FORMAT,
                    Map.of("tagCollection", tags.get("incorrect")));
            eventResult = null;
            return;
        }




        Map<String, EventTag> systemTags = context.getEventState().getPossibleTags();

        for(Map.Entry<String, String> entry : tagCollection.getTags().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //Checks if key exists

//            System.out.println("This is the key being checked rn: " + key);
            if(!(systemTags.containsKey(key))) {
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_COMMAND_INVALID_PREFERENCE_KEY,
                        Map.of("key", key));
                eventResult = null;
                return;
            }
            EventTag eventTag = systemTags.get(key);
            Set<String> values = eventTag.getValues();
            if(!(values.contains(value))) {
                view.displayFailure(
                        "CreateEventCommand",
                        CreateEventCommand.LogStatus.CREATE_EVENT_COMMAND_INVALID_PREFERENCE_VALUE,
                        Map.of("key", key));
                eventResult = null;
                return;
            }
        }








        Event event = context.getEventState().createEvent(title, type, numTickets,
                ticketPriceInPence, venueAddress, description,
                startDateTime, endDateTime, tagCollection);
        view.displaySuccess(
                "CreateEventCommand",
                CreateEventCommand.LogStatus.CREATE_EVENT_SUCCESS,
                Map.of("eventNumber", event.getEventNumber(),
                        "organiser", currentUser,
                        "title", title)
        );
        eventResult = event;
    }

    private enum LogStatus {
        CREATE_EVENT_USER_NOT_STAFF,
        CREATE_EVENT_START_AFTER_END,
        CREATE_EVENT_IN_THE_PAST,
        CREATE_EVENT_TITLE_AND_TIME_CLASH,
        CREATE_EVENT_NEGATIVE_TICKET_PRICE,
        CREATE_EVENT_SUCCESS,
        CREATE_EVENT_COMMAND_ADDRESS_LAT_OUTSIDE_RANGE,
        CREATE_EVENT_COMMAND_ADDRESS_LONG_OUTSIDE_RANGE,
        CREATE_EVENT_COMMAND_ADDRESS_INVALID_FORMAT,
        CREATE_EVENT_COMMAND_ADDRESS_OUTSIDE_BOUNDS,
        CREATE_EVENT_COMMAND_INVALID_PREFERENCE_KEY,
        CREATE_EVENT_COMMAND_INVALID_PREFERENCE_VALUE,
        CREATE_EVENT_INCORRECT_EVENT_TAG_FORMAT
    }
}

package state;

import model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link IEventState} is an interface representing the portion of application state that contains all the {@link Event}
 * information.
 */
public interface IEventState {
    /**
     * @return List of all registered {@link Event}s in the application
     */
    List<Event> getAllEvents();
    /**
     * @return a map of all {@link EventTag}s in the application
     */
    Map<String, EventTag> getPossibleTags();

    /**
     * @return an event tag of type {@link EventTag}
     */

    EventTag createEventTag(String name, Set<String> values, String defaultValue);


    /**
     * @param eventNumber unique event identifier to look up in the event state
     * @return {@link Event} corresponding to the specified event number if there is one, and null otherwise
     */
    Event findEventByNumber(long eventNumber);

    /**
     * Create a new {@link Event} (includes generating a new unique event number) and add it to the event state
     *
     * @param title               name of the new event
     * @param type                type of the new event
     * @param numTickets          maximum number of tickets for the new event, which are all initially available
     * @param ticketPriceInPence  price per ticket in GBP pence
     * @param venueAddress        address where the new performance will take place
     * @param description         additional details about the event
     * @param startDateTime       date and time when the new performance will begin
     * @param endDateTime         date and time when the new performance will end
     * @param tagCollection       contains a custom defined collection of tags
     * @return The newly created {@link Event}
     */

    //Problems associated with the Event class are because of the EventTagCollection replacing consumer preferences
    //This also impacts the JUnit tests as well. We'll have to update the JUnit tests accordingly
    Event createEvent(String title,
                      EventType type,
                      int numTickets,
                      int ticketPriceInPence,
                      String venueAddress,
                      String description,
                      LocalDateTime startDateTime,
                      LocalDateTime endDateTime,
                      EventTagCollection tagCollection);
}

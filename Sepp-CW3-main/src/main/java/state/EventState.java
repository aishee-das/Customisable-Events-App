package state;

import model.Event;
import model.EventTag;
import model.EventTagCollection;
import model.EventType;

import java.time.LocalDateTime;
import java.util.*;

/**
 * {@link EventState} is a concrete implementation of {@link IEventState}.
 */
public class EventState implements IEventState {
    private  List<Event> events;
    private  Map<String, EventTag> possibleTags;
    private long nextEventNumber;

    /**
     * Create a new EventState with an empty list of events, which keeps track of the next event and performance numbers
     * it will generate, starting from 1 and incrementing by 1 each time when requested
     */
    public EventState() {
        events = new LinkedList<>();
        possibleTags = new HashMap<String, EventTag>();
        nextEventNumber = 1;
    }

    /**
     * Copy constructor to make a deep copy of another EventState instance
     *
     * @param other instance to copy
     */
    public EventState(IEventState other) {
        EventState otherImpl = (EventState) other;
        events = new LinkedList<>(otherImpl.events);
        possibleTags = otherImpl.possibleTags;
        nextEventNumber = otherImpl.nextEventNumber;
    }

    @Override
    public List<Event> getAllEvents() {
        return events;
    }

    @Override
    public Map<String, EventTag> getPossibleTags() { return possibleTags; }

    @Override
    public EventTag createEventTag(String name, Set<String> values, String defaultValue) {
        EventTag newTag = new EventTag(values, defaultValue);
        possibleTags.put(name, newTag);
        return newTag;
    }

    @Override
    public Event findEventByNumber(long eventNumber) {
        return events.stream()
                .filter(event -> event.getEventNumber() == eventNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Event createEvent(String title,
                             EventType type,
                             int numTickets,
                             int ticketPriceInPence,
                             String venueAddress,
                             String description,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             EventTagCollection tagCollection) {
        long eventNumber = nextEventNumber;
        nextEventNumber++;

        Event event = new Event(eventNumber, title, type, numTickets,
                ticketPriceInPence, venueAddress, description, startDateTime,
                endDateTime, tagCollection);
        events.add(event);
        return event;
    }
}

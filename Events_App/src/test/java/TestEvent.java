import model.Event;
import model.EventStatus;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

//public class TestEvent extends ConsoleTest{
//
//
//}

public class TestEvent {
    Event event;

    @BeforeEach
    void setup() {
        event = new Event(1,
                "A Christmas Carol",
                EventType.Theatre,
                25,
                20,
                "55.94373085057988, -3.1887541458174073",
                "Pay",
                LocalDateTime.now().plusHours(23),
                LocalDateTime.now().plusHours(24),
                new EventTagCollection()
                );
    }

    @AfterEach
    void tearDown() {event = null;}

    @Test
    void event_cancel_test() {
        event.cancel();
        assert(event.getStatus() == EventStatus.CANCELLED);
    }

    @Test
    void event_get_title_test() throws NoSuchFieldException, IllegalAccessException {
        final Field title = event.getClass().getDeclaredField("title");
        title.setAccessible(true);
        String result = event.getTitle();
        assertEquals(result, title.get(event));
    }

    @Test
    void event_get_event_number_test() throws NoSuchFieldException, IllegalAccessException {
        final Field event_num = event.getClass().getDeclaredField("eventNumber");
        event_num.setAccessible(true);
        long result = event.getEventNumber();
        assertEquals(result, event_num.get(event));
    }

    @Test
    void event_get_ticket_price_in_pence_test() throws NoSuchFieldException, IllegalAccessException {
        final Field ticket_price_in_pence = event.getClass().getDeclaredField("ticketPriceInPence");
        ticket_price_in_pence.setAccessible(true);
        int result = event.getTicketPriceInPence();
        assertEquals(result, ticket_price_in_pence.get(event));
    }

    @Test
    void event_get_type_test() throws NoSuchFieldException, IllegalAccessException {
        final Field type = event.getClass().getDeclaredField("type");
        type.setAccessible(true);
        EventType result = event.getType();
        assertEquals(result, type.get(event));
    }

    @Test
    void event_get_status_test() throws NoSuchFieldException, IllegalAccessException {
        final Field status = event.getClass().getDeclaredField("status");
        status.setAccessible(true);
        EventStatus result = event.getStatus();
        assertEquals(result, status.get(event));
    }

    @Test
    void event_get_start_datetime_test() throws NoSuchFieldException, IllegalAccessException {
        final Field startDate = event.getClass().getDeclaredField("startDateTime");
        startDate.setAccessible(true);
        LocalDateTime result = event.getStartDateTime();
        assertEquals(result, startDate.get(event));
    }

    @Test
    void event_get_end_datetime_test() throws NoSuchFieldException, IllegalAccessException {
        final Field endDate = event.getClass().getDeclaredField("endDateTime");
        endDate.setAccessible(true);
        LocalDateTime result = event.getEndDateTime();
        assertEquals(result, endDate.get(event));
    }
    @Test
    void event_get_address_test() throws NoSuchFieldException, IllegalAccessException {
        final Field address = event.getClass().getDeclaredField("venueAddress");
        address.setAccessible(true);
        String result = event.getVenueAddress();
        assertEquals(result, address.get(event));
    }

    @Test
    void event_get_num_tickets_cap_test() throws NoSuchFieldException, IllegalAccessException {
        final Field cap = event.getClass().getDeclaredField("numTicketsCap");
        cap.setAccessible(true);
        int result = event.getNumTicketsCap();
        assertEquals(result, cap.get(event));
    }

    @Test
    void event_get_tag_collection_test() throws NoSuchFieldException, IllegalAccessException {
        final Field tags = event.getClass().getDeclaredField("eventTagCollection");
        tags.setAccessible(true);
        EventTagCollection result = event.getEventTagCollection();
        assertEquals(result, tags.get(event));
    } //dont know how to test for tags

    @Test
    void event_to_string_test() {
        String expected = "Event{" +
                "eventNumber=1, " +
                "title='A Christmas Carol', " +
                "type=Theatre, " +
                "numTicketsCap=25, " +
                "ticketPriceInPence=20, " +
                "venueAddress='55.94373085057988, -3.1887541458174073', " +
                "description='Pay, ";
    }
    
}
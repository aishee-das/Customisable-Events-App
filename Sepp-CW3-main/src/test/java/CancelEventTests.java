import command.*;
import controller.Context;
import controller.Controller;
import model.Event;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CancelEventTests extends ConsoleTest {
    @Test
    void cancelEventWithoutBookings() {
        Controller controller = createStaffAndEvent(10, 48);
        startOutputCapture();
        List<Event> events = getUserEvents(controller);
        controller.runCommand(new CancelEventCommand(events.get(0).getEventNumber(), "Too few bookings"));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    @Test
    void cancelEventAsConsumer() {
        Controller controller = createStaffAndEvent(10, 48);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        controller.runCommand(
                new CancelEventCommand(events.get(0).getEventNumber(),
                        "Let's try messing with this event"
                ));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_USER_NOT_STAFF"
        );
    }

    @Test
    void cancelCurrentEventWith1Booking() {
        Controller controller = createStaffAndEvent(10, 1);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller, 4);
        controller.runCommand(new LogoutCommand());

        startOutputCapture();
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));
        List<Event> events = getUserEvents(controller);
        controller.runCommand(new CancelEventCommand(
                events.get(0).getEventNumber(),
                "I guess we're just not bothered to run this after all"
        ));
        stopOutputCaptureAndCompare(
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "CANCEL_EVENT_REFUND_BOOKING_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    @Test
    void cancelFutureEventWith2Bookings() {
        Controller controller = createController();
        createStaff(controller);
        long eventNumber = createEvent(controller, 1000, 60000).getEventNumber();
        controller.runCommand(new LogoutCommand());

        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Dora The Explorer",
                "dora@explorer.com",
                "78945623",
                "55.944853077240545 -3.1873034598188967", // Informatics Forum
                "I <3 travelling"
        ));
        controller.runCommand(new BookEventCommand(eventNumber, 20));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand(
                "Metal Fan",
                "literally@a.fan",
                "like the kind that you put on a desk",
                "55.94458227461727 -3.1853257484630726", // The Pear Tree, Edinburgh
                "to cool down the room"
        ));
        controller.runCommand(new BookEventCommand(eventNumber, 1));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));

        CancelEventCommand cancelCmd = new CancelEventCommand(eventNumber, "Sorry!");
        controller.runCommand(cancelCmd);
        assertTrue(cancelCmd.getResult());

        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CANCEL_EVENT_REFUND_BOOKING_SUCCESS",
                "CANCEL_EVENT_REFUND_BOOKING_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    @Test
    void cancelFutureEventWithCancelledBooking() {
        Controller controller = createController();
        createStaff(controller);
        long eventNumber = createEvent(controller, 10000, 6000).getEventNumber();
        controller.runCommand(new LogoutCommand());

        createConsumer(controller);
        BookEventCommand bookEventCmd = new BookEventCommand(eventNumber, 20);
        controller.runCommand(bookEventCmd);
        long bookingNumber = bookEventCmd.getResult().getBookingNumber();

        CancelBookingCommand cancelBookingCmd = new CancelBookingCommand(bookingNumber);
        controller.runCommand(cancelBookingCmd);
        assertTrue(cancelBookingCmd.getResult());

        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));

        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventNumber, "Sorry!");
        controller.runCommand(cancelEventCmd);
        assertTrue(cancelEventCmd.getResult());

        stopOutputCaptureAndCompare(
                "USER_LOGIN_SUCCESS",
                "CANCEL_EVENT_SUCCESS"
        );
    }

    @Test
    void cancelEndedEvent() {
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        createStaff(controller);
        // Need to manually insert event into state, because CreateEventCommand does not allow
        // creating events in the past

        EventTagCollection eventTagCollection = new EventTagCollection("hasSocialDistancing=false,hasAirFiltration=true");
        Event event = testContext.getEventState().createEvent(
            "World Tour",
                EventType.Music,
                10000,
                200,
                "55.86440964478519 -4.252880444477458", // Glasgow Royal Concert Hall
                "Lady Gaga and Ariana Grande will be performing in a duet",
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().minusDays(6).plusHours(3),
                eventTagCollection
        );
        long eventNumber = event.getEventNumber();

        startOutputCapture();
        CancelEventCommand cancelEventCmd = new CancelEventCommand(
                eventNumber,
                "How do I clear this off the event list?!?!"
        );
        controller.runCommand(cancelEventCmd);
        assertFalse(cancelEventCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_EVENT_ALREADY_STARTED"
        );
    }

    @Test
    void cancelOngoingEvent() {
        Context testContext = new Context("", "", "", "Nec temere nec timide");
        Controller controller = new Controller(testContext, new TestView());
        createStaff(controller);
        // Need to manually insert event into state, because CreateEventCommand does not allow
        // creating events in the past
        EventTagCollection eventTagCollection = new EventTagCollection("hasSocialDistancing=false,hasAirFiltration=true");
        Event event = testContext.getEventState().createEvent(
                "World Tour",
                EventType.Music,
                10000,
                200,
                "55.86440964478519 -4.252880444477458", // Glasgow Royal Concert Hall
                "Lady Gaga and Ariana Grande will be performing in a duet",
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusHours(3),
                eventTagCollection
        );
        long eventNumber = event.getEventNumber();

        startOutputCapture();
        CancelEventCommand cancelEventCmd = new CancelEventCommand(
                eventNumber,
                "Earthquake emergency"
        );
        controller.runCommand(cancelEventCmd);
        assertFalse(cancelEventCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_EVENT_ALREADY_STARTED"
        );
    }
}

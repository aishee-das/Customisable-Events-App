import command.BookEventCommand;
import command.CancelBookingCommand;
import command.LogoutCommand;
import command.RegisterConsumerCommand;
import controller.Controller;
import model.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BookEventTests extends ConsoleTest {
    @Test
    void bookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumerAndBookFirstEvent(controller, 1);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );
    }

    @Test
    void bookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(5, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        controller.runCommand(new BookEventCommand(firstEventNumber, 3));
        controller.runCommand(new BookEventCommand(firstEventNumber, 2));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_SUCCESS"
        );
    }

    @Test
    void overbookTicketedEvent() {
        Controller controller = createStaffAndEvent(1, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumerAndBookFirstEvent(controller, 2);
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );
    }

    @Test
    void overbookTicketedEventMultiBookings() {
        Controller controller = createStaffAndEvent(2, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        controller.runCommand(new BookEventCommand(firstEventNumber, 1));
        controller.runCommand(new BookEventCommand(firstEventNumber, 2));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT"
        );
    }


    @Test
    void bookEventNotLoggedIn() {
        Controller controller = createStaffAndEvent(5, 1);
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();

        startOutputCapture();
        controller.runCommand(new BookEventCommand(firstEventNumber, 5));
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_USER_NOT_CONSUMER"
        );
    }

    @Test
    void bookNonExistingEvent() {
        Controller controller = createStaffAndEvent(5, 1);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);

        startOutputCapture();
        controller.runCommand(new BookEventCommand(1324, 1));
        stopOutputCaptureAndCompare(
                "BOOK_EVENT_EVENT_NOT_FOUND"
        );
    }
}

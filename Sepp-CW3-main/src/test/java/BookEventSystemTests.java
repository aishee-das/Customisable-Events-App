import command.BookEventCommand;
import command.CancelBookingCommand;
import command.LogoutCommand;
import controller.Controller;
import model.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BookEventSystemTests extends ConsoleTest {

    @Test
    void createBookingRequestingMoreTicketsThanAvailable() {
        Controller controller = createStaffAndEvent(10, 25);
        startOutputCapture();
        controller.runCommand((new LogoutCommand()));
        createConsumerAndBookFirstEvent(controller, 12);
        stopOutputCaptureAndCompare("USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT");
    }

    @Test
    void createBookingWithNegativeTicketsRequested() {
        Controller controller = createStaffAndEvent(10, 25);
        startOutputCapture();
        controller.runCommand((new LogoutCommand()));
        createConsumerAndBookFirstEvent(controller, -12);
        stopOutputCaptureAndCompare("USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_SUCCESS",
                "BOOK_EVENT_INVALID_NUM_TICKETS");
    }
}

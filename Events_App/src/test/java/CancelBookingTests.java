import command.*;
import controller.Controller;
import model.Booking;
import model.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CancelBookingTests extends ConsoleTest {
    @Test
    void bookEventThenCancelBooking() {
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);
        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        assertTrue(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_SUCCESS"
        );
    }

    @Test
    void cancelBookingWithin24H() {
        Controller controller = createStaffAndEvent(5, 12);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        List<Event> events = getAllEvents(controller);
        long firstEventNumber = events.get(0).getEventNumber();
        BookEventCommand bookCmd = new BookEventCommand(firstEventNumber, 2);
        controller.runCommand(bookCmd);
        long bookingNumber = bookCmd.getResult().getBookingNumber();

        startOutputCapture();
        controller.runCommand(new CancelBookingCommand(bookingNumber));
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_NO_CANCELLATIONS_WITHIN_24H"
        );
    }

    @Test
    void cancelNonExistingBooking() {
        Controller controller = createStaffAndEvent(5, 48);
        controller.runCommand(new LogoutCommand());
        createConsumer(controller);
        startOutputCapture();
        controller.runCommand(new CancelBookingCommand(103945));
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_BOOKING_NOT_FOUND"
        );
    }



    @Test
    void cancelAnotherUsersBooking() {
        Controller controller = createStaffAndEvent(5, 48);
        controller.runCommand(new LogoutCommand());
        createConsumerAndBookFirstEvent(controller, 2);
        controller.runCommand(new LogoutCommand());

        startOutputCapture();
        //todo: make address work if null
        controller.runCommand(new RegisterConsumerCommand(
                "Evil",
                "clever@hacks.net",
                "999",
                "55.955727 -3.205609",
                "password"));
        controller.runCommand(new CancelBookingCommand(1));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CANCEL_BOOKING_USER_IS_NOT_BOOKER"
        );
    }

    @Test
    void cancelBookingTwice() {
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);

        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_SUCCESS",
                "CANCEL_BOOKING_BOOKING_NOT_ACTIVE"
        );
    }

    @Test
    void cancelNotLoggedIn() {
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);

        startOutputCapture();
        controller.runCommand(new LogoutCommand());
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "USER_LOGOUT_SUCCESS",
                "CANCEL_BOOKING_USER_NOT_CONSUMER"
        );
    }

    @Test
    void cancelNotConsumer() {
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        Booking booking = createConsumerAndBookFirstEvent(controller, 1);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand(STAFF_EMAIL, STAFF_PASSWORD));

        startOutputCapture();
        CancelBookingCommand cancelCmd = new CancelBookingCommand(booking.getBookingNumber());
        controller.runCommand(cancelCmd);
        assertFalse(cancelCmd.getResult());
        stopOutputCaptureAndCompare(
                "CANCEL_BOOKING_USER_NOT_CONSUMER"
        );
    }
}

import command.*;
import controller.Controller;
import model.*;
import org.junit.jupiter.api.Test;

import java.awt.print.Book;
import java.time.LocalDateTime;

public class ReviewEventSystemTests extends ConsoleTest {

    @Test
    void reviewEventWithInvalidEventNumber(){
        Controller controller = createController();
        createStaffAndEvent(10, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new ReviewEventCommand(100, "I enjoyed it"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_INVALID_EVENT_NUM"
        );
    }
    @Test
    void reviewEventNotHappenedYet(){
        Controller controller = createController();

        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));

        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime more = LocalDateTime.now().plusHours(2);

        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                100,
                0,
                "55.94872684464941 -3.199892044473183",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);

        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new BookEventCommand(1, 1));
        controller.runCommand(new ReviewEventCommand(1, "I enjoyed it"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "REVIEW_EVENT_NOT_HAPPENED_YET"
        );
    }

    @Test
    void reviewEventAsStaff(){
        Controller controller = createController();

        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));

        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime more = LocalDateTime.now().plusHours(2);

        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                100,
                0,
                "55.94872684464941 -3.199892044473183",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);

        startOutputCapture();
        controller.runCommand(new ReviewEventCommand(1, "I enjoyed it"));
        stopOutputCaptureAndCompare(

                "REVIEW_EVENT_AUTHOR_NOT_CONSUMER"
        );
    }

    @Test
    void reviewEventWithNoBooking(){
        Controller controller = createController();
        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime more = LocalDateTime.now().plusHours(2);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                100,
                0,
                "55.94368888764689 -3.1888246174917114",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        RegisterConsumerCommand cmd = new RegisterConsumerCommand(
                "Joe",
                "Joe@gmail.com",
                "093",
                CONSUMER_ADDR,
                "joe"
        );
        controller.runCommand(cmd);
        controller.runCommand(new ReviewEventCommand(1, "It was good"));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "REVIEW_EVENT_AUTHOR_NO_BOOKING");
    }
    @Test
    void reviewEventWithInvalidBooking(){
        Controller controller = createController();
        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime more = LocalDateTime.now().plusHours(2);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                100,
                0,
                "55.94368888764689 -3.1888246174917114",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        RegisterConsumerCommand cmd = new RegisterConsumerCommand(
                "Joe",
                "Joe@gmail.com",
                "093",
                CONSUMER_ADDR,
                "joe"
        );
        controller.runCommand(cmd);
        BookEventCommand bookcmd = new BookEventCommand(1,1);
        controller.runCommand(bookcmd);
        Booking booking = bookcmd.getResult();
        booking.setStatus(BookingStatus.CancelledByConsumer);
        controller.runCommand(new ReviewEventCommand(1,"content"));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "REVIEW_EVENT_BOOKING_INVALID");
    }
    @Test
    void reviewEventSuccess(){
        Controller controller = createController();
        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime more = LocalDateTime.now().plusHours(2);
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                100,
                0,
                "55.94368888764689 -3.1888246174917114",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);
        Event event = eventCmd.getResult();
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        RegisterConsumerCommand cmd = new RegisterConsumerCommand(
                "Joe",
                "Joe@gmail.com",
                "093",
                CONSUMER_ADDR,
                "joe"
        );
        controller.runCommand(cmd);
        BookEventCommand bookcmd = new BookEventCommand(1,1);
        controller.runCommand(bookcmd);
        Booking booking = bookcmd.getResult();
        LocalDateTime timeEventStart = LocalDateTime.now().minusHours(25);
        LocalDateTime timeEventEnd = LocalDateTime.now().minusHours(24);
        LocalDateTime bookingDateTime = LocalDateTime.now().minusHours(26);

        event.setStartDateTime(timeEventStart);
        event.setEndDateTime(timeEventEnd);
        booking.setBookingDateTime(bookingDateTime);

        controller.runCommand(new ReviewEventCommand(1,"content"));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "BOOK_EVENT_SUCCESS",
                "REVIEW_EVENT_SUCCESS");
    }





}

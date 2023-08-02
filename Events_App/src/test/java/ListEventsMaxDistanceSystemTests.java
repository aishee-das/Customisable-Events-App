import command.*;
import controller.Controller;
import model.Consumer;
import model.EventType;
import model.TransportMode;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ListEventsMaxDistanceSystemTests extends ConsoleTest {

    @Test
    void listEventsMaxDistanceAsConsumer() {
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Joe",
                CONSUMER_EMAIL,
                "0123456789",
                "55.947248 -3.184188",
                CONSUMER_PASSWORD,
                "hasSocialDistancing=false,hasAirFiltration=false"
        ));
        controller.runCommand(new ListEventsMaxDistanceCommand(true, true, LocalDate.now(),
                                                                1, TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_UPDATE_PROFILE_SUCCESS",
                "LIST_EVENTS_MAX_DISTANCE_SUCCESS"
        );
    }
    @Test
    void listEventsMaxDistanceNotLoggedIn(){
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new ListEventsMaxDistanceCommand(true, true, LocalDate.now(),
                1, TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "LIST_EVENTS_MAX_DISTANCE_USER_NOT_LOGGED_IN"
        );
    }

    @Test
    void listEventsMaxDistanceAsStaff(){
        Controller controller = createStaffAndEvent(1, 48);
        startOutputCapture();
        controller.runCommand(new ListEventsMaxDistanceCommand(true, true, LocalDate.now(),
                1, TransportMode.car));
        stopOutputCaptureAndCompare(
                "LIST_EVENTS_MAX_DISTANCE_USER_NOT_CONSUMER"
        );
    }

    @Test
    void listEventsWithBlankConsumerAddress(){
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
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Joe",
                "joe@gmail.com",
                "093",
                "55.94368888764689 -3.188824617491",
                "123");
        controller.runCommand(cmd);
        Consumer consumer = cmd.getResult();
        consumer.setAddress("");
        controller.runCommand(new ListEventsMaxDistanceCommand(true,
                true,
                LocalDate.now(),
                1,
                TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_MAX_DISTANCE_NO_CONSUMER_ADDR"
        );
    }
    @Test
    void listEventsWithNullConsumerAddress(){
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
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Joe",
                "joe@gmail.com",
                "093",
                "55.94368888764689 -3.188824617491",
                "123");
        controller.runCommand(cmd);
        Consumer consumer = cmd.getResult();
        String test = null;
        consumer.setAddress(test);
        controller.runCommand(new ListEventsMaxDistanceCommand(true,
                true,
                LocalDate.now(),
                1,
                TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_MAX_DISTANCE_NO_CONSUMER_ADDR"
        );
    }

    @Test
    void listEventsWithNegativeDistance() {
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
        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Joe",
                "joe@gmail.com",
                "093",
                "55.94368888764689 -3.188824617491",
                "123");
        controller.runCommand(cmd);
        Consumer consumer = cmd.getResult();
        String test = null;
        consumer.setAddress(test);
        controller.runCommand(new ListEventsMaxDistanceCommand(true,
                true,
                LocalDate.now(),
                -10,
                TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LIST_EVENTS_MAX_DISTANCE_INVALID_DISTANCE"
        );
    }
}

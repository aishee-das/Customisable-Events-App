import command.*;
import controller.Controller;
import model.Consumer;
import model.Event;
import model.EventType;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class GetEventDirectionsSystemTests extends ConsoleTest {

    @Test
    void getDirectionsForEventWithinBounds(){
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new GetEventDirectionsCommand(1, TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "GET_EVENT_DIRECTIONS_SUCCESS"
        );
    }

    @Test
    void getDirectionsToEventThatIsNotReal(){
        Controller controller = createStaffAndEvent(1, 48);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new GetEventDirectionsCommand(100, TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "GET_EVENT_DIRECTIONS_NO_SUCH_EVENT"
        );
    }

    @Test
    void getDirectionsAsStaff(){
        Controller controller = createController();
        startOutputCapture();
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
        controller.runCommand(new GetEventDirectionsCommand(1, TransportMode.car));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER"
        );
    }

    @Test
    void getDirectionsWhenVenueAddressIsNull() {
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
        String temp = null;
        event.setVenueAddress(temp);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new GetEventDirectionsCommand(1, TransportMode.car));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS");
    }

    @Test
    void getDirectionsWhenUserAddressIsNull() {
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
                "55.94368888764689 -3.1888246174917114",
                "joe");

        controller.runCommand(cmd);
        Consumer consumer = cmd.getResult();
        consumer.setAddress("");


        controller.runCommand(new GetEventDirectionsCommand(1, TransportMode.car));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS");
    }


}

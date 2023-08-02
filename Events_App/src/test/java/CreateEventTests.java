import command.AddEventTagCommand;
import command.CreateEventCommand;
import command.LogoutCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.Event;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;

public class CreateEventTests extends ConsoleTest {
    private static void registerPawsForAwwws(Controller controller) {
        controller.runCommand(new RegisterStaffCommand(
                "hasta@vista.baby",
                "very insecure password 123",
                "Nec temere nec timide"
        ));
    }

    private static Event createEvent(Controller controller,
                                     LocalDateTime startDateTime,
                                     LocalDateTime endDateTime) {
        CreateEventCommand eventCmd = new CreateEventCommand(
                "Puppies against depression",
                EventType.Theatre,
                500,
                100,
                "55.931117 -3.177361",
                "Come and enjoy some pets for pets",
                startDateTime,
                endDateTime,
                "hasSocialDistancing=true,hasAirFiltration=true"
        );
        controller.runCommand(eventCmd);
        return eventCmd.getResult();
    }

    @Test
    void createNonTicketedEvent() {
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
                0,
                0,
                "55.94368888764689 -3.1888246174917114",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );

        //createStaffAndEvent(0, 1);
//
//        stopOutputCaptureAndCompare(
//                "REGISTER_STAFF_SUCCESS",
//                "USER_LOGIN_SUCCESS",
//                "CREATE_EVENT_SUCCESS"
//        );
    }

    @Test
    void createTicketedEvent() {
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
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS"
        );





//        startOutputCapture();
//        Controller controller = createStaffAndEvent(1, 1);
//        controller.runCommand(new LogoutCommand());
//        stopOutputCaptureAndCompare(
//                "REGISTER_STAFF_SUCCESS",
//                "USER_LOGIN_SUCCESS",
//                "CREATE_EVENT_SUCCESS",
//                "USER_LOGOUT_SUCCESS"
//        );
    }

    @Test
    void createEventInThePast() {
        Controller controller = createController();
        startOutputCapture();
        registerPawsForAwwws(controller);
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).plusHours(2)
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_IN_THE_PAST"
        );
    }

    @Test
    void createEventWithEndBeforeStart() {
        Controller controller = createController();
        startOutputCapture();
        registerPawsForAwwws(controller);
        Event event = createEvent(
                controller,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).minusHours(2)
        );
        assertNull(event);
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_START_AFTER_END"
        );
    }






}

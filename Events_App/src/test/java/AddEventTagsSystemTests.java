import command.AddEventTagCommand;
import command.CreateEventCommand;
import command.LogoutCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class AddEventTagsSystemTests extends ConsoleTest {
    @Test
    void createEventWithFourTags() {
        Controller controller = createStaffAndEvent(10, 1);
        Set<String> values = new HashSet<>();
        values.add("Small");
        values.add("Medium");
        values.add("Large");
        controller.runCommand(new AddEventTagCommand("venueSize","Medium", values));
        startOutputCapture();
        CreateEventCommand cmd = new CreateEventCommand("Title",
                EventType.Theatre,
                10,
                100,
                "55.931117 -3.177361",
                "description",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                "hasSocialDistancing=false,hasAirFiltration=false,venueCapacity=200,venueSize=Medium"
        );
        controller.runCommand(cmd);
        stopOutputCaptureAndCompare("CREATE_EVENT_SUCCESS");
    }

    @Test
    void addEventTagWithLessThanTwoValues() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        Set<String> values = new HashSet<>();
        values.add("Small");
        controller.runCommand(new AddEventTagCommand("Venue Size", "Small", values));
        stopOutputCaptureAndCompare("REGISTER_STAFF_SUCCESS","USER_LOGIN_SUCCESS", "INVALID_NUM_TAG_VALUES");
    }

    @Test
    void addEventTagThatClashes() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        Set<String> values = new HashSet<>();
        values.add("true");
        values.add("false");
        controller.runCommand(new AddEventTagCommand("hasSocialDistancing", "false", values));
        stopOutputCaptureAndCompare("REGISTER_STAFF_SUCCESS","USER_LOGIN_SUCCESS", "ADD_EVENT_TAG_NAME_CLASH");

    }

    @Test
    void addEventTagAsConsumer() {
        Controller controller = createStaffAndEvent(10, 1);
        controller.runCommand(new LogoutCommand());
        startOutputCapture();
        createConsumer(controller);
        Set<String> values = new HashSet<>();
        values.add("Small");
        values.add("Medium");
        values.add("Large");
        controller.runCommand(new AddEventTagCommand("Venue Size","Medium", values));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","CURRENT_USER_NOT_STAFF");
    }

    @Test
    void createEventTagCollectionWhereDefaultValueIsNotReal() {
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

        Set<String> values = new HashSet<>();
        values.add("Small");
        values.add("Medium");
        values.add("Large");
        controller.runCommand(new AddEventTagCommand("Venue Size","Extra Large", values));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "DEFAULT_TAG_NOT_IN_LIST");
    }



}

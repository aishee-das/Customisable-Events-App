import command.AddEventTagCommand;
import command.LoadAppStateCommand;
import command.LogoutCommand;
import command.RegisterConsumerCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class LoadAppStateSystemTests extends ConsoleTest {

    //FOR ANY OF THESE TESTS TO WORK RUN THE SAVEAPPSTATESYSTEMTESTS FIRST
    @Test
    void loadAppStateForTwoConsumersAndOneEvent() {
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);
        controller.runCommand(new LoadAppStateCommand("twoConsumersOneEvent"));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LOAD_APP_STATE_SUCCESS"
        );
    }

    @Test
    void loadAppStateAsConsumer() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new LoadAppStateCommand("venueSizeTag"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LOAD_APP_STATE_COMMAND_NOT_STAFF"
        );
    }

    @Test
    void loadAppStateForClashingUsers() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Joseph",
                "Joe@gmail.com",
                "012938123",
                "55.950915 -3.141129",
                "joe"
        ));
        controller.runCommand(new LogoutCommand());
        createStaff(controller);
        controller.runCommand(new LoadAppStateCommand("twoConsumersOneEvent"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "LOAD_APP_STATE_COMMAND_USER_EMAIL_CLASHES"
        );
    }

    @Test
    void loadAppStateForClashingEventTags(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);

        Set<String> values = new HashSet<>();
        values.add("Extra Small");
        values.add("Small");
        values.add("Medium");
        values.add("Large");
        values.add("Extra Large");
        controller.runCommand(new AddEventTagCommand(
                "venueSize",
                "Medium",
                values
        ));
        controller.runCommand(new LoadAppStateCommand("venueSizeTag"));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "LOAD_APP_STATE_COMMAND_EVENT_TAG_CLASHES"
        );
    }
}

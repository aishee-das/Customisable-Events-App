import command.*;
import controller.Controller;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class SaveAppStateSystemTests extends ConsoleTest {

    @Test
    void saveAppStateWithTwoConsumersAndOneEvent(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand(
                "Joe",
                "Joe@gmail.com",
                "091238122",
                "55.947248 -3.184188",
                "joe"
        ));
        controller.runCommand(new LogoutCommand());
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
        controller.runCommand(new SaveAppStateCommand("twoConsumersOneEvent"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "SAVE_APP_STATE_SUCCESS"
        );
    }

    @Test
    void saveAppStateAsConsumer(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new SaveAppStateCommand("backup"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "SAVE_APP_STATE_NOT_STAFF"
        );
    }

    @Test
    void saveAppStateWithNewTags(){
        Controller controller = createController();
        startOutputCapture();
        createStaff(controller);


        Set<String> values = new HashSet<>();
        values.add("Small");
        values.add("Medium");
        values.add("Large");
        controller.runCommand(new AddEventTagCommand("venueSize","Medium", values));
        controller.runCommand(new SaveAppStateCommand("venueSizeTag"));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "ADD_EVENT_TAG_SUCCESS",
                "SAVE_APP_STATE_SUCCESS"
        );
    }





}

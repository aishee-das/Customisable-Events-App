import command.CreateEventCommand;
import command.ListEventReviewsCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import model.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ListEventReviewsSystemTests extends ConsoleTest {
    @Test
    void listEventReviews() {
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
                "55.94872684464941 -3.199892044473183",
                "Please be prepared to pay 2.50 pounds on entry",
                now,
                more,
                "hasSocialDistancing=false,hasAirFiltration=false"
        );
        controller.runCommand(eventCmd);
        controller.runCommand(new ListEventReviewsCommand("Puppies against depression"));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "CREATE_EVENT_SUCCESS",
                "LIST_EVENT_REVIEWS_SUCCESS"
        );
    }
    @Test
    void listEventReviewsForEventThatDoesNotExist() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new ListEventReviewsCommand("Puppies against depression"));
        stopOutputCaptureAndCompare(
                "LIST_EVENT_REVIEWS_COMMAND_NO_EVENT_FOUND"
        );
    }

}

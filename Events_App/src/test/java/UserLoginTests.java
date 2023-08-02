import command.LoginCommand;
import command.LogoutCommand;
import command.RegisterConsumerCommand;
import command.RegisterStaffCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;

public class UserLoginTests extends ConsoleTest {
    @Test
    void createConsumer() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "55.947084 -3.208483",
                "McMuffin"
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }

    @Test
    void createConsumerAndRelogin() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "55.947084 -3.208483",
                "McMuffin"
        ));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand("always@lovin.it", "McMuffin"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }

    @Test
    void createConsumerThenLoginWrongEmail() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "55.947084 -3.208483",
                "McMuffin"
        ));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand("admin@inf2sepp.app", "123456"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_EMAIL_NOT_REGISTERED"
        );
    }

    @Test
    void createConsumerThenLoginWrongPassword() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "55.947084 -3.208483",
                "McMuffin"
        ));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand("always@lovin.it", "123456"));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_WRONG_PASSWORD"
        );
    }

    @Test
    void createStaff() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterStaffCommand(
                "always@lovin.it",
                "Ronald McDonald",
                "Nec temere nec timide"
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }

    @Test
    void createStaffAndRelogin() {
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterStaffCommand(
                "always@lovin.it",
                "McMuffin",
                "Nec temere nec timide"
        ));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new LoginCommand("always@lovin.it", "McMuffin"));
        stopOutputCaptureAndCompare(
                "REGISTER_STAFF_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_LOGIN_SUCCESS"
        );
    }
}

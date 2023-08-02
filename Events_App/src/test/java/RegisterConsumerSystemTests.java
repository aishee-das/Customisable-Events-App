import command.LogoutCommand;
import command.RegisterConsumerCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;

public class RegisterConsumerSystemTests extends ConsoleTest {
    @Test
    void registerNewConsumerWhileLoggedIn() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        controller.runCommand(new RegisterConsumerCommand(
                "Ronald McDonald",
                "always@lovin.it",
                "000",
                "55.947084 -3.208483",
                "McMuffin"
        ));
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","USER_REGISTER_LOGGED_IN"
        );
    }

    @Test
    void registerNewConsumerAddressNull(){
        Controller controller = createController();
        String addr = null;
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Joe",
                "joe@gmail.com",
                "093",
                addr,
                "joe"
        ));
        stopOutputCaptureAndCompare("USER_REGISTER_FIELDS_CANNOT_BE_NULL");
    }

    @Test
    void registerNewConsumerWithEmailAlreadyUsed(){
        Controller controller = createController();
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Joe",
                "joe@gmail.com",
                "093",
                CONSUMER_ADDR,
                "joe"
        ));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand(
                "Bob",
                "joe@gmail.com",
                "078",
                CONSUMER_ADDR,
                "bob"
        ));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_SUCCESS",
                "USER_LOGIN_SUCCESS",
                "USER_LOGOUT_SUCCESS",
                "USER_REGISTER_EMAIL_ALREADY_REGISTERED");
    }
    @Test
    void registerNewConsumerWithAddressInvalidFormat(){
        Controller controller = createController();
        String addr = "Edinburgh Castle";
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Joe",
                "joe@gmail.com",
                "093",
                addr,
                "joe"
        ));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_COMMAND_ADDRESS_INVALID_FORMAT");
    }
    @Test
    void registerNewConsumerWithAddressOutsideBounds(){
        Controller controller = createController();
        String addr = "41.466014 -77.366684";
        startOutputCapture();
        controller.runCommand(new RegisterConsumerCommand(
                "Joe",
                "joe@gmail.com",
                "093",
                addr,
                "joe"
        ));
        stopOutputCaptureAndCompare("REGISTER_CONSUMER_COMMAND_ADDRESS_OUTSIDE_BOUNDS");
    }


}

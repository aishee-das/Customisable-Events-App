import command.UpdateConsumerProfileCommand;
import controller.Controller;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateConsumerProfileSystemTests extends ConsoleTest{
    @Test
    void updateConsumerInvalidLongValue() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                "55.947084 -181.22",
                CONSUMER_PASSWORD,
                "hasSocialDistancing=true"
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_LONG_VALUE"
        );
    }

    @Test
    void updateConsumerInvalidLatValue() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                "100.22 -3.208483",
                CONSUMER_PASSWORD,
                "hasSocialDistancing=true"
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_LAT_VALUE"
        );
    }

    @Test
    void updateConsumerInvalidAddressFormat() {
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                "55.947084,-3.208483",
                CONSUMER_PASSWORD,
                "hasSocialDistancing=true"
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_FORMAT"
        );
    }

    @Test
    void updateConsumerNullAddress(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        String addr = null;
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                addr,
                CONSUMER_PASSWORD,
                "hasSocialDistancing=true"
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","UPDATE_CONSUMER_PROFILE_FIELDS_ARE_NULL"
        );
    }
    @Test
    void updateConsumerWithInvalidPreferenceKey(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                "55.947084 -3.208483",
                CONSUMER_PASSWORD,
                "hasFreeFood=true"
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","UPDATE_CONSUMER_PROFILE_INVALID_PREFERENCE_KEY"
        );
    }
    @Test
    void updateConsumerWithInvalidPreferenceValue(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324456897",
                "55.947084 -3.208483",
                CONSUMER_PASSWORD,
                "hasSocialDistancing=maybe"
        );
        controller.runCommand(updateCmd);
        assertFalse(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","UPDATE_CONSUMER_PROFILE_INVALID_PREFERENCE_VALUE"
        );
    }
    @Test
    void updateConsumerSuccess(){
        Controller controller = createController();
        startOutputCapture();
        createConsumer(controller);
        UpdateConsumerProfileCommand updateCmd = new UpdateConsumerProfileCommand(
                CONSUMER_PASSWORD,
                "Chihuahua Fan",
                CONSUMER_EMAIL,
                "01324156897",
                "55.947084 -3.208483",
                CONSUMER_PASSWORD,
                "hasSocialDistancing=false"
        );
        controller.runCommand(updateCmd);
        assertTrue(updateCmd.getResult());
        stopOutputCaptureAndCompare(
                "REGISTER_CONSUMER_SUCCESS","USER_LOGIN_SUCCESS","USER_UPDATE_PROFILE_SUCCESS"
        );
    }

}

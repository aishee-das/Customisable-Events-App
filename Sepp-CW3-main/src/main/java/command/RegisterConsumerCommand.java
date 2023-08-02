package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.Consumer;
import model.User;
import view.IView;

import java.util.Map;

/**
 * {@link RegisterConsumerCommand} allows users to register a new {@link Consumer} account on the system.
 * After registration, they are automatically logged in.
 */



public class RegisterConsumerCommand implements ICommand<Consumer> {
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String address;
    private final String password;
    private Consumer newConsumerResult;

    //55.947084 -3.208483

    /**
     *
     * @param name        full name of the consumer
     * @param email       personal email address (which will be used as the account email)
     * @param phoneNumber phone number (to allow notification of {@link model.Event} cancellations)
     * @param address     Consumer address (for filtering events by distance away), optional
     * @param password    password to log in to the system in the future
     */
    public RegisterConsumerCommand(String name,
                                   String email,
                                   String phoneNumber,
                                   String address,
                                   String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that no user is currently logged in
     * @verifies.that name, email, phoneNumber, and password are all not null
     * @verifies.that there is no user with the same email address already registered
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        if (currentUser != null) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_LOGGED_IN,
                    Map.of("currentUser", currentUser)
            );
            newConsumerResult = null;
            return;
        }

        if (name == null || email == null || phoneNumber == null || password == null || address==null) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL,
                    Map.of("name", String.valueOf(name),
                            "email", String.valueOf(email),
                            "phoneNumber", String.valueOf(phoneNumber),
                            "address", String.valueOf(address),
                            "password", "***")
            );
            newConsumerResult = null;
            return;
        }

        if (context.getUserState().getAllUsers().containsKey(email)) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED,
                    Map.of("email", email)
            );
            newConsumerResult = null;
            return;
        }

        //Checks if Consumer Address is within bounds
        //Checks if venueAddress is within bounds
        try {
            String[] firstSplit = address.split(" ");
            Double lat = Double.parseDouble(firstSplit[0]);
            Double longi = Double.parseDouble(firstSplit[1]);

            //Checks if latitude value is a real possible latitude value
            if(lat > 90 || lat < -90) {
                view.displayFailure(
                        "RegisterConsumerCommand",
                        RegisterConsumerCommand.LogStatus.REGISTER_CONSUMER_COMMAND_ADDRESS_INVALID_FORMAT,
                        Map.of("lat", lat));
                newConsumerResult = null;
                return;
            }
            //Checks if longitudinal value is a real possible longitudinal value
            if(longi > 180 || longi < -180) {
                view.displayFailure(
                        "RegisterConsumerCommand",
                        RegisterConsumerCommand.LogStatus.REGISTER_CONSUMER_COMMAND_ADDRESS_INVALID_FORMAT,
                        Map.of("lat", lat));
                newConsumerResult = null;
                return;
            }

            GHPoint newAddress = new GHPoint(lat, longi);
            MapSystem map = context.getMapSystem();
            boolean withinBounds = map.isPointWithingMapBounds(newAddress);

            //checks coords are actually within bounds of program
            if(!withinBounds) {
                view.displayFailure(
                        "RegisterConsumerCommand",
                        RegisterConsumerCommand.LogStatus.REGISTER_CONSUMER_COMMAND_ADDRESS_OUTSIDE_BOUNDS,
                        Map.of("address", address));
                newConsumerResult = null;
                return;
            }
            /**
             * Catch exception for failure to split the String into two
             * separate latitudinal values and longitudinal ones
             * */

        } catch (Exception ex) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    RegisterConsumerCommand.LogStatus.REGISTER_CONSUMER_COMMAND_ADDRESS_INVALID_FORMAT,
                    Map.of("address", address));
            newConsumerResult = null;
            return;
        }





        Consumer consumer = new Consumer(name, email, phoneNumber, address, password);
        context.getUserState().addUser(consumer);
        view.displaySuccess(
                "RegisterConsumerCommand",
                LogStatus.REGISTER_CONSUMER_SUCCESS,
                Map.of("name", name,
                        "email", email,
                        "phoneNumber", phoneNumber,
                        "address", String.valueOf(address),
                        "password", "***")
        );

        context.getUserState().setCurrentUser(consumer);
        view.displaySuccess(
                "RegisterConsumerCommand",
                LogStatus.USER_LOGIN_SUCCESS,
                Map.of("email", email,
                        "password", "***")
        );
        newConsumerResult = consumer;
    }

    /**
     * @return Instance of the newly registered {@link Consumer} if successful and null otherwise
     */
    @Override
    public Consumer getResult() {
        return newConsumerResult;
    }

    private enum LogStatus {
        REGISTER_CONSUMER_SUCCESS,
        USER_REGISTER_LOGGED_IN,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_EMAIL_ALREADY_REGISTERED,
        USER_LOGIN_SUCCESS,
        REGISTER_CONSUMER_COMMAND_ADDRESS_INVALID_FORMAT,
        REGISTER_CONSUMER_COMMAND_ADDRESS_OUTSIDE_BOUNDS

    }
}

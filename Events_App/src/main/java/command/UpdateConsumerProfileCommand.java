package command;

import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.*;
import view.IView;

import java.util.Map;
import java.util.Set;

/**
 * {@link UpdateConsumerProfileCommand} allows {@link Consumer}s to update their account details and change the default
 * Covid-19 preferences.
 */
public class UpdateConsumerProfileCommand extends UpdateProfileCommand {
    private final String oldPassword;
    private final String newName;
    private final String newEmail;
    private final String newPhoneNumber;
    private final String newAddress;
    private final String newPassword;
    private final EventTagCollection newPreferences;

    /**
     * @param oldPassword    account password before the change, required for extra security verification. Must not be null
     * @param newName        full name of the person holding this account. Must not be null
     * @param newEmail       email address to use for this account. Must not be null
     * @param newPhoneNumber phone number to use for this account (used for notifying the {@link Consumer} of any
     *                       {@link Event} cancellations that they have bookings for). Must not be null
     * @param newAddress     updated Consumer address. Optional and may be null
     * @param newPassword    password to use for this account. Must not be null
     * @param newPreferences a {@link EventTagCollection} object of  preferences, used for filtering events
     *                       in the {@link ListEventsCommand}. If null, default preferences are applied
     */
    public UpdateConsumerProfileCommand(String oldPassword,
                                        String newName,
                                        String newEmail,
                                        String newPhoneNumber,
                                        String newAddress,
                                        String newPassword,
                                        String newPreferences) {
        this.oldPassword = oldPassword;
        this.newName = newName;
        this.newEmail = newEmail;
        this.newPhoneNumber = newPhoneNumber;
        this.newAddress = newAddress;
        this.newPassword = newPassword;
        this.newPreferences = new EventTagCollection(newPreferences);
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that oldPassword, newName, newEmail, newPhoneNumber, and newPassword are all not null
     * @verifies.that current user is logged in
     * @verifies.that oldPassword matches the current user's password
     * @verifies.that there is no other user already registered with the same email address as newEmail
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that address is in correct format
     * @verifies.that address is within bounds
     * @verifies.that tag preferences have valid tag names
     * @verifies.that tag preferences have valid tag values
     */

    @Override
    public void execute(Context context, IView view) {
        //Checks for null values in input
        if (oldPassword == null || newName == null || newEmail == null || newPhoneNumber == null || newPassword == null || newAddress == null) {
            view.displayFailure(
                    "UpdateConsumerProfileCommand",
                    LogStatus.UPDATE_CONSUMER_PROFILE_FIELDS_ARE_NULL,
                    Map.of("oldPassword", "***",
                            "newName", String.valueOf(newName),
                            "newEmail", String.valueOf(newEmail),
                            "newPhoneNumber", String.valueOf(newPhoneNumber),
                            "newPassword", "***",
                            "newPreferences", newPreferences
                    ));
            successResult = false;
            return;
        }

        User currentUser = context.getUserState().getCurrentUser();

        boolean isInvalid = isProfileUpdateInvalid(context, view, oldPassword, newEmail);
        if(isInvalid) {
            successResult = false;
            return;
        }


        //Checks that current user is a Consumer
        if(!(currentUser instanceof Consumer)) {
            view.displayFailure(
                    "UpdateConsumerProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_NOT_CONSUMER,
                    Map.of("currentUser", currentUser instanceof Consumer));
            successResult = false;
            return;
        }
        //First checks that a newAddress is inputted
        if(newAddress != null) {
            //Checks it's in desired format
            try {
                String[] firstSplit = newAddress.split(" ");
                Double lat = Double.parseDouble(firstSplit[0]);
                Double longi = Double.parseDouble(firstSplit[1]);

                //Checks if latitude value is a real possible latitude value
                if(lat > 90 || lat < -90) {
                    view.displayFailure(
                            "UpdateConsumerProfileCommand",
                            LogStatus.UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_LAT_VALUE,
                            Map.of("lat", lat));
                    successResult = false;
                    return;
                }
                //Checks if longitudinal value is a real possible longitudinal value
                if(longi > 180 || longi < -180) {
                    view.displayFailure(
                            "UpdateConsumerProfileCommand",
                            LogStatus.UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_LONG_VALUE,
                            Map.of("lat", lat));
                    successResult = false;
                    return;
                }

                GHPoint newAddress = new GHPoint(lat, longi);
                MapSystem map = context.getMapSystem();
                boolean withinBounds = map.isPointWithingMapBounds(newAddress);

                //checks coords are actually within bounds of program
                if(!withinBounds) {
                    view.displayFailure(
                            "UpdateConsumerProfileCommand",
                            LogStatus.UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_OUTSIDE_BOUNDS,
                            Map.of("newAddress", newAddress));
                    successResult = false;
                    return;
                }
                /**
                 * Catch exception for failure to split the String into two
                 * separate latitudinal values and longitudinal ones
                 * */

            } catch (Exception ex) {
                view.displayFailure(
                        "UpdateConsumerProfileCommand",
                        LogStatus.UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_FORMAT,
                        Map.of("newAddress", newAddress));
                successResult = false;
                return;
            }


        }

        //Checks that new preferences only include known tag names and values
        Map<String, EventTag> systemTags = context.getEventState().getPossibleTags();

        //System.out.println("Tags known right now and their values");
        for(Map.Entry<String, EventTag> entry : systemTags.entrySet()) {
            EventTag eventTag = entry.getValue();
            Set<String> values = eventTag.getValues();
        }


        for(Map.Entry<String, String> entry : newPreferences.getTags().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //Checks if key exists

            //System.out.println("This is the key being checked rn: " + key);
            if(!(systemTags.containsKey(key))) {
                view.displayFailure(
                        "UpdateConsumerProfileCommand",
                        LogStatus.UPDATE_CONSUMER_PROFILE_INVALID_PREFERENCE_KEY,
                        Map.of("key", key));
                successResult = false;
                return;
            }
            EventTag eventTag = systemTags.get(key);
            Set<String> values = eventTag.getValues();
            //System.out.println("These are the values that the key could be:");
            //System.out.println(values);
            //System.out.println("And this is the value of the eventtag rn: ");
            //System.out.println(value);
            //Checks if value exists within possible tag values
            if(!(values.contains(value))) {
                view.displayFailure(
                        "UpdateConsumerProfileCommand",
                        LogStatus.UPDATE_CONSUMER_PROFILE_INVALID_PREFERENCE_VALUE,
                        Map.of("key", key));
                successResult = false;
                return;
            }
        }

        changeUserEmail(context, newEmail);
        currentUser.updatePassword(newPassword);
        Consumer consumer = (Consumer) currentUser;
        consumer.setName(newName);
        consumer.setPhoneNumber(newPhoneNumber);
        consumer.setAddress(newAddress);
        consumer.setPreferences(newPreferences);

        view.displaySuccess(
                "UpdateConsumerProfileCommand",
                LogStatus.USER_UPDATE_PROFILE_SUCCESS,
                Map.of("newName", newName,
                        "newEmail", newEmail,
                        "newPhoneNumber", newPhoneNumber,
                        "newAddress", String.valueOf(newAddress),
                        "newPassword", "***",
                        "preferences", newPreferences)
        );
        successResult = true;
        //System.out.println("Success Result: ");
        //System.out.println(successResult);
    }

    private enum LogStatus {
        UPDATE_CONSUMER_PROFILE_INVALID_UPDATE,
        UPDATE_CONSUMER_PROFILE_FIELDS_ARE_NULL,
        USER_UPDATE_PROFILE_NOT_CONSUMER,
        UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_FORMAT,
        UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_OUTSIDE_BOUNDS,
        UPDATE_CONSUMER_PROFILE_INVALID_PREFERENCE_KEY,
        UPDATE_CONSUMER_PROFILE_INVALID_PREFERENCE_VALUE,
        UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_LAT_VALUE,
        UPDATE_CONSUMER_PROFILE_NEW_ADDRESS_INVALID_LONG_VALUE,
        USER_UPDATE_PROFILE_SUCCESS
    }
}

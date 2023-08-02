package command;

import controller.Context;
import model.*;
import view.IView;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadAppStateCommand implements ICommand<Boolean>{

    private Boolean successResult;
    private String fileName;

    /**
     * @param fileName the name of the file which is being loaded
     * */

    public LoadAppStateCommand(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current user is {@Link Staff}
     * @verifies.that file actually exists
     * @verifies.that event tags don't clash
     * @verifies.that event values don't clash
     * @verifies.that bookings don't clash
     * @verifies.that users don't clash
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        //Checks that the currentUser is Staff
        if(!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_COMMAND_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            successResult = false;
            return;
        }
        String fileLocation = "data/" + fileName + ".ser";
        List<Serializable> unserializedData = new ArrayList<>();
        try{
            FileInputStream fileInputStream = new FileInputStream(fileLocation);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            unserializedData = (List<Serializable>)objectInputStream.readObject();
            fileInputStream.close();

        } catch(IOException ex) {
            ex.printStackTrace();
            view.displayFailure(
                    "LoadAppStateCommand",
                    LoadAppStateCommand.LogStatus.LOAD_APP_STATE_COMMAND_FILE_NOT_FOUND,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Event> loadedEvents = (List<Event>) unserializedData.get(0);
        List<Booking> loadedBookings = (List<Booking>) unserializedData.get(1);
        Map<String, User> loadedUsers = (Map<String, User>) unserializedData.get(2);
        Map<String, EventTag> loadedTags = (Map<String, EventTag>) unserializedData.get(3);

       //Check for clashing event tags
        Map<String, EventTag> currentTags = context.getEventState().getPossibleTags();

        for(Map.Entry<String, EventTag> entry: loadedTags.entrySet()) {
            String loadedTagName = entry.getKey();
            Set<String> loadedTagValues = entry.getValue().values;

            //Checks if tag already exists within system
            if(currentTags.containsKey(loadedTagName)) {
                Set<String> currentTagValues = currentTags.get(loadedTagName).getValues();
                //If the tag values for the eventTag are different
                if(!(currentTagValues.equals(loadedTagValues))) {
                    view.displayFailure(
                            "LoadAppStateCommand",
                            LoadAppStateCommand.LogStatus.LOAD_APP_STATE_COMMAND_EVENT_TAG_CLASHES,
                            Map.of("loadedTagValues", loadedTagValues,
                                    "currentTagValues", currentTagValues)
                    );
                    successResult = false;
                    return;
                }
                }
            }

        //Check for clashing user emails
        Map<String, User> currentUsers = context.getUserState().getAllUsers();
        List<String> emailsToRemove = new ArrayList<>();

        for(Map.Entry<String, User> entry: loadedUsers.entrySet()) {
            String loadedEmail = entry.getKey();
            User loadedUser = entry.getValue();
            //If the email has a registered user associated with it
            if(currentUsers.containsKey(loadedEmail)) {
                //If the user with the same email are different user instances
                if(!(loadedUser.equals(currentUsers.get(loadedEmail)))) {
                    view.displayFailure(
                            "LoadAppStateCommand",
                            LoadAppStateCommand.LogStatus.LOAD_APP_STATE_COMMAND_USER_EMAIL_CLASHES,
                            Map.of("userToBeLoadedIn", loadedUser.getEmail(),
                                    "userInSystem", currentUsers.get(loadedEmail).getEmail())
                    );
                    successResult = false;
                    return;
                }
                if(loadedUser.equals(currentUsers.get(loadedEmail))){
                    emailsToRemove.add(loadedEmail);
                }
            }
        }

        for(String emailToRemove : emailsToRemove) {
            loadedUsers.remove(emailToRemove);
        }

        //Check for clashing events
        for(int i = 0; i < loadedEvents.size(); i++) {
            Event loadedEvent = loadedEvents.get(i);
            long loadedEventNum = loadedEvent.getEventNumber();
            //Checks if event also exists in the current system
            if(context.getEventState().findEventByNumber(loadedEventNum) != null) {
                String loadedTitle = loadedEvent.getTitle();
                LocalDateTime loadedStartTime = loadedEvent.getStartDateTime();
                LocalDateTime loadedEndTime = loadedEvent.getEndDateTime();

                Event currentEvent = context.getEventState().findEventByNumber(loadedEventNum);
                String currentTitle = currentEvent.getTitle();
                LocalDateTime currentStartTime = currentEvent.getStartDateTime();
                LocalDateTime currentEndTime = currentEvent.getEndDateTime();

                boolean areTitlesSame = loadedTitle.equals(currentTitle);
                boolean areStartTimesSame = loadedStartTime.equals(currentStartTime);
                boolean areEndTimesSame = loadedEndTime.equals(currentEndTime);


                if(!(areTitlesSame) || !(areStartTimesSame) || !(areEndTimesSame)) {
                    view.displayFailure(
                            "LoadAppStateCommand",
                            LoadAppStateCommand.LogStatus.LOAD_APP_STATE_COMMAND_EVENT_CLASHES,
                            Map.of("loadedTitle", loadedTitle,
                                    "loadedStartTime", loadedStartTime,
                                    "loadedEndTime", loadedEndTime)
                    );
                    successResult = false;
                    return;
                }
            }
        }

        //Check for clashing bookings

        for(int i = 0; i < loadedBookings.size(); i++) {
            Booking loadedBooking = loadedBookings.get(i);
            Consumer loadedConsumer = loadedBooking.getBooker();
            Event loadedEvent = loadedBooking.getEvent();
            LocalDateTime loadedBookingDateTime = loadedBooking.getBookingDateTime();
            long loadedBookingNumber = loadedBooking.getBookingNumber();

            if(context.getBookingState().findBookingByNumber(loadedBookingNumber) != null) {
                Booking currentBooking = context.getBookingState().findBookingByNumber(loadedBookingNumber);
                Consumer currentconsumer = currentBooking.getBooker();
                Event currentEvent = currentBooking.getEvent();
                LocalDateTime currentBookingDateTime = currentBooking.getBookingDateTime();

                boolean isConsumerEqual = currentconsumer.equals(loadedConsumer);
                boolean isEventEqual = currentEvent.equals(loadedEvent);
                boolean isBookingTimeEqual = currentBookingDateTime.equals(loadedBookingDateTime);

                if(!(isConsumerEqual) || !(isEventEqual) || !(isBookingTimeEqual)) {
                    view.displayFailure(
                            "LoadAppStateCommand",
                            LoadAppStateCommand.LogStatus.LOAD_APP_STATE_COMMAND_EVENT_CLASHES,
                            Map.of("loadedConsumer", loadedConsumer,
                                    "loadedEvent", loadedEvent,
                                    "loadedBookingNumber", loadedBookingNumber)
                    );
                    successResult = false;
                    return;
                }
            }
        }


        //adding loadedEvents to events:
        for(int i = 0; i < loadedEvents.size(); i++) {
            context.getEventState().getAllEvents().add(loadedEvents.get(i));
        }

        //adding loadedBookings to bookings
        for(int i = 0; i < loadedBookings.size(); i++) {
            context.getBookingState().getAllBookings().add(loadedBookings.get(i));
        }
        //Adding loadedTags to possibleTags
        for(Map.Entry<String, EventTag> entry: loadedTags.entrySet()) {
            String loadedTagName = entry.getKey();
            EventTag loadedTagValues = entry.getValue();
            context.getEventState().getPossibleTags().put(loadedTagName, loadedTagValues);
        }

        //Adding loadedUsers to Users
        for(Map.Entry<String, User> entry: loadedUsers.entrySet()) {
            String loadedEmail = entry.getKey();
            User loadedUser = entry.getValue();
            context.getUserState().getAllUsers().put(loadedEmail, loadedUser);
        }
        successResult = true;
        view.displayFailure(
                "LoadAppStateCommand",
                LoadAppStateCommand.LogStatus.LOAD_APP_STATE_SUCCESS,
                Map.of("successResult", true)
        );
        }

    @Override
    public Boolean getResult() {
        return successResult;
    }
    private enum LogStatus {
        LOAD_APP_STATE_COMMAND_NOT_STAFF,
        LOAD_APP_STATE_COMMAND_EVENT_TAG_CLASHES,
        LOAD_APP_STATE_COMMAND_USER_EMAIL_CLASHES,
        LOAD_APP_STATE_COMMAND_EVENT_CLASHES,
        LOAD_APP_STATE_COMMAND_FILE_NOT_FOUND,
        LOAD_APP_STATE_SUCCESS

    }


}

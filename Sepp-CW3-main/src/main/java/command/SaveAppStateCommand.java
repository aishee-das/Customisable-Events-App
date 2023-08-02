package command;

import controller.Context;
import model.*;
import view.IView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveAppStateCommand implements ICommand<Boolean>{

    private boolean successResult;
    final private String fileName;
    /**
     * @param fileName      Input for constructor which gives name of fileName when exported
     *
     * **/
    public SaveAppStateCommand(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current user is {@Link Staff}
     */
    public void execute(Context context, IView view)  {

        User currentUser = context.getUserState().getCurrentUser();


        //Checks if current user callling the command is Staff
        if(!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "SaveAppStateCommand",
                    LogStatus.SAVE_APP_STATE_NOT_STAFF,
                    Map.of("user", currentUser != null ? currentUser : "none")
            );
            successResult = false;
            return;
        }


        //Get all information regarding all events, bookings and users ready for exporting
        //TO-DO: Need review class to be finished and events class for this to work properly

        List<Event> events = context.getEventState().getAllEvents();
        List<Booking> bookings = context.getBookingState().getAllBookings();
        Map<String, User> users = context.getUserState().getAllUsers();
        Map<String, EventTag> possibleTags = context.getEventState().getPossibleTags();


        //Current implementation of Serializing objects adheres to the following documentation:
        // https://www.oracle.com/technical-resources/articles/java/serializationapi.html

        FileOutputStream fos;
        ObjectOutputStream out;

        List<Serializable> dataToBeSerialized = new ArrayList<>();
        dataToBeSerialized.add((Serializable) events);
        dataToBeSerialized.add((Serializable) bookings);
        dataToBeSerialized.add((Serializable) users);
        dataToBeSerialized.add((Serializable) possibleTags);

        String fileLocation = "data/" + fileName + ".ser";

        try{
            fos = new FileOutputStream(fileLocation);
            out = new ObjectOutputStream(fos);
            out.writeObject(dataToBeSerialized);
            out.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        view.displaySuccess(
                "SaveAppStateCommand",
                LogStatus.SAVE_APP_STATE_SUCCESS,
                Map.of("dataToBeSerialized", dataToBeSerialized)
        );

        successResult = true;
    }

    @Override
    public Boolean getResult() {
        return successResult;
    }


    private enum LogStatus {
        SAVE_APP_STATE_NOT_STAFF,
        SAVE_APP_STATE_SUCCESS

    }



}

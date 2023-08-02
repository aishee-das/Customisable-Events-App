package command;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import controller.Context;
import external.MapSystem;
import model.*;
import view.IView;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class GetEventDirectionsCommand implements ICommand<String[]> {
    private long eventNumber;
    private String[] directionsResult;
    private TransportMode transportMode;

    /**
     * @param eventNumber       eventNumber used to identify what event directions are fetched for
     * @param transportMode     mode of transportation to create appropriate event directions
     * */

    public GetEventDirectionsCommand(long eventNumber, TransportMode transportMode) {
        this.eventNumber = eventNumber;
        this.transportMode = transportMode;
    }
    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current event exists
     * @verifies.that event address isn't null/blank
     * @verifies.that current user is a {@Link Consumer}
     * @verifies.that current user address isn't null/blank
     */
    @Override
    public void execute(Context context, IView view) {

        Event event = context.getEventState().findEventByNumber(eventNumber);

        if (event == null){
            view.displayFailure("GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
                    Map.of("eventNumber", eventNumber)
                    );
            directionsResult = null;
            return;
        }

        if (event.getVenueAddress() == null || event.getVenueAddress().isBlank()){
            view.displayFailure("GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS,
                    Map.of("event", event)
            );
            directionsResult = null;
            return;
        }

        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Consumer)){
            view.displayFailure("GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER,
                    Map.of("currentUser",String.valueOf(currentUser))
            );
            directionsResult = null;
            return;
        }

        Consumer consumer = (Consumer) currentUser;
        if (consumer.getAddress() == null || consumer.getAddress().isBlank()) {
            view.displayFailure("GetEventDirectionsCommand",
                    LogStatus.GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS,
                    Map.of("consumer", consumer)
            );
            directionsResult = null;
            return;
        }


        MapSystem map  = context.getMapSystem();
        GraphHopper hopper = context.getMapSystem().getHopper();

        String[] consumerAddress = consumer.getAddress().split(" ");
        Double consumerLat = Double.parseDouble(consumerAddress[0]);
        Double consumerLongi = Double.parseDouble(consumerAddress[1]);

        GHPoint consumerAddr = new GHPoint(consumerLat, consumerLongi);

        String[] venueAddress = event.getVenueAddress().split(" ");
        Double venueLat = Double.parseDouble(venueAddress[0]);
        Double venueLong = Double.parseDouble(venueAddress[1]);
        GHPoint venueAddr = new GHPoint(venueLat, venueLong);

        ResponsePath path = context.getMapSystem().routeBetweenPoints(transportMode, consumerAddr, venueAddr);


        InstructionList li = path.getInstructions();
        Translation tr = map.getTranslation();

        String[] directionsArray = new String[li.size()];

        for(int i = 0; i < li.size(); i++) {
            directionsArray[i] = li.get(i).getTurnDescription(tr);
        }

        view.displaySuccess("GetEventDirectionsCommand",
                LogStatus.GET_EVENT_DIRECTIONS_SUCCESS,
                Map.of("event", event, "consumer", consumer, "directions", Arrays.toString(directionsResult))
        );
//        directionsResult = directionsArray;
//        for(int i = 0; i < directionsResult.length; i++) {
//            System.out.println(directionsResult[i]);
//        }
    }
    @Override
    public String[] getResult() {
        return directionsResult;
    }

    private enum LogStatus {
        GET_EVENT_DIRECTIONS_NO_VENUE_ADDRESS,
        GET_EVENT_DIRECTIONS_NO_SUCH_EVENT,
        GET_EVENT_DIRECTIONS_USER_NOT_CONSUMER,
        GET_EVENT_DIRECTIONS_NO_CONSUMER_ADDRESS,
        GET_EVENT_DIRECTIONS_SUCCESS,
    }
}

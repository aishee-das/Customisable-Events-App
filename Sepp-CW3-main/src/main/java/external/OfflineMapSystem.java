package external;


import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.BBox;
import com.graphhopper.util.shapes.GHPoint;
import model.TransportMode;

import java.util.Locale;

public class OfflineMapSystem implements MapSystem {

    private GraphHopper hopper;

    public OfflineMapSystem() {
        //Scotland OSM file last downloaded 28/03/23 at 21:34
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile("data/scotland-latest.osm.pbf");
        hopper.setGraphHopperLocation("data/GraphHopperCache");
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
        hopper.importOrLoad();
        this.hopper = hopper;

    };


    public void close() throws Exception {

    }

    public GHPoint convertToCoordinates(String string) {
        String latAndLong = string;
        String[] coords = latAndLong.split(" ");

        double lat = Double.parseDouble(coords[0]);
        double longi = Double.parseDouble(coords[1]);

        GHPoint point = new GHPoint(lat, longi);

        return point;
    }

    public boolean isPointWithingMapBounds(GHPoint point) {
        BBox bounds = hopper.getBaseGraph().getBounds();
        return bounds.contains(point.lat, point.lon);
    }

    public ResponsePath routeBetweenPoints(TransportMode transport, GHPoint point, GHPoint point2) {

        GHRequest req = new GHRequest(point.lat, point.lon, point2.lat, point2.lon).setProfile(transport.toString()).setLocale(Locale.ENGLISH);
        GHResponse response = hopper.route(req);

        if (response.hasErrors()) {
            return null;
        } else {
            // Get the best response path
            ResponsePath responsePath = response.getBest();
            return responsePath;
        }
    }

    public Translation getTranslation() {
        Translation translation = hopper.getTranslationMap().get("en");
        return translation;
    }

    public GraphHopper getHopper() {
        return hopper;
    }

}
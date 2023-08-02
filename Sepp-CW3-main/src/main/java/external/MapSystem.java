package external;

import com.graphhopper.*;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import model.TransportMode;

public interface MapSystem extends AutoCloseable {

    GHPoint convertToCoordinates(String string);

    boolean isPointWithingMapBounds(GHPoint point);

    ResponsePath routeBetweenPoints(TransportMode transport, GHPoint point, GHPoint point2);

    Translation getTranslation();


    GraphHopper getHopper();
}

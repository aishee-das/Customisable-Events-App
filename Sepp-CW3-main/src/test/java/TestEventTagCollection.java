import command.AddEventTagCommand;
import command.CreateEventCommand;
import command.LogoutCommand;
import model.EventTagCollection;
import model.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.Controller;
import org.junit.jupiter.api.TestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEventTagCollection extends ConsoleTest {

    EventTagCollection eventTagCollection;

    @BeforeEach
    void setup(){
        eventTagCollection = new EventTagCollection("hasSocialDistancing=false");

    }

    @AfterEach
    void tearDown() {eventTagCollection = null;}

    @Test
    void event_tag_get_tags() throws IllegalAccessException, NoSuchFieldException {
        final Field tags = eventTagCollection.getClass().getDeclaredField("tags");
        tags.setAccessible(true);
        Map<String, String> result = eventTagCollection.getTags();
        assertEquals(result, tags.get(eventTagCollection));
    }

    @Test
    void event_tag_set_tags() throws IllegalAccessException, NoSuchFieldException {
        Map<String, String> test = new HashMap<>();
        test.put("hasAirFiltration", "false");
        eventTagCollection.setTags(test);

        final Field tags = eventTagCollection.getClass().getDeclaredField("tags");
        tags.setAccessible(true);
        assertEquals(tags.get(eventTagCollection), test);
    }



}


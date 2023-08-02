import command.RegisterConsumerCommand;
import command.UpdateConsumerProfileCommand;
import controller.Controller;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.List;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


//public class TestConsumer extends ConsoleTest{

public class TestConsumer {
    Consumer consumer;


    @BeforeEach
    void setup() {
        Event event = new Event(1,
                "A Christmas Carol",
                EventType.Theatre,
                25,
                20,
                "55.94373085057988, -3.1887541458174073",
                "Pay",
                LocalDateTime.now().plusHours(23),
                LocalDateTime.now().plusHours(24),
                new EventTagCollection()
        );

        consumer = new Consumer("Ronald McDonald",
                "always@lovin.it",
                "000",
                "55.947084 -3.208483",
                "McMuffin"

        );


        consumer.setPreferences(new EventTagCollection("hasSocialDistancing=true"));
        consumer.addBooking((new Booking(1, this.consumer, event, 1, LocalDateTime.now())));
    }

    @AfterEach
    void tearDown() {consumer = null;}

    @Test
    void consumer_get_name_test() throws NoSuchFieldException, IllegalAccessException {
        final Field name = consumer.getClass().getDeclaredField("name");
        name.setAccessible(true);
        String result = consumer.getName();
        assertEquals(result, name.get(consumer));
    }

    @Test
    void consumer_get_PhoneNumber_test() throws NoSuchFieldException, IllegalAccessException {
        final Field phoneNumber = consumer.getClass().getDeclaredField("phoneNumber");
        phoneNumber.setAccessible(true);
        String result = consumer.getPhoneNumber();
        assertEquals(result, phoneNumber.get(consumer));
    }

    @Test
    void consumer_get_address_test() throws NoSuchFieldException, IllegalAccessException {
        final Field address = consumer.getClass().getDeclaredField("address");
        address.setAccessible(true);
        String result = consumer.getAddress();
        assertEquals(result, address.get(consumer));
    }

    @Test
    void consumer_get_preferences_test() throws NoSuchFieldException, IllegalAccessException {
        final Field preferences = consumer.getClass().getDeclaredField("preferences");
        preferences.setAccessible(true);
        EventTagCollection result = consumer.getPreferences();
        assertEquals(result, preferences.get(consumer));
    }

    @Test
    void consumer_get_bookings_test() throws NoSuchFieldException, IllegalAccessException {
        final Field bookings = consumer.getClass().getDeclaredField("bookings");
        bookings.setAccessible(true);
        List<Booking> result = consumer.getBookings();
        assertEquals(result, bookings.get(consumer));
    }


    @Test
    void consumer_to_string_test() {
        String expected = "Consumer{" +
                "bookings= 1, this.consumer, event, 1, LocalDateTime.now(), " +
                "name='Ronald McDonald'," +
                "phoneNumber='000' ." +
                "address='55.947084, -3.208483', " +
                "preferences= hasSocialDistancing=true" ;

    }

    
}

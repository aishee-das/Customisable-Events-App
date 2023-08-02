import command.*;
import controller.Context;
import controller.Controller;
import model.*;
import view.ConsoleView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static int getInteger() {
        int input = scanner.nextInt();
        scanner.nextLine(); // clear remaining input until the end of the line
        return input;
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseObjectFromString(String str, Class<T> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, DateTimeException, IllegalArgumentException {
        if (type.isPrimitive()) {
            if (int.class.equals(type)) {
                return (T) Integer.valueOf(str);
            } else if (long.class.equals(type)) {
                return (T) Long.valueOf(str);
            } else if (short.class.equals(type)) {
                return (T) Short.valueOf(str);
            } else if (char.class.equals(type)) {
                return (T) Character.valueOf(str.charAt(0));
            } else if (boolean.class.equals(type)) {
                return (T) Boolean.valueOf(str);
            } else if (byte.class.equals(type)) {
                return (T) Byte.valueOf(str);
            }
        } else if (type.isEnum()) {
            return (T) Enum.valueOf((Class) type, str);
        }

        str = str.strip();
        if (str.isBlank()) {
            return null;
        } else if (Set.class.equals(type)) {
            String[] values = str.split(",");
            return (T) Arrays.stream(values).collect(Collectors.toSet());
        } else if (LocalDateTime.class.equals(type)) {
            return (T) LocalDateTime.parse(str);
        } else if (LocalDate.class.equals(type)) {
            return (T) LocalDate.parse(str);
        }
        return type.getConstructor(String.class).newInstance(str);
    }

    private static <T> T commandFromUserInput(Class<T> commandType) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Below cast warning is safe to ignore because the constructor is guaranteed to be type T
        // Its return value is generic because the constructors array could, in theory, be modified after getting them
        @SuppressWarnings("unchecked")
        Constructor<T> constructor = (Constructor<T>) commandType.getDeclaredConstructors()[0];
        Parameter[] params = constructor.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; ++i) {
            boolean parsed = false;
            while (!parsed) {
                try {
                    System.out.print("Please enter " + params[i].getName() + " (" + params[i].getType().getSimpleName() + "): ");
                    String line = scanner.nextLine();
                    args[i] = parseObjectFromString(line, params[i].getType());
                    parsed = true;
                } catch (InvocationTargetException e) {
                    Throwable target = e.getTargetException();
                    System.out.println(target.getClass().getSimpleName() + ": " + target.getLocalizedMessage());
                } catch (ReflectiveOperationException | RuntimeException e) {
                    System.out.println(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
                }
            }
        }

        return constructor.newInstance(args);
    }

    private static <T> void runCommandFromUserInput(Controller controller, Class<? extends ICommand<T>> commandType) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        controller.runCommand(commandFromUserInput(commandType));
    }

    private static void goToMainMenu(Controller controller, ConsoleView view) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        view.displayMainMenu();
        int input;
        while ((input = getInteger()) != ConsoleView.MAIN_MENU_EXIT) {
            switch (input) {
                case ConsoleView.MAIN_MENU_LOGIN -> runCommandFromUserInput(controller, LoginCommand.class);
                case ConsoleView.MAIN_MENU_LOGOUT -> runCommandFromUserInput(controller, LogoutCommand.class);
                case ConsoleView.MAIN_MENU_LIST_EVENTS -> runCommandFromUserInput(controller, ListEventsCommand.class);
                case ConsoleView.MAIN_MENU_LIST_EVENT_REVIEWS -> runCommandFromUserInput(controller, ListEventReviewsCommand.class);

                case ConsoleView.MAIN_MENU_CONSUMER -> goToConsumerMenu(controller, view);
                case ConsoleView.MAIN_MENU_STAFF -> goToStaffMenu(controller, view);
            }
            view.displayMainMenu();
        }
    }

    private static void goToConsumerMenu(Controller controller, ConsoleView view) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        view.displayConsumerMenu();
        int input;
        while ((input = getInteger()) != ConsoleView.CONSUMER_MENU_RETURN) {
            switch (input) {
                case ConsoleView.CONSUMER_MENU_REGISTER -> runCommandFromUserInput(controller, RegisterConsumerCommand.class);
                case ConsoleView.CONSUMER_MENU_UPDATE_PROFILE -> runCommandFromUserInput(controller, UpdateConsumerProfileCommand.class);
                case ConsoleView.CONSUMER_MENU_BOOK_EVENT -> runCommandFromUserInput(controller, BookEventCommand.class);
                case ConsoleView.CONSUMER_MENU_CANCEL_BOOKING -> runCommandFromUserInput(controller, CancelBookingCommand.class);
                case ConsoleView.CONSUMER_MENU_LIST_BOOKINGS -> runCommandFromUserInput(controller, ListConsumerBookingsCommand.class);
                case ConsoleView.CONSUMER_MENU_DIRECTIONS -> runCommandFromUserInput(controller, GetEventDirectionsCommand.class);
                case ConsoleView.CONSUMER_MENU_REVIEW_EVENT -> runCommandFromUserInput(controller, ReviewEventCommand.class);
                case ConsoleView.CONSUMER_MENU_LIST_EVENTS_MAX_DISTANCE -> runCommandFromUserInput(controller, ListEventsMaxDistanceCommand.class);
            }
            view.displayConsumerMenu();
        }
    }
    private static void goToStaffMenu(Controller controller, ConsoleView view) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        view.displayStaffMenu();
        int input;
        while ((input = getInteger()) != ConsoleView.STAFF_MENU_RETURN) {
            switch (input) {
                case ConsoleView.STAFF_MENU_REGISTER -> runCommandFromUserInput(controller, RegisterStaffCommand.class);
                case ConsoleView.STAFF_MENU_UPDATE_PROFILE -> runCommandFromUserInput(controller, UpdateStaffProfileCommand.class);
                case ConsoleView.STAFF_MENU_CREATE_EVENT -> runCommandFromUserInput(controller, CreateEventCommand.class);
                case ConsoleView.STAFF_MENU_CANCEL_EVENT -> runCommandFromUserInput(controller, CancelEventCommand.class);
                case ConsoleView.STAFF_MENU_LIST_EVENT_BOOKINGS -> runCommandFromUserInput(controller, ListEventBookingsCommand.class);
                case ConsoleView.STAFF_MENU_ADD_EVENT_TAG -> runCommandFromUserInput(controller, AddEventTagCommand.class);
                case ConsoleView.STAFF_MENU_SAVE_APP_STATE -> runCommandFromUserInput(controller, SaveAppStateCommand.class);
                case ConsoleView.STAFF_MENU_LOAD_APP_STATE -> runCommandFromUserInput(controller, LoadAppStateCommand.class);

            }
            view.displayStaffMenu();
        }
    }

    private static Timer scheduleAutoSaves(Context context) {
        Timer autoSaveTimer = new Timer();
        TimerTask autoSaveTask = new TimerTask() {
            @Override
            public void run() {
//                List<Event> events = context.getEventState().getAllEvents();
//                List<Booking> bookings = context.getBookingState().getAllBookings();
//                Map<String, User> users = context.getUserState().getAllUsers();
//                Map<String, EventTag> possibleTags = context.getEventState().getPossibleTags();
//
//                //Current implementation of Serializing objects adheres to the following documentation:
//                // https://www.oracle.com/technical-resources/articles/java/serializationapi.html
//
//                FileOutputStream fos;
//                ObjectOutputStream out;
//
//                //Serializing a List of Serializable objects, otherwise must serialize multiple objects
//                //at the same time. Easier to serialize one collection.
//                List<Serializable> dataToBeSerialized = new ArrayList<>();
//                dataToBeSerialized.add((Serializable) events);
//                dataToBeSerialized.add((Serializable) bookings);
//                dataToBeSerialized.add((Serializable) users);
//                dataToBeSerialized.add((Serializable) possibleTags);
//
//                String time = LocalDateTime.now().toString();
//                String fileLocation = "/data/autoBackup" + time + ".ser";
//
//                try{
//                    fos = new FileOutputStream(fileLocation);
//                    out = new ObjectOutputStream(fos);
//                    out.writeObject(dataToBeSerialized);
//                    out.close();
//                } catch(IOException ex) {
//                    ex.printStackTrace();
//                }
            }
        };
        long fiveMinutes = 5 * 60 * 1000;
        autoSaveTimer.scheduleAtFixedRate(autoSaveTask, 0L, fiveMinutes);
        return autoSaveTimer;
    }

    /**
     * Important note: DO NOT DO STORE SECRETS LIKE THIS IN A REAL APPLICATION.
     * Why? Because anyone with the right tools can reverse engineer the code and see the secret in plain text.
     * What to do instead? Load the appropriate details from environment variables or get them from an external server.
     * Why did we not do this? It makes testing more difficult for you and security is not our main goal here - this
     * will be the topic of next year's Computer Security course.
     */
    public static void main(String[] args) {
        try (Context context = new Context(
                "The University of Edinburgh, School of Informatics",
                "10 Crichton Street, Edinburgh EH8 9AB, United Kingdom",
                "epay@ed.ac.uk",
                "Nec temere nec timide"
        )) {
            ConsoleView view = new ConsoleView();
            Controller controller = new Controller(context, view);
            Timer autoSaveTimer = scheduleAutoSaves(context);

            Map<String, EventTag> allTags = context.getEventState().getPossibleTags();

            System.out.println("Default Tags");
            for(Map.Entry<String, EventTag> entry : allTags.entrySet()) {
                System.out.println(entry.getKey());
            }

            goToMainMenu(controller, view);
            autoSaveTimer.cancel();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

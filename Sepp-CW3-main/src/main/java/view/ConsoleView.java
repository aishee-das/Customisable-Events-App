package view;

import java.util.Iterator;
import java.util.Map;

public class ConsoleView implements IView {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    //INTS FOR MAIN MENU
    public static final int MAIN_MENU_LOGIN = 1;
    public static final int MAIN_MENU_LOGOUT = 2;
    public static final int MAIN_MENU_LIST_EVENTS = 3;
    public static final int MAIN_MENU_LIST_EVENT_REVIEWS = 4;
    public static final int MAIN_MENU_CONSUMER = 5;
    public static final int MAIN_MENU_STAFF = 6;
    public static final int MAIN_MENU_EXIT = -1;
    //INTS FOR CONSUMER MENU
    public static final int CONSUMER_MENU_REGISTER = 1;
    public static final int CONSUMER_MENU_UPDATE_PROFILE = 2;
    public static final int CONSUMER_MENU_BOOK_EVENT = 3;
    public static final int CONSUMER_MENU_CANCEL_BOOKING = 4;
    public static final int CONSUMER_MENU_LIST_BOOKINGS = 5;
    public static final int CONSUMER_MENU_DIRECTIONS = 6;
    public static final int CONSUMER_MENU_LIST_EVENTS_MAX_DISTANCE = 7;
    public static final int CONSUMER_MENU_REVIEW_EVENT = 8;
    public static final int CONSUMER_MENU_RETURN = -1;
    //INTS FOR STAFF_MENU
    public static final int STAFF_MENU_REGISTER = 1;
    public static final int STAFF_MENU_UPDATE_PROFILE = 2;
    public static final int STAFF_MENU_CREATE_EVENT = 3;
    public static final int STAFF_MENU_CANCEL_EVENT = 4;
    public static final int STAFF_MENU_LIST_EVENT_BOOKINGS = 5;
    public static final int STAFF_MENU_ADD_EVENT_TAG = 6;
    public static final int STAFF_MENU_SAVE_APP_STATE = 7;
    public static final int STAFF_MENU_LOAD_APP_STATE = 8;
    public static final int STAFF_MENU_RETURN = -1;

    /**
     * Show menu of options that can lead to the other menus or quitting the application
     */
    @Override
    public void displayMainMenu() {
        System.out.println("Welcome to the event app!");
        System.out.println("Please select one of the following options: ");
        System.out.println("[" + MAIN_MENU_LOGIN + "] LoginCommand");
        System.out.println("[" + MAIN_MENU_LOGOUT + "] LogoutCommand");
        System.out.println("[" + MAIN_MENU_LIST_EVENTS + "] ListEventsCommand");
        System.out.println("[" + MAIN_MENU_LIST_EVENT_REVIEWS + "] ListEventReviewsCommand");
        System.out.println("[" + MAIN_MENU_CONSUMER + "] Go to consumer menu");
        System.out.println("[" + MAIN_MENU_STAFF + "] Go to staff menu");
        System.out.println("[" + MAIN_MENU_EXIT + "] Exit");
    }

    /**
     * Show menu of options relevant to {@link model.Consumer}s
     */
    @Override
    public void displayConsumerMenu() {
        System.out.println("Please select one of the following options: ");
        System.out.println("[" + CONSUMER_MENU_REGISTER + "] RegisterConsumerCommand");
        System.out.println("[" + CONSUMER_MENU_UPDATE_PROFILE + "] UpdateConsumerProfileCommand");
        System.out.println("[" + CONSUMER_MENU_BOOK_EVENT + "] BookEventCommand");
        System.out.println("[" + CONSUMER_MENU_CANCEL_BOOKING + "] CancelBookingCommand");
        System.out.println("[" + CONSUMER_MENU_LIST_BOOKINGS + "] ListConsumerBookingsCommand");
        System.out.println("[" + CONSUMER_MENU_DIRECTIONS + "] GetEventDirectionsCommand");
        System.out.println("[" + CONSUMER_MENU_LIST_EVENTS_MAX_DISTANCE + "] ListEventsMaxDistanceCommand");
        System.out.println("[" + CONSUMER_MENU_REVIEW_EVENT + "] ReviewEventCommand");
        System.out.println("[" + CONSUMER_MENU_RETURN + "] Return to main menu");
    }

    /**
     * Show menu of options relevant to {@link model.Staff} members
     */
    @Override
    public void displayStaffMenu() {
        System.out.println("[" + STAFF_MENU_REGISTER + "] RegisterStaffCommand");
        System.out.println("[" + STAFF_MENU_UPDATE_PROFILE + "] UpdateStaffProfileCommand");
        System.out.println("[" + STAFF_MENU_CREATE_EVENT + "] CreateEventCommand");
        System.out.println("[" + STAFF_MENU_CANCEL_EVENT + "] CancelEventCommand");
        System.out.println("[" + STAFF_MENU_LIST_EVENT_BOOKINGS + "] ListEventBookingsCommand");
        System.out.println("[" + STAFF_MENU_ADD_EVENT_TAG + "] AddEventTagCommand");
        System.out.println("[" + STAFF_MENU_SAVE_APP_STATE + "] SaveAppStateCommand");
        System.out.println("[" + STAFF_MENU_LOAD_APP_STATE + "] LoadAppStateCommand");
        System.out.println("[" + STAFF_MENU_RETURN + "] Return to main menu");
    }

    private static String formatMessage(String callerName, Object result, Map<String, Object> additionalInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(callerName);
        sb.append('(');
        Iterator<Map.Entry<String, Object>> iter = additionalInfo.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            sb.append("\n\t");
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append(iter.hasNext() ? ',' : '\n');
        }
        sb.append(") => ");
        sb.append(result);
        return sb.toString();
    }

    /**
     * Print a message to the console indicating a success
     *
     * @param callerName human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result     a single string representing the result of the operation that is being logged
     */
    @Override
    public void displaySuccess(String callerName, Object result) {
        System.out.println(ANSI_GREEN + String.format("%s => %s", callerName, result) + ANSI_RESET);
    }

    /**
     * Print a message to the console indicating a success
     *
     * @param callerName     human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result         a single string representing the result of the operation that is being logged
     * @param additionalInfo a map containing any additional information that may help to explain the result,
     *                       the keys should be variable names and the values should be their values.
     *                       For convenience, the values are allowed to be any {@link Object}s that get converted
     *                       to {@link String}s
     */
    @Override
    public void displaySuccess(String callerName, Object result, Map<String, Object> additionalInfo) {
        System.out.println(ANSI_GREEN + formatMessage(callerName, result, additionalInfo) + ANSI_RESET);
    }

    /**
     * Print a message to the console indicating a failure
     *
     * @param callerName human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result     a single string representing the result of the operation that is being logged
     */
    @Override
    public void displayFailure(String callerName, Object result) {
        System.out.println(ANSI_YELLOW + String.format("%s => %s", callerName, result) + ANSI_RESET);
    }

    /**
     * Print a message to the console indicating a failure
     *
     * @param callerName     human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result         a single string representing the result of the operation that is being logged
     * @param additionalInfo a map containing any additional information that may help to explain the result,
     *                       the keys should be variable names and the values should be their values.
     *                       For convenience, the values are allowed to be any {@link Object}s that get converted
     *                       to {@link String}s
     */
    @Override
    public void displayFailure(String callerName, Object result, Map<String, Object> additionalInfo) {
        System.out.println(ANSI_YELLOW + formatMessage(callerName, result, additionalInfo) + ANSI_RESET);
    }
}

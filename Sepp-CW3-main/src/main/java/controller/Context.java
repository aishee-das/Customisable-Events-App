package controller;

import external.MapSystem;
import external.MockPaymentSystem;
import external.OfflineMapSystem;
import external.PaymentSystem;
import state.*;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link Context} is a wrapper around the entire app state. It keeps references to the internal states:
 * {@link IUserState}, {@link IEventState}, and {@link IBookingState}.
 * The state classes are kept as interfaces, so that other classes using the context cannot depend on their
 * implementation details.
 */
public class Context implements AutoCloseable {
    private final String orgName;
    private final String orgAddress;
    private final String orgEmail;
    private final String orgSecret;
    private final PaymentSystem paymentSystem;
    private final IUserState userState;
    private final IEventState eventState;
    private final IBookingState bookingState;
    private final MapSystem mapSystem;

    /**
     * Initialises all the state members with default constructors of the concrete implementations:
     * {@link UserState}, {@link EventState}, and {@link BookingState}.
     *
     * @param orgName Name of the organisation that owns this application instance
     * @param orgAddress Address of the organisation that owns this application instance
     * @param orgEmail Email address of the organisation, used for payment transactions
     * @param orgSecret Secret passcode only known to organisation staff, required for registering new Staff accounts
     */
    public Context(String orgName, String orgAddress, String orgEmail, String orgSecret) {
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.orgEmail = orgEmail;
        this.orgSecret = orgSecret;
        this.paymentSystem = new MockPaymentSystem();
        this.userState = new UserState();
        this.eventState = new EventState();
        this.bookingState = new BookingState();
        this.mapSystem = new OfflineMapSystem();

        Set<String> values = new HashSet<>();
        values.add("true");
        values.add("false");
        this.eventState.createEventTag("hasSocialDistancing", values, "false");
        this.eventState.createEventTag("hasAirFiltration", values, "false");
        Set<String> newValues = new HashSet<>();
        newValues.add("<20");
        newValues.add("20-100");
        newValues.add("100-200");
        newValues.add("200");
        this.eventState.createEventTag("venueCapacity", newValues, "<20");



    }

    /**
     * Copy constructor, makes a deep copy of another {@link Context}.
     *
     * @param other context to copy
     */
    /** gg */
    public Context(Context other) {
        // Strings are immutable in Java, hence no need to re-create a copy
        orgName = other.orgName;
        orgAddress = other.orgAddress;
        orgEmail = other.orgEmail;
        orgSecret = other.orgSecret;
        paymentSystem = new MockPaymentSystem((MockPaymentSystem) other.paymentSystem);
        userState = new UserState(other.userState);
        eventState = new EventState(other.eventState);
        bookingState = new BookingState(other.bookingState);
        mapSystem = new OfflineMapSystem();
    }

    public String getOrgName() { return orgName; }

    public String getOrgAddress() { return orgAddress; }

    public String getOrgEmail() {
        return orgEmail;
    }

    public String getOrgSecret() { return orgSecret; }

    public PaymentSystem getPaymentSystem() {
        return paymentSystem;
    }

    public IUserState getUserState() {
        return userState;
    }

    public IBookingState getBookingState() {
        return bookingState;
    }

    public MapSystem getMapSystem(){
        return mapSystem;
    }

    public IEventState getEventState() {
        return eventState;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     */
    @Override
    public void close() throws Exception {
        paymentSystem.close();
    }
}

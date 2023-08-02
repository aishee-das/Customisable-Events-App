package command;

import controller.Context;
import model.Staff;
import model.User;
import view.IView;

import java.util.Map;

/**
 * {@link RegisterStaffCommand} allows users to register a new {@link Staff} account
 * on the system. After registration, they are automatically logged in.
 */
public class RegisterStaffCommand implements ICommand<Staff> {
    private final String email;
    private final String password;
    private final String secret;
    private Staff newStaffResult;

    /**
     * @param email   main email address of the organisation. Must not be null
     * @param password   password to log in to the system in the future. Must not be null
     * @param secret    secret passcode only known by staff required to sign up for a staff account
     */
    public RegisterStaffCommand(String email,
                                String password,
                                String secret) {
        this.email = email;
        this.password = password;
        this.secret = secret;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that no user is currently logged in
     * @verifies.that email, password, and secret are all not null
     * @verifies.that the secret matches the one that is set up for this organisation
     * @verifies.that there is no user with the same email address already registered
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();
        if (currentUser != null) {
            view.displayFailure(
                    "RegisterConsumerCommand",
                    LogStatus.USER_REGISTER_LOGGED_IN,
                    Map.of("currentUser", currentUser)
            );
            newStaffResult = null;
            return;
        }

        Map<String, User> allUsers = context.getUserState().getAllUsers();

        if (email == null || password == null || secret == null) {
            view.displayFailure(
                    "RegisterStaffCommand",
                    LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL,
                    Map.of("email", String.valueOf(email),
                            "password", "***",
                            "secret", "***"
                    ));
            newStaffResult = null;
            return;
        }

        if (!context.getOrgSecret().equals(secret)) {
            view.displayFailure(
                    "RegisterStaffCommand",
                    LogStatus.USER_REGISTER_WRONG_STAFF_SECRET,
                    Map.of("secret", "***"));
            newStaffResult = null;
            return;
        }

        if (allUsers.containsKey(email)) {
            view.displayFailure(
                    "RegisterStaffCommand",
                    LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED,
                    Map.of("email", email)
            );
            newStaffResult = null;
            return;
        }

        Staff staff = new Staff(email, password);
        context.getUserState().addUser(staff);

        view.displaySuccess(
                "RegisterStaffCommand",
                LogStatus.REGISTER_STAFF_SUCCESS,
                Map.of("email", email,
                        "password", "***")
        );

        context.getUserState().setCurrentUser(staff);
        view.displaySuccess(
                "RegisterStaffCommand",
                LogStatus.USER_LOGIN_SUCCESS,
                Map.of("email", email,
                        "password", "***")
        );

        newStaffResult = staff;
    }

    /**
     * @return Instance of the newly registered {@link Staff} and null otherwise.
     */
    @Override
    public Staff getResult() {
        return newStaffResult;
    }

    private enum LogStatus {
        REGISTER_STAFF_SUCCESS,
        USER_REGISTER_LOGGED_IN,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_WRONG_STAFF_SECRET,
        USER_REGISTER_EMAIL_ALREADY_REGISTERED,
        USER_LOGIN_SUCCESS
    }
}

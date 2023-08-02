package command;

import controller.Context;
import model.User;
import view.IView;

import java.util.Map;

/**
 * {@link LoginCommand} allows a previously registered {@link User} to log in to the system by providing their login
 * details.
 */
public class LoginCommand implements ICommand<User> {
    private final String email;
    private final String password;
    private User userResult;

    /**
     * @param email    account email
     * @param password account password
     */
    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that the account email is registered on the system
     * @verifies.that the password matches the saved password for the corresponding account
     */
    @Override
    public void execute(Context context, IView view) {
        Map<String, User> allUsers = context.getUserState().getAllUsers();

        if (!allUsers.containsKey(email)) {
            view.displayFailure(
                    "LoginCommand",
                    LogStatus.USER_LOGIN_EMAIL_NOT_REGISTERED,
                    Map.of("email", email,
                            "password", "***")
            );
            userResult = null;
            return;
        }

        User user = allUsers.get(email);

        if (!user.checkPasswordMatch(password)) {
            view.displayFailure(
                    "LoginCommand",
                    LogStatus.USER_LOGIN_WRONG_PASSWORD,
                    Map.of("email", email,
                            "password", "***")
            );
            userResult = null;
            return;
        }

        context.getUserState().setCurrentUser(user);
        view.displaySuccess(
                "LoginCommand",
                LogStatus.USER_LOGIN_SUCCESS,
                Map.of("email", email,
                        "password", "***")
        );
        userResult = user;
    }

    /**
     * @return The logged in {@link User} instance if successful and null otherwise
     */
    @Override
    public User getResult() {
        return userResult;
    }

    private enum LogStatus {
        USER_LOGIN_SUCCESS,
        USER_LOGIN_EMAIL_NOT_REGISTERED,
        USER_LOGIN_WRONG_PASSWORD
    }
}

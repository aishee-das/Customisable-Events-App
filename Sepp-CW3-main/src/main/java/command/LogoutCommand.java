package command;

import controller.Context;
import view.IView;

/**
 * {@link LogoutCommand} allows the currently logged in {@link model.User} to log out
 */
public class LogoutCommand implements ICommand<Void> {
    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that the current user is logged in
     */
    @Override
    public void execute(Context context, IView view) {
        if (context.getUserState().getCurrentUser() == null) {
            view.displayFailure(
                    "LogoutCommand",
                    LogStatus.USER_LOGOUT_NOT_LOGGED_IN
            );
            return;
        }

        context.getUserState().setCurrentUser(null);
        view.displaySuccess(
                "LogoutCommand",
                LogStatus.USER_LOGOUT_SUCCESS
        );
    }

    /**
     * @return Always null
     */
    @Override
    public Void getResult() {
        return null;
    }

    private enum LogStatus {
        USER_LOGOUT_SUCCESS,
        USER_LOGOUT_NOT_LOGGED_IN
    }
}

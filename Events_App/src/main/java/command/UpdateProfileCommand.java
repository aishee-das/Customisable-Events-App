package command;

import controller.Context;
import model.User;
import view.IView;

import java.util.Map;

/**
 * {@link UpdateProfileCommand} contains common behaviour shared between profile update commands
 */
public abstract class UpdateProfileCommand implements ICommand<Boolean> {
    protected Boolean successResult;

    /**
     * Common error checking method for all profile updates.
     *
     * @param context     object that provides access to global application state
     * @param view        allows passing information to the user interface
     * @param oldPassword password before the change, which must match the account's password
     * @param newEmail    specified email address to use for the account after the change
     * @return True/false based on whether the profile update is valid
     */
    protected boolean isProfileUpdateInvalid(Context context, IView view, String oldPassword, String newEmail) {
        User currentUser = context.getUserState().getCurrentUser();
        //Verifies that user is logged in
        if (currentUser == null) {
            view.displayFailure(
                    "UpdateProfileCommand",
                    LogStatus.UPDATE_PROFILE_USER_NOT_LOGGED_IN);
            return true;
        }

        //Verifies that user is user by checking current password against password they've just inputted
        if (!currentUser.checkPasswordMatch(oldPassword)) {
            view.displayFailure(
                    "UpdateProfileCommand",
                    LogStatus.USER_UPDATE_PROFILE_WRONG_PASSWORD);
            return true;
        }

        //Verifies that newEmail isn't null
        Map<String, User> allUsers = context.getUserState().getAllUsers();

        //checks if another user has an account with the newEmail address
        //if there is no one, then returns false
        if(!(allUsers.containsKey(newEmail))) {
            return false;
        }
        User otherUser = allUsers.get(newEmail);

        //checks if the otherUser is the same as the current user
        //if they aren't the same User, then abort update since email is used for a different user
        if(!(otherUser.equals(currentUser))) {
                view.displayFailure(
                        "UpdateConsumerProfileCommand",
                        UpdateProfileCommand.LogStatus.USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE,
                        Map.of("newEmail", newEmail));
                return true;
        }


        return false;
    }

    /**
     * Common update method for profile changes that involve a change of email (used as unique user identifier).
     * The method assumes error checking has already been performed before invoking this.
     * @param context  object that provides access to global application state
     * @param newEmail new email address for the current user
     */
    protected void changeUserEmail(Context context, String newEmail) {
        User currentUser = context.getUserState().getCurrentUser();
        String oldEmail = currentUser.getEmail();
        if (oldEmail.equals(newEmail)) {
            // Email hasn't changed, no need to do anything
            return;
        }

        currentUser.setEmail(newEmail);
        context.getUserState().getAllUsers().remove(oldEmail);
        context.getUserState().addUser(currentUser);
    }

    /**
     * @return True if successful, false if not, and null if the command has not been executed yet
     */
    @Override
    public Boolean getResult() {
        return successResult;
    }

    private enum LogStatus {
        UPDATE_PROFILE_USER_NOT_LOGGED_IN,
        USER_UPDATE_PROFILE_WRONG_PASSWORD,
        USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE
    }
}

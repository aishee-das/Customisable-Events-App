package state;

import model.User;

import java.util.Map;

/**
 * {@link IUserState} is an interface representing the portion of the application that contains all the registered
 * {@link User} information.
 */
public interface IUserState {
    /**
     * Add a new {@link User} instance to the user state
     *
     * @param user user
     */
    void addUser(User user);

    /**
     * @return A collection of all registered users
     */
    Map<String, User> getAllUsers();

    /**
     * @return The currently logged-in user if there is one, or null otherwise
     */
    User getCurrentUser();

    /**
     * Set the currently logged-in user to the specified user
     *
     * @param user user
     */
    void setCurrentUser(User user);
}

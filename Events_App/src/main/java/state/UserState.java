package state;

import model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link UserState} is a concrete implementation of {@link IUserState}.
 */
public class UserState implements IUserState {

    //String is email, User is the User type
    private HashMap<String, User> users;
    private User currentUser;

    /**
     * Create a new UserState with an empty collection of users and the currently logged-in user set to null.
     */
    public UserState() {
        users = new HashMap<String, User>();
        currentUser = null;
    }

    /**
     * Copy constructor to create a deep copy of another UserState instance
     *
     * @param other instance to copy
     */
    public UserState(IUserState other) {
        UserState otherImpl = (UserState) other;
        users = new HashMap<>(otherImpl.users);
        currentUser = otherImpl.currentUser;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getEmail(), user);
    }

    @Override
    public Map<String, User> getAllUsers() {
        return users;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}

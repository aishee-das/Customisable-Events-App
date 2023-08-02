package controller;

import command.ICommand;
import view.IView;

/**
 * {@link Controller} is the main external interface of this application. It allows executing commands.
 */
public class Controller {
    private final Context context;
    private final IView view;

    /**
     * The {@link Controller} keeps a reference to a {@link Context} object providing access to application state
     * for commands and a reference to a {@link IView}, allowing commands to interact with the user interface.
     *
     * @param context Encapsulating class for application state and external systems
     * @param view View class that handles interactions with the user interface
     */
    public Controller(Context context, IView view) {
        this.context = context;
        this.view = view;
    }

    /**
     * This method runs a given command, by calling its {@link ICommand#execute(Context, IView)} method and passing in the
     * {@link Controller}'s {@link Context} and {@link IView} instances.
     *
     * @param command command to run
     */
    public void runCommand(ICommand<?> command) {
        command.execute(context, view);
    }
}

package command;

import controller.Context;
import view.IView;

/**
 * {@link ICommand} is the interface implemented by all commands. This common interface allows the
 * {@link controller.Controller} to run any command using a single common method
 * {@link controller.Controller#runCommand(ICommand)}.
 * Every command must specify a type parameter, which is the command result type.
 */
public interface ICommand<T> {
    /**
     * This method should not be called directly outside of testing.
     * Normal usage is to create a command object and execute it by passing to
     * {@link controller.Controller#runCommand(ICommand)} instead.
     *
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     */
    void execute(Context context, IView view);

    /**
     * Get the result from the latest run of the command.
     *
     * @return The command result (type varies by command) if successful and null or false otherwise.
     */
    T getResult();
}

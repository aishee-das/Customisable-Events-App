package view;

import java.util.Map;

public interface IView {
    /**
     * Show menu of options that can lead to the other menus or quitting the application
     */
    void displayMainMenu();

    /**
     * Show menu of options relevant to {@link model.Consumer}s
     */
    void displayConsumerMenu();

    /**
     * Show menu of options relevant to {@link model.Staff} members
     */
    void displayStaffMenu();

    /**
     * Show a success in the user interface (which can vary, depending on how the view is implemented)
     *
     * @param callerName human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result     a single string representing the result of the operation that is being logged
     */
    void displaySuccess(String callerName, Object result);

    /**
     * Show a success in the user interface (which can vary, depending on how the view is implemented)
     *
     * @param callerName     human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result         a single string representing the result of the operation that is being logged
     * @param additionalInfo a map containing any additional information that may help to explain the result,
     *                       the keys should be variable names and the values should be their values.
     */
    void displaySuccess(String callerName, Object result, Map<String, Object> additionalInfo);

    /**
     * Show a failure in the user interface (which can vary, depending on how the view is implemented)
     *
     * @param callerName human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result     a single string representing the result of the operation that is being logged
     */
    void displayFailure(String callerName, Object result);

    /**
     * Show a failure in the user interface (which can vary, depending on how the view is implemented)
     *
     * @param callerName     human-readable name (usually in the format ClassName.MethodName) where the message is logged from
     * @param result         a single string representing the result of the operation that is being logged
     * @param additionalInfo a map containing any additional information that may help to explain the result,
     *                       the keys should be variable names and the values should be their values.
     */
    void displayFailure(String callerName, Object result, Map<String, Object> additionalInfo);
}

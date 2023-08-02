import view.IView;

import java.util.Map;
import java.util.stream.Collectors;

public class TestView implements IView {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public void displayMainMenu() {
        // stub
    }

    @Override
    public void displayConsumerMenu() {
        // stub
    }

    @Override
    public void displayStaffMenu() {
        // stub
    }

    private static String formatMessage(String callerName, Object result, Map<String, Object> additionalInfo) {
        return String.format(
                "%s(%s) => %s",
                callerName,
                additionalInfo
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> String.valueOf(entry.getValue()))
                        ),
                result
        );
    }

    @Override
    public void displaySuccess(String callerName, Object result) {
        System.out.println(ANSI_GREEN + String.format("%s => %s", callerName, result) + ANSI_RESET);
    }

    @Override
    public void displaySuccess(String callerName, Object result, Map<String, Object> additionalInfo) {
        System.out.println(ANSI_GREEN + formatMessage(callerName, result, additionalInfo) + ANSI_RESET);
    }

    @Override
    public void displayFailure(String callerName, Object result) {
        System.out.println(ANSI_YELLOW + String.format("%s => %s", callerName, result) + ANSI_RESET);
    }

    @Override
    public void displayFailure(String callerName, Object result, Map<String, Object> additionalInfo) {
        System.out.println(ANSI_YELLOW + formatMessage(callerName, result, additionalInfo) + ANSI_RESET);
    }
}

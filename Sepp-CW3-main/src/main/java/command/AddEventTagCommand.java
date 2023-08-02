package command;

import controller.Context;
import model.*;
import view.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddEventTagCommand implements ICommand<EventTag> {
    private EventTag eventTagResult;
    private final String tagName;
    private Set<String> tagValues;
    private final String defaultValue;

    /**
     * @param defaultValue  the default value for the event tag
     * @param tagName       the name of the tag
     * @param tagValues     the possible values associated with the event tag
     * */


    public AddEventTagCommand(String tagName, String defaultValue, Set<String> tagValues) {
        this.tagName = tagName;
        this.defaultValue = defaultValue;
        this.tagValues = tagValues;
    }
    /**
     * @param context object that provides access to global application state
     * @param view    allows passing information to the user interface
     * @verifies.that current user is {@Link Staff}
     * @verifies.that tagValues has 2 or more values
     * @verifies.that defaultValue exists in tagValues
     * @verifies.that tagName doesn't clash with existing tag names
     */
    @Override
    public void execute(Context context, IView view) {
        User currentUser = context.getUserState().getCurrentUser();

        if (!(currentUser instanceof Staff)) {
            view.displayFailure(
                    "AddEventTagCommand",
                    LogStatus.CURRENT_USER_NOT_STAFF,
                    Map.of("currentUser", currentUser != null ? currentUser : "none")
            );
            eventTagResult = null;
            return;
        }

        if (tagValues.size() < 2) {
            view.displayFailure(
                    "AddEventTagCommand",
                    LogStatus.INVALID_NUM_TAG_VALUES,
                    Map.of("tagValues", tagValues)
            );
            eventTagResult = null;
            return;
        }

        if (!tagValues.contains(defaultValue)) {
            view.displayFailure(
                    "AddEventTagCommand",
                    LogStatus.DEFAULT_TAG_NOT_IN_LIST,
                    Map.of("tagValues", tagValues)
            );
            eventTagResult = null;
            return;
            }

        //Check for eventTag duplicates

        Map<String, EventTag> tags = context.getEventState().getPossibleTags();
        if(tags.containsKey(tagName)) {
            view.displayFailure(
                    "AddEventTagCommand",
                    LogStatus.ADD_EVENT_TAG_NAME_CLASH,
                    Map.of("tagName", tagName)
            );
            eventTagResult = null;
            return;
        }
        //Displays Success:
        view.displaySuccess(
                "AddEventTagCommand",
                AddEventTagCommand.LogStatus.ADD_EVENT_TAG_SUCCESS,
                Map.of("tagName", tagName,
                        "tagValues", tagValues.toString())
        );
        EventTag newTag = context.getEventState().createEventTag(tagName, tagValues, defaultValue);
        eventTagResult = newTag;
    }
    @Override
    public EventTag getResult() {
        return eventTagResult;
    }


    private enum LogStatus {
        CURRENT_USER_NOT_STAFF,
        INVALID_NUM_TAG_VALUES,
        DEFAULT_TAG_NOT_IN_LIST,
        ADD_EVENT_TAG_NAME_CLASH,
        ADD_EVENT_TAG_SUCCESS
    }


}
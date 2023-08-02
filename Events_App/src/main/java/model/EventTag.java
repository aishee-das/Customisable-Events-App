package model;

import state.EventState;

import java.io.Serializable;
import java.util.Set;

/**not sure whether it should EventState */
public class EventTag extends EventState implements Serializable {
    public Set<String> values;
    public String defaultValue;

    public EventTag(Set<String> values, String defaultValue) {
        this.values = values;
        this.defaultValue = defaultValue;
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values){
        this.values = values;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


}

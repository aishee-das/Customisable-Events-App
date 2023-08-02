package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventTagCollection implements Serializable {

    //First String: tag name
    //Second String: tag value

    private Map<String, String> tags;


    public EventTagCollection() {
        this.tags = new HashMap<String, String>();
    }

    public EventTagCollection(String string) {
        if(string.isEmpty()){
            return;
        }
        this.tags = new HashMap<String, String>();
        try {
            if (string.contains(",")) {
                String[] firstSplit = string.split(",");
                for (int i = 0; i < firstSplit.length; i++) {
                    String[] secondSplit = firstSplit[0].split("=");
                    tags.put(secondSplit[0], secondSplit[1]);
                }
            } else {
                String[] split = string.split("=");
                tags.put(split[0], split[1]);
            }
        } catch(Exception ex) {
            tags.put("incorrect","format");
        }
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}

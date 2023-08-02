package model;

// !!! THIS CLASS HAS BEEN DEPRECATED AND WILL BE DELETED. DON'T MAKE ANY ADJUSTMENTS TO THIS CLASS !!!


/**
 * A set of Covid-19 preferences that {@link Consumer}s can save in their profile and filter {@link Event}s based on
 * them.
 */
public class ConsumerPreferences {
    public boolean preferSocialDistancing;
    public boolean preferAirFiltration;
    public boolean preferOutdoorsOnly;
    public int preferredMaxCapacity;

    /**
     * Default values are:
     * <ul>
     *     <li>preferSocialDistancing = false</li>
     *     <li>preferAirFiltration = false</li>
     *     <li>preferOutdoorsOnly = false</li>
     *     <li>preferredMaxCapacity = Integer.MAX_VALUE</li>
     * </ul>
     */
    public ConsumerPreferences() {
        this.preferSocialDistancing = false;
        this.preferAirFiltration = false;
        this.preferOutdoorsOnly = false;
        this.preferredMaxCapacity = Integer.MAX_VALUE;
    }

    public ConsumerPreferences(String data) {
        this();
        String[] keysValues = data.split(",");
        for (String keyValue : keysValues) {
            String[] keyValuePair = keyValue.split("=");
            switch (keyValuePair[0]) {
                case "preferSocialDistancing" -> preferSocialDistancing = Boolean.parseBoolean(keyValuePair[1]);
                case "preferAirFiltration" -> preferAirFiltration = Boolean.parseBoolean(keyValuePair[1]);
                case "preferOutdoorsOnly" -> preferOutdoorsOnly = Boolean.parseBoolean(keyValuePair[1]);
                case "preferredMaxCapacity" -> preferredMaxCapacity = Integer.parseInt(keyValuePair[1]);
                default -> throw new IllegalArgumentException(
                        "Please enter comma-separated key-value pairs, with an = sign between each key and value"
                );
            }
        }
    }

    @Override
    public String toString() {
        return "ConsumerPreferences{" +
                "preferSocialDistancing=" + preferSocialDistancing +
                ", preferAirFiltration=" + preferAirFiltration +
                ", preferOutdoorsOnly=" + preferOutdoorsOnly +
                ", preferredMaxCapacity=" + preferredMaxCapacity +
                '}';
    }
}

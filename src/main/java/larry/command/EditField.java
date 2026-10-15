package larry.command;

import java.util.Arrays;
import java.util.Optional;

/**
 * Identifies one task field that can be changed by an edit command.
 */
public enum EditField {
    DESCRIPTION("/description"),
    DUE_DATE_TIME("/by"),
    START_DATE_TIME("/from"),
    END_DATE_TIME("/to");

    private final String marker;

    EditField(String marker) {
        this.marker = marker;
    }

    /**
     * Finds the edit field represented by a command marker.
     *
     * @param marker Case-sensitive field marker.
     * @return Matching edit field, or an empty value for an unsupported marker.
     */
    public static Optional<EditField> fromMarker(String marker) {
        return Arrays.stream(values())
                .filter(field -> field.marker.equals(marker))
                .findFirst();
    }

    /**
     * Returns the command marker for this field.
     *
     * @return Marker used in edit commands.
     */
    public String getMarker() {
        return marker;
    }
}

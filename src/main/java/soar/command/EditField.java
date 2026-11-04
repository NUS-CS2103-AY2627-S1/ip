package soar.command;

import java.util.Arrays;
import java.util.Optional;

/**
 * Identifies a task detail that can be changed by an edit command.
 */
public enum EditField {
    DESCRIPTION("/description"),
    BY("/by"),
    FROM("/from"),
    TO("/to");

    /** Marker entered before this field's value. */
    private final String marker;

    /** Creates an editable field represented by a command marker. */
    EditField(String marker) {
        this.marker = marker;
    }

    /**
     * Finds the field represented by a command marker.
     *
     * @param marker Marker entered by the user.
     * @return matching field, or an empty result for an unknown marker
     */
    public static Optional<EditField> fromMarker(String marker) {
        return Arrays.stream(values())
                .filter(field -> field.marker.equals(marker))
                .findFirst();
    }

    /**
     * Returns the command marker for this field.
     *
     * @return slash-prefixed field marker
     */
    public String getMarker() {
        return marker;
    }
}

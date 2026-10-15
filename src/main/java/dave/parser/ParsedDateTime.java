package dave.parser;

import java.time.LocalDateTime;

/**
 * Represents a parsed date and optional time.
 */
public class ParsedDateTime {

    /** Parsed date and time value. */
    private final LocalDateTime dateTime;
    /** Indicates whether time of day was explicitly specified. */
    private final boolean hasTime;

    /**
     * Constructs a ParsedDateTime with the specified date-time and time presence flag.
     *
     * @param dateTime Parsed LocalDateTime object.
     * @param hasTime True if time was specified, false if date only.
     */
    public ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Returns the parsed date and time value.
     *
     * @return Parsed LocalDateTime object.
     */
    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    /**
     * Returns whether time of day was explicitly specified.
     *
     * @return True if time was specified, false otherwise.
     */
    public boolean hasTime() {
        return this.hasTime;
    }
}

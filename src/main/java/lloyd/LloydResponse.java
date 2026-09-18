package lloyd;

import java.util.Objects;

/** Contains Lloyd's response text and the mood with which it should be presented. */
public record LloydResponse(String text, LloydMood mood) {

    /** Validates the response fields. */
    public LloydResponse {
        Objects.requireNonNull(text);
        Objects.requireNonNull(mood);
    }
}

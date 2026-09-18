package herta.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the shared task-description validation policy.
 */
class TaskDescriptionValidatorTest {

    @Test
    void validate_supportedUnicodeText_acceptsIt() {
        TaskDescriptionValidator.validate("review café ☕");
    }

    @Test
    void validate_delimiterControlsInvisibleAndMalformedSurrogates_rejectsThem() {
        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate("bad | task"));
        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate("bad\u0000task"));
        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate("bad\u202Etask"));
        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate("bad\uD800"));
    }

    @Test
    void validate_nullAndOrdinaryWhitespace_rejectsInput() {
        assertThrows(NullPointerException.class, () -> TaskDescriptionValidator.validate(null));
        assertThrows(IllegalArgumentException.class, () -> TaskDescriptionValidator.validate(" "));
        assertThrows(IllegalArgumentException.class, () -> TaskDescriptionValidator.validate(" \t "));
    }

    @Test
    void validate_descriptionLength_usesUtf16CodeUnitLimit() {
        String maximumDescription = "a".repeat(TaskDescriptionValidator.MAX_DESCRIPTION_LENGTH);
        String overlongDescription = maximumDescription + "a";

        TaskDescriptionValidator.validate(maximumDescription);
        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate(overlongDescription));
    }

    @Test
    void validate_unsupportedSpacesAndControls_rejectThem() {
        String[] invalidDescriptions = {
            "bad\rtext", "bad\ntext", "bad\u0009text", "bad\u0000text",
            "bad\u202Etext", "bad\u00A0text", "bad\u2003text", "bad\uDC00"
        };

        for (String description : invalidDescriptions) {
            assertThrows(IllegalArgumentException.class, () ->
                    TaskDescriptionValidator.validate(description));
        }
    }

    @Test
    void validate_supplementaryUnicodeAndMatchedSurrogates_acceptThem() {
        TaskDescriptionValidator.validate("review ☕ and 🚀");
        TaskDescriptionValidator.validate("matched \uD83D\uDE80");
    }

    @Test
    void validate_emojiAtUtf16Limit_acceptsExactlyTheMaximumLength() {
        String maximumEmojiDescription = "🚀".repeat(500);

        TaskDescriptionValidator.validate(maximumEmojiDescription);
    }

    @Test
    void validate_emojiBeyondUtf16Limit_rejectsDescription() {
        String overlongEmojiDescription = "🚀".repeat(501);

        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate(overlongEmojiDescription));
    }

    @Test
    void validate_highSurrogateFollowedByOrdinaryCharacter_rejectsDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                TaskDescriptionValidator.validate("bad\uD800text"));
    }
}

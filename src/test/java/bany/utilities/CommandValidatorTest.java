package bany.utilities;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests validation of critical tags and tag warnings for task commands. */
class CommandValidatorTest {
    private final CommandValidator validator = new CommandValidator();

    @Test
    void findDuplicateCriticalTag_noDuplicateCriticalTags_returnsNull() {
        assertAll(() -> assertNull(validator.findDuplicateCriticalTag(
                        "DEADLINE", List.of("by"))), () -> assertNull(validator.findDuplicateCriticalTag(
                        "EVENT", List.of("from", "to"))), () -> assertNull(validator.findDuplicateCriticalTag(
                        "TODO", List.of("tag", "tag")))
        );
    }

    @Test
    void findDuplicateCriticalTag_duplicateCriticalTag_returnsDuplicateTag() {
        assertAll(() -> assertEquals("by", validator.findDuplicateCriticalTag(
                        "DEADLINE", List.of("by", "important", "by"))), () -> assertEquals(
                        "from", validator.findDuplicateCriticalTag(
                        "EVENT", List.of("from", "from", "to"))), () -> assertEquals("to",
                        validator.findDuplicateCriticalTag(
                        "EVENT", List.of("from", "to", "to")))
        );
    }

    @Test
    void findDuplicateCriticalTag_duplicateNonCriticalTag_ignoresDuplicate() {
        assertNull(validator.findDuplicateCriticalTag(
                "DEADLINE", List.of("note", "note", "by")));
    }

    @Test
    void hasTagWarning_expectedTagsInOrder_returnsFalse() {
        assertAll(() -> assertFalse(validator.hasTagWarning(
                        "DEADLINE", List.of("by"))), () -> assertFalse(validator.hasTagWarning(
                        "EVENT", List.of("from", "to"))), () -> assertFalse(validator.hasTagWarning(
                        "TODO", List.of()))
        );
    }

    @Test
    void hasTagWarning_extraOrMisorderedTags_returnsTrue() {
        assertAll(() -> assertTrue(validator.hasTagWarning(
                        "DEADLINE", List.of("by", "note"))), () -> assertTrue(validator.hasTagWarning(
                        "EVENT", List.of("to", "from"))), () -> assertTrue(validator.hasTagWarning(
                        "TODO", List.of("note")))
        );
    }

    @Test
    void hasTagWarning_duplicateRequiredTag_returnsTrue() {
        assertAll(() -> assertTrue(validator.hasTagWarning(
                        "DEADLINE", List.of("by", "by"))), () -> assertTrue(validator.hasTagWarning(
                        "EVENT", List.of("from", "to", "from")))
        );
    }

    @Test
    void hasRescheduleTagWarning_partialUpdateDoesNotWarnForMissingCriticalTags() {
        assertFalse(validator.hasRescheduleTagWarning(
                List.of("from", "to"), List.of("from")));
    }

    @Test
    void hasRescheduleTagWarning_misorderedOrExtraTagsReturnsTrue() {
        boolean hasMisorderedTags = validator.hasRescheduleTagWarning(
                List.of("from", "to"), List.of("to", "from"));
        boolean hasExtraTags = validator.hasRescheduleTagWarning(
                List.of("from", "to"), List.of("from", "note"));

        assertTrue(hasMisorderedTags);
        assertTrue(hasExtraTags);
    }
}

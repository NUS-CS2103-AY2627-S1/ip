package turtley.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;

/** Tests tag validation and task tag ordering. */
class TagValidatorTest {

    @Test
    void validateTag_validTags_areAccepted() {
        TagValidator.validateTags(List.of("#fun", "#work-2026", "#!!!"));
    }

    @Test
    void validateTag_missingPrefixEmptyOrTooLongTag_isRejected() {
        assertThrows(TurtleyException.class, () -> TagValidator.validateTag("fun"));
        assertThrows(TurtleyException.class, () -> TagValidator.validateTag("#"));
        assertThrows(TurtleyException.class, () -> TagValidator.validateTag("#12345678901"));
    }

    @Test
    void task_addTags_sortsAndIgnoresCaseSensitiveDuplicates() {
        Task task = new ToDo("task");

        task.addTags(List.of("#z", "#Fun", "#fun", "#a", "#z"));

        assertEquals(List.of("#a", "#Fun", "#fun", "#z"), task.getTags());
        assertEquals("[T][ ] task [#a] [#Fun] [#fun] [#z]", task.toString());
    }

    @Test
    void addTags_moreThanTenUniqueTags_isRejectedAtomically() {
        Task task = new ToDo("task", List.of("#one", "#two", "#three", "#four", "#five",
                "#six", "#seven", "#eight", "#nine"));

        assertThrows(TurtleyException.class, () -> task.addTags(List.of("#ten", "#eleven")));

        assertEquals(9, task.getTags().size());
    }
}

package kiwi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import kiwi.KiwiException;

/**
 * Unit tests for {@link TaskType#fromIcon(String)}.
 */
class TaskTypeTest {

    @ParameterizedTest
    @CsvSource({
        "T, TODO",
        "D, DEADLINE",
        "E, EVENT"
    })
    void fromIcon_knownIcon_returnsMatchingType(String icon, TaskType expected) throws KiwiException {
        assertEquals(expected, TaskType.fromIcon(icon));
    }

    @Test
    void fromIcon_unknownIcon_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> TaskType.fromIcon("X"));
        assertEquals("unknown task type \"X\"", exception.getMessage());
    }
}

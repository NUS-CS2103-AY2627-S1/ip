package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SortDirectionTest {
    @Test
    void fromArgument_recognizedWords_returnsMatchingDirection() {
        assertEquals(SortDirection.ASC, SortDirection.fromArgument("asc"));
        assertEquals(SortDirection.DESC, SortDirection.fromArgument("desc"));
    }

    @Test
    void fromArgument_unrecognizedWord_returnsNull() {
        assertNull(SortDirection.fromArgument("descending"));
    }

    @Test
    void fromArgument_wrongCase_returnsNull() {
        assertNull(SortDirection.fromArgument("ASC"));
    }
}

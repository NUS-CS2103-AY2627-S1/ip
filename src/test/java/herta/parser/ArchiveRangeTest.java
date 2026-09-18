package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests validation of one-based archive ranges.
 */
class ArchiveRangeTest {

    @Test
    void archiveRange_zeroOrNegativeStart_rejectsRange() {
        assertThrows(IllegalArgumentException.class, () -> new ArchiveRange(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new ArchiveRange(-1, 1));
    }

    @Test
    void archiveRange_descendingEnd_rejectsRange() {
        assertThrows(IllegalArgumentException.class, () -> new ArchiveRange(3, 2));
    }

    @Test
    void archiveRange_equalEndpoints_acceptsOneElementRange() {
        ArchiveRange range = new ArchiveRange(2, 2);

        assertEquals(2, range.start());
        assertEquals(2, range.end());
    }
}

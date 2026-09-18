package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests archive-selection copying and immutable access behavior.
 */
class ArchiveSelectionTest {

    @Test
    void archiveSelection_inputRangesAreCopied() {
        List<ArchiveRange> ranges = new ArrayList<>(List.of(new ArchiveRange(1, 2)));
        ArchiveSelection selection = new ArchiveSelection(ranges, false);

        ranges.add(new ArchiveRange(3, 3));

        assertEquals(List.of(new ArchiveRange(1, 2)), selection.getRanges());
    }

    @Test
    void archiveSelection_returnedRangesAreImmutable() {
        ArchiveSelection selection = new ArchiveSelection(List.of(new ArchiveRange(1, 1)), false);

        assertThrows(UnsupportedOperationException.class, () ->
                selection.getRanges().add(new ArchiveRange(2, 2)));
    }

    @Test
    void archiveSelection_nullRanges_rejectInput() {
        assertThrows(NullPointerException.class, () -> new ArchiveSelection(null, false));
    }

    @Test
    void archiveSelection_allFlag_isIndependentFromRanges() {
        ArchiveSelection allSelection = new ArchiveSelection(List.of(), true);
        ArchiveSelection explicitSelection = new ArchiveSelection(List.of(), false);

        assertTrue(allSelection.isAllSelected());
        assertFalse(explicitSelection.isAllSelected());
        assertEquals(List.of(), allSelection.getRanges());
        assertEquals(List.of(), explicitSelection.getRanges());
    }
}

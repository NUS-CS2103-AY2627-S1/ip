package remy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import remy.task.TaskList;

/**
 * Tests the immutable result returned after recovering saved tasks.
 */
class StorageLoadResultTest {

    @Test
    void constructor_nullTaskList_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new StorageLoadResult(null, List.of()));
    }

    @Test
    void constructor_nullInvalidLineNumbers_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new StorageLoadResult(new TaskList(), null));
    }

    @Test
    void getInvalidLineNumbers_sourceListChanges_unmodifiableSnapshotReturned() {
        List<Integer> invalidLineNumbers = new ArrayList<>(List.of(2));
        StorageLoadResult result = new StorageLoadResult(new TaskList(), invalidLineNumbers);

        invalidLineNumbers.add(4);

        assertEquals(List.of(2), result.getInvalidLineNumbers());
        assertThrows(UnsupportedOperationException.class, () -> result.getInvalidLineNumbers().add(5));
    }

    @Test
    void hasInvalidLines_emptyAndPopulatedLists_expectedResultsReturned() {
        StorageLoadResult validResult = new StorageLoadResult(new TaskList(), List.of());
        StorageLoadResult invalidResult = new StorageLoadResult(new TaskList(), List.of(3));

        assertFalse(validResult.hasInvalidLines());
        assertTrue(invalidResult.hasInvalidLines());
    }
}

package clover.tutoree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests list operations and name searches for learning companions. */
class TutoreeListTest {

    @Test
    void constructorAndAsList_sourceOrReturnedListChanged_listIsUnchanged() {
        List<Tutoree> source = new ArrayList<>(List.of(new Tutoree("Alice Tan", "Home", "50")));
        TutoreeList tutorees = new TutoreeList(source);
        source.add(new Tutoree("Bob Lim", "School", "60"));

        assertEquals(1, tutorees.size());
        assertThrows(UnsupportedOperationException.class, () ->
                tutorees.asList().add(new Tutoree("Cara", "Park", "70")));
    }

    @Test
    void findExactName_trimsAndIgnoresCase_returnsMatchingTutoree() {
        TutoreeList tutorees = populatedList();

        assertEquals("Alice Tan", tutorees.findExactName("  aLiCe tAn ").orElseThrow().getName());
        assertTrue(tutorees.findExactName("nobody").isEmpty());
    }

    @Test
    void findNameContaining_ignoresCaseAndPreservesInsertionOrder() {
        TutoreeList tutorees = populatedList();

        assertEquals(List.of("Alice Tan", "Alicia Goh"), tutorees.findNameContaining("ALI").stream()
                .map(Tutoree::getName).toList());
    }

    @Test
    void addAndConstructor_nullValue_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TutoreeList(null));
        assertThrows(AssertionError.class, () -> new TutoreeList().add(null));
    }

    @Test
    void removeLast_lastTutoree_returnsAndRemovesTutoree() {
        TutoreeList tutorees = populatedList();

        assertEquals("Alicia Goh", tutorees.removeLast().getName());
        assertEquals(2, tutorees.size());
        assertFalse(tutorees.findExactName("Alicia Goh").isPresent());
    }

    @Test
    void tutoree_constructorNullValuesAndGetters_behaveAsExpected() {
        assertThrows(AssertionError.class, () -> new Tutoree(null, "Home", "50"));
        assertThrows(AssertionError.class, () -> new Tutoree("Alice", null, "50"));
        assertThrows(AssertionError.class, () -> new Tutoree("Alice", "Home", null));

        Tutoree tutoree = new Tutoree("Alice", "Home", "50");
        assertEquals("Alice", tutoree.getName());
        assertEquals("Home", tutoree.getAddress());
        assertEquals("50", tutoree.getFee());
    }

    private TutoreeList populatedList() {
        return new TutoreeList(List.of(
                new Tutoree("Alice Tan", "Home", "50"),
                new Tutoree("Bob Lim", "School", "60"),
                new Tutoree("Alicia Goh", "Park", "70")));
    }
}

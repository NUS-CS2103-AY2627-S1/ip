package benbot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests ordered storage and display operations for non-task entities. */
class EntityListTest {
    @Test
    void emptyList_returnsEmptyViews() {
        EntityList<Note> entities = new EntityList<>();

        assertEquals(0, entities.size());
        assertArrayEquals(new String[0], entities.toNumberedDisplayLines());
        assertEquals(List.of(), entities.toStorageCommands());
        assertEquals(0, entities.stream().count());
    }

    @Test
    void add_multipleEntities_preservesInsertionOrder() {
        EntityList<Note> entities = new EntityList<>();
        Note firstNote = new Note("first");
        Note secondNote = new Note("second");

        entities.add(firstNote);
        entities.add(secondNote);

        assertEquals(2, entities.size());
        assertArrayEquals(new String[] {"1.[N] first", "2.[N] second"},
                entities.toNumberedDisplayLines());
        assertEquals(List.of("note first", "note second"), entities.toStorageCommands());
        assertEquals(List.of(firstNote, secondNote), entities.stream().toList());
    }

    @Test
    void remove_firstEntity_returnsItAndRenumbersRemainingEntity() {
        EntityList<Note> entities = new EntityList<>();
        Note firstNote = new Note("first");
        Note secondNote = new Note("second");
        entities.add(firstNote);
        entities.add(secondNote);

        Note removedNote = entities.remove(0);

        assertSame(firstNote, removedNote);
        assertEquals(1, entities.size());
        assertArrayEquals(new String[] {"1.[N] second"}, entities.toNumberedDisplayLines());
    }

    @Test
    void add_nullEntity_throwsAssertionError() {
        EntityList<Note> entities = new EntityList<>();

        assertThrows(AssertionError.class, () -> entities.add(null));
    }

    @Test
    void remove_missingEntity_throwsAssertionError() {
        EntityList<Note> entities = new EntityList<>();

        assertThrows(AssertionError.class, () -> entities.remove(0));
    }
}

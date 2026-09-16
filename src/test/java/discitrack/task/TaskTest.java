package discitrack.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Verifies tag identity, validation boundaries, and collection protection.
 */
public class TaskTest {
    @Test
    public void tags_caseAndOrder_preserveFirstSpellingPerTask() {
        Task task = new Todo("slides");
        Task other = new Todo("meeting");
        assertTrue(task.addTag("School"));
        assertFalse(task.addTag("school"));
        assertTrue(other.addTag("school"));
        task.addTag("CS2103T");
        assertEquals("School", task.removeTag("SCHOOL"));
        task.addTag("sChOoL");
        assertEquals(List.of("CS2103T", "sChOoL"), task.getTags());
        assertThrows(UnsupportedOperationException.class, () -> task.getTags().add("bypass"));
        assertThrows(IllegalArgumentException.class, () -> task.replaceTags(List.of("valid", "#invalid")));
        assertEquals(List.of("CS2103T", "sChOoL"), task.getTags());
    }

    @Test
    public void validateTag_boundariesAndCharacters_enforcesRules() {
        Task task = new Todo("test");
        for (String valid : List.of("a", "2026", "nus-teacher", "project_2", "a".repeat(30))) {
            assertTrue(task.addTag(valid));
        }
        for (String invalid : List.of("", "a".repeat(31), "_bad", "-bad", "two words", "#tag",
                "school,urgent", "a|b", "\"school\"", "école")) {
            assertEquals(Task.INVALID_TAG_MESSAGE,
                    assertThrows(IllegalArgumentException.class, () -> task.addTag(invalid)).getMessage());
        }
        assertThrows(IllegalArgumentException.class, () -> task.addTag(null));
        for (int i = 0; i < 50; i++) {
            assertTrue(task.addTag("tag" + i));
        }
    }

    @Test
    public void tagIdentity_turkishLocale_ignoresCaseConsistently() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Task task = new Todo("slides");
            task.addTag("IMPORTANT");
            assertFalse(task.addTag("important"));
            assertEquals("IMPORTANT", task.removeTag("important"));
        } finally {
            Locale.setDefault(original);
        }
    }
}

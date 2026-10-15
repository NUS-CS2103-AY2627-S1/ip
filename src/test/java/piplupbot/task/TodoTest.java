package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}, and with it the behavior every task inherits from
 * {@link Task}.
 *
 * <p>{@code Task} is abstract, so its methods can only be reached through one of
 * the three kinds of task. {@code Todo} is used for that here because it adds
 * nothing of its own beyond its letter: whatever these cases show is therefore
 * the shared behavior rather than anything peculiar to todos, and
 * {@link DeadlineTest} and {@link EventTest} are left to cover only the parts
 * that differ.</p>
 *
 * <p>Both of the renderings a task has are checked, because they are two
 * separate promises to two different readers: {@code toString} is what the user
 * sees, and {@code toFileFields} is what the save file must hold for the task to
 * survive being closed and reopened.</p>
 */
public class TodoTest {

    // ---------- What the user sees ----------

    @Test
    public void toString_newTodo_showsTypeLabelAndEmptyStatusBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_doneTodo_showsCrossInStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    /** The description is shown exactly as typed, punctuation and all. */
    @Test
    public void toString_descriptionWithPunctuation_showsItUnchanged() {
        assertEquals("[T][ ] buy milk | eggs", new Todo("buy milk | eggs").toString());
    }

    // ---------- The done status ----------

    @Test
    public void getStatusIcon_newTask_returnsSpace() {
        assertEquals(" ", new Todo("read book").getStatusIcon());
    }

    @Test
    public void getStatusIcon_afterMarkAsDone_returnsCross() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    /** {@code unmark} has to be able to undo {@code mark}, not just refuse it. */
    @Test
    public void getStatusIcon_afterMarkAsNotDone_returnsSpaceAgain() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }

    /** Marking a task that is already done leaves it done rather than toggling. */
    @Test
    public void getStatusIcon_markedDoneTwice_staysDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    // ---------- What a search matches ----------

    @Test
    public void descriptionContains_textInTheDescription_returnsTrue() {
        assertTrue(new Todo("read book").descriptionContains("book"));
    }

    @Test
    public void descriptionContains_textNotInTheDescription_returnsFalse() {
        assertFalse(new Todo("read book").descriptionContains("homework"));
    }

    /**
     * Part of a word matches, so a half-remembered keyword still finds the task.
     * A rule that matched whole words only would leave {@code find boo} finding
     * nothing, which reads as "you have no such task" rather than as "type
     * more".
     */
    @Test
    public void descriptionContains_partOfAWord_returnsTrue() {
        assertTrue(new Todo("read book").descriptionContains("boo"));
        assertTrue(new Todo("read book").descriptionContains("ead bo"));
    }

    /**
     * Capitals are ignored, in both directions. This is the one decision in the
     * matching rule that {@code String.contains} does not make by itself, so it
     * is the one a later "simplification" would quietly drop -- and dropping it
     * would show as a search that finds nothing rather than as a failure.
     */
    @Test
    public void descriptionContains_textInADifferentCase_returnsTrue() {
        assertTrue(new Todo("Read Book").descriptionContains("book"));
        assertTrue(new Todo("read book").descriptionContains("BOOK"));
        assertTrue(new Todo("Read Book").descriptionContains("d b"));
    }

    /**
     * Capitals are ignored the same way on every machine. The default language
     * is set to Turkish here, which lower-cases a capital I to a dotless letter,
     * so lower-casing in the machine's language rather than in
     * {@code Locale.ROOT} would stop "FILE TAXES" from containing "file".
     *
     * <p>The default language is shared by the whole program, so it is put back
     * in a {@code finally} block, as {@link DateTimesTest} does.</p>
     */
    @Test
    public void descriptionContains_turkishDefaultLocale_stillIgnoresCapitals() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(new Todo("FILE TAXES").descriptionContains("file"));
            assertTrue(new Todo("file taxes").descriptionContains("FILE"));
        } finally {
            Locale.setDefault(original);
        }
    }

    // ---------- What the save file holds ----------

    /**
     * The fields are handed over separately, in the order the file writes them,
     * and the done status as {@code 0} or {@code 1} rather than
     * {@code true}/{@code false}.
     */
    @Test
    public void toFileFields_newTodo_returnsTypeCodeDoneFlagAndDescription() {
        assertArrayEquals(new String[] {"T", "0", "read book"},
                new Todo("read book").toFileFields());
    }

    @Test
    public void toFileFields_doneTodo_returnsDoneFlagOfOne() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertArrayEquals(new String[] {"T", "1", "read book"}, todo.toFileFields());
    }

    /**
     * The description is handed over exactly as the user typed it, including the
     * character the file uses to separate fields. Protecting it is
     * {@link piplupbot.Storage}'s job, not the task's -- so a task that escaped
     * it here would have it escaped twice.
     */
    @Test
    public void toFileFields_descriptionContainingSeparator_returnsItUnescaped() {
        assertArrayEquals(new String[] {"T", "0", "buy milk | eggs"},
                new Todo("buy milk | eggs").toFileFields());
    }
}

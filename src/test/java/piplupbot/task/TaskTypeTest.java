package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests {@link TaskType}, which says how each kind of task appears in the save
 * file.
 *
 * <p>{@code fromCode} is on the path every saved line takes, and it is the one
 * place that decides a task's kind from data rather than letting the object
 * answer for itself, so getting it wrong shows up as a task quietly missing
 * after a restart rather than as an error anyone sees.</p>
 *
 * <p>The last case looks like it is testing a getter, which would normally not
 * be worth a test. It is not: it checks that the field count this enum
 * <em>declares</em> is the number the task classes actually <em>write</em>.
 * Nothing else would notice if those two disagreed, and a mismatch would let a
 * task be saved and then refused as damaged on the next run.</p>
 *
 * <p>There is deliberately no case asserting that each kind's letter is the one
 * its task writes. A task writes {@code getType().getCode()}, so such a case
 * could never fail -- both sides would move together. The letters are pinned
 * instead by {@code fromCode_codeOfEachKind_returnsThatKind} above, which names
 * them literally, because they are what every existing save file already
 * holds.</p>
 */
public class TaskTypeTest {

    // ---------- Reading a letter back ----------

    @Test
    public void fromCode_codeOfEachKind_returnsThatKind() throws PiplupBotException {
        assertEquals(TaskType.TODO, TaskType.fromCode("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromCode("D"));
        assertEquals(TaskType.EVENT, TaskType.fromCode("E"));
    }

    @Test
    public void fromCode_unknownCode_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> TaskType.fromCode("X"));
    }

    /**
     * The save file is ordinary text that anything can edit, so a hand-typed
     * {@code t} is refused rather than taken for a todo. Matching loosely would
     * be one {@code equalsIgnoreCase} away, and would mean rebuilding a task
     * from a line this program could never have written.
     */
    @Test
    public void fromCode_lowerCaseCode_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> TaskType.fromCode("t"));
    }

    /**
     * A line beginning with the separator splits into an empty first field.
     * Without this the empty string would have to match nothing by luck rather
     * than by rule.
     */
    @Test
    public void fromCode_emptyCode_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> TaskType.fromCode(""));
    }

    // ---------- Agreeing with what the tasks actually write ----------

    @Test
    public void getExtraFieldCount_eachKind_matchesHowManyFieldsTheTaskWrites()
            throws PiplupBotException {
        // A todo adds no fields of its own, so what it writes is the shared part
        // every task begins with. Taking the number from a todo rather than
        // writing 3 here keeps this case true if that shared part ever changes.
        int sharedFieldCount = new Todo("read book").toFileFields().length;

        assertEquals(sharedFieldCount + TaskType.TODO.getExtraFieldCount(), sharedFieldCount);
        assertEquals(sharedFieldCount + TaskType.DEADLINE.getExtraFieldCount(),
                new Deadline("return book", "2019-10-15 1800").toFileFields().length);
        assertEquals(sharedFieldCount + TaskType.EVENT.getExtraFieldCount(),
                new Event("meeting", "2019-10-02 1400", "2019-10-02 1600").toFileFields().length);
    }
}

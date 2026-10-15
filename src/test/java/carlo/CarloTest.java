package carlo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carlo.exception.CarloException;
import carlo.task.Task;
import carlo.task.Todo;

/**
 * Tests for {@link Carlo#getTaskIndex(String, String, int)},
 * {@link Carlo#findMatches(List, String)}, and the instance methods that
 * add, mark, unmark, delete, and search tasks.
 */
class CarloTest {
    // Carlo's constructor always reads/writes "./data/carlo.txt", so each
    // test backs up whatever is there and restores it afterwards to avoid
    // clobbering a real save file.
    private static final Path SAVE_FILE = Path.of("./data/carlo.txt");
    private byte[] backedUpBytes;
    private boolean fileExistedBefore;

    @BeforeEach
    void backUpExistingSaveFile() throws IOException {
        fileExistedBefore = Files.exists(SAVE_FILE);
        if (fileExistedBefore) {
            backedUpBytes = Files.readAllBytes(SAVE_FILE);
        }
    }

    @AfterEach
    void restoreSaveFile() throws IOException {
        if (fileExistedBefore) {
            Files.write(SAVE_FILE, backedUpBytes);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    void getTaskIndex_firstTask_returnsZero() throws CarloException {
        assertEquals(0, Carlo.getTaskIndex("mark 1", "mark", 3));
    }

    @Test
    void getTaskIndex_lastTask_returnsLastZeroBasedIndex() throws CarloException {
        assertEquals(2, Carlo.getTaskIndex("mark 3", "mark", 3));
    }

    @Test
    void getTaskIndex_numberBelowRange_throwsCarloException() {
        assertThrows(CarloException.class, () -> Carlo.getTaskIndex("mark 0", "mark", 3));
    }

    @Test
    void getTaskIndex_numberAboveRange_throwsCarloException() {
        assertThrows(CarloException.class, () -> Carlo.getTaskIndex("mark 4", "mark", 3));
    }

    @Test
    void getTaskIndex_negativeNumber_throwsCarloException() {
        assertThrows(CarloException.class, () -> Carlo.getTaskIndex("mark -1", "mark", 3));
    }

    @Test
    void getTaskIndex_missingNumber_throwsCarloException() {
        assertThrows(CarloException.class, () -> Carlo.getTaskIndex("mark", "mark", 3));
    }

    @Test
    void getTaskIndex_nonNumericInput_throwsCarloException() {
        assertThrows(CarloException.class, () -> Carlo.getTaskIndex("mark abc", "mark", 3));
    }

    @Test
    void getTaskIndex_emptyTaskList_anyNumberThrowsCarloException() {
        assertThrows(CarloException.class, () -> Carlo.getTaskIndex("mark 1", "mark", 0));
    }

    @Test
    void findMatches_singleMatch_returnsOnlyThatTaskTrue() {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("buy milk"));
        boolean[] expected = {true, false};
        assertArrayEquals(expected, Carlo.findMatches(tasks, "book"));
    }

    @Test
    void findMatches_multipleMatches_returnsAllMatchingTrue() {
        List<Task> tasks = List.of(
                new Todo("read book"),
                new Todo("return book"),
                new Todo("buy milk"));
        boolean[] expected = {true, true, false};
        assertArrayEquals(expected, Carlo.findMatches(tasks, "book"));
    }

    @Test
    void findMatches_caseInsensitive_matchesRegardlessOfCase() {
        List<Task> tasks = List.of(new Todo("Read Book"));
        boolean[] expected = {true};
        assertArrayEquals(expected, Carlo.findMatches(tasks, "BOOK"));
    }

    @Test
    void findMatches_noMatches_returnsAllFalse() {
        List<Task> tasks = List.of(new Todo("buy milk"), new Todo("walk dog"));
        boolean[] expected = {false, false};
        assertArrayEquals(expected, Carlo.findMatches(tasks, "book"));
    }

    @Test
    void findMatches_emptyTaskList_returnsEmptyArray() {
        assertArrayEquals(new boolean[0], Carlo.findMatches(List.of(), "book"));
    }

    @Test
    void findMatches_keywordMatchesPartialWord_returnsTrue() {
        List<Task> tasks = List.of(new Todo("submit textbook order"));
        boolean[] expected = {true};
        assertArrayEquals(expected, Carlo.findMatches(tasks, "book"));
    }

    // ---- addTodo(String) ----

    @Test
    void addTodo_validDescription_addsTaskToList() throws CarloException {
        Carlo carlo = new Carlo();
        int before = carlo.getTasks().size();

        carlo.addTodo("read book");

        assertEquals(before + 1, carlo.getTasks().size());
        assertEquals("read book", carlo.getTasks().getLast().getDescription());
    }

    @Test
    void addTodo_blankDescription_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.addTodo("   "));
    }

    @Test
    void addTodo_nullDescription_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.addTodo(null));
    }

    // ---- addDeadline(String, String) ----

    @Test
    void addDeadline_validInputs_addsTaskToList() throws CarloException {
        Carlo carlo = new Carlo();
        int before = carlo.getTasks().size();

        carlo.addDeadline("submit report", "2019-12-02");

        assertEquals(before + 1, carlo.getTasks().size());
        assertEquals("submit report", carlo.getTasks().getLast().getDescription());
    }

    @Test
    void addDeadline_blankDescription_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.addDeadline("  ", "2019-12-02"));
    }

    @Test
    void addDeadline_blankDueTime_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.addDeadline("submit report", "  "));
    }

    // ---- addEvent(String, String, String) ----

    @Test
    void addEvent_validInputs_addsTaskToList() throws CarloException {
        Carlo carlo = new Carlo();
        int before = carlo.getTasks().size();

        carlo.addEvent("trip", "2019-12-01", "2019-12-05");

        assertEquals(before + 1, carlo.getTasks().size());
        assertEquals("trip", carlo.getTasks().getLast().getDescription());
    }

    @Test
    void addEvent_blankFrom_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.addEvent("trip", " ", "2019-12-05"));
    }

    @Test
    void addEvent_blankTo_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.addEvent("trip", "2019-12-01", " "));
    }

    // ---- markTask(int) / unmarkTask(int) ----

    @Test
    void markTask_validIndex_marksTaskAsDone() throws CarloException {
        Carlo carlo = new Carlo();
        carlo.addTodo("read book");
        int lastIndex = carlo.getTasks().size() - 1;

        carlo.markTask(lastIndex);

        assertTrue(carlo.getTasks().get(lastIndex).isDone());
    }

    @Test
    void markTask_invalidIndex_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.markTask(-1));
        assertThrows(CarloException.class, () -> carlo.markTask(carlo.getTasks().size()));
    }

    @Test
    void unmarkTask_validIndex_marksTaskAsNotDone() throws CarloException {
        Carlo carlo = new Carlo();
        carlo.addTodo("read book");
        int lastIndex = carlo.getTasks().size() - 1;
        carlo.markTask(lastIndex);

        carlo.unmarkTask(lastIndex);

        assertFalse(carlo.getTasks().get(lastIndex).isDone());
    }

    @Test
    void unmarkTask_invalidIndex_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.unmarkTask(carlo.getTasks().size() + 1));
    }

    // ---- deleteTask(int) ----

    @Test
    void deleteTask_validIndex_removesTaskFromList() throws CarloException {
        Carlo carlo = new Carlo();
        carlo.addTodo("read book");
        int before = carlo.getTasks().size();
        int lastIndex = before - 1;

        carlo.deleteTask(lastIndex);

        assertEquals(before - 1, carlo.getTasks().size());
    }

    @Test
    void deleteTask_invalidIndex_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.deleteTask(carlo.getTasks().size()));
    }

    // ---- findTasks(String) ----

    @Test
    void findTasks_matchingKeyword_returnsMatchingTasks() throws CarloException {
        Carlo carlo = new Carlo();
        carlo.addTodo("read a very particular book");

        List<Task> found = carlo.findTasks("very particular book");

        assertTrue(found.stream().anyMatch(t -> t.getDescription().equals("read a very particular book")));
    }

    @Test
    void findTasks_blankKeyword_throwsCarloException() {
        Carlo carlo = new Carlo();
        assertThrows(CarloException.class, () -> carlo.findTasks(" "));
    }

    // ---- getTasksOnDate(LocalDate) ----

    @Test
    void getTasksOnDate_deadlineOnDate_isIncluded() throws CarloException {
        Carlo carlo = new Carlo();
        carlo.addDeadline("a very particular deadline task", "2019-12-02");

        List<Task> onDate = carlo.getTasksOnDate(LocalDate.of(2019, 12, 2));

        assertTrue(onDate.stream()
                .anyMatch(t -> t.getDescription().equals("a very particular deadline task")));
    }

    @Test
    void getTasksOnDate_deadlineOnDifferentDate_isExcluded() throws CarloException {
        Carlo carlo = new Carlo();
        carlo.addDeadline("a very particular deadline task", "2019-12-02");

        List<Task> onDate = carlo.getTasksOnDate(LocalDate.of(2019, 12, 3));

        assertFalse(onDate.stream()
                .anyMatch(t -> t.getDescription().equals("a very particular deadline task")));
    }
}

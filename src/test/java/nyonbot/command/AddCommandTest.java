package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nyonbot.Logic.Result;
import nyonbot.Parser;
import nyonbot.model.Deadline;
import nyonbot.model.Event;
import nyonbot.model.NyonException;
import nyonbot.model.TaskList;
import nyonbot.model.ToDo;

class AddCommandTest {
    private TaskList tasks;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
    }

    @Test
    void todoCommand_validDescription_addsTodo() throws NyonException {
        Result result = new TodoCommand(arguments("todo read a book"), tasks).execute();

        assertEquals(1, tasks.size());
        assertInstanceOf(ToDo.class, tasks.get(0));
        assertEquals("read a book", tasks.get(0).getName());
        assertTrue(result.out().contains("There are 1 tasks"));
    }

    @Test
    void todoCommand_missingDescription_throwsNyonException() {
        assertThrows(NyonException.class, () -> new TodoCommand(arguments("todo"), tasks).execute());
    }

    @Test
    void deadlineCommand_validInput_addsDeadline() throws NyonException {
        new DeadlineCommand(
                arguments("deadline submit report --by 31/12/2026 2359"),
                tasks).execute();

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("submit report", deadline.getName());
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59),
                deadline.getDeadline());
    }

    @Test
    void deadlineCommand_missingDescription_throwsNyonException() {
        assertThrows(NyonException.class, () -> new DeadlineCommand(
                arguments("deadline --by 31/12/2026 2359"), tasks).execute());
    }

    @Test
    void deadlineCommand_missingByMarker_throwsNyonException() {
        assertThrows(NyonException.class, () -> new DeadlineCommand(
                arguments("deadline submit report"), tasks).execute());
    }

    @Test
    void deadlineCommand_emptyByValue_throwsNyonException() {
        assertThrows(NyonException.class, () -> new DeadlineCommand(
                arguments("deadline submit report --by"), tasks).execute());
    }

    @Test
    void deadlineCommand_invalidDate_throwsNyonException() {
        assertThrows(NyonException.class, () -> new DeadlineCommand(
                arguments("deadline submit report --by tomorrow"), tasks).execute());
    }

    @Test
    void eventCommand_validInput_addsEventAndRequestsWrite() throws NyonException {
        Result result = new EventCommand(
                arguments("event lecture --from 05/09/2026 0900 "
                        + "--to 05/09/2026 1100"),
                tasks).execute();

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("lecture", event.getName());
        assertEquals(LocalDateTime.of(2026, 9, 5, 9, 0),
                event.getEventTimes()[0]);
        assertEquals(LocalDateTime.of(2026, 9, 5, 11, 0),
                event.getEventTimes()[1]);
        assertTrue(result.shouldWrite());
    }

    @Test
    void eventCommand_missingFromMarker_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event lecture --to 05/09/2026 1100"), tasks).execute());
    }

    @Test
    void eventCommand_missingToMarker_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event lecture --from 05/09/2026 0900"), tasks).execute());
    }

    @Test
    void eventCommand_missingDescription_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event --from 05/09/2026 0900 --to 05/09/2026 1100"),
                tasks).execute());
    }

    @Test
    void eventCommand_emptyStartTime_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event lecture --from --to 05/09/2026 1100"), tasks).execute());
    }

    @Test
    void eventCommand_invalidDate_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event lecture --from tomorrow --to 05/09/2026 1100"),
                tasks).execute());
    }

    @Test
    void eventCommand_endBeforeStart_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event lecture --from 05/09/2026 1100 "
                        + "--to 05/09/2026 0900"),
                tasks).execute());
    }

    @Test
    void eventCommand_sameStartAndEnd_throwsNyonException() {
        assertThrows(NyonException.class, () -> new EventCommand(
                arguments("event lecture --from 05/09/2026 0900 "
                        + "--to 05/09/2026 0900"),
                tasks).execute());
    }

    @Test
    void eventCommand_flagsInDifferentOrder_addsEvent() throws NyonException {
        new EventCommand(
                arguments("event lecture --to 05/09/2026 1100 "
                        + "--from 05/09/2026 0900"),
                tasks).execute();

        assertInstanceOf(Event.class, tasks.get(0));
    }

    private static HashMap<String, String> arguments(String input) {
        return Parser.getInstance().parseArguments(input);
    }
}

package gongrilla;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises anticipated input mistakes through the chatbot without losing existing tasks. */
class ErrorHandlingTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_extraWhitespace_acceptsTabsAndSpacedDateTimes() {
        Gongrilla bot = new Gongrilla(temporaryDirectory.resolve("tasks.txt"));
        bot.getResponse(" \tdeadline\treport  /by\t20/9/2026    1800  ");
        assertEquals("AddCommand", bot.getCommandType());
        assertTrue(bot.getResponse("list").contains("20 Sep 2026, 6:00PM"));
        bot.getResponse("mark\t 1");
        assertEquals("MarkCommand", bot.getCommandType());
        assertTrue(bot.getResponse("list").contains("[X] report"));
    }

    @Test
    void getResponse_repeatedUnknownOrMissingParameters_keepsDataUnchanged() throws Exception {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Gongrilla bot = new Gongrilla(path);
        bot.getResponse("todo original");
        String saved = Files.readString(path);
        List<String> commands = List.of(
                "deadline task /by 1/1/2026 /BY 2/1/2026",
                "event task /from 1/1/2026 /from 2/1/2026 /to 3/1/2026",
                "event task /from 1/1/2026 /to 2/1/2026 /to 3/1/2026",
                "deadline task /until 1/1/2026",
                "event task /to 2/1/2026 /from 1/1/2026",
                "deadline /by 1/1/2026", "deadline task /by",
                "event /from 1/1/2026 /to 2/1/2026", "event task /from /to",
                "todo first\ntodo second", "todo hidden\u0000text", "mark 1!", "mark 1 2");
        for (String command : commands) {
            assertTrue(bot.getResponse(command).contains("Type help"), command);
            assertEquals("Error", bot.getCommandType(), command);
            assertEquals(saved, Files.readString(path), command);
        }
    }

    @Test
    void getResponse_duplicateTasks_rejectsAcrossCaseSpacingAndCompletion() throws Exception {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Gongrilla bot = new Gongrilla(path);
        bot.getResponse("todo Buy bananas");
        bot.getResponse("mark 1");
        String saved = Files.readString(path);
        assertTrue(bot.getResponse("todo buy   BANANAS").contains("already have"));
        assertEquals("Error", bot.getCommandType());
        assertEquals(saved, Files.readString(path));
        assertTrue(new Gongrilla(path).getResponse("todo BUY bananas").contains("already have"));
        bot.getResponse("delete 1");
        bot.getResponse("todo Buy bananas");
        assertEquals("AddCommand", bot.getCommandType());
    }

    @Test
    void getResponse_sameNameWithDifferentTypeOrDates_acceptsDistinctTasks() {
        Gongrilla bot = new Gongrilla(temporaryDirectory.resolve("tasks.txt"));
        List<String> commands = List.of("todo lunch", "deadline lunch /by 1/1/2026",
                "deadline lunch /by 2/1/2026", "event lunch /from 1/1/2026 /to 2/1/2026",
                "event lunch /from 1/1/2026 /to 3/1/2026",
                "event lunch /from 2/1/2026 /to 3/1/2026");
        for (String command : commands) {
            bot.getResponse(command);
            assertEquals("AddCommand", bot.getCommandType(), command);
            assertTrue(bot.getResponse(command).contains("already have"), command);
        }
    }

    @Test
    void getResponse_equalOrReversedEventTimes_rejectsWithoutCreatingFile() {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Gongrilla bot = new Gongrilla(path);
        for (String end : List.of("1/1/2026", "31/12/2025")) {
            assertTrue(bot.getResponse("event lunch /from 1/1/2026 /to " + end).contains("after or equal"));
            assertEquals("Error", bot.getCommandType());
        }
        assertFalse(Files.exists(path));
    }
}

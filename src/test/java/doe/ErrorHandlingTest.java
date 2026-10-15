package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises invalid input and file recovery through domain and application boundaries. */
class ErrorHandlingTest {
    @TempDir
    Path directory;

    @Test
    void datedTasks_impossibleDatesAndTimes_rejected() {
        for (String input : List.of("30-02-2026 1800", "29-02-2025 1200", "31-04-2026 1200",
                "01-01-2026 2400", "01-01-2026 1260", "01-13-2026 1200", "")) {
            assertThrows(DateTimeParseException.class, () -> new Deadline("test", input));
            assertThrows(DateTimeParseException.class, () -> new Event("test", input));
        }
        assertEquals("2024-02-29T18:00", new Event("test", " 29-02-2024   1800 ").getDate().toString());
    }

    @Test
    void descriptions_blankOrStorageCharacters_rejected() {
        for (String input : List.of("", "   ", "bad|name", "bad\nname", "bad\rname")) {
            assertThrows(IllegalArgumentException.class, () -> new Todo(input));
        }
        assertEquals("read!", new Todo(" read! ").getDescription());
    }

    @Test
    void duplicates_sameDetailsAndDifferentDatesAllowed() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.addTask(new Todo("read"));
        tasks.addTask(new Todo(" READ "));
        tasks.addTask(new Deadline("read", "01-01-2027 0900"));
        tasks.addTask(new Deadline("read", "02-01-2027 0900"));
        assertEquals(4, tasks.size());
        assertEquals(tasks.getTasks(), tasks.findTasks("  "));
    }

    @Test
    void load_corruptedRecords_reportsLinesAndPreventsOverwrite() throws Exception {
        Path path = directory.resolve("tasks.txt");
        String original = String.join("\n", "TODO|false|valid", "DEADLINE|false|bad|30-02-2026 1800",
                "TODO|maybe|bad", "TODO|false|bad|extra", "TODO|false|", "TODO|false|valid",
                "EVENT|false|bad|nonsense", "UNKNOWN|false|bad");
        Files.writeString(path, original);
        Storage storage = new Storage(path.toString());
        assertEquals(2, storage.load().size());
        assertTrue(storage.getLoadWarning().contains("2, 3, 4, 5, 7, 8"));
        assertThrows(IllegalStateException.class, () -> storage.save(List.of(new Todo("replacement"))));
        assertEquals(original, Files.readString(path));
        Doe doe = new Doe(path.toString());
        assertFalse(doe.getWelcomeMessage().contains("could not be loaded"));
        assertTrue(doe.getStartupWarning().contains("saving is paused"));
    }

    @Test
    void save_missingParentCreated_replacementRoundTrips() {
        Storage storage = new Storage(directory.resolve("nested/tasks.txt").toString());
        assertTrue(storage.load().isEmpty());
        storage.save(List.of(new Todo("first")));
        storage.save(List.of(new Todo("second")));
        assertEquals("second", storage.load().getFirst().getDescription());
    }

    @Test
    void save_failureExplainedInResponse_memoryRetainedForRetry() throws Exception {
        Path parent = directory.resolve("parent");
        Doe doe = new Doe(parent.resolve("tasks.txt").toString());
        Files.writeString(parent, "blocks directory creation");
        String response = addTodo(doe, "read");
        assertTrue(response.contains("has not been saved"));
        assertTrue(response.contains("How to fix:"));
        assertTrue(doe.wasLastResponseWarning());
        assertFalse(response.contains("saved successfully"));
        Files.delete(parent);
        doe.getResponse("todo");
        doe.getResponse("sort");
        assertEquals("read", new Storage(parent.resolve("tasks.txt").toString()).load().getFirst().getDescription());
    }

    @Test
    void load_unreadablePath_blocksSave() {
        Storage storage = new Storage(directory.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarning().contains("could not read"));
        assertThrows(IllegalStateException.class, () -> storage.save(List.of()));
    }

    @Test
    void search_subsetKeepsOriginalNumbers_blankShowsEverything() {
        Doe doe = new Doe(directory.resolve("tasks.txt").toString());
        addTodo(doe, "first");
        addTodo(doe, "second");
        doe.getResponse("todo");
        doe.getResponse("find");
        assertTrue(doe.getResponse("second").contains("2. [t][ ] second"));
        doe.getResponse("todo");
        doe.getResponse("find");
        String response = doe.getResponse("   ");
        assertTrue(response.contains("1. [t][ ] first\n2. [t][ ] second"));
    }

    @Test
    void console_endOfInputAtNestedPrompts_exitsPolitely() {
        var originalInput = System.in;
        var originalOutput = System.out;
        try {
            for (String input : List.of("", "todo\n", "todo\nadd\ntodo\n", "todo\nmark\n")) {
                System.setIn(new ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                System.setOut(new PrintStream(output));
                new Doe(directory.resolve("tasks.txt").toString()).run();
                assertTrue(output.toString().contains("bye. hope to see you again soon!"));
            }
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    @Test
    void datedDescription_blankRejected_canCorrectAtSamePrompt() {
        Doe doe = new Doe(directory.resolve("tasks.txt").toString());
        doe.getResponse("todo");
        doe.getResponse("add");
        doe.getResponse("deadline");
        assertTrue(doe.getResponse("  ").contains("description cannot be blank"));
        assertTrue(doe.getResponse("report").contains("input deadline"));
        assertTrue(doe.getResponse("01-01-2027 1800").contains("saved successfully"));
    }

    private String addTodo(Doe doe, String description) {
        doe.getResponse("todo");
        doe.getResponse("add");
        doe.getResponse("todo");
        return doe.getResponse(description);
    }
}

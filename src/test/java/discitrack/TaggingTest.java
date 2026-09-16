package discitrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import discitrack.task.Task;

/**
 * Tests tagging through the same command-response interface used by the GUI.
 */
public class TaggingTest {
    @TempDir
    public Path tempDir;

    private DisciTrack newApp() {
        return new DisciTrack(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void createAndEditTags_validCommands_returnsExactResponses() {
        DisciTrack app = newApp();
        String added = String.join(System.lineSeparator(), "Your next challenge:", "",
                "[T] [ ] sleep [#personal]", "You now have 1 task.", "",
                "Planning done. Now comes the most important part: doing it!");
        assertEquals(added, app.getResponse("todo sleep /tag personal"));
        assertEquals("Task 1 is now wearing the \"School\" badge!" + System.lineSeparator()
                + "[T] [ ] sleep [#personal] [#School]", app.getResponse("tag 1 School"));
        assertEquals("Task 1 already has tag \"School\".", app.getResponse("tag 1 SCHOOL"));
        assertEquals("\"School\" badge removed from task 1. Task stays in the game." + System.lineSeparator()
                + "[T] [ ] sleep [#personal]", app.getResponse("untag 1 school"));
        assertEquals("Task 1 does not have tag \"urgent\".", app.getResponse("untag 1 urgent"));
    }

    @Test
    public void taggedTaskTypes_restart_preservesTagsDatesAndStatus() {
        DisciTrack app = newApp();
        app.getResponse("todo slides /tag School /tag school /tag CS2103T");
        app.getResponse("deadline report /by 2026-10-01 /tag School");
        app.getResponse("event meeting /from 2026-10-01 /to 2026-10-02 /tag project");
        app.getResponse("mark 3");
        app.getResponse("tag 3 School");
        String expected = String.join(System.lineSeparator(),
                "1. [T] [ ] slides [#School] [#CS2103T]",
                "2. [D] [ ] report (by: Oct 01 2026) [#School]",
                "3. [E] [X] meeting (from: Oct 01 2026 to: Oct 02 2026) [#project] [#School]");
        assertEquals(expected, newApp().getResponse("list"));
    }

    @Test
    public void findAndCheckdate_filteredTasks_showFullListNumbers() {
        DisciTrack app = newApp();
        app.getResponse("todo groceries");
        app.getResponse("todo slides /tag school");
        app.getResponse("deadline slides /by 2026-10-01 /tag project");
        assertEquals(String.join(System.lineSeparator(), "Scouting report is in! Here's what matches:",
                "2. [T] [ ] slides [#school]", "3. [D] [ ] slides (by: Oct 01 2026) [#project]"),
                app.getResponse("find SLIDES"));
        assertTrue(app.getResponse("tag 3 urgent").contains("[D] [ ] slides"));
        assertFalse(app.getResponse("find groceries").contains("urgent"));
        assertEquals("Here are your tasks for this date:" + System.lineSeparator()
                + "3. [D] [ ] slides (by: Oct 01 2026) [#project] [#urgent]",
                app.getResponse("checkdate 2026-10-01"));
        assertEquals("Nothing on the radar, champ. Try another keyword or hit Show all.",
                app.getResponse("find urgent"));
        app.getResponse("delete 1");
        assertTrue(app.getResponse("checkdate 2026-10-01").contains("2. [D]"));
    }

    @Test
    public void invalidTagCommands_errors_leaveMemoryAndFileUnchanged() throws IOException {
        DisciTrack app = newApp();
        app.getResponse("todo sleep /tag personal");
        Path file = tempDir.resolve("tasks.txt");
        String originalFile = Files.readString(file);
        String originalList = app.getResponse("list");
        String[][] cases = {
            {"tag", "UHOH! Use: tag NUMBER TAG"},
            {"tag 1", "UHOH! Use: tag NUMBER TAG"},
            {"tag 1 school urgent", "UHOH! Use: tag NUMBER TAG"},
            {"untag 1", "UHOH! Use: untag NUMBER TAG"},
            {"untag 1 school urgent", "UHOH! Use: untag NUMBER TAG"},
            {"tag abc school", "UHOH! Use a whole-number task number from your list. Type list to check."},
            {"tag 0 #bad", "UHOH! No task has that number. Type list to check your task numbers."},
            {"tag 9 school", "UHOH! No task has that number. Type list to check your task numbers."},
            {"tag 1 #school", Task.INVALID_TAG_MESSAGE},
            {"untag 1 _bad", Task.INVALID_TAG_MESSAGE},
            {"todo sleep /tag", "UHOH! Each /tag must be followed by one tag name."},
            {"todo sleep /tag /tag school", "UHOH! Each /tag must be followed by one tag name."},
            {"todo sleep /tag school urgent", "UHOH! Put tags at the end using /tag TAG for each tag."},
            {"deadline report /tag school /by 2026-10-01",
                "UHOH! Put tags at the end using /tag TAG for each tag."},
            {"todo sleep /tag good /tag #bad", Task.INVALID_TAG_MESSAGE},
            {"todo /tag school", "OOPSIE! Add a description after todo. Example: todo exercise."}
        };
        for (String[] entry : cases) {
            assertEquals(entry[1], app.getResponse(entry[0]), entry[0]);
            assertEquals(originalList, app.getResponse("list"), entry[0]);
            assertEquals(originalFile, Files.readString(file), entry[0]);
        }
    }

    @Test
    public void creationSyntax_spacesAndReservedMarker_areHandledAsSpecified() {
        DisciTrack app = newApp();
        app.getResponse("todo   sleep   /tag   personal   /tag school   ");
        app.getResponse("todo explain /tag syntax");
        app.getResponse("todo explain /tagging syntax");
        assertEquals(String.join(System.lineSeparator(),
                "1. [T] [ ] sleep [#personal] [#school]",
                "2. [T] [ ] explain [#syntax]",
                "3. [T] [ ] explain /tagging syntax"), app.getResponse("list"));
    }

    @Test
    public void malformedTagFile_startup_blocksCommandsAndPreservesFile() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        String content = "T | 0 | sleep\nT | 0 | report | tags=#bad\n";
        Files.writeString(file, content);
        DisciTrack app = newApp();
        assertEquals("UHOH! Could not load your tasks: invalid tag data on line 2."
                + System.lineSeparator()
                + "Task commands are disabled to protect your saved data. Fix the file and restart.",
                app.getGreeting());
        for (String command : List.of("list", "todo test", "tag 1 school", "mark 1", "delete 1", "find sleep")) {
            assertEquals("UHOH! Task commands are disabled because your saved data could not be loaded. "
                    + "Fix the file and restart.", app.getResponse(command));
        }
        assertTrue(app.getResponse("help").contains("tag NUMBER TAG"));
        assertEquals(content, Files.readString(file));
        app.getResponse("bye");
        assertTrue(app.shouldExit());
        Files.writeString(file, "T | 0 | sleep\n");
        assertEquals("1. [T] [ ] sleep", newApp().getResponse("list"));
    }

    @Test
    public void unreadableExistingPath_startup_doesNotStartWritableEmptyList() throws IOException {
        Files.createDirectory(tempDir.resolve("tasks.txt"));
        DisciTrack app = newApp();
        assertTrue(app.getGreeting().contains("Could not load your tasks"));
        assertTrue(app.getResponse("todo overwrite").contains("Task commands are disabled"));
        assertTrue(Files.isDirectory(tempDir.resolve("tasks.txt")));
    }
}

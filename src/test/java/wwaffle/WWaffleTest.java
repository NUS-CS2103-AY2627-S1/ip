package wwaffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WWaffleTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_delete_preservesDeletedDetailsAndPersistsRemainingTasks() {
        String path = temporaryDirectory.resolve("delete-response.txt").toString();
        WWaffle app = new WWaffle(path);
        app.getResponse("todo keep this");
        app.getResponse("todo remove this");
        app.getResponse("mark 2");
        assertEquals("Task deleted. ☀️\n[T][X] remove this\nYou have 1 task left.",
                app.getResponse("delete 2"));
        WWaffle reloaded = new WWaffle(path);
        assertEquals(1, reloaded.getTaskCount());
        assertEquals("keep this", reloaded.getTasksForDisplay().get(0).getDescription());
        assertTrue(app.getResponse("delete 999").startsWith("⚠️"));
        assertEquals("Task deleted. ☀️\n[T][ ] keep this\nYou have 0 tasks left.",
                app.getResponse("delete 1"));
    }

    @Test
    void getResponse_unmark_reopensSelectedTaskAndPersists() {
        String path = temporaryDirectory.resolve("unmark.txt").toString();
        WWaffle app = new WWaffle(path);
        app.getResponse("todo first");
        app.getResponse("todo second");
        app.getResponse("mark 1");
        app.getResponse("mark 2");
        assertEquals("Task unmarked. 😑\n[T][ ] second", app.getResponse("unmark 2"));
        WWaffle reloaded = new WWaffle(path);
        assertTrue(reloaded.getTasksForDisplay().get(0).isDone());
        assertEquals(false, reloaded.getTasksForDisplay().get(1).isDone());
        assertTrue(app.getResponse("unmark 999").startsWith("⚠️"));
    }

    @Test
    void getResponse_socialPhrases_preserveTasksAndRecognizeFarewells() {
        WWaffle app = new WWaffle(temporaryDirectory.resolve("social.txt").toString());
        app.getResponse("todo say hello");
        String original = app.getResponse("list");
        for (String greeting : new String[]{"hi", "hello", "hey", "hey there", "  HEY   THERE  "}) {
            assertEquals("Hey there. ☕", app.getResponse(greeting));
        }
        for (String thanks : new String[]{"thanks", "thank you", "THANK YOU"}) {
            assertEquals("Anytime. 🧁", app.getResponse(thanks));
        }
        for (String farewell : new String[]{"bye", "goodbye", "see you", "See You Next Time"}) {
            assertTrue(app.isExitCommand(farewell));
            assertEquals("Goodbye. 👋", app.getResponse(farewell));
        }
        assertEquals("⚠️ No idea 🧇\nTry list, todo, deadline, "
                + "event, mark, unmark, delete, find, sort, clear, or bye.", app.getResponse("hello "
                + "unexpected arguments"));
        assertEquals(original, app.getResponse("list"));
        assertEquals(original, new WWaffle(temporaryDirectory.resolve("social.txt").toString())
                .getResponse("list"));
    }

    @Test
    void getResponse_taskWorkflow_returnsExpectedMessages() {
        WWaffle wwaffle = new WWaffle(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Task added. 🧁\n[T][ ] read book\n1 task total.",
                wwaffle.getResponse("todo read book"));
        assertEquals("Here are your little problems, neatly arranged. ☕\n1. [T][ ] read "
                + "book", wwaffle.getResponse("list"));
        assertEquals("Task marked. 🤩\n[T][X] read "
                + "book", wwaffle.getResponse("mark 1"));
        assertEquals(1, wwaffle.getTaskCount());
    }

    @Test
    void getResponse_invalidAndExitCommands_returnsHelpfulMessages() {
        WWaffle wwaffle = new WWaffle(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("⚠️ No idea 🧇\nTry list, todo, deadline, "
                + "event, mark, unmark, delete, find, sort, clear, or bye.", wwaffle.getResponse("dance"));
        assertEquals("Goodbye. 👋", wwaffle.getResponse("bye"));
        assertTrue(wwaffle.getWelcomeMessage().contains("No tasks yet."));
    }
    @Test
    void getResponse_sortName_preservesTiesAndPersistsNewIndices() {
        String filePath = temporaryDirectory.resolve("sorted.txt").toString();
        WWaffle app = new WWaffle(filePath);
        app.getResponse("todo zebra");
        app.getResponse("todo apple");
        app.getResponse("todo Apple");
        app.getResponse("mark 3");

        String expected = "Here are your little problems, neatly arranged by NAME. ☕\n1. [T][ ] "
                + "apple\n2. [T][X] Apple\n3. [T][ ] zebra";
        assertEquals(expected, app.getResponse("sort name"));
        WWaffle reopened = new WWaffle(filePath);
        assertTrue(reopened.getResponse("list").endsWith(
                "1. [T][ ] apple\n2. [T][X] Apple\n3. [T][ ] zebra"));
        assertEquals("Task marked. 🤩\n[T][X] "
                + "apple", reopened.getResponse("mark 1"));
        reopened.getResponse("todo aardvark");
        assertTrue(reopened.getResponse("list").endsWith("4. [T][ ] aardvark"));
    }

    @Test
    void getResponse_sortStatus_preservesOrderWithinGroupsAndPersists() {
        String filePath = temporaryDirectory.resolve("status.txt").toString();
        WWaffle app = new WWaffle(filePath);
        app.getResponse("todo first");
        app.getResponse("event second /from morning /to evening");
        app.getResponse("todo third");
        app.getResponse("todo fourth");
        app.getResponse("mark 1");
        app.getResponse("mark 3");

        app.getResponse("sort status");
        String sorted = app.getResponse("list");
        assertTrue(sorted.indexOf("second") < sorted.indexOf("fourth"));
        assertTrue(sorted.indexOf("fourth") < sorted.indexOf("first"));
        assertTrue(sorted.indexOf("first") < sorted.indexOf("third"));
        assertEquals(4, app.getTaskCount());
        assertEquals(sorted, new WWaffle(filePath).getResponse("list"));
    }

    @Test
    void getResponse_sortCommands_nameTheAppliedSortKey() {
        WWaffle app = new WWaffle(temporaryDirectory.resolve("headings.txt").toString());
        app.getResponse("todo zebra");
        app.getResponse("todo apple");

        assertTrue(app.getResponse("sort name")
                .startsWith("Here are your little problems, neatly arranged by NAME. ☕"));
        assertTrue(app.getResponse("sort status")
                .startsWith("Here are your little problems, neatly arranged by STATUS. ☕"));
    }

    @Test
    void getResponse_clear_removesEveryTaskAndPersistsEmptyList() {
        String filePath = temporaryDirectory.resolve("clear.txt").toString();
        WWaffle app = new WWaffle(filePath);
        app.getResponse("todo one");
        app.getResponse("deadline two /by 2026-12-02");

        assertEquals("Cleared!", app.getResponse("clear"));
        assertEquals(0, app.getTaskCount());
        assertEquals("Nothing here. Nature is healing. 🥐", app.getResponse("list"));
        assertEquals(0, new WWaffle(filePath).getTaskCount());
        assertEquals("⚠️ No idea 🧇\nTry: clear", app.getResponse("clear now"));
        assertEquals("Cleared!", app.getResponse("clear"));
    }

    @Test
    void getResponse_invalidSort_keepsMemoryAndStorageUnchanged() {
        String filePath = temporaryDirectory.resolve("invalid.txt").toString();
        WWaffle app = new WWaffle(filePath);
        app.getResponse("todo zebra");
        app.getResponse("todo apple");
        String original = app.getResponse("list");

        for (String command : new String[]{"sort", "sort date", "sort name extra", "sort NAME"}) {
            assertEquals("⚠️ No idea 🧇\nTry \"sort name\" or \"sort status\".", app.getResponse(command));
            assertEquals(original, app.getResponse("list"));
            assertEquals(original, new WWaffle(filePath).getResponse("list"));
        }
    }

    @Test
    void getResponse_sortEmptyOrSingleTask_returnsUnchangedList() {
        WWaffle app = new WWaffle(temporaryDirectory.resolve("small.txt").toString());
        assertEquals("Nothing here. Nature is healing. 🥐", app.getResponse("sort name"));
        assertEquals("Nothing here. Nature is healing. 🥐", app.getResponse("sort status"));
        app.getResponse("deadline submit /by 2026-12-02");
        String original = app.getResponse("list");
        assertTrue(app.getResponse("sort name").startsWith(
                "Here are your little problems, neatly arranged by NAME. ☕"));
        assertTrue(app.getResponse("sort status").startsWith(
                "Here are your little problems, neatly arranged by STATUS. ☕"));
        assertEquals(original, app.getResponse("list"));
    }
}

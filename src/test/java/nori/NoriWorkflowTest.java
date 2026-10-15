package nori;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises complete command workflows against real temporary storage, including recovery paths.
 */
class NoriWorkflowTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getReply_mixedTasksAndRestart_preservesTypesOrderAndCompletion() {
        Path dataFile = temporaryDirectory.resolve("data/nori.txt");
        Nori nori = new Nori(dataFile);

        assertFalse(nori.getReply("todo read book").isError());
        assertFalse(nori.getReply("deadline submit report /by 2026-09-18 2359").isError());
        assertFalse(nori.getReply("event meeting /from Monday 2pm /to Monday 3pm").isError());
        assertFalse(nori.getReply("mark 2").isError());

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][X] submit report (by: Sep 18 2026, 11:59 PM)\n"
                + "3.[E][ ] meeting (from: Monday 2pm to: Monday 3pm)",
                new Nori(dataFile).getResponse("list"));
    }

    @Test
    void getReply_markUnmarkAndDelete_updatesPersistedTaskNumbers() {
        Path dataFile = temporaryDirectory.resolve("nori.txt");
        Nori nori = new Nori(dataFile);
        nori.getResponse("todo first");
        nori.getResponse("todo second");
        nori.getResponse("mark 2");

        assertFalse(nori.getReply("unmark 2").isError());
        assertFalse(nori.getReply("delete 1").isError());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] second",
                new Nori(dataFile).getResponse("list"));
        assertTrue(nori.getReply("mark 2").isError());
    }

    @Test
    void getReply_invalidCommands_returnsErrorsWithoutChangingSavedTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nori.txt");
        Nori nori = new Nori(dataFile);
        nori.getResponse("todo keep me");
        String savedData = Files.readString(dataFile);
        String[] commands = {"", "nonsense", "todo", "deadline /by 2026-09-18 2359",
            "deadline report /by 2026-02-30 1200", "event /from Monday /to Tuesday",
            "event meeting /from /to Tuesday", "event meeting /from Monday /to",
            "mark 0", "mark -1", "mark 2", "unmark abc", "delete 2147483648", "find"};

        for (String command : commands) {
            assertTrue(nori.getReply(command).isError(), command);
            assertEquals(savedData, Files.readString(dataFile), command);
        }
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep me", nori.getResponse("list"));
    }

    @Test
    void getReply_whitespaceAndUnicode_preservesDescriptionAfterRestart() {
        Path dataFile = temporaryDirectory.resolve("nori.txt");
        Nori nori = new Nori(dataFile);

        assertFalse(nori.getReply("  todo\tread  résumé | notes  ").isError());

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read  résumé | notes",
                new Nori(dataFile).getResponse("list"));
    }

    @Test
    void getReply_searchIsCaseSensitiveAndNonDestructive() {
        Nori nori = new Nori(temporaryDirectory.resolve("nori.txt"));
        nori.getResponse("todo read book");
        nori.getResponse("todo return book");

        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n2.[T][ ] return book", nori.getResponse("find book"));
        assertEquals("No matching tasks. Search is case-sensitive; try another keyword.",
                nori.getResponse("find Book"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n2.[T][ ] return book", nori.getResponse("list"));
    }

    @Test
    void getReply_emptyListAndLastDeletion_explainsHowToStart() {
        Nori nori = new Nori(temporaryDirectory.resolve("nori.txt"));
        String emptyMessage = "Your list is empty. Start with: todo read a book";

        assertEquals(emptyMessage, nori.getResponse("list"));
        nori.getResponse("todo temporary");
        nori.getResponse("delete 1");
        assertEquals(emptyMessage, nori.getResponse("list"));
    }

    @Test
    void getReply_corruptFile_reportsErrorAndPreservesOriginal() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nori.txt");
        Files.writeString(dataFile, "damaged data\n");
        Nori nori = new Nori(dataFile);

        Reply reply = nori.getReply("todo do not overwrite");

        assertTrue(reply.isError());
        assertEquals("OOPS!!! The task data on line 1 is invalid.", reply.message());
        assertEquals("damaged data\n", Files.readString(dataFile));
        assertFalse(nori.getReply("help").isError());
        assertFalse(nori.getReply("bye").isError());
    }

    @Test
    void getReply_repairedFile_retriesLoadingWithoutRestart() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nori.txt");
        Files.writeString(dataFile, "damaged data");
        Nori nori = new Nori(dataFile);
        assertTrue(nori.getReply("list").isError());

        Files.writeString(dataFile, "");

        assertFalse(nori.getReply("todo recovered").isError());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] recovered", nori.getResponse("list"));
    }

    @Test
    void getReply_blockedDataDirectory_reportsErrorInsteadOfCrashing() throws IOException {
        Path parent = temporaryDirectory.resolve("data");
        Files.writeString(parent, "a file blocks the data directory");
        Nori nori = new Nori(parent.resolve("nori.txt"));

        assertTrue(nori.getReply("todo task").isError());
        assertEquals("a file blocks the data directory", Files.readString(parent));
    }

    @Test
    void getReply_failedSave_discardsUnsavedMutationAndRecovers() throws IOException {
        String[] commands = {"todo unsaved", "delete 1", "mark 1", "unmark 2"};
        for (int i = 0; i < commands.length; i++) {
            Path dataFile = temporaryDirectory.resolve("nori-" + i + ".txt");
            Nori nori = new Nori(dataFile);
            nori.getResponse("todo first");
            nori.getResponse("todo second");
            nori.getResponse("mark 2");
            String savedData = Files.readString(dataFile);
            String originalList = nori.getResponse("list");
            // A directory at the file path reliably forces a write failure on every OS.
            Files.delete(dataFile);
            Files.createDirectory(dataFile);

            assertTrue(nori.getReply(commands[i]).isError(), commands[i]);
            Files.delete(dataFile);
            Files.writeString(dataFile, savedData);

            assertEquals(originalList, nori.getResponse("list"), commands[i]);
        }
    }
}

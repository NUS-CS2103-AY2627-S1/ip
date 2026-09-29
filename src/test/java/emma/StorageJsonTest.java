package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks how the save file is read: what a hand-edited file may look like and still be
 * accepted, and that every way of getting it wrong is reported with the line to look at
 * rather than being silently ignored.
 */
public class StorageJsonTest {

    @TempDir
    Path tempDir;

    /** Writes the text as the save file and returns storage pointed at it. */
    private Storage storageHolding(String contents) throws IOException {
        Path file = tempDir.resolve("emma.json");
        Files.writeString(file, contents);
        return new Storage(file.toString());
    }

    /** Returns the complaint made when the save file is read. */
    private String complaintAbout(String contents) throws IOException {
        Storage storage = storageHolding(contents);
        return assertThrows(EmmaException.class, storage::load).getMessage();
    }

    @Test
    public void load_emptyArray_returnsNoTasks() throws Exception {
        assertTrue(storageHolding("[]").load().isEmpty());
    }

    @Test
    public void load_fileWrittenOnOneLine_isStillRead() throws Exception {
        List<Task> tasks = storageHolding(
                "[{\"type\":\"T\",\"done\":false,\"description\":\"read book\"}]").load();
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void load_fileWithGenerousWhitespace_isStillRead() throws Exception {
        List<Task> tasks = storageHolding(
                "\n\n  [\n\t{  \"type\"  :  \"T\" ,  \"done\" :  true ,\n"
                + "     \"description\" : \"read book\"  }  \n ]  \n\n").load();
        assertEquals("[T][x] read book", tasks.get(0).toString());
    }

    @Test
    public void load_fieldsInAnyOrder_areStillUnderstood() throws Exception {
        List<Task> tasks = storageHolding(
                "[{\"description\":\"return book\",\"by\":\"2019-10-15\","
                + "\"done\":true,\"type\":\"D\"}]").load();
        assertEquals("[D][x] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    public void load_escapedCharactersInADescription_comeBackAsThemselves() throws Exception {
        List<Task> tasks = storageHolding(
                "[{\"type\":\"T\",\"done\":false,"
                + "\"description\":\"say \\\"hi\\\"\\tnow\\\\then\"}]").load();
        assertEquals("[T][ ] say \"hi\"\tnow\\then", tasks.get(0).toString());
    }

    @Test
    public void load_everyTaskType_buildsTheRightKindOfTask() throws Exception {
        List<Task> tasks = storageHolding("[\n"
                + "{\"type\":\"T\",\"done\":false,\"description\":\"a\"},\n"
                + "{\"type\":\"D\",\"done\":false,\"description\":\"b\",\"by\":\"2019-10-15\"},\n"
                + "{\"type\":\"E\",\"done\":false,\"description\":\"c\","
                + "\"from\":\"2019-10-15\",\"to\":\"2019-10-16\"}\n]").load();
        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0) instanceof Todo);
        assertTrue(tasks.get(1) instanceof Deadline);
        assertTrue(tasks.get(2) instanceof Event);
    }

    @Test
    public void load_textThatIsNotAnArray_isRejected() throws IOException {
        assertTrue(complaintAbout("not json at all").contains("expected '['"));
    }

    @Test
    public void load_arrayThatIsNeverClosed_isRejected() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"T\",\"done\":false,\"description\":\"a\"}")
                .contains("expected ']'"));
    }

    @Test
    public void load_textAfterTheClosingBracket_isRejected() throws IOException {
        assertTrue(complaintAbout("[] and then some")
                .contains("there is extra text after the closing ']'"));
    }

    @Test
    public void load_taskMissingItsDescription_saysWhichFieldIsMissing() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"T\",\"done\":false}]")
                .contains("a task is missing its \"description\" field"));
    }

    @Test
    public void load_taskMissingItsType_saysWhichFieldIsMissing() throws IOException {
        assertTrue(complaintAbout("[{\"done\":false,\"description\":\"a\"}]")
                .contains("a task is missing its \"type\" field"));
    }

    @Test
    public void load_deadlineMissingItsDate_saysWhichFieldIsMissing() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"D\",\"done\":false,\"description\":\"a\"}]")
                .contains("a task is missing its \"by\" field"));
    }

    @Test
    public void load_eventMissingItsEndDate_saysWhichFieldIsMissing() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"E\",\"done\":false,\"description\":\"a\","
                + "\"from\":\"2019-10-15\"}]").contains("a task is missing its \"to\" field"));
    }

    @Test
    public void load_typeThatIsNotATaskEmmaKnows_isRejected() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"X\",\"done\":false,\"description\":\"a\"}]")
                .contains("\"type\" must be \"T\", \"D\" or \"E\""));
    }

    @Test
    public void load_dateThatIsNotARealDate_saysWhatItExpected() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"D\",\"done\":false,\"description\":\"a\","
                        + "\"by\":\"Sunday\"}]")
                .contains("\"by\" must be a date like \"2019-10-15\", not \"Sunday\""));
    }

    @Test
    public void load_doneThatIsNotTrueOrFalse_isRejected() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"T\",\"done\":\"yes\",\"description\":\"a\"}]")
                .contains("\"done\" must be true or false"));
    }

    @Test
    public void load_textMissingItsClosingQuote_isRejected() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"T\",\"done\":false,\"description\":\"a}]")
                .contains("a piece of text is missing its closing quote"));
    }

    @Test
    public void load_newlineEscapeInADescription_comesBackAsALineBreak() throws Exception {
        List<Task> tasks = storageHolding(
                "[{\"type\":\"T\",\"done\":false,\"description\":\"first\\nsecond\"}]").load();
        assertEquals("[T][ ] first\nsecond", tasks.get(0).toString());
    }

    @Test
    public void load_escapeEmmaDoesNotKnow_keepsTheCharacterItself() throws Exception {
        List<Task> tasks = storageHolding(
                "[{\"type\":\"T\",\"done\":false,\"description\":\"a\\qb\"}]").load();
        assertEquals("[T][ ] aqb", tasks.get(0).toString());
    }

    @Test
    public void load_fileEndingInABackslash_isRejectedRatherThanReadPastTheEnd() throws IOException {
        assertTrue(complaintAbout("[{\"type\":\"T\",\"done\":false,\"description\":\"a\\")
                .contains("a piece of text is missing its closing quote"));
    }

    @Test
    public void load_pathThatIsAFolderNotAFile_reportsThatItCannotBeRead() throws IOException {
        Path folder = tempDir.resolve("emma.json");
        Files.createDirectory(folder);
        String message = assertThrows(EmmaException.class, () ->
                new Storage(folder.toString()).load()).getMessage();
        assertTrue(message.startsWith("I couldn't read"), message);
    }

    @Test
    public void load_problemOnALaterLine_reportsThatLine() throws IOException {
        String message = complaintAbout("[\n"
                + "{\"type\":\"T\",\"done\":false,\"description\":\"a\"},\n"
                + "{\"type\":\"X\",\"done\":false,\"description\":\"b\"}\n]");
        assertTrue(message.contains("looks wrong on line 3"), message);
    }

    @Test
    public void load_anyProblem_namesTheFileToFix() throws IOException {
        assertTrue(complaintAbout("nonsense").contains("emma.json"));
    }
}

package xian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class XianTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void executeCommand_updateDescription_updatesExistingTask() throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("todo read book");

        String response = xian.executeCommand("update 1 desc read textbook");

        assertEquals("\tDone — I've updated this task:\n\t [T][ ] read textbook\n", response);
    }

    @Test
    public void executeCommand_updateDeadlineDate_updatesExistingTask() throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("deadline submit report /by 10/9/2026 1800");

        String response = xian.executeCommand("update 1 by 11/9/2026 1800");

        assertEquals("\tDone — I've updated this task:\n"
                + "\t [D][ ] submit report (by: 11/9/2026 1800)\n", response);
    }

    @Test
    public void executeCommand_updateEventEnd_updatesExistingTask() throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("event project meeting /from 10/9/2026 1400 /to 10/9/2026 1600");

        String response = xian.executeCommand("update 1 to 10/9/2026 1700");

        assertEquals("\tDone — I've updated this task:\n"
                + "\t [E][ ] project meeting (from: 10/9/2026 1400 to: 10/9/2026 1700)\n", response);
    }

    @Test
    public void executeCommand_updateTodoDate_throwsXianException() throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("todo read book");

        assertThrows(XianException.class, () ->
                xian.executeCommand("update 1 by 11/9/2026 1800"));
    }

    @Test
    public void executeCommand_updateInvalidDate_throwsDateTimeParseException()
            throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("deadline submit report /by 10/9/2026 1800");

        assertThrows(DateTimeParseException.class, () ->
                xian.executeCommand("update 1 by not-a-date"));
    }

    @Test
    public void executeCommand_blankInput_throwsXianException() {
        Xian xian = createXian();

        assertThrows(XianException.class, () -> xian.executeCommand("   "));
    }

    @Test
    public void executeCommand_nonNumericTaskIndex_throwsXianException() throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("todo read book");

        assertThrows(XianException.class, () ->
                xian.executeCommand("delete one"));
    }

    @Test
    public void executeCommand_eventEndBeforeStart_throwsXianException() {
        Xian xian = createXian();

        assertThrows(XianException.class, () ->
                xian.executeCommand("event project meeting /from 10/9/2026 1700 /to 10/9/2026 1600"));
    }

    @Test
    public void executeCommand_invalidDate_throwsDateTimeParseException() {
        Xian xian = createXian();

        assertThrows(DateTimeParseException.class, () ->
                xian.executeCommand("deadline submit report /by 31/2/2026 1800"));
    }

    @Test
    public void executeCommand_extraWhitespace_acceptsNormalizedCommand()
            throws IOException, XianException {
        Xian xian = createXian();

        xian.executeCommand("  todo   read   book  ");

        assertEquals("\tHere are your tasks:\n"
                + "\t1. [T][ ] read book\n", xian.executeCommand("list"));
    }

    @Test
    public void executeCommand_storageDelimiterInDescription_throwsXianException() {
        Xian xian = createXian();

        assertThrows(XianException.class, () ->
                xian.executeCommand("todo read | book"));
    }

    @Test
    public void executeCommand_unknownCommand_throwsXianException() {
        Xian xian = createXian();

        assertThrows(XianException.class, () ->
                xian.executeCommand("archive old tasks"));
    }

    @Test
    public void executeCommand_updateEventEndBeforeStart_throwsXianException()
            throws IOException, XianException {
        Xian xian = createXian();
        xian.executeCommand("event project meeting /from 10/9/2026 1400 /to 10/9/2026 1600");

        assertThrows(XianException.class, () ->
                xian.executeCommand("update 1 to 10/9/2026 1300"));

        assertEquals("\tHere are your tasks:\n"
                + "\t1. [E][ ] project meeting (from: 10/9/2026 1400 to: 10/9/2026 1600)\n",
                xian.executeCommand("list"));
    }

    @Test
    public void executeCommand_listWithArguments_throwsXianException() {
        Xian xian = createXian();

        assertThrows(XianException.class, () -> xian.executeCommand("list extra"));
    }

    @Test
    public void getStartupWarning_malformedSaveFile_returnsWarning() throws IOException {
        Path saveFile = temporaryDirectory.resolve("xian.txt");
        Files.writeString(saveFile, "T | 2 | read book");

        Xian xian = new Xian(saveFile.toString());

        assertFalse(xian.getStartupWarning().isBlank());
    }

    private Xian createXian() {
        Path saveFile = temporaryDirectory.resolve("xian.txt");
        return new Xian(saveFile.toString());
    }
}

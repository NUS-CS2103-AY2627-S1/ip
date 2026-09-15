package monkey;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonkeyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void defaultFilePath_usesProductName() throws ReflectiveOperationException {
        Field defaultFilePath = Monkey.class.getDeclaredField("DEFAULT_FILE_PATH");
        defaultFilePath.setAccessible(true);

        assertEquals("data/monkey.txt", defaultFilePath.get(null));
    }

    @Test
    void processCommand_byeReportsExit() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        CommandResult result = monkey.processCommand("bye");

        assertTrue(result.shouldExit());
    }

    @Test
    void getResponse_todoCommand_returnsCommandOutput() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        String response = monkey.getResponse("todo learn JavaFX");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] learn JavaFX\n"
                + "Now you have 1 tasks in the list.", response);
    }

    @Test
    void getResponse_leadingAndRepeatedWhitespace_parsesCommandAndArguments() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        String response = monkey.getResponse("   todo    learn   Java   ");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] learn Java\n"
                + "Now you have 1 tasks in the list.", response);
    }

    @Test
    void getResponse_listWithArguments_rejectsUnexpectedArguments() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        String response = monkey.getResponse("list extra");

        assertEquals("OOPS! Monkey says: The 'list' command does not accept arguments.", response);
    }

    @Test
    void getResponse_byeWithArguments_rejectsUnexpectedArguments() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        String response = monkey.getResponse("bye now");

        assertEquals("OOPS! Monkey says: The 'bye' command does not accept arguments.", response);
    }

    @Test
    void getResponse_duplicateTodo_rejectsSecondTask() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());
        monkey.getResponse("todo buy milk");

        String response = monkey.getResponse("todo   buy   milk");

        assertEquals("OOPS! Monkey says: That task is already in your list.", response);
        assertEquals("Here are the tasks in your list:\n1.[T][ ] buy milk", monkey.getResponse("list"));
    }

    @Test
    void getResponse_descriptionWithStorageDelimiter_rejectsTask() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        String response = monkey.getResponse("todo buy | milk");

        assertEquals("OOPS! Monkey says: Task descriptions cannot contain '|'.", response);
    }

    @Test
    void getResponse_malformedStoredTask_reportsWarningBeforeCommandOutput() throws Exception {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Files.writeString(saveFile, "T | 0 | buy milk\nmalformed data\n");
        Monkey monkey = new Monkey(saveFile.toString());

        String response = monkey.getResponse("list");

        assertEquals("OOPS! Monkey says: Ignored malformed saved task data on line 2.\n"
                + "Here are the tasks in your list:\n"
                + "1.[T][ ] buy milk", response);
        assertEquals("Here are the tasks in your list:\n1.[T][ ] buy milk", monkey.getResponse("list"));
    }

    @Test
    void getResponse_blankAndNullInput_reportsMissingCommand() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        assertEquals("OOPS! Monkey says: This monkey heard nothing! Please swing over a command.",
                monkey.getResponse("   "));
        assertEquals("OOPS! Monkey says: This monkey heard nothing! Please swing over a command.",
                monkey.getResponse(null));
    }

    @Test
    void getResponse_unknownCommand_reportsSupportedCommands() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        assertTrue(monkey.getResponse("dance").startsWith("OOPS! Monkey says: This monkey does not recognize"));
    }

    @Test
    void constructor_existingSave_restoresTasksForNextSession() {
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        Monkey firstSession = new Monkey(saveFile.toString());
        firstSession.getResponse("todo persistent task");

        Monkey secondSession = new Monkey(saveFile.toString());

        assertEquals("Here are the tasks in your list:\n1.[T][ ] persistent task",
                secondSession.getResponse("list"));
    }

    @Test
    void getResponse_invalidDeadlineDate_reportsModelValidationError() {
        Monkey monkey = new Monkey(temporaryDirectory.resolve("monkey.txt").toString());

        assertTrue(monkey.getResponse("deadline submit /by invalid").startsWith("Use a date like"));
    }
}
